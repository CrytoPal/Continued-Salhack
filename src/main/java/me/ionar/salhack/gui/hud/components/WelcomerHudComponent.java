package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.events.MinecraftEvent;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.Calendar;

public class WelcomerHudComponent extends HudComponentItem
{

    MinecraftClient mc = MinecraftClient.getInstance();
    private static String WatermarkString = "";

    Calendar c = Calendar.getInstance();

    public WelcomerHudComponent()
    {
        super("Welcomer", 415, 2);
        SetHidden(false);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        if (timeOfDay >= 6 && timeOfDay < 12) {
            WatermarkString = Formatting.AQUA + "Good Morning, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 12 && timeOfDay < 17) {
            WatermarkString = Formatting.AQUA + "Good Afternoon, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 17 && timeOfDay < 22) {
            WatermarkString = Formatting.AQUA + "Good Evening, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else if (timeOfDay >= 22 || timeOfDay < 6) {
            WatermarkString = Formatting.AQUA + "Good Night, " + Formatting.WHITE + mc.getSession().getUsername() + Formatting.AQUA + " :)";
        } else {
            WatermarkString = Formatting.AQUA + "Hello, " + Formatting.WHITE + mc.getSession().getUsername() + ".. psst! something went wrong!" + Formatting.AQUA + " :(";
        }

            context.drawTextWithShadow(mc.textRenderer, Text.of(WatermarkString), (int) GetX(), (int) GetY(), GetTextColor());

            SetWidth(Wrapper.GetMC().textRenderer.getWidth(WatermarkString));
            SetHeight(Wrapper.GetMC().textRenderer.fontHeight);

    }
}
