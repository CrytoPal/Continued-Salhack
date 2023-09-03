package me.ionar.salhack.util.entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.AmbientEntity;
import net.minecraft.entity.mob.EndermanEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

public class EntityUtil
{

    public static ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<>();
        for (Entity entity : Wrapper.GetMC().world.getEntities()) {
            entities.add(entity);
        }
        return entities;
    }

    public static boolean isPassive(Entity e)
    {
        if (e instanceof WolfEntity && ((WolfEntity) e).isUniversallyAngry(Wrapper.GetMC().world))
            return false;
        if (e instanceof AnimalEntity || e instanceof TameableEntity
                || e instanceof AmbientEntity || e instanceof SquidEntity)
            return true;
        if (e instanceof IronGolemEntity && ((IronGolemEntity) e).getTarget() == null)
            return true;
        return false;
    }

    public static boolean isLiving(Entity e)
    {
        return e instanceof LivingEntity;
    }

    public static boolean isFakeLocalPlayer(Entity entity)
    {
        return entity != null && entity.getId() == -100 && Wrapper.GetMC().player != entity;
    }

    /**
     * If the mob by default wont attack the player, but will if the player attacks
     * it
     */
    public static boolean isNeutralMob(Entity entity)
    {
        return entity instanceof ZombifiedPiglinEntity || entity instanceof WolfEntity || entity instanceof EndermanEntity;
    }

    /**
     * If the mob is hostile
     */
    public static boolean isHostileMob(Entity entity)
    {
        return (entity instanceof HostileEntity);
    }

    public static boolean isInWater(Entity entity)
    {
        if (entity == null)
            return false;

        double y = entity.getY() + 0.01;

        for (int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); x++)
            for (int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); z++)
            {
                BlockPos pos = new BlockPos(x, (int) y, z);

                if (Wrapper.GetMC().world.getBlockState(pos).getFluidState() != null)
                    return true;
            }

        return false;
    }

    public static boolean isDrivenByPlayer(Entity entityIn)
    {
        return Wrapper.GetMC().player != null && entityIn != null
                && entityIn.equals(Wrapper.GetMC().player.getVehicle());
    }

    public static boolean isAboveWater(Entity entity)
    {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(Entity entity, boolean packet)
    {
        if (entity == null)
            return false;

        double y = entity.getY() - (packet ? 0.03 : (EntityUtil.isPlayer(entity) ? 0.2 : 0.5)); // increasing this seems
        // to flag more in NCP but
        // needs to be increased
        // so the player lands on
        // solid water

        for (int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); x++)
            for (int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); z++)
            {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);

                if (Wrapper.GetMC().world.getBlockState(pos).getFluidState() != null)
                    return true;
            }

        return false;
    }

    public static double[] calculateLookAt(double px, double py, double pz, PlayerEntity me)
    {
        double dirx = me.getX() - px;
        double diry = me.getY() - py;
        double dirz = me.getZ() - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        // to degree
        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]
                { yaw, pitch };
    }

    public static boolean isPlayer(Entity entity)
    {
        return entity instanceof PlayerEntity;
    }

    public static double getRelativeX(float yaw)
    {
        return (double) (MathHelper.sin(-yaw * 0.017453292F));
    }

    public static double getRelativeZ(float yaw)
    {
        return (double) (MathHelper.cos(yaw * 0.017453292F));
    }

    public static int GetPlayerMS(PlayerEntity p_Player)
    {
        if (p_Player.getUuid() == null) return 0;

        return Objects.requireNonNull(Wrapper.GetMC().getNetworkHandler().getPlayerListEntry(p_Player.getUuid()).getLatency());
    }
}