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

public class AntiCityBoss extends Module {
    public final Value<Boolean> trapCheck = new Value<>("TrapCheck", new String[]{"HC"}, "Only functions if you're trapped", false);

    public AntiCityBoss() {
        super("AntiCityBoss", new String[]{ "AntiTrap" }, "Automatically places 4 obsidian in the direction your facing to prevent getting crystaled", 0, -1, ModuleType.COMBAT);
    }

    @EventHandler
    public void onPlayerUpdate(PlayerMotionUpdate event) {
        if (event.getEra() != EventEra.PRE || mc.player == null) return;
        if (trapCheck.getValue() && !PlayerUtil.isPlayerTrapped()) return;
        final int slot = findStackHotbar(Blocks.OBSIDIAN);
        if (slot == -1) return;
        BlockPos centerPos = PlayerUtil.getLocalPlayerPosFloored();
        ArrayList<BlockPos> BlocksToFill = new ArrayList<>();
        switch (PlayerUtil.getFacing()) {
            case East -> {
                BlocksToFill.add(centerPos.east().east());
                BlocksToFill.add(centerPos.east().east().up());
                BlocksToFill.add(centerPos.east().east().east());
                BlocksToFill.add(centerPos.east().east().east().up());
            }
            case North -> {
                BlocksToFill.add(centerPos.north().north());
                BlocksToFill.add(centerPos.north().north().up());
                BlocksToFill.add(centerPos.north().north().north());
                BlocksToFill.add(centerPos.north().north().north().up());
            }
            case South -> {
                BlocksToFill.add(centerPos.south().south());
                BlocksToFill.add(centerPos.south().south().up());
                BlocksToFill.add(centerPos.south().south().south());
                BlocksToFill.add(centerPos.south().south().south().up());
            }
            case West -> {
                BlocksToFill.add(centerPos.west().west());
                BlocksToFill.add(centerPos.west().west().up());
                BlocksToFill.add(centerPos.west().west().west());
                BlocksToFill.add(centerPos.west().west().west().up());
            }
            default -> {}
        }
        BlockPos PosToFill = null;
        for (BlockPos pos : BlocksToFill) {
            ValidResult valid = BlockInteractionHelper.valid(pos);
            if (valid != ValidResult.Ok) continue;
            PosToFill = pos;
            break;
        }
        if (PosToFill != null) {
            int lastSlot;
            lastSlot = mc.player.getInventory().selectedSlot;
            mc.player.getInventory().selectedSlot = slot;
            mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
            event.cancel();
            float[] rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(PosToFill.getX(), PosToFill.getY(), PosToFill.getZ()));
            PlayerUtil.packetFacePitchAndYaw(rotations[0], rotations[1]);
            BlockInteractionHelper.place(PosToFill, 5.0f, false, false);
            finish(lastSlot);
        }
    }

    private void finish(int lastSlot) {
        if (mc.player == null) return;
        if (!slotEqualsBlock(lastSlot, Blocks.OBSIDIAN)) mc.player.getInventory().selectedSlot = lastSlot;
        mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
    }

    public boolean hasStack(Block type) {
        if (mc.player.getMainHandStack().getItem() instanceof BlockItem block) return block.getBlock() == type;
        return false;
    }

    private boolean slotEqualsBlock(int slot, Block type) {
        if (mc.player == null) return false;
        if (mc.player.getInventory().getStack(slot).getItem() instanceof BlockItem block) return block.getBlock() == type;
        return false;
    }

    private int findStackHotbar(Block type) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block && block.getBlock() == type) return i;
        }
        return -1;
    }
}
