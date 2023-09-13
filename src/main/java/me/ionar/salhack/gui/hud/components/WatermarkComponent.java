package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WatermarkComponent extends HudComponentItem {
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public final Value<Boolean> reliant = new Value<>("Reliant", new String[]{""}, "Shows reliant text instead of salhack", false);
    private static final String watermarkString = SalHackMod.NAME + Formatting.WHITE + " " + SalHackMod.VERSION;

    public WatermarkComponent() {
        super("Watermark", 2, 2);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);

        if (reliant.getValue()) {
            final String text = "Reliant (rel-1.20.1-Fabric)";
            if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), text, (int) (getPositionX()), (int) (getPositionY()), HudModule.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
            else context.drawTextWithShadow(mc.textRenderer, Text.of(text), (int) getPositionX(), (int) getPositionY(), HudModule.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
            rainbow.onRender();
            setWidth(mc.textRenderer.getWidth(text));
            setHeight(mc.textRenderer.fontHeight);
        }
        else {
            if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), watermarkString, (int) (getPositionX()), (int) (getPositionY()), HudModule.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
            else context.drawTextWithShadow(mc.textRenderer, Text.of(watermarkString), (int) getPositionX(), (int) getPositionY(), HudModule.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
            rainbow.onRender();
            setWidth(mc.textRenderer.getWidth(watermarkString));
            setHeight(mc.textRenderer.fontHeight);
        }
    }
}
