package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Formatting;

public class PlayerCountComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    private String playerCount;
    public PlayerCountComponent() {
        super("PlayerCount", 2, 133);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);

        if (mc.player != null) {
           playerCount = "Players: " + Formatting.WHITE + mc.world.getPlayers().size();
        } else playerCount = "Players: ";

        SetWidth(mc.textRenderer.getWidth(playerCount));
        SetHeight(mc.textRenderer.fontHeight);

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), playerCount, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, playerCount, (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
    }

}