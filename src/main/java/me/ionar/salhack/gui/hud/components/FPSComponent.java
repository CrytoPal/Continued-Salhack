package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.mixin.MinecraftClientAccessor;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FPSComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);

    MinecraftClient mc = MinecraftClient.getInstance();
    public FPSComponent() {
        super("FPS", 2, 23);
        setHidden(false);
    }



    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);



        final String fPS = "FPS " + Formatting.WHITE + MinecraftClientAccessor.getCurrentFps();

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), fPS, (int) (getPositionX()), (int) (getPositionY()), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(fPS), (int) getPositionX(), (int) getPositionY(), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.onRender();
        setWidth(Wrapper.GetMC().textRenderer.getWidth(fPS));
        setHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

}