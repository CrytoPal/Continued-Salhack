package me.ionar.salhack.managers;

import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.RaycastContext;

import static me.ionar.salhack.main.Wrapper.mc;

public class BlockManager
{

    private static BlockPos _currBlock = null;
    private static boolean _started = false;

    public static void SetCurrentBlock(BlockPos block)
    {
        _currBlock = block;
        _started = false;
    }

    public static BlockPos GetCurrBlock()
    {
        return _currBlock;
    }

    public static boolean GetState()
    {
        if (_currBlock != null)
            return IsDoneBreaking(mc.world.getBlockState(_currBlock));

        return false;
    }

    private static boolean IsDoneBreaking(BlockState blockState)
    {
        return blockState.getBlock() == Blocks.BEDROCK
                || blockState.getBlock() == Blocks.AIR
                || blockState.getBlock() instanceof FluidBlock;
    }

    public static boolean Update(float range, boolean rayTrace)
    {
        if (_currBlock == null)
            return false;

        BlockState state = mc.world.getBlockState(_currBlock);

        if (IsDoneBreaking(state) || mc.player.squaredDistanceTo(_currBlock.toCenterPos()) > Math.pow(range, range))
        {
            _currBlock = null;
            return false;
        }

        // CPacketAnimation
        mc.player.swingHand(Hand.MAIN_HAND);

        Direction facing = Direction.UP;

        if (rayTrace)
        {
            BlockHitResult result = PlayerUtil.rayCastBlock(new RaycastContext(mc.player.getEyePos(), _currBlock.toCenterPos().add(0,0.5,0), RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, mc.player), _currBlock);

            if (result != null && result.getSide() != null)
                facing = result.getSide();
        }

        if (!_started)
        {
            _started = true;
            // Start Break

            mc.player.networkHandler.sendPacket(new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.START_DESTROY_BLOCK, _currBlock, facing));

        }
        else
        {
            mc.interactionManager.updateBlockBreakingProgress(_currBlock, facing);
        }

        return true;
    }
}