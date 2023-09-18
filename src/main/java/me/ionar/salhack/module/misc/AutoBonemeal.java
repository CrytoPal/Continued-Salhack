package me.ionar.salhack.module.misc;


import java.util.Comparator;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.entity.EntityUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static me.ionar.salhack.main.Wrapper.mc;

public class AutoBonemeal extends Module {
    public static Value<Integer> Radius = new Value<>("Radius", new String[] {"R"}, "Radius to search for not fully grown seeds", 4, 0, 10, 1);

    public AutoBonemeal() {
        super("AutoBonemeal", "Bonemeals anything nearby", 0, -1, ModuleType.MISC);
    }

    private boolean IsRunning = false;

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event) {
        if(p_Event.getEra() != EventEra.PRE)
            return;

        BlockPos l_ClosestPos = BlockInteractionHelper.getSphere(PlayerUtil.GetLocalPlayerPosFloored(), Radius.getValue(), Radius.getValue(), false, true, 0).stream()
                .filter(p_Pos -> IsValidBlockPos(p_Pos))
                .min(Comparator.comparing(p_Pos -> mc.player.squaredDistanceTo(p_Pos.toCenterPos())))
                .orElse(null);

        if (l_ClosestPos != null && UpdateBonemealIfNeed()) {
            p_Event.cancel();

            final double l_Pos[] =  EntityUtil.calculateLookAt(
                    l_ClosestPos.getX() + 0.5,
                    l_ClosestPos.getY() + 0.5,
                    l_ClosestPos.getZ() + 0.5,
                    mc.player);

            PlayerUtil.PacketFacePitchAndYaw((float)l_Pos[0], (float)l_Pos[1]);

            mc.interactionManager.interactBlock(mc.player, mc.player.getOffHandStack().getItem() instanceof BoneMealItem ? Hand.OFF_HAND : Hand.MAIN_HAND,
                    new BlockHitResult(l_ClosestPos.toCenterPos().offset(Direction.UP, 0.5f), Direction.UP, l_ClosestPos, false));

            mc.player.swingHand(Hand.MAIN_HAND);


            IsRunning = true;
        }
        else
            IsRunning = false;
    }

    private boolean IsValidBlockPos(final BlockPos p_Pos) {
        BlockState l_State = mc.world.getBlockState(p_Pos);

        // crops XD
        if (l_State.getBlock() instanceof CropBlock l_Crop) {
            if (l_Crop.getMaxAge() != l_Crop.getAge(l_State))
                return true;
        }

        return false;
    }

    public boolean IsRunning() {
        return IsRunning;
    }

    private boolean UpdateBonemealIfNeed() {
        ItemStack l_Main = mc.player.getMainHandStack();
        ItemStack l_Off = mc.player.getOffHandStack();

        if (!l_Main.isEmpty() && l_Main.getItem() instanceof BoneMealItem) {
            if (IsBoneMealItem(l_Main))
                return true;
        }
        else if (!l_Off.isEmpty() && l_Off.getItem() instanceof BoneMealItem) {
            if (IsBoneMealItem(l_Off))
                return true;
        }

        for (int l_I = 0; l_I < 9; ++l_I) {
            ItemStack l_Stack = mc.player.getInventory().getStack(l_I);

            if (l_Stack.isEmpty() || !IsBoneMealItem(l_Stack))
                continue;

            mc.player.getInventory().selectedSlot = l_I;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(l_I));
            return true;
        }
        return false;
    }

    private boolean IsBoneMealItem(ItemStack p_Stack) {
        return p_Stack.getItem() instanceof BoneMealItem;
    }
}