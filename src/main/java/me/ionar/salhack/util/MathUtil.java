package me.ionar.salhack.util;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class MathUtil {
    public static Vec3d interpolateEntity(Entity entity) {
        return entity.getPos().subtract(getInterpolationOffset(entity));
    }

    public static Vec3d getInterpolationOffset(Entity entity) {
        if (MinecraftClient.getInstance().isPaused()) return Vec3d.ZERO;
        double tickDelta = MinecraftClient.getInstance().getTickDelta();
        return new Vec3d(entity.getX() - MathHelper.lerp(tickDelta, entity.lastRenderX, entity.getX()), entity.getY() - MathHelper.lerp(tickDelta, entity.lastRenderY, entity.getY()), entity.getZ() - MathHelper.lerp(tickDelta, entity.lastRenderZ, entity.getZ()));
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        final double difX = to.x - from.x;
        final double difY = (to.y - from.y) * -1.0F;
        final double difZ = to.z - from.z;
        final double dist = Math.sqrt(difX * difX + difZ * difZ);
        return new float[]{ (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0f), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist))) };
    }

    public static double[] directionSpeedNoForward(double speed) {
        final MinecraftClient mc = Wrapper.GetMC();
        if (mc.player == null) return new double[0];
        float forward = 1f;

        if (mc.options.leftKey.isPressed() || mc.options.rightKey.isPressed() || mc.options.backKey.isPressed() || mc.options.forwardKey.isPressed()) forward = mc.player.input.movementForward;

        float side = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta();

        if (forward != 0) {
            if (side > 0) yaw += (forward > 0 ? -45 : 45);
            else if (side < 0) yaw += (forward > 0 ? 45 : -45);
            side = 0;

            // forward = clamp(forward, 0, 1);
            if (forward > 0) forward = 1;
            else if (forward < 0) forward = -1;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{ posX, posZ };
    }

    public static double[] directionSpeed(double speed) {
        final MinecraftClient mc = Wrapper.GetMC();
        if (mc.player == null) return new double[0];
        float forward = mc.player.input.movementForward;
        float side = mc.player.input.movementSideways;
        float yaw = mc.player.prevYaw + (mc.player.getYaw() - mc.player.prevYaw) * mc.getTickDelta();

        if (forward != 0) {
            if (side > 0) yaw += (forward > 0 ? -45 : 45);
            else if (side < 0) yaw += (forward > 0 ? 45 : -45);

            side = 0;

            // forward = clamp(forward, 0, 1);
            if (forward > 0) forward = 1;
            else if (forward < 0) forward = -1;
        }

        final double sin = Math.sin(Math.toRadians(yaw + 90));
        final double cos = Math.cos(Math.toRadians(yaw + 90));
        final double posX = (forward * speed * cos + side * speed * sin);
        final double posZ = (forward * speed * sin - side * speed * cos);
        return new double[]{ posX, posZ };
    }

    public static double degToRad(double deg) {
        return deg * (float) (Math.PI / 180.0f);
    }

    public Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;
        return new Vec3d(x, y, z);
    }
}
