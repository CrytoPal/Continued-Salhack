package me.ionar.salhack.util.entity;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class PlayerUtil {
    final static DecimalFormat Formatter = new DecimalFormat("#.#");
    private static final MinecraftClient mc = MinecraftClient.getInstance();

    public static int GetItemSlot(Item input) {
        if (mc.player == null)
            return 0;

        for (int i = 0; i < mc.player.getInventory().size(); ++i) {
            if (i == 0 || i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack s = mc.player.getInventory().getStack(i);

            if (s.isEmpty())
                continue;

            if (s.getItem() == input) {
                return i;
            }
        }
        return -1;
    }

    public static int GetRecursiveItemSlot(Item input) {
        if (mc.player == null)
            return 0;

        for (int i = mc.player.getInventory().size() - 1; i > 0; --i) {
            if (i == 5 || i == 6 || i == 7 || i == 8)
                continue;

            ItemStack s = mc.player.getInventory().getStack(i);

            if (s.isEmpty())
                continue;

            if (s.getItem() == input) {
                return i;
            }
        }
        return -1;
    }

    public static int GetItemSlotNotHotbar(Item input) {
        if (mc.player == null)
            return 0;

        for (int i = 9; i < 36; i++) {
            final Item item = mc.player.getInventory().getStack(i).getItem();
            if (item == input) {
                return i;
            }
        }
        return -1;
    }

    public static int GetItemCount(Item input) {
        if (mc.player == null)
            return 0;

        int items = 0;

        for (int i = 0; i < 45; i++) {
            final ItemStack stack = mc.player.getInventory().getStack(i);
            if (stack.getItem() == input) {
                items += stack.getCount();
            }
        }

        return items;
    }


    public static boolean IsEating() {
        return mc.player != null && mc.player.getMainHandStack().getItem().isFood() && mc.player.isUsingItem();
    }

    public static int GetItemInHotbar(Item item) {
        if (mc.player == null) return -1;
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.getInventory().getStack(i);

            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() == item) {
                    return i;
                }
            }
        }

        return -1;
    }

    public static BlockPos GetLocalPlayerPosFloored() {
        if (mc.player == null) return new BlockPos(-1, -1, -1);
        return new BlockPos((int) Math.floor(mc.player.getX()), (int) Math.floor(mc.player.getY()), (int) Math.floor(mc.player.getZ()));
    }

    public static BlockPos EntityPosToFloorBlockPos(Entity e) {
        return new BlockPos((int) Math.floor(e.getX()), (int) Math.floor(e.getY()), (int) Math.floor(e.getZ()));
    }

    public static float GetHealthWithAbsorption() {
        if (mc.player == null) return -1;
        return mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public static FacingDirection GetFacing() {
        if (mc.player == null) return FacingDirection.North;
        return switch (MathHelper.floor((double) (mc.player.getYaw() * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0, 1 -> FacingDirection.South;
            case 2, 3 -> FacingDirection.West;
            case 6, 7 -> FacingDirection.East;
            default -> FacingDirection.North;
        };
    }

    public static float getSpeedInKM() {
        if (mc.player == null) return 0f;
        final double deltaX = mc.player.getX() - mc.player.prevX;
        final double deltaZ = mc.player.getZ() - mc.player.prevZ;

        float distance = MathHelper.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));

        double kMH = Math.floor((distance / 1000.0f) / (0.05f / 3600.0f));

        String formatter = Formatter.format(kMH);

        if (!formatter.contains("."))
            formatter += ".0";

        return Float.valueOf(formatter);
    }

    public enum FacingDirection {
        North,
        South,
        East,
        West,
    }
}