package me.ionar.salhack.util;

import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.util.entity.EntityUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;

public class SalUtil {

    static MinecraftClient mc = MinecraftClient.getInstance();

    public static PlayerEntity findClosestTarget()
    {

        if (mc.world.getPlayers().isEmpty())
            return null;

        PlayerEntity closestTarget = null;

        for (final PlayerEntity target : mc.world.getPlayers())
        {
            if (target == mc.player)
                continue;

            if (FriendManager.Get().IsFriend(target))
                continue;

            if (!EntityUtil.isLiving((Entity)target))
                continue;

            if (target.getHealth() <= 0.0f)
                continue;

            if (closestTarget != null)
                if (mc.player.squaredDistanceTo(target) > mc.player.squaredDistanceTo(closestTarget))
                    continue;

            closestTarget = target;
        }

        return closestTarget;
    }

    public Vec3d GetCenter(double posX, double posY, double posZ)
    {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D ;

        return new Vec3d(x, y, z);
    }
}
