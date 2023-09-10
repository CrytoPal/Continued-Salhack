package me.ionar.salhack.gui.hud.components;

import java.text.DecimalFormat;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class RotationComponent extends HudComponentItem {

    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);

    public final Value<Boolean> Yaw = new Value<Boolean>("Yaw", new String[]{ "Y" }, "Include Yaw", true);

    public final Value<Boolean> Pitch = new Value<Boolean>("Pitch", new String[]{ "P" }, "Include Pitch", true);
    public RotationComponent()
    {
        super("Rotation", 2, 103);
        setHidden(false);
    }

    private String direction;

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context)
    {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);

        DecimalFormat l_Format = new DecimalFormat("#.##");
        float l_Yaw = MathHelper.wrapDegrees(mc.player.getBodyYaw());
        float l_Pitch = MathHelper.wrapDegrees(mc.player.getPitch());

        String Yaw2 = l_Format.format(l_Yaw);
        String Pitch2 = l_Format.format(l_Pitch);

        if (Yaw.getValue() && !Pitch.getValue()) {
            direction = "Rotation: " + Formatting.WHITE + Yaw2;
        }
        if (Pitch.getValue() && !Yaw.getValue()) {
            direction = "Rotation: " + Formatting.WHITE + Pitch2;
        }
        if (Pitch.getValue() && Yaw.getValue()) {
            direction = "Rotation: " + Formatting.WHITE + Yaw2 + ", " + Pitch2;
        }
        if (!Pitch.getValue() && !Yaw.getValue()) {
            direction = "Rotation: ";
        }

        if (!Yaw2.contains("."))
            Yaw2 += ".00";
        else
        {
            String[] l_Split = Yaw2.split("\\.");

            if (l_Split != null && l_Split[1] != null && l_Split[1].length() != 2)
                Yaw2 += 0;
        }
        if (!Pitch2.contains("."))
            Pitch2 += "0";
        else
        {
            String[] l_Split = Pitch2.split("\\.");

            if (l_Split != null && l_Split[1] != null && l_Split[1].length() != 2)
                Pitch2 += 0;
        }

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), direction, (int) (getPositionX()), (int) (getPositionY()), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(direction), (int) getPositionX(), (int) getPositionY(), hud.Rainbow.getValue() ? Rainbow.getRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }
        Rainbow.onRender();
        setWidth(Wrapper.GetMC().textRenderer.getWidth(direction));
        setHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

}