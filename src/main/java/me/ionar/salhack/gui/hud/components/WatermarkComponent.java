package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class WatermarkComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public final Value<Boolean> Reliant = new Value<Boolean>("Reliant", new String[]
            { "" }, "Shows reliant text instead of salhack", false);

    private static String WatermarkString = SalHackMod.NAME + Formatting.WHITE + " " + SalHackMod.VERSION;

    public WatermarkComponent() {
        super("Watermark", 2, 2);
        SetHidden(false);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);

        if (Reliant.getValue()) {
            final String l_Text = "Reliant (rel-1.20.1-Fabric)";

            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), l_Text, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(l_Text), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
            Rainbow.OnRender();
            SetWidth(mc.textRenderer.getWidth(l_Text));
            SetHeight(mc.textRenderer.fontHeight);
        }
        else {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), WatermarkString, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(WatermarkString), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
            Rainbow.OnRender();
            SetWidth(mc.textRenderer.getWidth(WatermarkString));
            SetHeight(mc.textRenderer.fontHeight);
        }
    }
}
