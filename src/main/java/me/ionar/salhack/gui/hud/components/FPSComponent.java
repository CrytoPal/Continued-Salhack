package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.mixin.MinecraftClientAccessor;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class FPSComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public FPSComponent() {
        super("FPS", 2, 23);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        final String fPS = "FPS " + Formatting.WHITE + MinecraftClientAccessor.getCurrentFps();
        if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), fPS, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
        else context.drawTextWithShadow(mc.textRenderer, Text.of(fPS), (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
        rainbow.onRender();
        setWidth(mc.textRenderer.getWidth(fPS));
        setHeight(mc.textRenderer.fontHeight);
    }
}