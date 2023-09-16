package me.ionar.salhack.util.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.SlotActionType;

import static me.ionar.salhack.main.Wrapper.mc;

public class ItemUtil {
    public static boolean is32K(ItemStack item) {
        if (item.getEnchantments() != null) {
            final NbtList tags = item.getEnchantments();
            for (int i = 0; i < tags.size(); i++) {
                final NbtCompound tagCompound = tags.getCompound(i);
                if (tagCompound != null && Enchantment.byRawId(tagCompound.getByte("id")) != null) {
                    final Enchantment enchantment = Enchantment.byRawId(tagCompound.getShort("id"));
                    final short lvl = tagCompound.getShort("lvl");
                    if (enchantment != null) {
                        if (enchantment.isCursed()) continue;
                        if (lvl >= 1000) return true;
                    }
                }
            }
        }
        return false;
    }

    public static void move(int slotFrom, int slotTo) {
        if (mc.player == null || mc.interactionManager == null) return;
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotFrom, 0, SlotActionType.PICKUP, mc.player);
        mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, slotTo, 0, SlotActionType.PICKUP, mc.player);
    }
}
