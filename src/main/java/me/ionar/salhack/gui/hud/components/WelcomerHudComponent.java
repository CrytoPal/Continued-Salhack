package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Calendar;

public class WelcomerHudComponent extends HudComponentItem {
    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    MinecraftClient mc = Wrapper.GetMC();
    Calendar c = Calendar.getInstance();

    public WelcomerHudComponent() {
        super("Welcomer", 415, 2);
        setHidden(false);
    }

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        String watermarkString;
        if (timeOfDay >= 6 && timeOfDay < 12) {
            watermarkString = "Good Morning, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            watermarkString = "Good Afternoon, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 17 && timeOfDay < 22) {
            watermarkString = "Good Evening, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else {
            watermarkString = "Good Night, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        }

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), watermarkString, (int) (getPositionX()), (int) (getPositionY()), HudModule.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(watermarkString), (int) getPositionX(), (int) getPositionY(), HudModule.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.onRender();
        setWidth(Wrapper.GetMC().textRenderer.getWidth(watermarkString));
        setHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }
}
