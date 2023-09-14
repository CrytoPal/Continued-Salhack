package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import io.github.racoondog.norbit.EventPriority;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.managers.TickRateManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.MathUtil;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.ItemUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ShulkerBulletEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

import java.util.Comparator;

public class KillAura extends Module {
    public final Value<Modes> Mode = new Value<Modes>("Mode", new String[]{"Mode"}, "The KillAura Mode to use", Modes.Closest);
    public final Value<Float> Distance = new Value<Float>("Distance", new String[]{"Range"}, "Range for attacking a target", 5.0f, 0.0f, 10.0f, 1.0f);
    public final Value<Boolean> HitDelay = new Value<Boolean>("Hit Delay", new String[]{"Hit Delay"}, "Use vanilla hit delay", true);
    public final Value<Boolean> TPSSync = new Value<Boolean>("TPSSync", new String[]{"TPSSync"}, "Use TPS Sync for hit delay", false);
    public final Value<Boolean> Players = new Value<Boolean>("Players", new String[]{"Players"}, "Should we target Players", true);
    public final Value<Boolean> Monsters = new Value<Boolean>("Monsters", new String[]{"Players"}, "Should we target Monsters", true);
    public final Value<Boolean> Neutrals = new Value<Boolean>("Neutrals", new String[]{"Players"}, "Should we target Neutrals", false);
    public final Value<Boolean> Animals = new Value<Boolean>("Animals", new String[]{"Players"}, "Should we target Animals", false);
    public final Value<Boolean> Tamed = new Value<Boolean>("Tamed", new String[]{"Players"}, "Should we target Tamed", false);
    public final Value<Boolean> Projectiles = new Value<Boolean>("Projectile", new String[]{"Projectile"}, "Should we target Projectiles (shulker bullets, etc)", false);
    public final Value<Boolean> SwordOnly = new Value<Boolean>("SwordOnly", new String[]{"SwordOnly"}, "Only activate on sword", false);
    public final Value<Boolean> PauseIfCrystal = new Value<Boolean>("PauseIfCrystal", new String[]{"PauseIfCrystal"}, "Pauses if a crystal is in your hand", false);
    public final Value<Boolean> PauseIfEating = new Value<Boolean>("PauseIfEating", new String[]{"PauseIfEating"}, "Pauses if your eating", false);
    public final Value<Boolean> AutoSwitch = new Value<Boolean>("AutoSwitch", new String[]{"AutoSwitch"}, "Automatically switches to a sword in your hotbar", false);
    public final Value<Integer> Ticks = new Value<Integer>("Ticks", new String[]{"Ticks"}, "If you don't have HitDelay on, how fast the kill aura should be hitting", 10, 0, 40, 1);
    public final Value<Integer> Iterations = new Value<Integer>("Iterations", new String[]{""}, "Allows you to do more iteratons per tick", 1, 1, 10, 1);
    public final Value<Boolean> Only32k = new Value<Boolean>("32kOnly", new String[]{""}, "Only killauras when 32k sword is in your hand", false);

    public enum Modes {
        Closest,
        Priority,
        Switch,
    }

    public KillAura() {
        super("KillAura", new String[]{"Aura"}, "Automatically faces and hits entities around you", 0, 0xFF0000, ModuleType.COMBAT);
    }

    private Entity CurrentTarget;
    private Timer AimbotResetTimer = new Timer();
    private int RemainingTicks = 0;

