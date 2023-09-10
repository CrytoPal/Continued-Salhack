package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ArmorHudComponent extends HudComponentItem {
    public final Value<modes> mode = new Value<>("Mode", new String[]{"Mode"}, "Mode of displaying coordinates", modes.Under);
    public final Value<Boolean> ArmorPercentage = new Value<>("Armor Percentage", new String[]{"AP"}, "Shows Armor Percentage", false);
    public ArmorHudComponent() {
        super("ArmorHud", 2, 160);
    }
    public enum modes {
        Above,
        Under
    }
    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);
        if (mc.player != null) {
            for (int slot = 0; slot<4; slot++) {
                if (!mc.player.getInventory().getArmorStack(slot).isEmpty()) {
                    ItemStack armor = mc.player.getInventory().getArmorStack(slot);
                    int offset = getXOffSetFromSlot(slot, false);
                    context.drawItem(armor, (int) getPositionX() - offset, (int) getPositionY() - 5);
                    if (mode.getValue() == modes.Under) context.drawItemInSlot(mc.textRenderer, armor, (int) getPositionX() - offset, (int) getPositionY() - 5);
                    else context.drawItemInSlot(mc.textRenderer, armor, (int) getPositionX() - offset, (int) getPositionY() - 17);
                    if (ArmorPercentage.getValue()) {
                        String durability = Formatting.GREEN + "" + ((armor.getMaxDamage() - armor.getDamage()) * 100 / armor.getMaxDamage());
                        context.drawTextWithShadow(mc.textRenderer, Text.of(durability), (int) getPositionX() - getXOffSetFromSlot(slot, true), (int) getPositionY() - 15, getTextColor());
                    }
                }
            }
            setWidth(60);
            setHeight(8);
        }
    }
    private int getXOffSetFromSlot(int slot, boolean armorPercentage) {
        if (armorPercentage) return slot == 0 ? 3 : (20 * slot) - 3;
        else return slot == 0? 5 : (20 * slot) - 5;
    }
}
