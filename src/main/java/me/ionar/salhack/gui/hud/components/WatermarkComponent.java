package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WatermarkComponent extends HudComponentItem {

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public final Value<Boolean> Reliant = new Value<>("Reliant", new String[]{""}, "Shows reliant text instead of salhack", false);

    private static final String WatermarkString = SalHackMod.NAME + Formatting.WHITE + " " + SalHackMod.VERSION;

    public WatermarkComponent() {
        super("Watermark", 2, 2);
        setHidden(false);
    }

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);

        if (Reliant.getValue()) {
            final String l_Text = "Reliant (rel-1.20.1-Fabric)";

            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), l_Text, (int) (getPositionX()), (int) (getPositionY()), HudModule.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(l_Text), (int) getPositionX(), (int) getPositionY(), HudModule.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
            Rainbow.onRender();
            setWidth(Wrapper.GetMC().textRenderer.getWidth(l_Text));
            setHeight(Wrapper.GetMC().textRenderer.fontHeight);
        }
        else {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), WatermarkString, (int) (getPositionX()), (int) (getPositionY()), HudModule.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(WatermarkString), (int) getPositionX(), (int) getPositionY(), HudModule.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
            Rainbow.onRender();
            setWidth(Wrapper.GetMC().textRenderer.getWidth(WatermarkString));
            setHeight(Wrapper.GetMC().textRenderer.fontHeight);
        }
    }
}
