package me.ionar.salhack.util;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;

import java.util.Arrays;
import java.util.List;

import static java.lang.Double.isNaN;

@SuppressWarnings("BooleanMethodIsAlwaysInverted")
public class BlockInteractionHelper {
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.CHERRY_TRAPDOOR,Blocks.BIRCH_TRAPDOOR, Blocks.ACACIA_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.CRIMSON_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR, Blocks.MANGROVE_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR , Blocks.ENCHANTING_TABLE);
    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    private static final MinecraftClient mc = Wrapper.GetMC();
    public enum ValidResult {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }
    public enum PlaceResult {
        NotReplaceable,
        Neighbors,
        CantPlace,
        Placed,
    }

    public static void placeBlockScaffold(BlockPos pos) {
        if (mc.player == null) return;
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        for (Direction side : Direction.values()) {
            BlockPos neighbor = pos.offset(side);
            Direction side2 = side.getOpposite();
            if (!canBeClicked(neighbor)) continue;
            Vec3d hitVec = new Vec3d(neighbor.getX() + 0.5 + side2.getVector().getX() * 0.5, neighbor.getY() + 0.5 + side2.getVector().getY() * 0.5, neighbor.getZ() + 0.5 + side2.getVector().getZ() * 0.5);
            if (eyesPos.squaredDistanceTo(hitVec) > 18.0625) continue;
            faceVectorPacketInstant(hitVec);
            processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingHand(Hand.MAIN_HAND);
            return;
        }
    }

    public static float[] getLegitRotations(Vec3d vec) {
        if (mc.player == null) return new float[]{0};
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));
        return new float[]{ mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw()), mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch()) };
    }

    private static Vec3d getEyesPos() {
        if (mc.player == null) return new Vec3d(-1, -1, -1);
        return new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
    }

    public static void faceVectorPacketInstant(Vec3d vec) {
        if (mc.player == null) return;
        float[] rotations = getLegitRotations(vec);
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rotations[0], rotations[1], mc.player.isOnGround()));
    }

    private static void processRightClickBlock(BlockPos pos, Direction side, Vec3d hitVec) {
        if (mc.interactionManager == null) return;
        mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hitVec, side, pos, false));
    }

    public static boolean canBeClicked(BlockPos pos) {
        if (mc.world == null) return false;
        return mc.world.getBlockState(pos).getCollisionShape(mc.world, pos) != VoxelShapes.empty();
    }

    public static boolean checkForNeighbours(BlockPos blockPos) {
        if (!hasNeighbour(blockPos)) {
            for (Direction side : Direction.values()) {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour)) return true;
                /*if (side == Direction.UP && mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER) {
                    if (mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled()) return true;
                }*/
            } return false;
        } return true;
    }

    public static boolean hasNeighbour(BlockPos blockPos) {
        if (mc.world == null) return true;
        for (Direction side : Direction.values()) {
            BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).isReplaceable()) return true;
        } return false;
    }

    public static ValidResult valid(BlockPos pos) {
        if (mc.world == null) return ValidResult.NoEntityCollision;
        if (!mc.world.getNonSpectatingEntities(Entity.class, new Box(pos)).isEmpty()) return ValidResult.NoEntityCollision;
        /*if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.WATER)
            if (ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled()) return ValidResult.Ok;
        */
        if (!BlockInteractionHelper.checkForNeighbours(pos)) return ValidResult.NoNeighbors;
        BlockState state = mc.world.getBlockState(pos);
        if (state.getBlock() == Blocks.AIR) {
            final BlockPos[] blockPositions = { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down() };
            for (BlockPos blockPos : blockPositions) {
                BlockState blockState = mc.world.getBlockState(blockPos);
                if (blockState.getBlock() == Blocks.AIR) continue;
                for (final Direction side : Direction.values()) {
                    final BlockPos neighbor = pos.offset(side);
                    boolean isWater = mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;
                    if (mc.world.getBlockState(neighbor).getCollisionShape(mc.world, neighbor) != VoxelShapes.empty() /*|| (l_IsWater && ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled())*/) return ValidResult.Ok;
                }
            }
            return ValidResult.NoNeighbors;
        }
        return ValidResult.AlreadyBlockThere;
        /*
         * final BlockPos[] l_Blocks = { pos.north(), pos.south(), pos.east(), pos.west(), pos.up() };
         *
         * for (BlockPos l_Pos : l_Blocks) { IBlockState state = mc.world.getBlockState(l_Pos);
         *
         * if (state.getBlock() == Blocks.AIR) continue;
         *
         * return ValidResult.Ok; }
         *
         * return ValidResult.NoNeighbors;
         */
    }

    public static void place(BlockPos pos, float distance, boolean rotate, boolean useSlabRule) {
        place(pos, distance, rotate, useSlabRule, false);
    }

    public static void place(BlockPos pos, float distance, boolean rotate, boolean useSlabRule, boolean packetSwing) {
        if (mc.world == null || mc.player == null || mc.interactionManager == null) return;
        BlockState blockState = mc.world.getBlockState(pos);
        boolean replaceable = blockState.isReplaceable();
        boolean isSlabAtBlock = blockState.getBlock() instanceof SlabBlock;
        if (!replaceable && !isSlabAtBlock) return;
        if (!BlockInteractionHelper.checkForNeighbours(pos)) return;
        if (!isSlabAtBlock && valid(pos)!= ValidResult.Ok) return;
        if (useSlabRule && isSlabAtBlock && !blockState.isFullCube(mc.world, pos)) return;
        final Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        for (final Direction side : Direction.values()) {
            final BlockPos neighbor = pos.offset(side);
            final Direction side2 = side.getOpposite();
            boolean isWater = mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;
            if (mc.world.getBlockState(neighbor).getCollisionShape(mc.world, neighbor) != VoxelShapes.empty() /*|| (l_IsWater && ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled())*/) {
                final Vec3d hitVec = new Vec3d(neighbor.getX() + 0.5 + side2.getVector().getX() * 0.5, neighbor.getY() + 0.5 + side2.getVector().getY() * 0.5, neighbor.getZ() + 0.5 + side2.getVector().getZ() * 0.5);
                if (eyesPos.distanceTo(hitVec) <= distance) {
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                    final boolean activated = BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos);
                    if (activated) mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                    if (rotate) BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    ActionResult actionResult = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hitVec, side2, neighbor, false));
                    if (actionResult != ActionResult.FAIL) {
                        if (packetSwing) mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                        else mc.player.swingHand(Hand.MAIN_HAND);
                        if (activated) mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                        return;
                    }
                }
            }
        }
    }

    public static boolean isLiquidOrAir(BlockPos pos) {
        if (mc.world == null) return false;
        BlockState state = mc.world.getBlockState(pos);

        return state.getBlock() instanceof FluidBlock || state.getBlock() instanceof AirBlock;
    }

    public static float[] getFacingRotations(int x, int y, int z, Direction facing) {
        return getFacingRotations(x, y, z, facing, 1);
    }

    public static float[] getFacingRotations(int x, int y, int z, Direction facing, double width) {
        return getRotationsForPosition(x + 0.5 + facing.getVector().getX() * width / 2.0, y + 0.5 + facing.getVector().getY() * width / 2.0, z + 0.5 + facing.getVector().getZ() * width / 2.0);
    }

    public static float[] getRotationsForPosition(double x, double y, double z) {
        if (mc.player == null) return new float[]{0,0};
        return getRotationsForPosition(x, y, z, mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
    }

    public static float[] getRotationsForPosition(double x, double y, double z, double sourceX, double sourceY, double sourceZ) {
        double deltaX = x - sourceX;
        double deltaY = y - sourceY;
        double deltaZ = z - sourceZ;
        double yawToEntity;
        double degrees = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if (deltaZ < 0 && deltaX < 0) yawToEntity = 90D + degrees;
        else if (deltaZ < 0 && deltaX > 0) yawToEntity = -90D + degrees;
        else yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        yawToEntity = wrapAngleTo180((float) yawToEntity);
        pitchToEntity = wrapAngleTo180((float) pitchToEntity);
        yawToEntity = isNaN(yawToEntity) ? 0 : yawToEntity;
        pitchToEntity = isNaN(pitchToEntity) ? 0 : pitchToEntity;
        return new float[] { (float) yawToEntity, (float) pitchToEntity };
    }

    public static float wrapAngleTo180(float angle) {
        angle %= 360.0F;
        while (angle >= 180.0F) angle -= 360.0F;
        while (angle < -180.0F) angle += 360.0F;
        return angle;
    }
}
