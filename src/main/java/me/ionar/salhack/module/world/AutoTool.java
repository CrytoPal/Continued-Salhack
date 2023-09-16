package me.ionar.salhack.module.world;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.block.AirBlock;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

import static me.ionar.salhack.main.Wrapper.mc;

public class AutoTool extends Module {

    public final Value<Boolean> swapBack = new Value<>("SwapBack", new String[]{ "" }, "Swaps to the item you originally had.", true);
    public static int itemslot;
    int index = -1;
    private boolean send;

    public AutoTool() {
        super("AutoTool", new String[]{"S"} ,"yes",0,-1 ,ModuleType.WORLD);
    }

    List<Integer> previousSlot = new ArrayList<>();
    private int getToolHotbar(BlockPos pos)
    {
        float Speed = 1.0f;
        if (mc.player != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                final ItemStack stack = mc.player.getInventory().getStack(i);
                if (stack != null && stack != ItemStack.EMPTY) {
                    final float digSpeed = EnchantmentHelper.getLevel(Enchantments.EFFICIENCY, stack);
                    final float destroySpeed = stack.getMiningSpeedMultiplier(mc.world.getBlockState(pos));

                    if (mc.world.getBlockState(pos).getBlock() instanceof AirBlock) {
                        return 0;
                    }
                    if (digSpeed + destroySpeed > Speed)
                    {
                        Speed = digSpeed + destroySpeed;
                        index = i;
                        blockStrength(pos);
                    }
                }
            }
        }
        return index;
    }

    private float blockStrength(BlockPos pos) {
        float hardness = mc.world.getBlockState(pos).getHardness(mc.world, pos);

        if (hardness < 0.0F) {
            return 0.0F;
        }
        return hardness;
    }

    @EventHandler
    private void onBlockHit(TickEvent event) {
        if (event.isPre()) return;

        if (mc.player != null)
        {
            if (mc.crosshairTarget == null || !(mc.crosshairTarget instanceof BlockHitResult)) return;
            BlockHitResult Blockhit = (BlockHitResult) mc.crosshairTarget;
            BlockPos BlockPos = Blockhit.getBlockPos();

            if (mc.crosshairTarget instanceof BlockHitResult) {
                if (getToolHotbar(BlockPos) != -1 && mc.options.attackKey.isPressed()) {
                    previousSlot.add(mc.player.getInventory().selectedSlot);

                    mc.player.getInventory().selectedSlot = getToolHotbar(BlockPos);

                    itemslot = getToolHotbar(BlockPos);
                    send = true;

                } else if (!previousSlot.isEmpty() && swapBack.getValue()) {
                    if (send) {
                        mc.player.getInventory().selectedSlot = previousSlot.get(0);

                        itemslot = previousSlot.get(0);
                        previousSlot.clear();
                        send = false;
                    }
                }
            }
        }
    }
}