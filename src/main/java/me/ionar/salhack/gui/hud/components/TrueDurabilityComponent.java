package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TrueDurabilityComponent extends HudComponentItem {
    public TrueDurabilityComponent() {
        super("TrueDurability", 2, 113);
    }

    private String durability;

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        ItemStack stack = mc.player.getMainHandStack();

        if (!stack.isEmpty() && (stack.getItem() instanceof ToolItem || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof SwordItem)) {
            durability = "Durability: " + Formatting.GREEN + (stack.getMaxDamage() - stack.getDamage());

        } else {
            durability = "Durability:";
        }

        context.drawTextWithShadow(mc.textRenderer, Text.of(durability), (int) GetX(), (int) GetY(), GetTextColor());
        SetWidth(Wrapper.GetMC().textRenderer.getWidth(durability));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }
}