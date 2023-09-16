package me.ionar.salhack.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SlabBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.HandSwingC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.*;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Double.isNaN;

public class BlockInteractionHelper {
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER,
            Blocks.DROPPER, Blocks.DISPENSER, Blocks.CHERRY_TRAPDOOR,Blocks.BIRCH_TRAPDOOR, Blocks.ACACIA_TRAPDOOR, Blocks.BAMBOO_TRAPDOOR, Blocks.CRIMSON_TRAPDOOR, Blocks.JUNGLE_TRAPDOOR,
            Blocks.MANGROVE_TRAPDOOR, Blocks.OAK_TRAPDOOR, Blocks.DARK_OAK_TRAPDOOR , Blocks.ENCHANTING_TABLE);

    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX,
            Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX,
            Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);

    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static void placeBlockScaffold(BlockPos pos)
    {
        Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        for (Direction side : Direction.values())
        {
            BlockPos neighbor = pos.offset(side);
            Direction side2 = side.getOpposite();

            // check if neighbor can be right clicked
            if (!canBeClicked(neighbor))
            {
                continue;
            }


            Vec3d hitVec = new Vec3d(neighbor.getX() + 0.5 + side2.getVector().getX() * 0.5, neighbor.getY() + 0.5 + side2.getVector().getY() * 0.5, neighbor.getZ() + 0.5 + side2.getVector().getZ() * 0.5);

            // check if hitVec is within range (4.25 blocks)
            if (eyesPos.squaredDistanceTo(hitVec) > 18.0625)
            {
                continue;
            }

            // place block
            faceVectorPacketInstant(hitVec);
            processRightClickBlock(neighbor, side2, hitVec);
            mc.player.swingHand(Hand.MAIN_HAND);

         //   mc.rightClickDelayTimer = 4;

            return;
        }

    }

    public static float[] getLegitRotations(Vec3d vec)
    {
        Vec3d eyesPos = getEyesPos();

        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new float[]
                { mc.player.getYaw() + MathHelper.wrapDegrees(yaw - mc.player.getYaw()),
                        mc.player.getPitch() + MathHelper.wrapDegrees(pitch - mc.player.getPitch()) };
    }

    private static Vec3d getEyesPos()
    {
        return new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
    }

    public static void faceVectorPacketInstant(Vec3d vec)
    {
        float[] rotations = getLegitRotations(vec);
        mc.player.networkHandler.sendPacket(new PlayerMoveC2SPacket.LookAndOnGround(rotations[0], rotations[1], mc.player.isOnGround()));
    }

    private static void processRightClickBlock(BlockPos pos, Direction side, Vec3d hitVec)
    {
        getPlayerController().interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hitVec, side, pos, false));
    }

    public static boolean canBeClicked(BlockPos pos)
    {
        return getState(pos).getCollisionShape(mc.world, pos) != VoxelShapes.empty();
    }

    private static Block getBlock(BlockPos pos)
    {
        return getState(pos).getBlock();
    }

    private static ClientPlayerInteractionManager getPlayerController()
    {
        return mc.interactionManager;
    }

    private static BlockState getState(BlockPos pos)
    {
        return mc.world.getBlockState(pos);
    }

    public static boolean checkForNeighbours(BlockPos blockPos)
    {
        // check if we don't have a block adjacent to blockpos
        if (!hasNeighbour(blockPos))
        {
            // find air adjacent to blockpos that does have a block adjacent to it, let's fill this first as to form a bridge between the player and the original blockpos. necessary if the player is
            // going diagonal.
            for (Direction side : Direction.values())
            {
                BlockPos neighbour = blockPos.offset(side);
                if (hasNeighbour(neighbour))
                {
                    return true;
                }

                if (side == Direction.UP && mc.world.getBlockState(blockPos).getBlock() == Blocks.WATER)
                {
                    /*
                    if (mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR && ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled())
                        return true;

                     */
                }
            }
            return false;
        }
        return true;
    }

    public static boolean hasNeighbour(BlockPos blockPos)
    {
        for (Direction side : Direction.values())
        {
            BlockPos neighbour = blockPos.offset(side);
            if (!mc.world.getBlockState(neighbour).isReplaceable())
            {
                return true;
            }
        }
        return false;
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

    public enum ValidResult
    {
        NoEntityCollision,
        AlreadyBlockThere,
        NoNeighbors,
        Ok,
    }

    public static ValidResult valid(BlockPos pos)
    {
        // There are no entities to block placement,
        if (!mc.world.getNonSpectatingEntities(Entity.class, new Box(pos)).isEmpty())
            return ValidResult.NoEntityCollision;

        /*
        if (mc.world.getBlockState(pos.down()).getBlock() == Blocks.WATER)

            if (ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled())
                return ValidResult.Ok;

             */

        if (!BlockInteractionHelper.checkForNeighbours(pos))
            return ValidResult.NoNeighbors;

        BlockState l_State = mc.world.getBlockState(pos);

        if (l_State.getBlock() == Blocks.AIR)
        {
            final BlockPos[] l_Blocks =
                    { pos.north(), pos.south(), pos.east(), pos.west(), pos.up(), pos.down() };

            for (BlockPos l_Pos : l_Blocks)
            {
                BlockState l_State2 = mc.world.getBlockState(l_Pos);

                if (l_State2.getBlock() == Blocks.AIR)
                    continue;

                for (final Direction side : Direction.values())
                {
                    final BlockPos neighbor = pos.offset(side);

                    boolean l_IsWater = mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;



                    if (mc.world.getBlockState(neighbor).getCollisionShape(mc.world, neighbor) != VoxelShapes.empty()
                        /*|| (l_IsWater && ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled())*/)
                    {
                        return ValidResult.Ok;
                    }
                }
            }

            return ValidResult.NoNeighbors;
        }

        return ValidResult.AlreadyBlockThere;
        /*
         * final BlockPos[] l_Blocks = { pos.north(), pos.south(), pos.east(), pos.west(), pos.up() };
         *
         * for (BlockPos l_Pos : l_Blocks) { IBlockState l_State = mc.world.getBlockState(l_Pos);
         *
         * if (l_State.getBlock() == Blocks.AIR) continue;
         *
         * return ValidResult.Ok; }
         *
         * return ValidResult.NoNeighbors;
         */
    }

    public enum PlaceResult
    {
        NotReplaceable,
        Neighbors,
        CantPlace,
        Placed,
    }

    public static PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule)
    {
        return place(pos, p_Distance, p_Rotate, p_UseSlabRule, false);
    }

    public static PlaceResult place(BlockPos pos, float p_Distance, boolean p_Rotate, boolean p_UseSlabRule, boolean packetSwing)
    {
        BlockState l_State = mc.world.getBlockState(pos);

        boolean l_Replaceable = l_State.isReplaceable();

        boolean l_IsSlabAtBlock = l_State.getBlock() instanceof SlabBlock;

        if (!l_Replaceable && !l_IsSlabAtBlock)
            return PlaceResult.NotReplaceable;
        if (!BlockInteractionHelper.checkForNeighbours(pos))
            return PlaceResult.Neighbors;

        if (!l_IsSlabAtBlock)
        {
            ValidResult l_Result = valid(pos);

            if (l_Result != ValidResult.Ok && !l_Replaceable)
                return PlaceResult.CantPlace;
        }

        if (p_UseSlabRule)
        {
            if (l_IsSlabAtBlock && !l_State.isFullCube(mc.world, pos))
                return PlaceResult.CantPlace;
        }

        final Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());

        for (final Direction side : Direction.values())
        {
            final BlockPos neighbor = pos.offset(side);
            final Direction side2 = side.getOpposite();

            boolean l_IsWater = mc.world.getBlockState(neighbor).getBlock() == Blocks.WATER;

            if (mc.world.getBlockState(neighbor).getCollisionShape(mc.world, neighbor) != VoxelShapes.empty()
                    /*|| (l_IsWater && ModuleManager.Get().GetMod(LiquidInteractModule.class).isEnabled())*/)
            {
                final Vec3d hitVec = new Vec3d(neighbor.getX() + 0.5 + side2.getVector().getX() * 0.5, neighbor.getY() + 0.5 + side2.getVector().getY() * 0.5, neighbor.getZ() + 0.5 + side2.getVector().getZ() * 0.5);


                if (eyesPos.distanceTo(hitVec) <= p_Distance)
                {
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();

                    final boolean activated = BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos);

                    if (activated)
                    {
                        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                    }
                    if (p_Rotate)
                    {
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    }

                    ActionResult l_Result2 = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hitVec, side2, neighbor, false));


                    if (l_Result2 != ActionResult.FAIL)
                    {
                        if (packetSwing)
                            mc.player.networkHandler.sendPacket(new HandSwingC2SPacket(Hand.MAIN_HAND));
                        else
                            mc.player.swingHand(Hand.MAIN_HAND);
                        if (activated)
                        {
                            mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                        }
                        return PlaceResult.Placed;
                    }
                }
            }
        }
        return PlaceResult.CantPlace;
    }

    public static boolean IsLiquidOrAir(BlockPos p_Pos)
    {
        BlockState l_State = mc.world.getBlockState(p_Pos);

        return l_State.getBlock() == Blocks.WATER || l_State.getBlock() == Blocks.LAVA || l_State.getBlock() == Blocks.AIR;
    }

    public static float[] getFacingRotations(int x, int y, int z, Direction facing)
    {
        return getFacingRotations(x, y, z, facing, 1);
    }

    public static float[] getFacingRotations(int x, int y, int z, Direction facing, double width)
    {
        return getRotationsForPosition(x + 0.5 + facing.getVector().getX() * width / 2.0, y + 0.5 + facing.getVector().getY() * width / 2.0, z + 0.5 + facing.getVector().getZ() * width / 2.0);
    }

    public static float[] getRotationsForPosition(double x, double y, double z)
    {
        return getRotationsForPosition(x, y, z, mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
    }

    public static float[] getRotationsForPosition(double x, double y, double z, double sourceX, double sourceY, double sourceZ)
    {
        double deltaX = x - sourceX;
        double deltaY = y - sourceY;
        double deltaZ = z - sourceZ;

        double yawToEntity;

        if (deltaZ < 0 && deltaX < 0) { // quadrant 3
            yawToEntity = 90D + Math.toDegrees(Math.atan(deltaZ / deltaX)); // 90
            // degrees
            // forward
        } else if (deltaZ < 0 && deltaX > 0) { // quadrant 4
            yawToEntity = -90D + Math.toDegrees(Math.atan(deltaZ / deltaX)); // 90
            // degrees
            // back
        } else { // quadrants one or two
            yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        double distanceXZ = Math.sqrt(deltaX * deltaX + deltaZ
                * deltaZ);

        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));

        yawToEntity = wrapAngleTo180((float) yawToEntity);
        pitchToEntity = wrapAngleTo180((float) pitchToEntity);

        yawToEntity = isNaN(yawToEntity) ? 0 : yawToEntity;
        pitchToEntity = isNaN(pitchToEntity) ? 0 : pitchToEntity;

        return new float[] { (float) yawToEntity, (float) pitchToEntity };
    }

    public static float wrapAngleTo180(float angle)
    {
        angle %= 360.0F;

        while (angle >= 180.0F) {
            angle -= 360.0F;
        }
        while (angle < -180.0F) {
            angle += 360.0F;
        }

        return angle;
    }
}
