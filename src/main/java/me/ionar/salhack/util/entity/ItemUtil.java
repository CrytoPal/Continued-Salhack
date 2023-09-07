package me.ionar.salhack.util.entity;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.screen.slot.SlotActionType;

public class ItemUtil {
    public static boolean Is32k(ItemStack item) {
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

    public static void Move(int slot_from, int slot_to) {
        Wrapper.GetMC().interactionManager.clickSlot(Wrapper.GetMC().player.currentScreenHandler.syncId, slot_from, 0, SlotActionType.PICKUP, Wrapper.GetMC().player);
        Wrapper.GetMC().interactionManager.clickSlot(Wrapper.GetMC().player.currentScreenHandler.syncId, slot_to, 0, SlotActionType.PICKUP, Wrapper.GetMC().player);
    }
}
