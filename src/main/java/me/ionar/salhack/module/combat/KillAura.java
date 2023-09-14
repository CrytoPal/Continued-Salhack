package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.ItemUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Items;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.Hand;

import java.util.Comparator;

public class KillAuraModule extends Module {
    public final Value<modes> mode = new Value<>("Mode", new String[]{"Mode"}, "The KillAura Mode to use", modes.Closest);
    public final Value<Float> distance = new Value<>("Distance", new String[]{"Range"}, "Range for attacking a target", 5.0f, 0.0f, 10.0f, 1.0f);
    public final Value<Boolean> hitDelay = new Value<>("Hit Delay", new String[]{"Hit Delay"}, "Use vanilla hit delay", true);
    public final Value<Boolean> tpsSync = new Value<>("TPSSync", new String[]{"TPSSync"}, "Use TPS Sync for hit delay", false);
    public final Value<Boolean> players = new Value<>("Players", new String[]{"Players"}, "Should we target Players", true);
    public final Value<Boolean> monsters = new Value<>("Monsters", new String[]{"Players"}, "Should we target Monsters", true);
    public final Value<Boolean> neutrals = new Value<>("Neutrals", new String[]{"Players"}, "Should we target Neutrals", false);
    public final Value<Boolean> animals = new Value<>("Animals", new String[]{"Players"}, "Should we target Animals", false);
    public final Value<Boolean> tamed = new Value<>("Tamed", new String[]{"Players"}, "Should we target Tamed", false);
    public final Value<Boolean> projectiles = new Value<>("Projectile", new String[]{"Projectile"}, "Should we target Projectiles (shulker bullets, etc)", false);
    public final Value<Boolean> swordOnly = new Value<>("SwordOnly", new String[]{"SwordOnly"}, "Only activate on sword", false);
    public final Value<Boolean> pauseIfCrystal = new Value<>("PauseIfCrystal", new String[]{"PauseIfCrystal"}, "Pauses if a crystal is in your hand", false);
    public final Value<Boolean> pauseIfEating = new Value<>("PauseIfEating", new String[]{"PauseIfEating"}, "Pauses if your eating", false);
    public final Value<Boolean> autoSwitch = new Value<>("AutoSwitch", new String[]{"AutoSwitch"}, "Automatically switches to a sword in your hotbar", false);
    public final Value<Integer> ticks = new Value<>("Ticks", new String[]{"Ticks"}, "If you don't have HitDelay on, how fast the kill aura should be hitting", 10, 0, 40, 1);
    public final Value<Integer> iterations = new Value<>("Iterations", new String[]{""}, "Allows you to do more iterations per tick", 1, 1, 10, 1);
    public final Value<Boolean> only32K = new Value<>("32kOnly", new String[]{""}, "Only killauras when 32k sword is in your hand", false);
    private Entity currentTarget;
    private final Timer aimbotResetTimer = new Timer();
    private int remainingTicks = 0;
    public enum modes {
        Closest,
        Priority,
        Switch,
    }

    public KillAuraModule() {
        super("KillAura", new String[] {"Aura"}, "Automatically faces and hits entities around you", 0, 0xFF0000, ModuleType.COMBAT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        remainingTicks = 0;
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public String getMetaData() {
        return mode.getValue().toString();
    }

    private boolean isValidTarget(Entity entity, Entity toIgnore) {
        if (!(entity instanceof LivingEntity)) {
            boolean isProjectile = entity instanceof ProjectileEntity;
            if (!isProjectile || !projectiles.getValue()) return false;
        }
        if (toIgnore != null && entity == toIgnore) return false;
        if (entity instanceof PlayerEntity && (entity == mc.player || !players.getValue() || SalHack.getFriendManager().isFriend(entity))) return false;
        if (EntityUtil.isHostileMob(entity) && !monsters.getValue()) return false;
        if (EntityUtil.isPassive(entity)) {
            if (entity instanceof HorseEntity horse && horse.isTame() && !tamed.getValue()) return false;
            if (!animals.getValue()) return false;
        }
        if (EntityUtil.isHostileMob(entity) && !monsters.getValue()) return false;
        if (EntityUtil.isNeutralMob(entity) && !neutrals.getValue()) return false;
        boolean healthCheck = true;
        if (entity instanceof LivingEntity base) {
            healthCheck = !base.isDead() && base.getHealth() > 0.0f;
        }
        return healthCheck && entity.distanceTo(entity) <= distance.getValue();
    }

    @EventHandler
    private void onTick(TickEvent event) {
        if (event.isPre() || mc.player == null || mc.interactionManager == null) return;
        if (!(mc.player.getMainHandStack().getItem() instanceof SwordItem)) {
            if (mc.player.getMainHandStack().getItem() == Items.END_CRYSTAL && pauseIfCrystal.getValue()) return;
            if (mc.player.getMainHandStack().getItem() == Items.GOLDEN_APPLE && pauseIfEating.getValue()) return;
            int slot = -1;
            if (autoSwitch.getValue()) {
                for (int i = 0; i < 9; ++i) {
                    if (mc.player.getInventory().getStack(i).getItem() instanceof SwordItem) {
                        slot = i;
                        mc.player.getInventory().selectedSlot = slot;
                        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                        break;
                    }
                }
            }
            if (swordOnly.getValue() && slot == -1) return;
        }

        if (only32K.getValue() && !ItemUtil.is32K(mc.player.getMainHandStack())) return;
        if (aimbotResetTimer.passed(5000)) aimbotResetTimer.reset();
        if (remainingTicks > 0) --remainingTicks;
        /// Chose target based on current mode
        Entity targetToHit = currentTarget;
        switch (mode.getValue()) {
            case Closest -> targetToHit = EntityUtil.getEntities().stream().filter(entity -> isValidTarget(entity, null)).min(Comparator.comparing(entity -> mc.player.distanceTo(entity))).orElse(null);
            case Priority -> {
                if (targetToHit == null) targetToHit = EntityUtil.getEntities().stream().filter(entity -> isValidTarget(entity, null)).min(Comparator.comparing(entity -> mc.player.distanceTo(entity))).orElse(null);
            } case Switch -> {
                targetToHit = EntityUtil.getEntities().stream().filter(entity -> isValidTarget(entity, null)).min(Comparator.comparing(entity -> mc.player.distanceTo(entity))).orElse(null);
                if (targetToHit == null) targetToHit = currentTarget;
            } default -> {}
        }
        /// nothing to hit - return until next tick for searching
        if (targetToHit == null || targetToHit.distanceTo(mc.player) > distance.getValue()) {
            currentTarget = null;
            return;
        }
        final float ticks = 20.0f - SalHack.getTickRateManager().getTickRate();
        final boolean isAttackReady = !hitDelay.getValue() || (mc.player.getAttackCooldownProgress(tpsSync.getValue() ? -ticks : 0.0f) >= 1);
        if (!isAttackReady) return;
        if (!hitDelay.getValue() && remainingTicks > 0) return;
        remainingTicks = this.ticks.getValue();
        //  mc.playerController.attackEntity(mc.player, l_TargetToHit);
        for (int i = 0; i < iterations.getValue(); ++i) {
            mc.interactionManager.attackEntity(mc.player, targetToHit);
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }
}
