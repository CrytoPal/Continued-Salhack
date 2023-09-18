package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerJumpEvent;
import me.ionar.salhack.events.player.PlayerMoveEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.world.Timer;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import static me.ionar.salhack.main.Wrapper.mc;

public class Speed extends Module {
    public final Value<Modes> Mode = new Value<>("Mode", new String[]{"Mode"}, "The mode of speed to use", Modes.Strafe);
    public final Value<Boolean> UseTimer = new Value<>("UseTimer", new String[]{"UseTimer"}, "Uses timer to go faster", false);
    public static final Value<Double> Speed = new Value<>("Speed", new String[] {""}, "Speed you move at (OnGround).", 1.0, 0.0, 2.0, 0.1);
    public static final Value<Double> StrafeSpeed = new Value<>("StrafeSpeed", new String[] {""}, "Speed you move at (Strafe)", 1.0, 0.0, 2.0, 0.1);
    public final Value<Boolean> AutoSprint = new Value<>("AutoSprint", new String[]{"AutoSprint"}, "Automatically sprints for you", false);
    public final Value<Boolean> SpeedInLiquids = new Value<>("SpeedInWater", new String[]{"SpeedInWater"}, "Speeds in water", false);

    public enum Modes {
        Strafe,
        OnGround
    }

    private Timer timer = null;

    public Speed() {
        super("Speed", "Speed strafe", 0, 0xDB2468, ModuleType.MOVEMENT);
    }


    @Override
    public String getMetaData() {
        return String.valueOf(Mode.getValue());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer = (me.ionar.salhack.module.world.Timer) ModuleManager.Get().GetMod(me.ionar.salhack.module.world.Timer.class);
    }

    @EventHandler
    public void OnPlayerTick(TickEvent event) {
        if (mc.player != null) {
            double velocity = Math.abs(mc.player.getVelocity().getX()) + Math.abs(mc.player.getVelocity().getZ());
            if ((mc.player.isTouchingWater() || mc.player.isInLava()) && !SpeedInLiquids.getValue()) return;
            if (UseTimer.getValue()) timer.SetOverrideSpeed(1.088f);

            if (Mode.getValue() == Modes.OnGround) {
                if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0) && mc.player.isOnGround()) {
                    if (!mc.player.isSprinting()) {
                        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                    }

                    mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                    mc.player.updateVelocity(Speed.getValue().floatValue(), new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                }
            } else if (Mode.getValue() == Modes.Strafe) {
                if ((mc.player.forwardSpeed != 0 || mc.player.sidewaysSpeed != 0)) {
                    if (AutoSprint.getValue()) {
                        if (!mc.player.isSprinting()) {
                            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.START_SPRINTING));
                        }
                    }

                    mc.player.setVelocity(new Vec3d(0, mc.player.getVelocity().y, 0));
                    mc.player.updateVelocity(StrafeSpeed.getValue().floatValue(), new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));

                    if (velocity >= 0.12 && mc.player.isOnGround()) {
                        mc.player.updateVelocity(velocity >= 0.3 ? 0.0f : 0.15f, new Vec3d(mc.player.sidewaysSpeed, 0, mc.player.forwardSpeed));
                        mc.player.jump();
                    }
                }
            }
        }
    }

    @EventHandler
    private void OnPlayerJump(PlayerJumpEvent event) {
        if (Mode.getValue() == Modes.Strafe) event.cancel();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (UseTimer.getValue()) timer.SetOverrideSpeed(1.0f);
    }
}
