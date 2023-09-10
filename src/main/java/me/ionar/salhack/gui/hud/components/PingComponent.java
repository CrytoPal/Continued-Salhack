package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Objects;

public class PingComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    private final int i = 0;
    public PingComponent() {
        super("Ping", 2, 43);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);

        if (mc.world != null) {
            PlayerListEntry playerListEntry = Objects.requireNonNull(mc.player).networkHandler.getPlayerListEntry(mc.player.getUuid());

            final String ping = "Ping " + Formatting.WHITE + playerListEntry.getLatency();

            if (HudModule.customFont.getValue()) {
                FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), ping, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor(), true);
            } else {
                context.drawTextWithShadow(mc.textRenderer, Text.of(ping), (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : getTextColor());
            }
            Rainbow.onRender();
            setWidth(mc.textRenderer.getWidth(ping));
            setHeight(mc.textRenderer.fontHeight);
        }
    }
}