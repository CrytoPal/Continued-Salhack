package me.ionar.salhack.util;

import com.google.gson.internal.NonNullElementWrapperList;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static me.ionar.salhack.main.Wrapper.mc;

public class CrystalUtils {


    public static boolean canPlaceCrystal(final BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();

        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(pos.up()).getBlock();
            // deprecated
            //final Block ceil = mc.world.getBlockState(pos.add(0, 2, 0)).getBlock();

            if (floor == Blocks.AIR /*&& ceil == Blocks.AIR*/) {
                if (mc.world.getOtherEntities(null, new Box(pos.up())).isEmpty()) {
                    return true;
                }
            }
        }

        return false;
    }

    /// Returns a BlockPos object of player's position floored.
    public static BlockPos GetPlayerPosFloored(final PlayerEntity p_Player) {
        return BlockPos.ofFloored(p_Player.getPos());
    }

    public static List<BlockPos> findCrystalBlocks(final PlayerEntity p_Player, float p_Range) {
        List<BlockPos> positions = new ArrayList<>(getSphere(GetPlayerPosFloored(p_Player), p_Range, (int) p_Range, false, true, 0)
                .stream().filter(CrystalUtils::canPlaceCrystal).toList());
        return positions;
    }

    public static List<BlockPos> getSphere(BlockPos loc, float r, int h, boolean hollow, boolean sphere, int plus_y)
    {
        List<BlockPos> circleblocks = new ArrayList<>();
        int cx = loc.getX();
        int cy = loc.getY();
        int cz = loc.getZ();
        for (int x = cx - (int) r; x <= cx + r; x++)
        {
            for (int z = cz - (int) r; z <= cz + r; z++)
            {
                for (int y = (sphere ? cy - (int) r : cy); y < (sphere ? cy + r : cy + h); y++)
                {
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < r * r && !(hollow && dist < (r - 1) * (r - 1)))
                    {
                        circleblocks.add(new BlockPos(x, y + plus_y, z));
                    }
                }
            }
        }
        return circleblocks;
    }

    public static boolean checkBase(BlockPos bp){
        return mc.world.getBlockState(bp).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(bp).getBlock() == Blocks.BEDROCK;
    }

    public static float calculateDamage(final World p_World, double posX, double posY, double posZ, PlayerEntity target,
                                        int p_InterlopedAmount) {

        // TODO p_InterlopedAmount = predict

        if (p_World.getDifficulty() == Difficulty.PEACEFUL) return 0f;

        Explosion explosion = new Explosion(p_World, null, posX, posY, posZ, 6f, false, Explosion.DestructionType.DESTROY);

        double maxDist = 12;
        if (!new Box(MathHelper.floor(posX - maxDist - 1.0), MathHelper.floor(posY - maxDist - 1.0), MathHelper.floor(posZ - maxDist - 1.0), MathHelper.floor(posX + maxDist + 1.0), MathHelper.floor(posY + maxDist + 1.0), MathHelper.floor(posZ + maxDist + 1.0)).intersects(target.getBoundingBox())) {
            return 0f;
        }

        if (!target.isImmuneToExplosion(explosion) && !target.isInvulnerable()) {
            double distExposure = MathHelper.sqrt((float) target.squaredDistanceTo(new Vec3d(posX, posY, posZ))) / maxDist;
            if (distExposure <= 1.0) {
                double xDiff = target.getX() - posX;
                double yDiff = target.getY() - posY;
                double zDiff = target.getX() - posZ;
                double diff = MathHelper.sqrt((float) (xDiff * xDiff + yDiff * yDiff + zDiff * zDiff));
                if (diff != 0.0) {
                    double exposure = Explosion.getExposure(new Vec3d(posX, posY, posZ), target);
                    double finalExposure = (1.0 - distExposure) * exposure;

                    float toDamage = (float) Math.floor((finalExposure * finalExposure + finalExposure) / 2.0 * 7.0 * maxDist + 1.0);

                    if (p_World.getDifficulty() == Difficulty.EASY) {
                        toDamage = Math.min(toDamage / 2f + 1f, toDamage);
                    } else if (p_World.getDifficulty() == Difficulty.HARD) {
                        toDamage = toDamage * 3f / 2f;
                    }

                    toDamage = DamageUtil.getDamageLeft(toDamage, target.getArmor(), (float) target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());

                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                        float resistance_1 = toDamage * resistance;
                        toDamage = Math.max(resistance_1 / 25f, 0f);
                    }

                    if (toDamage <= 0f) {
                        toDamage = 0f;
                    } else {
                        int protAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(),  mc.world.getDamageSources().explosion(explosion));
                        if (protAmount > 0) {
                            toDamage = DamageUtil.getInflictedDamage(toDamage, protAmount);
                        }
                    }
                    return toDamage;
                }
            }
        }
        return 0f;
    }


}
