package me.ionar.salhack.module.combat;


import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.mixin.ClientPlayerEntityAccessor;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.MathUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.UpdateSelectedSlotC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import static me.ionar.salhack.util.BlockInteractionHelper.ValidResult;

public class Surround extends Module {
    public final Value<Boolean> disable = new Value<>("Toggles", new String[]{"Toggles", "Disables"}, "Will toggle off after a place", false);
    public final Value<Boolean> toggleOffGround = new Value<>("ToggleOffGround", new String[]{"Toggles", "Disables"}, "Will toggle off after a place", false);
    public final Value<centerModes> centerMode = new Value<>("Center", new String[]{"Center"}, "Moves you to center of block", centerModes.NCP);
    public final Value<Boolean> rotate = new Value<>("Rotate", new String[]{"rotate"}, "Rotate", true);
    public final Value<Integer> blocksPerTick = new Value<>("BlocksPerTick", new String[]{"BPT"}, "Blocks per tick", 1, 1, 10, 1);
    public final Value<Boolean> activateOnlyOnShift = new Value<>("ActivateOnlyOnShift", new String[]{"AoOS"}, "Activates only when shift is pressed.", false);
    private Vec3d center = Vec3d.ZERO;
    public enum centerModes {
        Teleport,
        NCP,
        None,
    }

    public Surround() {
        super("Surround", new String[]{"NoCrystal"}, "Automatically surrounds you with obsidian in the four cardinal directions", 0, 0x5324DB, ModuleType.COMBAT);
    }

    @Override
    public String getMetaData() {
        return centerMode.getValue().toString();
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player == null) {
            toggle(true);
            return;
        }
        if (activateOnlyOnShift.getValue()) return;
        center = getCenter(mc.player.getX(), mc.player.getY(), mc.player.getZ());
        if (centerMode.getValue() != centerModes.None) mc.player.setVelocity(.0, mc.player.getVelocity().getY(), .0);
        if (centerMode.getValue() == centerModes.Teleport) {
            mc.player.setPosition(center.x, center.y, center.z);
            ((ClientPlayerEntityAccessor) mc.player).invokeSync();
        }
    }

    @EventHandler
    public void onPlayerMotionUpdate(PlayerMotionUpdate event){
        if (event.getEra() != EventEra.PRE || mc.player == null || mc.world == null) return;
        if (activateOnlyOnShift.getValue()) {
            if (!mc.options.sneakKey.isPressed()) {
                center = Vec3d.ZERO;
                return;
            }
            if (center == Vec3d.ZERO) {
                center = getCenter(mc.player.getX(), mc.player.getY(), mc.player.getZ());
                if (centerMode.getValue() != centerModes.None) mc.player.setVelocity(.0, mc.player.getVelocity().getY(), .0);
                if (centerMode.getValue() == centerModes.Teleport) {
                    mc.player.setPosition(center.x, center.y, center.z);
                    ((ClientPlayerEntityAccessor) mc.player).invokeSync();
                }
            }
        }
        /// NCP Centering
        if (center != Vec3d.ZERO && centerMode.getValue() == centerModes.NCP) {
            double diffX = Math.abs(center.x - mc.player.getX());
            double diffZ = Math.abs(center.z - mc.player.getZ());
            if (diffX <= 0.1 && diffZ <= 0.1) center = Vec3d.ZERO;
            else {
                double motionX = center.x - mc.player.getX();
                double motionZ = center.z - mc.player.getZ();
                mc.player.setVelocity(motionX / 2, mc.player.getVelocity().getY(), motionZ / 2);
            }
        }
        if (!mc.player.isOnGround() && !activateOnlyOnShift.getValue() && toggleOffGround.getValue()) {
            toggle(true);
            sendMessage("You are off ground! toggling!");
            return;
        }
        final Vec3d pos = MathUtil.interpolateEntity(mc.player);
        final BlockPos interopPos = BlockPos.ofFloored(pos);
        final BlockPos north = interopPos.north();
        final BlockPos south = interopPos.south();
        final BlockPos east = interopPos.east();
        final BlockPos west = interopPos.west();
        BlockPos[] interops = {north, south, east, west};
        if (isSurrounded(mc.player)) return;
        int lastSlot;
        final int slot = findStackHotbar(Blocks.OBSIDIAN);
        if (hasStack(Blocks.OBSIDIAN) || slot != -1) {
            if ((mc.player.isOnGround())) {
                lastSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = slot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));
                int blocksPerTickValue = blocksPerTick.getValue();
                for (BlockPos blockPos : interops) {
                    ValidResult valid = BlockInteractionHelper.valid(blockPos);
                    if (valid == ValidResult.AlreadyBlockThere && !mc.world.getBlockState(blockPos).isReplaceable()) continue;
                    if (valid == ValidResult.NoNeighbors) {
                        final BlockPos[] blockPos2 = {blockPos.down(), blockPos.north(), blockPos.south(), blockPos.east(), blockPos.west(), blockPos.up(),};
                        for (BlockPos pos1 : blockPos2) {
                            ValidResult validResult = BlockInteractionHelper.valid(pos1);
                            if (validResult == ValidResult.NoNeighbors || validResult == ValidResult.NoEntityCollision) continue;
                            BlockInteractionHelper.place(pos1, 5.0f, false, false);
                            event.cancel();
                            float[] rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(pos1.getX(), pos1.getY(), pos1.getZ()));
                            PlayerUtil.packetFacePitchAndYaw(rotations[0], rotations[1]);
                            break;
                        }
                        continue;
                    }
                    BlockInteractionHelper.place(blockPos, 5.0f, false, false);
                    event.cancel();
                    float[] rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(blockPos.getX(), blockPos.getY(), blockPos.getZ()));
                    PlayerUtil.packetFacePitchAndYaw(rotations[0], rotations[1]);
                    if (--blocksPerTickValue <= 0) break;
                }
                if (!slotEqualsBlock(lastSlot, Blocks.OBSIDIAN)) mc.player.getInventory().selectedSlot = lastSlot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));
                if (this.disable.getValue()) this.toggle(true);
            }
        }
    }


    public boolean isSurrounded(PlayerEntity player) {
        final Vec3d interpolated = MathUtil.interpolateEntity(player);
        final BlockPos floored = BlockPos.ofFloored(interpolated);
        final BlockPos north = floored.north();
        final BlockPos south = floored.south();
        final BlockPos east = floored.east();
        final BlockPos west = floored.west();
        BlockPos[] blockPositions = {north, south, east, west};
        for (BlockPos blockPos : blockPositions) {
            if (BlockInteractionHelper.valid(blockPos) != BlockInteractionHelper.ValidResult.AlreadyBlockThere) return false;
        }
        return true;
    }

    public boolean hasStack(Block type) {
        return mc.player.getMainHandStack().getItem() instanceof BlockItem block && block.getBlock() == type;
    }

    private boolean slotEqualsBlock(int slot, Block type) {
        if (mc.player == null) return false;
        return mc.player.getInventory().getStack(slot).getItem() instanceof BlockItem block && block.getBlock() == type;
    }

    private int findStackHotbar(Block type) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block && block.getBlock() == type) return i;
        }
        return -1;
    }

    public Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D;
        return new Vec3d(x, y, z);
    }

    public boolean hasObsidian() {
        return findStackHotbar(Blocks.OBSIDIAN) != -1;
    }
}