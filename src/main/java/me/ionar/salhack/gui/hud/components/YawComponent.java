package me.ionar.salhack.gui.hud.components;

import java.text.DecimalFormat;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class YawComponent extends HudComponentItem
{
    public YawComponent()
    {
        super("Yaw", 2, 103);
    }

    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context)
    {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);

        DecimalFormat l_Format = new DecimalFormat("#.##");
        float l_Yaw = MathHelper.wrapDegrees(mc.player.getBodyYaw());

        String direction = "Yaw: " + Formatting.WHITE + l_Format.format(l_Yaw);

        if (!direction.contains("."))
            direction += ".00";
        else
        {
            String[] l_Split = direction.split("\\.");

            if (l_Split != null && l_Split[1] != null && l_Split[1].length() != 2)
                direction += 0;
        }

        context.drawTextWithShadow(mc.textRenderer, Text.of(direction), (int) GetX(), (int) GetY(), GetTextColor());

        SetWidth(Wrapper.GetMC().textRenderer.getWidth(direction));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

}