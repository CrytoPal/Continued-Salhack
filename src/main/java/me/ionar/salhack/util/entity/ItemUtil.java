package me.ionar.salhack.util.entity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;

public class ItemUtil
{
    public static boolean Is32k(ItemStack p_Stack)
    {
        if (p_Stack.getEnchantments() != null)
        {
            final NbtList tags = p_Stack.getEnchantments();
            for (int i = 0; i < tags.size(); i++)
            {
                final NbtCompound tagCompound = tags.getCompound(i);
                if (tagCompound != null && Enchantment.byRawId(tagCompound.getByte("id")) != null)
                {
                    final Enchantment enchantment = Enchantment.byRawId(tagCompound.getShort("id"));
                    final short lvl = tagCompound.getShort("lvl");
                    if (enchantment != null)
                    {
                        if (enchantment.isCursed()) continue;

                        if (lvl >= 1000)
                            return true;
                    }
                }
            }
        }
        return false;
    }
}
