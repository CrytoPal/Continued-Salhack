package me.ionar.salhack.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.DamageUtil;
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

import static me.ionar.salhack.main.Wrapper.mc;

public class CrystalUtils {
    public static boolean canPlaceCrystal(final BlockPos pos) {
        final Block block = mc.world.getBlockState(pos).getBlock();
        if (block == Blocks.OBSIDIAN || block == Blocks.BEDROCK) {
            final Block floor = mc.world.getBlockState(pos.up()).getBlock();
            if (floor == Blocks.AIR) return mc.world.getOtherEntities(null, new Box(pos.up())).isEmpty();
        }
        return false;
    }

    public static BlockPos getPlayerPosFloored(final PlayerEntity player) {
        return BlockPos.ofFloored(player.getPos());
    }

    public static List<BlockPos> findCrystalBlocks(final PlayerEntity player, float range) {
        return new ArrayList<>(BlockInteractionHelper.getSphere(getPlayerPosFloored(player), range, (int) range, false, true, 0).stream().filter(CrystalUtils::canPlaceCrystal).toList());
    }

    public static boolean checkBase(BlockPos bp){
        return mc.world.getBlockState(bp).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(bp).getBlock() == Blocks.BEDROCK;
    }

    public static float calculateDamage(final World world, double posX, double posY, double posZ, PlayerEntity target) {
        if (world.getDifficulty() == Difficulty.PEACEFUL) return 0f;
        Explosion explosion = new Explosion(world, null, posX, posY, posZ, 6f, false, Explosion.DestructionType.DESTROY);
        double maxDist = 12;
        if (!new Box(MathHelper.floor(posX - maxDist - 1.0), MathHelper.floor(posY - maxDist - 1.0), MathHelper.floor(posZ - maxDist - 1.0), MathHelper.floor(posX + maxDist + 1.0), MathHelper.floor(posY + maxDist + 1.0), MathHelper.floor(posZ + maxDist + 1.0)).intersects(target.getBoundingBox())) return 0f;
        if (!target.isImmuneToExplosion() && !target.isInvulnerable()) {
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
                    if (world.getDifficulty() == Difficulty.EASY) toDamage = Math.min(toDamage / 2f + 1f, toDamage);
                    else if (world.getDifficulty() == Difficulty.HARD) toDamage = toDamage * 3f / 2f;
                    toDamage = DamageUtil.getDamageLeft(toDamage, target.getArmor(), (float) target.getAttributeInstance(EntityAttributes.GENERIC_ARMOR_TOUGHNESS).getValue());
                    if (target.hasStatusEffect(StatusEffects.RESISTANCE)) {
                        int resistance = 25 - (target.getStatusEffect(StatusEffects.RESISTANCE).getAmplifier() + 1) * 5;
                        float resistance_1 = toDamage * resistance;
                        toDamage = Math.max(resistance_1 / 25f, 0f);
                    }
                    if (toDamage <= 0f) toDamage = 0f;
                    else {
                        int protectionAmount = EnchantmentHelper.getProtectionAmount(target.getArmorItems(), explosion.getDamageSource());
                        if (protectionAmount > 0) toDamage = DamageUtil.getInflictedDamage(toDamage, protectionAmount);
                    }
                    return toDamage;
                }
            }
        }
        return 0f;
    }
}