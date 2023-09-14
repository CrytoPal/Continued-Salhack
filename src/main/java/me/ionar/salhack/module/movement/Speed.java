package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerJumpEvent;
import me.ionar.salhack.events.player.PlayerMoveEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.world.TimerModule;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class SpeedModule extends Module {
    public final Value<modes> mode = new Value<>("Mode", new String[]{"Mode"}, "The mode of speed to use", modes.Strafe);
    public final Value<Boolean> useTimer = new Value<>("UseTimer", new String[]{"UseTimer"}, "Uses timer to go faster", false);
    public final Value<Boolean> autoSprint = new Value<>("AutoSprint", new String[]{"AutoSprint"}, "Automatically sprints for you", false);
    public final Value<Boolean> speedInWater = new Value<>("SpeedInWater", new String[]{"SpeedInWater"}, "Speeds in water", false);
    public final Value<Boolean> autoJump = new Value<>("AutoJump", new String[]{"AutoJump"}, "Automatically jumps", true);
    public final Value<Boolean> strict = new Value<>("Strict", new String[]{"Strict"}, "Strict mode, use this for when hauses patch comes back for strafe", false);
    private TimerModule timer = null;
    public enum modes {
        Strafe,
        OnGround
    }

    public SpeedModule() {
        super("Speed", new String[]{ "Strafe" }, "Speed strafe", 0, 0xDB2468, ModuleType.MOVEMENT);
    }

    @Override
    public String getMetaData() {
        return String.valueOf(mode.getValue());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        timer = (TimerModule) SalHack.getModuleManager().getMod(TimerModule.class);
    }

    @EventHandler
    private void onPlayerTick(TickEvent event) {
        if (event.isPre()) return;
        if (mc.player == null || mc.player.isRiding()) return;
        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && !speedInWater.getValue()) return;
        if (useTimer.getValue()) timer.setOverrideSpeed(1.088f);
        if (mc.player.forwardSpeed != 0.0f || mc.player.sidewaysSpeed != 0.0f) {
            if (autoSprint.getValue()) mc.player.setSprinting(true);
            if (mc.player.isOnGround() && mode.getValue() == modes.Strafe) {
                if (autoJump.getValue()) mc.player.setVelocity(mc.player.getVelocity().x, 0.405f, mc.player.getVelocity().z);
                final float yaw = getRotationYawForCalc();
                mc.player.setVelocity(mc.player.getVelocity().x - MathHelper.sin(yaw) * 0.2f, mc.player.getVelocity().y, mc.player.getVelocity().z + MathHelper.cos(yaw) * 0.2f);
            } else if (mc.player.isOnGround() && mode.getValue() == modes.OnGround) {
                final float yaw = getRotationYawForCalc();
                mc.player.setVelocity(mc.player.getVelocity().x - MathHelper.sin(yaw) * 0.2f, mc.player.getVelocity().y, mc.player.getVelocity().z + MathHelper.cos(yaw) * 0.2f);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY()+0.4, mc.player.getZ(), false));
            }
        }
        if (mc.options.jumpKey.isPressed() && mc.player.isOnGround()) mc.player.setVelocity(mc.player.getVelocity().x, 0.405f, mc.player.getVelocity().z);
    }

    private float getRotationYawForCalc() {
        if (mc.player == null) return 0;
        float rotationYaw = mc.player.getYaw();
        if (mc.player.forwardSpeed < 0.0f) rotationYaw += 180.0f;
        float n = 1.0f;
        if (mc.player.forwardSpeed < 0.0f) n = -0.5f;
        else if (mc.player.forwardSpeed > 0.0f) n = 0.5f;
        if (mc.player.sidewaysSpeed > 0.0f) rotationYaw -= 90.0f * n;
        if (mc.player.sidewaysSpeed < 0.0f) rotationYaw += 90.0f * n;
        return rotationYaw * 0.017453292f;
    }

    @EventHandler
    private void onPlayerJump(PlayerJumpEvent event) {
        if (mode.getValue() == modes.Strafe) event.cancel();
    }

    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        if (!event.isPre() || mode.getValue() == modes.OnGround || mc.player == null || mc.player.isOnGround()) return;
        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && !speedInWater.getValue()) return;
        if (mc.player.getAbilities() != null && (mc.player.getAbilities().flying || mc.player.isFallFlying())) return;
        // movement data variables
        float playerSpeed = 0.2873f;
        float moveForward = mc.player.input.movementForward;
        float moveStrafe = mc.player.input.movementSideways;
        float rotationYaw = mc.player.getYaw();
        // check for speed potion
        if (mc.player.hasStatusEffect(StatusEffects.SPEED)) {
            StatusEffectInstance speed = mc.player.getStatusEffect(StatusEffects.SPEED);
            if (speed != null) {
                final int amplifier = speed.getAmplifier();
                playerSpeed *= (1.0f + 0.2f * (amplifier + 1));
            }
        }
        if (!strict.getValue()) playerSpeed *= 1.0064f;
        // not movement input, stop all motion
        if (moveForward == 0.0f && moveStrafe == 0.0f) {
            event.setX(0);
            event.setZ(0);
        } else {
            if (moveForward != 0.0f) {
                if (moveStrafe > 0.0f) rotationYaw += ((moveForward > 0.0f) ? -45 : 45);
                else if (moveStrafe < 0.0f) rotationYaw += ((moveForward > 0.0f) ? 45 : -45);
                moveStrafe = 0.0f;
                if (moveForward > 0.0f) moveForward = 1.0f;
                else if (moveForward < 0.0f) moveForward = -1.0f;
            }
            double cos = Math.cos(Math.toRadians((rotationYaw + 90.0f)));
            double sin = Math.sin(Math.toRadians((rotationYaw + 90.0f)));
            event.setX((moveForward * playerSpeed) * cos + (moveStrafe * playerSpeed) * sin);
            event.setZ((moveForward * playerSpeed) * sin - (moveStrafe * playerSpeed) * cos);
        }
        event.cancel();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (useTimer.getValue()) timer.setOverrideSpeed(1.0f);
    }
}
