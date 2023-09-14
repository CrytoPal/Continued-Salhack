package me.ionar.salhack.util.entity;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
public class EntityUtil {

    public static ArrayList<Entity> getEntities() {
        ArrayList<Entity> entities = new ArrayList<>();
        if (Wrapper.GetMC().world != null) {
            for (Entity entity : Wrapper.GetMC().world.getEntities()) {
                entities.add(entity);
            }
        }
        return entities;
    }

    public static boolean isPassive(Entity entity) {
        if (entity instanceof WolfEntity && ((WolfEntity) entity).isUniversallyAngry(Wrapper.GetMC().world)) return false;
        if (entity instanceof AnimalEntity || entity instanceof AmbientEntity || entity instanceof SquidEntity) return true;
        return entity instanceof IronGolemEntity && ((IronGolemEntity) entity).getTarget() == null;
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof LivingEntity;
    }

    public static boolean isFakePlayer(Entity entity) {
        return entity != null && entity.getId() == -100 && Wrapper.GetMC().player != entity;
    }

    public static BlockPos GetPositionVectorBlockPos(Entity entity, @Nullable BlockPos toAdd)
    {
        final Vec3d v = entity.getPos();

        if (toAdd == null)
            return BlockPos.ofFloored(v.x, v.y, v.z);

        return BlockPos.ofFloored(v.x, v.y, v.z).add(toAdd);
    }

    /**
     * If the mob by default won't attack the player, but will if the player attacks
     * it
     */
    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof Angerable;
    }

    /**
     * If the mob is hostile
     */
    public static boolean isHostileMob(Entity entity) {
        return entity instanceof HostileEntity;
    }

    public static boolean isInWater(Entity entity) {
        if (entity == null || Wrapper.GetMC().world == null) return false;
        double y = entity.getY() + 0.01;
        for (int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); x++)
            for (int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); z++) {
                BlockPos pos = new BlockPos(x, (int) y, z);
                if (Wrapper.GetMC().world.getBlockState(pos).getFluidState() != null) return true;
            }
        return false;
    }

    public static boolean isDrivenByPlayer(Entity entity) {
        return Wrapper.GetMC().player != null && entity != null && entity.equals(Wrapper.GetMC().player.getVehicle());
    }

    public static boolean isAboveWater(Entity entity) {
        return isAboveWater(entity, false);
    }

    public static boolean isAboveWater(Entity entity, boolean packet) {
        if (entity == null || Wrapper.GetMC().world == null) return false;
        double y = entity.getY() - (packet ? 0.03 : (EntityUtil.isPlayer(entity) ? 0.2 : 0.5));
        // increasing this seems
        // to flag more in NCP but
        // needs to be increased
        // so the player lands on
        // solid water
        for (int x = MathHelper.floor(entity.getX()); x < MathHelper.ceil(entity.getX()); x++)
            for (int z = MathHelper.floor(entity.getZ()); z < MathHelper.ceil(entity.getZ()); z++) {
                BlockPos pos = new BlockPos(x, MathHelper.floor(y), z);
                if (Wrapper.GetMC().world.getBlockState(pos).getFluidState() != null) return true;
            }
        return false;
    }

    public static double[] calculateLookAt(double x, double y, double z, PlayerEntity player) {
        double dirx = player.getX() - x;
        double diry = player.getY() - y;
        double dirz = player.getZ() - z;
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
        return new double[]{ yaw, pitch };
    }

    public static boolean isPlayer(Entity entity) {
        return entity instanceof PlayerEntity;
    }

    public static double getRelativeX(float yaw) {
        return MathHelper.sin(-yaw * 0.017453292F);
    }

    public static double getRelativeZ(float yaw) {
        return MathHelper.cos(yaw * 0.017453292F);
    }

    public static int getPlayerMS(PlayerEntity player) {
        if (player.getUuid() != null) {
            ClientPlayNetworkHandler handler = Wrapper.GetMC().getNetworkHandler();
            if (handler != null) {
                PlayerListEntry entry = handler.getPlayerListEntry(player.getUuid());
                if (entry != null) return entry.getLatency();
            }
        }
        return 0;
    }
}
