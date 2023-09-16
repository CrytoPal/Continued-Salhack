package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.mixin.MinecraftClientAccessor;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FPSComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);

    MinecraftClient mc = MinecraftClient.getInstance();
    public FPSComponent() {
        super("FPS", 2, 23);
        SetHidden(false);
    }



    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);



        final String fPS = "FPS " + Formatting.WHITE + MinecraftClientAccessor.getCurrentFps();

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), fPS, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(fPS), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.onRender();
        SetWidth(mc.textRenderer.getWidth(fPS));
        SetHeight(mc.textRenderer.fontHeight);
    }

}