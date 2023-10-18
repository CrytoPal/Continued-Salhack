package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.player.PlayerMotionUpdate;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.BlockInteractionHelper;
import me.ionar.salhack.util.ChatUtils;
import me.ionar.salhack.util.SalUtil;
import me.ionar.salhack.util.entity.PlayerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.EnderChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShapes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.ionar.salhack.main.Wrapper.mc;

public final class AutoTrap extends Module {
    private final Vec3d[] offsetsDefault = new Vec3d[]{
                    new Vec3d(0.0, 0.0, -1.0), // left
                    new Vec3d(1.0, 0.0, 0.0),  // right
                    new Vec3d(0.0, 0.0, 1.0), // forwards
                    new Vec3d(-1.0, 0.0, 0.0), // back
                    new Vec3d(0.0, 1.0, -1.0), // +1 left
                    new Vec3d(1.0, 1.0, 0.0), // +1 right
                    new Vec3d(0.0, 1.0, 1.0), // +1 forwards
                    new Vec3d(-1.0, 1.0, 0.0), // +1 back
                    new Vec3d(0.0, 2.0, -1.0), // +2 left
                    new Vec3d(1.0, 2.0, 0.0), // +2 right
                    new Vec3d(0.0, 2.0, 1.0), // +2 forwards
                    new Vec3d(-1.0, 2.0, 0.0), // +2 backwards
                    new Vec3d(0.0, 3.0, -1.0), // +3 left
                    new Vec3d(0.0, 3.0, 0.0) // +3 middle
            };
    private final Vec3d[]  offsetsTall = new Vec3d[]{
                    new Vec3d(0.0, 0.0, -1.0), // left
                    new Vec3d(1.0, 0.0, 0.0),  // right
                    new Vec3d(0.0, 0.0, 1.0), // forwards
                    new Vec3d(-1.0, 0.0, 0.0), // back
                    new Vec3d(0.0, 1.0, -1.0), // +1 left
                    new Vec3d(1.0, 1.0, 0.0), // +1 right
                    new Vec3d(0.0, 1.0, 1.0), // +1 forwards
                    new Vec3d(-1.0, 1.0, 0.0), // +1 back
                    new Vec3d(0.0, 2.0, -1.0), // +2 left
                    new Vec3d(1.0, 2.0, 0.0), // +2 right
                    new Vec3d(0.0, 2.0, 1.0), // +2 forwards
                    new Vec3d(-1.0, 2.0, 0.0), // +2 backwards
                    new Vec3d(0.0, 3.0, -1.0), // +3 left
                    new Vec3d(0.0, 3.0, 0.0), // +3 middle
                    new Vec3d(0.0, 4.0, 0.0) // +4 middle
            };
    public final Value<Boolean> toggleMode = new Value<>("toggleMode", new String[]{ "toggleMode "}, "ToggleMode", true);
    public final Value<Float> range = new Value<>("range", new String[]{ "range" }, "Range", 5.5f, 0f, 10.0f, 1.0f);
    public final Value<Integer> blockPerTick = new Value<>("blockPerTick", new String[]{ "blockPerTick" }, "Blocks per Tick", 4, 1, 10, 1);
    public final Value<Boolean> rotate = new Value<>("rotate", new String[]{ "rotate" }, "Rotate", true);
    public final Value<Boolean> announceUsage = new Value<>("announceUsage", new String[]{ "announceUsage" }, "Announce Usage", true);
    public final Value<Boolean> EChests = new Value<>("EChests", new String[]{ "EChests" }, "EChests", false);

    public final Value<Modes> Mode = new Value<>("Mode", new String[] {"Mode"}, "The mode to use for autotrap", Modes.Full);

    public enum Modes {
        Full,
        Tall,
    }

    public AutoTrap() {
        super("AutoTrap", "Traps enemies in obsidian", 0, 0x24DB43, Module.ModuleType.COMBAT);
    }

    private String lastTickTargetName = "";
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private boolean isSneaking = false;
    private int offsetStep = 0;
    private boolean firstRun = true;

    @Override
    public String getMetaData() {
        if (EChests.getValue())
            return "Ender Chests";

        return "Obsidian";
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.player == null) {
            toggle(true);
            return;
        }

        firstRun = true;
        playerHotbarSlot = mc.player.getInventory().selectedSlot;
        lastHotbarSlot = -1;