    @Override
    public void onEnable() {
        super.onEnable();
        RemainingTicks = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public String getMetaData() {
        return Mode.getValue().toString();
    }

    private boolean IsValidTarget(Entity p_Entity, Entity p_ToIgnore) {
        if (!(p_Entity instanceof LivingEntity)) {
            boolean l_IsProjectile = (p_Entity instanceof ShulkerBulletEntity || p_Entity instanceof FireballEntity);

            if (!l_IsProjectile)
                return false;

            if (l_IsProjectile && !Projectiles.getValue())
                return false;
        }

        if (p_ToIgnore != null && p_Entity == p_ToIgnore)
            return false;

        if (p_Entity instanceof PlayerEntity) {
            /// Ignore if it's us
            if (p_Entity == mc.player)
                return false;

            if (!Players.getValue())
                return false;

            /// They are a friend, ignore it.
            if (FriendManager.Get().IsFriend(p_Entity))
                return false;
        }

        if (EntityUtil.isHostileMob(p_Entity) && !Monsters.getValue()) return false;

        if (EntityUtil.isPassive(p_Entity)) {
            if (p_Entity instanceof HorseEntity) {
                HorseEntity l_Horse = (HorseEntity) p_Entity;

                if (l_Horse.isTame() && !Tamed.getValue())
                    return false;
            }

            if (!Animals.getValue())
                return false;
        }

        if (EntityUtil.isHostileMob(p_Entity) && !Monsters.getValue())
            return false;

        if (EntityUtil.isNeutralMob(p_Entity) && !Neutrals.getValue())
            return false;

        boolean l_HealthCheck = true;

        if (p_Entity instanceof LivingEntity) {
            LivingEntity l_Base = (LivingEntity) p_Entity;

            l_HealthCheck = !l_Base.isDead() && l_Base.getHealth() > 0.0f;
        }

        return l_HealthCheck && p_Entity.distanceTo(p_Entity) <= Distance.getValue();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void OnTick(PlayerMotionUpdate event) {
        if (event.getEra() != EventEra.PRE)
            return;

        if (!(mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL && PauseIfCrystal.getValue())
                return;

            if (mc.player.getMainHandStack().getItem() == Items.GOLDEN_APPLE && PauseIfEating.getValue())
                return;

            int l_Slot = -1;

            if (AutoSwitch.getValue()) {
                for (int l_I = 0; l_I < 9; ++l_I) {
                    if (mc.player.getInventory().getStack(l_I).getItem() instanceof SwordItem) {
                        l_Slot = l_I;
                        mc.player.getInventory().selectedSlot = l_Slot;
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(l_Slot));
                        break;
                    }
                }
            }

            if (SwordOnly.getValue() && l_Slot == -1)
                return;
        }

        if (Only32k.getValue()) {
            if (!ItemUtil.Is32k(mc.player.getMainHandStack()))
                return;
        }

        if (AimbotResetTimer.passed(5000)) {
            AimbotResetTimer.reset();
        }

        if (RemainingTicks > 0) {
            --RemainingTicks;
        }

        /// Chose target based on current mode
        Entity l_TargetToHit = CurrentTarget;

        switch (Mode.getValue()) {
            case Closest:
                l_TargetToHit = EntityUtil.getEntities().stream()
                        .filter(p_Entity -> IsValidTarget(p_Entity, null))
                        .min(Comparator.comparing(p_Entity -> mc.player.distanceTo(p_Entity)))
                        .orElse(null);
                break;
            case Priority:
                if (l_TargetToHit == null) {
                    l_TargetToHit = EntityUtil.getEntities().stream()
                            .filter(p_Entity -> IsValidTarget(p_Entity, null))
                            .min(Comparator.comparing(p_Entity -> mc.player.distanceTo(p_Entity)))
                            .orElse(null);
                }
                break;
            case Switch:
                l_TargetToHit = EntityUtil.getEntities().stream()
                        .filter(p_Entity -> IsValidTarget(p_Entity, null))
                        .min(Comparator.comparing(p_Entity -> mc.player.distanceTo(p_Entity)))
                        .orElse(null);

                if (l_TargetToHit == null)
                    l_TargetToHit = CurrentTarget;

                break;
            default:
                break;

        }

        /// nothing to hit - return until next tick for searching
        if (l_TargetToHit == null || l_TargetToHit.distanceTo(mc.player) > Distance.getValue()) {
            CurrentTarget = null;
            return;
        }

        float[] l_Rotation = MathUtil.calcAngle(mc.player.getEyePos(), l_TargetToHit.getEyePos());

        PlayerUtil.PacketFacePitchAndYaw(l_Rotation[0], l_Rotation[1]);
        event.cancel();

        final float l_Ticks = 20.0f - TickRateManager.Get().getTickRate();

        final boolean l_IsAttackReady = this.HitDelay.getValue() ? (mc.player.getAttackCooldownProgress(TPSSync.getValue() ? -l_Ticks : 0.0f) >= 1) : true;

        if (!l_IsAttackReady)
            return;

        if (!HitDelay.getValue() && RemainingTicks > 0)
            return;

        RemainingTicks = Ticks.getValue();

        //  mc.playerController.attackEntity(mc.player, l_TargetToHit);
        for (int l_I = 0; l_I < Iterations.getValue(); ++l_I) {
            mc.interactionManager.attackEntity(mc.player, l_TargetToHit);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
