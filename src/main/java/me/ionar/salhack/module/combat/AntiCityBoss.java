package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

import me.ionar.salhack.util.BlockInteractionHelper.ValidResult;
import net.minecraft.util.math.Vec3d;

import static me.ionar.salhack.main.Wrapper.mc;

public class AntiCityBoss extends Module
{
    public final Value<Boolean> TrapCheck = new Value<>("TrapCheck", new String[]
            {"HC"}, "Only functions if you're trapped", false);

    public AntiCityBoss()
    {
        super("AntiCityBoss", "Automatically places 4 obsidian in the direction your facing to prevent getting crystaled", 0, -1, ModuleType.COMBAT);
    }



    @EventHandler
    public void onPlayerUpdate(PlayerMotionUpdate p_Event)
    {
        if (p_Event.getEra() != EventEra.PRE)
            return;

        if (TrapCheck.getValue() && !PlayerUtil.IsPlayerTrapped())
            return;

        final int slot = findStackHotbar(Blocks.OBSIDIAN);

        /// Make sure we have obby.
        if (slot == -1)
            return;

        BlockPos l_CenterPos = PlayerUtil.GetLocalPlayerPosFloored();
        ArrayList<BlockPos> BlocksToFill = new ArrayList<BlockPos>();

        switch (PlayerUtil.GetFacing())
        {
            case East:
                BlocksToFill.add(l_CenterPos.east().east());
                BlocksToFill.add(l_CenterPos.east().east().up());
                BlocksToFill.add(l_CenterPos.east().east().east());
                BlocksToFill.add(l_CenterPos.east().east().east().up());
                break;
            case North:
                BlocksToFill.add(l_CenterPos.north().north());
                BlocksToFill.add(l_CenterPos.north().north().up());
                BlocksToFill.add(l_CenterPos.north().north().north());
                BlocksToFill.add(l_CenterPos.north().north().north().up());
                break;
            case South:
                BlocksToFill.add(l_CenterPos.south().south());
                BlocksToFill.add(l_CenterPos.south().south().up());
                BlocksToFill.add(l_CenterPos.south().south().south());
                BlocksToFill.add(l_CenterPos.south().south().south().up());
                break;
            case West:
                BlocksToFill.add(l_CenterPos.west().west());
                BlocksToFill.add(l_CenterPos.west().west().up());
                BlocksToFill.add(l_CenterPos.west().west().west());
                BlocksToFill.add(l_CenterPos.west().west().west().up());
                break;
            default:
                break;
        }

        BlockPos l_PosToFill = null;

        for (BlockPos l_Pos : BlocksToFill)
        {
            ValidResult l_Result = BlockInteractionHelper.valid(l_Pos);

            if (l_Result != ValidResult.Ok)
                continue;

            l_PosToFill = l_Pos;
            break;
        }

        if (l_PosToFill != null)
        {
            int lastSlot;
            lastSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = slot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

            p_Event.cancel();
            float[] rotations = BlockInteractionHelper
                    .getLegitRotations(new Vec3d(l_PosToFill.getX(), l_PosToFill.getY(), l_PosToFill.getZ()));
            PlayerUtil.PacketFacePitchAndYaw(rotations[0], rotations[1]);
            BlockInteractionHelper.place(l_PosToFill, 5.0f, false, false);
            Finish(lastSlot);
        }
    }

    private void Finish(int p_LastSlot)
    {
        if (!slotEqualsBlock(p_LastSlot, Blocks.OBSIDIAN))
        {
            mc.player.getInventory().selectedSlot = p_LastSlot;
        }
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
    }

    public boolean hasStack(Block type)
    {
        if (mc.player.getMainHandStack().getItem() instanceof BlockItem block)
        {
            return block.getBlock() == type;
        }
        return false;
    }

    private boolean slotEqualsBlock(int slot, Block type)
    {
        if (mc.player.getInventory().getStack(slot).getItem() instanceof BlockItem block)
        {
            return block.getBlock() == type;
        }

        return false;
    }

    private int findStackHotbar(Block type)
    {
        for (int i = 0; i < 9; i++)
        {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block)
            {
                if (block.getBlock() == type)
                {
                    return i;
                }
            }
        }
        return -1;
    }
}