        if (findObiInHotbar() == -1) {
            ChatUtils.errorMessage(String.format(Formatting.BLUE + "[AutoTrap] " + Formatting.RED + "You do not have any %s in your hotbar!", Formatting.LIGHT_PURPLE + (EChests.getValue() ? "Ender Chests" : "Obsidian") + Formatting.RESET));
            toggle(true);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (mc.player != null) {
            if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1)
                mc.player.getInventory().selectedSlot = playerHotbarSlot;

            if (isSneaking) {
                mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                isSneaking = false;
            }
            playerHotbarSlot = -1;
            lastHotbarSlot = -1;
            if (announceUsage.getValue())
                ChatUtils.sendMessage(Formatting.BLUE + "[AutoTrap] " + Formatting.WHITE + "Disabled!");
        }
    }

    @EventHandler
    public void OnPlayerUpdate(PlayerMotionUpdate p_Event){
        if (p_Event.getEra() != EventEra.PRE)
            return;

        PlayerEntity closestTarget = SalUtil.findClosestTarget();
        if (mc.player != null) {
            if (closestTarget == null) {
                if (firstRun) {
                    firstRun = false;
                    if (announceUsage.getValue()) {
                        ChatUtils.sendMessage(Formatting.BLUE + "[AutoTrap] " + Formatting.WHITE + "Enabled, waiting for target.");
                    }
                }
                return;
            }
            if (firstRun) {
                firstRun = false;
                lastTickTargetName = closestTarget.getName().getString();
                if (announceUsage.getValue()) {
                    ChatUtils.sendMessage(Formatting.BLUE + "[AutoTrap]" + Formatting.WHITE + "Target: " + lastTickTargetName);
                }
            } else if (!lastTickTargetName.equals(closestTarget.getName().getString())) {
                lastTickTargetName = closestTarget.getName().getString();
                offsetStep = 0;
                if (announceUsage.getValue()) {
                    ChatUtils.sendMessage(Formatting.BLUE + "[AutoTrap]" + Formatting.WHITE + "New target: " + lastTickTargetName);
                }
            }

            if (toggleMode.getValue()) {
                if (PlayerUtil.IsEntityTrapped(closestTarget)) {
                    toggle(true);
                    return;
                }
            }

            final List<Vec3d> placeTargets = new ArrayList<>();

            switch (Mode.getValue()) {
                case Full -> Collections.addAll(placeTargets, offsetsDefault);
                case Tall -> Collections.addAll(placeTargets, offsetsTall);
                default -> {
                }
            }

            int blocksPlaced = 0;
            while (blocksPlaced < blockPerTick.getValue()) {
                if (offsetStep >= placeTargets.size()) {
                    offsetStep = 0;
                    break;
                }
                final BlockPos offsetPos = BlockPos.ofFloored(placeTargets.get(offsetStep));
                final BlockPos targetPos = BlockPos.ofFloored(closestTarget.getPos()).down().add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());

                boolean shouldTryToPlace = true;
                if (!mc.world.getBlockState(targetPos).isReplaceable())
                    shouldTryToPlace = false;

                for (final Entity entity : mc.world.getOtherEntities(null, new Box(targetPos))) {
                    if (!(entity instanceof ItemEntity) && !(entity instanceof ExperienceOrbEntity)) {
                        shouldTryToPlace = false;
                        break;
                    }
                }

                if (shouldTryToPlace && placeBlock(targetPos)) {
                    ++blocksPlaced;
                }
                ++offsetStep;
            }
            if (blocksPlaced > 0) {
                if (lastHotbarSlot != playerHotbarSlot && playerHotbarSlot != -1) {
                    mc.player.getInventory().selectedSlot = playerHotbarSlot;
                    lastHotbarSlot = playerHotbarSlot;
                }
                if (isSneaking) {
                    mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY));
                    isSneaking = false;
                }
            }
        }
    }

    private boolean placeBlock(final BlockPos pos) {
        if (!mc.world.getBlockState(pos).isReplaceable())
            return false;
        if (!BlockInteractionHelper.checkForNeighbours(pos))
            return false;
        final Vec3d eyesPos = new Vec3d(mc.player.getX(), mc.player.getY() + mc.player.getEyeHeight(mc.player.getPose()), mc.player.getZ());
        for (final Direction side : Direction.values()) {
            final BlockPos neighbor = pos.offset(side);
            final Direction side2 = side.getOpposite();
            if (mc.world.getBlockState(neighbor).getCollisionShape(mc.world, neighbor) != VoxelShapes.empty()) {
                final Vec3d hitVec = new Vec3d(neighbor.getX() + 0.5 + side2.getVector().getX() * 0.5, neighbor.getY() + 0.5 + side2.getVector().getY() * 0.5, neighbor.getZ() + 0.5 + side2.getVector().getZ() * 0.5);

                if (eyesPos.distanceTo(hitVec) <= range.getValue()) {
                    final int obiSlot = findObiInHotbar();
                    if (obiSlot == -1) {
                        toggle(true);
                        return false;
                    }
                    if (lastHotbarSlot != obiSlot) {
                        mc.player.getInventory().selectedSlot = obiSlot;
                        lastHotbarSlot = obiSlot;
                    }
                    final Block neighborPos = mc.world.getBlockState(neighbor).getBlock();
                    if (BlockInteractionHelper.blackList.contains(neighborPos) || BlockInteractionHelper.shulkerList.contains(neighborPos)) {
                        mc.player.networkHandler.sendPacket(new ClientCommandC2SPacket(mc.player, ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY));
                        isSneaking = true;
                    }
                    if (rotate.getValue()) {
                        BlockInteractionHelper.faceVectorPacketInstant(hitVec);
                    }

                    mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, new BlockHitResult(hitVec, side2, neighbor, false));

                    mc.player.swingHand(Hand.MAIN_HAND);
                    return true;
                }
            }
        }
        return false;
    }

    private int findObiInHotbar() {
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack != ItemStack.EMPTY && stack.getItem() instanceof BlockItem) {
                final Block block = ((BlockItem) stack.getItem()).getBlock();

                if (EChests.getValue()) {
                    if (block instanceof EnderChestBlock)
                        return i;
                }
                else if (block == Blocks.OBSIDIAN) {
                    return i;
                }
            }
        }
        return -1;
    }
}