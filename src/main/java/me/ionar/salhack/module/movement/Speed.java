package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerJumpEvent;
import me.ionar.salhack.events.player.PlayerMoveEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.MathHelper;

public class Speed extends Module {
    public final Value<Modes> Mode = new Value<>("Mode", new String[]{"Mode"}, "The mode of speed to use", Modes.Strafe);
    public final Value<Boolean> UseTimer = new Value<>("UseTimer", new String[]{"UseTimer"}, "Uses timer to go faster", false);
    public final Value<Boolean> AutoSprint = new Value<>("AutoSprint", new String[]{"AutoSprint"}, "Automatically sprints for you", false);
    public final Value<Boolean> SpeedInWater = new Value<>("SpeedInWater", new String[]{"SpeedInWater"}, "Speeds in water", false);
    public final Value<Boolean> AutoJump = new Value<>("AutoJump", new String[]{"AutoJump"}, "Automatically jumps", true);
    public final Value<Boolean> Strict = new Value<>("Strict", new String[]{"Strict"}, "Strict mode, use this for when hauses patch comes back for strafe", false);

    public enum Modes {
        Strafe,
        OnGround
    }

    public Speed() {
        super("Speed", new String[]{ "Strafe" }, "Speed strafe", 0, 0xDB2468, ModuleType.MOVEMENT);
    }

    private me.ionar.salhack.module.world.Timer Timer = null;

    @Override
    public String getMetaData() {
        return String.valueOf(Mode.getValue());
    }

    @Override
    public void onEnable() {
        super.onEnable();
        Timer = (me.ionar.salhack.module.world.Timer) ModuleManager.Get().GetMod(me.ionar.salhack.module.world.Timer.class);
    }

    @EventHandler
    private void OnPlayerTick(TickEvent event) {
        if (event.isPre()) return;

        if (mc.player == null || mc.player.isRiding()) return;

        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && !SpeedInWater.getValue()) return;

        if (UseTimer.getValue()) Timer.SetOverrideSpeed(1.088f);

        if (mc.player.forwardSpeed != 0.0f || mc.player.sidewaysSpeed != 0.0f) {
            if (AutoSprint.getValue()) mc.player.setSprinting(true);

            if (mc.player.isOnGround() && Mode.getValue() == Modes.Strafe) {
                if (AutoJump.getValue()) mc.player.setVelocity(mc.player.getVelocity().x, 0.405f, mc.player.getVelocity().z);

                final float yaw = GetRotationYawForCalc();
                mc.player.setVelocity(mc.player.getVelocity().x - MathHelper.sin(yaw) * 0.2f, mc.player.getVelocity().y, mc.player.getVelocity().z + MathHelper.cos(yaw) * 0.2f);
            } else if (mc.player.isOnGround() && Mode.getValue() == Modes.OnGround) {
                final float yaw = GetRotationYawForCalc();
                mc.player.setVelocity(mc.player.getVelocity().x - MathHelper.sin(yaw) * 0.2f, mc.player.getVelocity().y, mc.player.getVelocity().z + MathHelper.cos(yaw) * 0.2f);
                mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(mc.player.getX(), mc.player.getY()+0.4, mc.player.getZ(), false));
                /*
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, true));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY+0.4, mc.player.posZ, true));*/
            }
        }

        if (mc.options.jumpKey.isPressed() && mc.player.isOnGround()) mc.player.setVelocity(mc.player.getVelocity().x, 0.405f, mc.player.getVelocity().z);
    }

    private float GetRotationYawForCalc() {
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
    private void OnPlayerJump(PlayerJumpEvent event) {
        if (Mode.getValue() == Modes.Strafe) event.cancel();
    }

    @EventHandler
    private void OnPlayerMove(PlayerMoveEvent event) {
        if (!event.isPre() || Mode.getValue() == Modes.OnGround || mc.player == null || mc.player.isOnGround()) return;
        if ((mc.player.isTouchingWater() || mc.player.isInLava()) && !SpeedInWater.getValue()) return;
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

        if (!Strict.getValue()) playerSpeed *= 1.0064f;

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
        if (UseTimer.getValue()) Timer.SetOverrideSpeed(1.0f);
    }
}
