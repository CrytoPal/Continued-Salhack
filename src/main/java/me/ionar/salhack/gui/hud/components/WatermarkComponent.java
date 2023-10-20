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
import net.minecraft.util.Identifier;

public class WatermarkComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public final Value<Boolean> Image = new Value<Boolean>("Image", new String[]
            { "" }, "Shows Image instead of SalHack text", false);

    private static String WatermarkString = SalHackMod.NAME + Formatting.WHITE + " " + SalHackMod.VERSION;

    private static final Identifier img = new Identifier("salhack","icon.png");

    public WatermarkComponent() {
        super("Watermark", 2, 2);
        SetHidden(false);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);

        if (!(Image.getValue())) {
            if (HudModule.CustomFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), WatermarkString, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(WatermarkString), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
            }
            Rainbow.OnRender();
            SetWidth(mc.textRenderer.getWidth(WatermarkString));
            SetHeight(mc.textRenderer.fontHeight);
        } else {
            context.drawTexture(img, (int) GetX(), (int) GetY(), 0, 0, 0, 175, 90, 175, 90);
            SetWidth(175);
            SetHeight(90);
        }
    }
}
