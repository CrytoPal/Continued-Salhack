package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ArmorHudComponent extends HudComponentItem {
    public final Value<modes> mode = new Value<>("Mode", new String[]{"Mode"}, "Mode of displaying coordinates", modes.Under);
    public final Value<Boolean> ArmorPercentage = new Value<>("Armor Percentage", new String[]{"AP"}, "Shows Armor Percentage", false);
    public final Value<armorRenderModes> armorMode = new Value<>("Render Mode", new String[]{"line"}, "mode for armor percentage", armorRenderModes.Number);
    public ArmorHudComponent() {
        super("ArmorHud", 2, 160);
    }
    public enum armorRenderModes {
        Line,
        Number
    }
    public enum modes {
        Above,
        Under
    }
    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        if (mc.player != null) {
            super.onRender(mouseX, mouseY, partialTicks, context);
            for (int slot = 0; slot<4; slot++) {
                System.out.println(slot);
                if (!mc.player.getInventory().getArmorStack(slot).isEmpty()) {
                    ItemStack armor = mc.player.getInventory().getArmorStack(slot);
                    int offset = (20 * slot+1);
                    context.drawItem(armor, (int) getPositionX() + offset + 5, (int) getPositionY() - 5);
                    context.drawItemInSlot(mc.textRenderer, armor, (int) getPositionX() + offset + 5, (int) getPositionY() - 5);
                    if (ArmorPercentage.getValue()) {
                        if (armorMode.getValue() == armorRenderModes.Line) {
                            int remaining = (int)getPctFromStack(armor);
                            int bad = (100-remaining)/10;
                            int good = remaining/10;
                            if (mode.getValue() == modes.Above) {
                                context.fill(offset, (int)getPositionY()-10, offset+bad, (int)getPositionY()-15, Colors.RED);
                                context.fill(offset+bad, (int)getPositionY()-10, offset+bad+good, (int)getPositionY()-15, new Color(0x00ff00, false).getRGB());
                            } else if (mode.getValue() == modes.Under) {
                                context.fill(offset, (int)getPositionY()+10, offset+bad, (int)getPositionY()+20, Colors.RED);
                                context.fill(offset+bad, (int)getPositionY()+10, offset+bad+good, (int)getPositionY()+20, new Color(0x00ff00, false).getRGB());
                            }
                        } else if (armorMode.getValue() == armorRenderModes.Number){
                            String durability = Formatting.GREEN + "" + ((armor.getMaxDamage() - armor.getDamage()) * 100 / armor.getMaxDamage());
                            if (mode.getValue() == modes.Above) context.drawTextWithShadow(mc.textRenderer, Text.of(durability), (int) getPositionX() + ((20 * slot+1)+3), (int) getPositionY()-15, getTextColor());
                            else if (mode.getValue() == modes.Under) context.drawTextWithShadow(mc.textRenderer, Text.of(durability), (int) getPositionX() + ((-20 * slot+1)+3), (int) getPositionY()+15, getTextColor());
                        }
                    }
                }
            }
            setWidth(70);
            setHeight(10);
        }
    }

    public static float getPctFromStack(ItemStack stack) {
        float armorPercent = ((float)(stack.getMaxDamage()-stack.getDamage()) /  (float)stack.getMaxDamage())*100.0f;
        return Math.min(armorPercent, 100.0f);
    }
}