package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Calendar;

public class WelcomerHudComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);

    MinecraftClient mc = MinecraftClient.getInstance();
    private static String WatermarkString = "";

    Calendar c = Calendar.getInstance();

    public WelcomerHudComponent() {
        super("Welcomer", 415, 2);
        SetHidden(false);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 12) {
            WatermarkString = "Good Morning, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            WatermarkString = "Good Afternoon, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 17 && timeOfDay < 22) {
            WatermarkString = "Good Evening, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 22 || timeOfDay < 6) {
            WatermarkString = "Good Night, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else {
            WatermarkString = "Hello, " + Formatting.WHITE + mc.getSession().getUsername() + ".. psst! something went wrong!" + Formatting.AQUA + " :(";
        }

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), WatermarkString, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(WatermarkString), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.onRender();
        SetWidth(mc.textRenderer.getWidth(WatermarkString));
        SetHeight(mc.textRenderer.fontHeight);
    }
}
