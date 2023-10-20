package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ArmorHudComponent extends HudComponentItem {
    private String durabilityHead;
    private String durabilityChest;
    private String durabilityLegging;
    private String durabilityBoot;

    private ItemStack Head;
    private ItemStack Chest;
    private ItemStack Legging;
    private ItemStack Boots;

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]
            {"Mode"}, "Mode of displaying coordinates", Mode.Under);
    public final Value<Boolean> ArmorPercentage = new Value<Boolean>("Armor Percentage", new String[]{ "AP" }, "Shows Armor Percentage", false);
    public ArmorHudComponent() {
        super("ArmorHud", 2, 160);
    }

    private enum Mode {
        Above,
        Under
    }
    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);
        if (mc.player != null) {
            if (!mc.player.getInventory().getArmorStack(3).isEmpty()) {
                // Gets the Armor from the player
                Head = mc.player.getInventory().getArmorStack(3);
                // Renders the Armor
                context.drawItem(Head, (int) GetX() - 5, (int) GetY() - 5);
                if (mode.getValue() == Mode.Under) {
                    context.drawItemInSlot(mc.textRenderer, Head, (int) GetX() - 5, (int) GetY() - 5);
                } else {
                    context.drawItemInSlot(mc.textRenderer, Head, (int) GetX() - 5, (int) GetY() - 17);
                }
                if (ArmorPercentage.getValue()) {
                    // Calculates the Health Percentage
                    durabilityHead = Formatting.GREEN + "" + ((Head.getMaxDamage() - Head.getDamage()) * 100 / Head.getMaxDamage());
                    // Renders the Health Percentage
                    context.drawTextWithShadow(mc.textRenderer, Text.of(durabilityHead), (int) GetX() - 3, (int) GetY() - 15, GetTextColor());
                }
            }
            if (!mc.player.getInventory().getArmorStack(2).isEmpty()) {
                Chest = mc.player.getInventory().getArmorStack(2);
                context.drawItem(Chest, (int) GetX() + 15, (int) GetY() - 5);
                if (mode.getValue() == Mode.Under) {
                    context.drawItemInSlot(mc.textRenderer, Chest, (int) GetX() + 15, (int) GetY() - 5);
                } else {
                    context.drawItemInSlot(mc.textRenderer, Chest, (int) GetX() + 15, (int) GetY() - 17);
                }
                if (ArmorPercentage.getValue()) {
                    durabilityChest = Formatting.GREEN + "" + ((Chest.getMaxDamage() - Chest.getDamage()) * 100 / Chest.getMaxDamage());
                    context.drawTextWithShadow(mc.textRenderer, Text.of(durabilityChest), (int) GetX() + 17, (int) GetY() - 15, GetTextColor());
                }
            }
            if (!mc.player.getInventory().getArmorStack(1).isEmpty()) {
                Legging = mc.player.getInventory().getArmorStack(1);
                context.drawItem(Legging, (int) GetX() + 35, (int) GetY() - 5);
                if (mode.getValue() == Mode.Under) {
                    context.drawItemInSlot(mc.textRenderer, Legging, (int) GetX() + 35, (int) GetY() - 5);
                } else {
                    context.drawItemInSlot(mc.textRenderer, Legging, (int) GetX() + 35, (int) GetY() - 17);
                }
                if (ArmorPercentage.getValue()) {
                    durabilityLegging = Formatting.GREEN + "" + ((Legging.getMaxDamage() - Legging.getDamage()) * 100 / Legging.getMaxDamage());
                    context.drawTextWithShadow(mc.textRenderer, Text.of(durabilityLegging), (int) GetX() + 37, (int) GetY() - 15, GetTextColor());
                }
            }
            if (!mc.player.getInventory().getArmorStack(0).isEmpty()) {
                Boots = mc.player.getInventory().getArmorStack(0);
                context.drawItem(Boots, (int) GetX() + 55, (int) GetY() - 5);
                if (mode.getValue() == Mode.Under) {
                    context.drawItemInSlot(mc.textRenderer, Boots, (int) GetX() + 55, (int) GetY() - 5);
                } else {
                    context.drawItemInSlot(mc.textRenderer, Boots, (int) GetX() + 55, (int) GetY() - 17);
                }
                if (ArmorPercentage.getValue()) {
                    durabilityBoot = Formatting.GREEN + "" + ((Boots.getMaxDamage() - Boots.getDamage()) * 100 / Boots.getMaxDamage());
                    context.drawTextWithShadow(mc.textRenderer, Text.of(durabilityBoot), (int) GetX() + 57, (int) GetY() - 15, GetTextColor());
                }
            }
        } else {
            context.drawItem(Items.NETHERITE_HELMET.getDefaultStack(), (int) GetX() - 5, (int) GetY() - 5);
            context.drawItem(Items.NETHERITE_CHESTPLATE.getDefaultStack(), (int) GetX() + 15, (int) GetY() - 5);
            context.drawItem(Items.NETHERITE_LEGGINGS.getDefaultStack(), (int) GetX() + 35, (int) GetY() - 5);
            context.drawItem(Items.NETHERITE_BOOTS.getDefaultStack(), (int) GetX() + 55, (int) GetY() - 5);
        }
        SetWidth(60);
        SetHeight(8);
    }

    public static float GetPctFromStack(ItemStack p_Stack)
    {
        float l_ArmorPct = ((float)(p_Stack.getMaxDamage()-p_Stack.getDamage()) /  (float)p_Stack.getMaxDamage())*100.0f;
        float l_ArmorBarPct = Math.min(l_ArmorPct, 100.0f);

        return l_ArmorBarPct;
    }
}