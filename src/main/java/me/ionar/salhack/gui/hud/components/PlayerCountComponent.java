package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class PlayerCountComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public PlayerCountComponent() {
        super("PlayerCount", 2, 133);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.world == null) return;
        final String playerCount = "Players: " + Formatting.WHITE + mc.world.getPlayers().size();
        setWidth(mc.textRenderer.getWidth(playerCount));
        setHeight(mc.textRenderer.fontHeight);
        if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), playerCount, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
        else context.drawTextWithShadow(mc.textRenderer, playerCount, (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
    }
}