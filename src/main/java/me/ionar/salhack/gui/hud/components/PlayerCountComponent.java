package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class PlayerCountComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public PlayerCountComponent() {
        super("PlayerCount", 2, 133);
    }

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);

        final String playerCount = "Players: " + Formatting.WHITE + mc.world.getPlayers().size();

        setWidth(Wrapper.GetMC().textRenderer.getWidth(playerCount));
        setHeight(Wrapper.GetMC().textRenderer.fontHeight);

        if (HudModule.customFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), playerCount, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, playerCount, (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor());
        }
    }

}