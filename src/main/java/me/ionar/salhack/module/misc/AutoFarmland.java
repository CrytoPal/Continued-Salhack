package me.ionar.salhack.module.misc;

import java.util.Comparator;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.GrassBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.HoeItem;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class AutoFarmland extends Module {
    public final Value<Integer> Radius = new Value<>("Radius", new String[] {"R"}, "Radius to search for grass/dirt", 4, 0, 10, 1);
    public final Value<Float> Delay = new Value<>("Delay", new String[] {"D"}, "Delay to use", 1.0f, 0.0f, 10.0f, 1.0f);

    public AutoFarmland() {
        super("AutoFarmland", new String[] {""}, "Automatically sets grass or dirt to farmland", 0, -1, ModuleType.MISC);
    }

    private Timer timer = new Timer();

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event) {
        if (!timer.passed(Delay.getValue() * 100))
            return;

        BlockPos l_ClosestPos = BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), Radius.getValue(), Radius.getValue(), false, true, 0).stream()
                .filter(p_Pos -> IsValidBlockPos(p_Pos))
                .min(Comparator.comparing(p_Pos -> mc.player.squaredDistanceTo(p_Pos.toCenterPos())))
                .orElse(null);

        if (l_ClosestPos != null && mc.player.getMainHandStack().getItem() instanceof HoeItem) {
            timer.reset();

            p_Event.cancel();

            final double l_Pos[] =  EntityUtil.calculateLookAt(
                    l_ClosestPos.getX() + 0.5,
                    l_ClosestPos.getY() - 0.5,
                    l_ClosestPos.getZ() + 0.5,
                    mc.player);


            PlayerUtil.PacketFacePitchAndYaw((float)l_Pos[0], (float)l_Pos[1]);

            mc.interactionManager.interactBlock(mc.player,Hand.MAIN_HAND, new BlockHitResult(l_ClosestPos.toCenterPos().offset(Direction.UP, 0.5f), Direction.UP, l_ClosestPos, false));
            mc.player.swingHand(Hand.MAIN_HAND);
        }
    }

    private boolean IsValidBlockPos(final BlockPos p_Pos) {
        BlockState l_State = mc.world.getBlockState(p_Pos);

        if (l_State.getBlock() == Blocks.DIRT || l_State.getBlock() instanceof GrassBlock)
            return mc.world.getBlockState(p_Pos.up()).getBlock() == Blocks.AIR;

        return false;
    }
}