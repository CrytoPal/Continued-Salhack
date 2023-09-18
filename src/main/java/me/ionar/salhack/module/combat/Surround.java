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

import static me.ionar.salhack.main.Wrapper.mc;
import static me.ionar.salhack.util.BlockInteractionHelper.ValidResult;

public class Surround extends Module {
    public final Value<Boolean> disable = new Value<>("Toggles", new String[]
            {"Toggles", "Disables"}, "Will toggle off after a place", false);
    public final Value<Boolean> ToggleOffGround = new Value<>("ToggleOffGround", new String[]
            {"Toggles", "Disables"}, "Will toggle off after a place", false);
    public final Value<CenterModes> CenterMode = new Value<>("Center", new String[]
            {"Center"}, "Moves you to center of block", CenterModes.NCP);

    public final Value<Boolean> rotate = new Value<>("Rotate", new String[]
            {"rotate"}, "Rotate", true);
    public final Value<Integer> BlocksPerTick = new Value<>("BlocksPerTick", new String[]{"BPT"}, "Blocks per tick", 1, 1, 10, 1);
    public final Value<Boolean> ActivateOnlyOnShift = new Value<>("ActivateOnlyOnShift", new String[]
            {"AoOS"}, "Activates only when shift is pressed.", false);

    public enum CenterModes {
        Teleport,
        NCP,
        None,
    }

    public Surround() {
        super("Surround", "Automatically surrounds you with obsidian in the four cardinal directions", 0, 0x5324DB, ModuleType.COMBAT);
    }

    private Vec3d Center = Vec3d.ZERO;

    @Override
    public String getMetaData() {
        return CenterMode.getValue().toString();
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.player == null) {
            toggle(true);
            return;
        }

        if (ActivateOnlyOnShift.getValue())
            return;

        Center = GetCenter(mc.player.getX(), mc.player.getY(), mc.player.getZ());

        if (CenterMode.getValue() != CenterModes.None) {
            mc.player.setVelocity(.0, mc.player.getVelocity().getY(), .0);
        }

