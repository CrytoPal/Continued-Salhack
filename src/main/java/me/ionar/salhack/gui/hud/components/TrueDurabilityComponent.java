package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TrueDurabilityComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public TrueDurabilityComponent() {
        super("TrueDurability", 2, 113);
        setHidden(false);
    }


    private String durability;

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);

        ItemStack stack = mc.player.getMainHandStack();

        if (!stack.isEmpty() && (stack.getItem() instanceof ToolItem || stack.getItem() instanceof ArmorItem || stack.getItem() instanceof SwordItem)) {
            durability = "Durability: " + Formatting.GREEN + (stack.getMaxDamage() - stack.getDamage());

        } else {
            durability = "Durability:";
        }

        if (HudModule.customFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), durability, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(durability), (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor());
        }
        Rainbow.onRender();
        setWidth(Wrapper.GetMC().textRenderer.getWidth(durability));
        setHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }
}