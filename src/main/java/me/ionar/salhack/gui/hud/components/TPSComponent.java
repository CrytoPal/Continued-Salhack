package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.managers.TickRateManager;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class TPSComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    private final int i = 0;
    public TPSComponent() {
        super("TPS", 2, 33);
        SetHidden(false);
    }

    final DecimalFormat Formatter = new DecimalFormat("#.#");

    public String format(double input) {
        String result = Formatter.format(input);

        if (!result.contains("."))
            result += ".0";

        return result;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        final String tickrate = "TPS " + Formatting.WHITE +  format(TickRateManager.Get().getTickRate());

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), tickrate, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(tickrate), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.OnRender();
        SetWidth(Wrapper.GetMC().textRenderer.getWidth(tickrate));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

}