        if (CenterMode.getValue() == CenterModes.Teleport) {
            mc.player.setPosition(Center.x, Center.y, Center.z);
            ((ClientPlayerEntityAccessor) mc.player).invokeSync();
        }
    }

    @EventHandler
    public void onPlayerMotionUpdate(PlayerMotionUpdate p_Event){
        if (p_Event.getEra() != EventEra.PRE)
            return;

        if (ActivateOnlyOnShift.getValue()) {
            if (!mc.options.sneakKey.isPressed()) {
                Center = Vec3d.ZERO;
                return;
            }

            if (Center == Vec3d.ZERO) {
                Center = GetCenter(mc.player.getX(), mc.player.getY(), mc.player.getZ());

                if (CenterMode.getValue() != CenterModes.None) {
                    mc.player.setVelocity(.0, mc.player.getVelocity().getY(), .0);
                }

                if (CenterMode.getValue() == CenterModes.Teleport) {
                    mc.player.setPosition(Center.x, Center.y, Center.z);
                    ((ClientPlayerEntityAccessor) mc.player).invokeSync();
                }
            }
        }

        /// NCP Centering
        if (Center != Vec3d.ZERO && CenterMode.getValue() == CenterModes.NCP) {
            double l_XDiff = Math.abs(Center.x - mc.player.getX());
            double l_ZDiff = Math.abs(Center.z - mc.player.getZ());

            if (l_XDiff <= 0.1 && l_ZDiff <= 0.1) {
                Center = Vec3d.ZERO;
            } else {
                double l_MotionX = Center.x - mc.player.getX();
                double l_MotionZ = Center.z - mc.player.getZ();

                mc.player.setVelocity(l_MotionX / 2, mc.player.getVelocity().getY(), l_MotionZ / 2);
            }
        }

        if (!mc.player.isOnGround() && !ActivateOnlyOnShift.getValue()) {
            if (ToggleOffGround.getValue()) {
                toggle(true);
                SalHack.SendMessage("[Surround]: You are off ground! toggling!");
                return;
            }
        }

        final Vec3d pos = MathUtil.interpolateEntity(mc.player);

        final BlockPos interpPos = BlockPos.ofFloored(pos);

        final BlockPos north = interpPos.north();
        final BlockPos south = interpPos.south();
        final BlockPos east = interpPos.east();
        final BlockPos west = interpPos.west();

        BlockPos[] l_Array = {north, south, east, west};

        /// We don't need to do anything if we are not surrounded
        if (IsSurrounded(mc.player))
            return;

        int lastSlot;
        final int slot = findStackHotbar(Blocks.OBSIDIAN);
        if (hasStack(Blocks.OBSIDIAN) || slot != -1) {
            if ((mc.player.isOnGround())) {
                lastSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = slot;
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(slot));

                int l_BlocksPerTick = BlocksPerTick.getValue();

                for (BlockPos l_Pos : l_Array) {
                    ValidResult l_Result = BlockInteractionHelper.valid(l_Pos);

                    if (l_Result == ValidResult.AlreadyBlockThere && !mc.world.getBlockState(l_Pos).isReplaceable())
                        continue;

                    if (l_Result == ValidResult.NoNeighbors) {
                        final BlockPos[] l_Test = {l_Pos.down(), l_Pos.north(), l_Pos.south(), l_Pos.east(), l_Pos.west(), l_Pos.up(),};

                        for (BlockPos l_Pos2 : l_Test) {
                            ValidResult l_Result2 = BlockInteractionHelper.valid(l_Pos2);

                            if (l_Result2 == ValidResult.NoNeighbors || l_Result2 == ValidResult.NoEntityCollision)
                                continue;

                            BlockInteractionHelper.place(l_Pos2, 5.0f, false, false);
                            p_Event.cancel();
                            float[] rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(l_Pos2.getX(), l_Pos2.getY(), l_Pos2.getZ()));
                            PlayerUtil.PacketFacePitchAndYaw(rotations[0], rotations[1]);
                            break;
                        }

                        continue;
                    }

                    BlockInteractionHelper.place(l_Pos, 5.0f, false, false);

                    p_Event.cancel();

                    float[] rotations = BlockInteractionHelper.getLegitRotations(new Vec3d(l_Pos.getX(), l_Pos.getY(), l_Pos.getZ()));
                    PlayerUtil.PacketFacePitchAndYaw(rotations[0], rotations[1]);
                    if (--l_BlocksPerTick <= 0)
                        break;
                }

                if (!slotEqualsBlock(lastSlot, Blocks.OBSIDIAN)) {
                    mc.player.getInventory().selectedSlot = lastSlot;
                }
                mc.player.networkHandler.sendPacket(new UpdateSelectedSlotC2SPacket(mc.player.getInventory().selectedSlot));

                if (this.disable.getValue()) {
                    this.toggle(true);
                }
            }
        }
    }


    public boolean IsSurrounded(PlayerEntity p_Who) {
        final Vec3d l_PlayerPos = MathUtil.interpolateEntity(p_Who);

        final BlockPos l_InterpPos = BlockPos.ofFloored(l_PlayerPos);

        final BlockPos l_North = l_InterpPos.north();
        final BlockPos l_South = l_InterpPos.south();
        final BlockPos l_East = l_InterpPos.east();
        final BlockPos l_West = l_InterpPos.west();

        BlockPos[] l_Array = {l_North, l_South, l_East, l_West};

        for (BlockPos l_Pos : l_Array) {
            if (BlockInteractionHelper.valid(l_Pos) != BlockInteractionHelper.ValidResult.AlreadyBlockThere) {
                return false;
            }
        }

        return true;
    }

    public boolean hasStack(Block type) {
        if (mc.player.getMainHandStack().getItem() instanceof BlockItem block) {
            return block.getBlock() == type;
        }
        return false;
    }

    private boolean slotEqualsBlock(int slot, Block type) {
        if (mc.player.getInventory().getStack(slot).getItem() instanceof BlockItem block) {
            return block.getBlock() == type;
        }
        return false;
    }

    private int findStackHotbar(Block type) {
        for (int i = 0; i < 9; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() instanceof BlockItem block) {
                if (block.getBlock() == type) {
                    return i;
                }
            }
        }
        return -1;
    }

    public Vec3d GetCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5D;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5D;

        return new Vec3d(x, y, z);
    }

    public boolean HasObsidian() {
        return findStackHotbar(Blocks.OBSIDIAN) != -1;
    }
}