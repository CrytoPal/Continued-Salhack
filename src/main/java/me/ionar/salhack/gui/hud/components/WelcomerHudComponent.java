package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Calendar;

public class WelcomerHudComponent extends HudComponentItem {
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    Calendar calendar = Calendar.getInstance();

    public WelcomerHudComponent() {
        super("Welcomer", 415, 2);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        int timeOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        String watermarkString;
        if (timeOfDay >= 6 && timeOfDay < 12) watermarkString = "Good Morning, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        else if (timeOfDay >= 12 && timeOfDay < 17) watermarkString = "Good Afternoon, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        else if (timeOfDay >= 17 && timeOfDay < 22) watermarkString = "Good Evening, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        else watermarkString = "Good Night, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), watermarkString, (int) (getPositionX()), (int) (getPositionY()), HudModule.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
        else context.drawTextWithShadow(mc.textRenderer, Text.of(watermarkString), (int) getPositionX(), (int) getPositionY(), HudModule.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
        rainbow.onRender();
        setWidth(mc.textRenderer.getWidth(watermarkString));
        setHeight(mc.textRenderer.fontHeight);
    }
}
