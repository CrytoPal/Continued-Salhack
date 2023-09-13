package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class RotationComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public final Value<Boolean> yaw = new Value<>("Yaw", new String[]{"Y"}, "Include Yaw", true);
    public final Value<Boolean> pitch = new Value<>("Pitch", new String[]{"P"}, "Include Pitch", true);
    private String direction;
    public RotationComponent() {
        super("Rotation", 2, 103);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.player == null) return;
        DecimalFormat format = new DecimalFormat("#.##");
        float yaw = MathHelper.wrapDegrees(mc.player.getBodyYaw());
        float pitch = MathHelper.wrapDegrees(mc.player.getPitch());
        String Yaw2 = format.format(yaw);
        String Pitch2 = format.format(pitch);
        if (this.yaw.getValue() && !this.pitch.getValue()) direction = "Rotation: " + Formatting.WHITE + Yaw2;
        if (this.pitch.getValue() && !this.yaw.getValue()) direction = "Rotation: " + Formatting.WHITE + Pitch2;
        if (this.pitch.getValue() && this.yaw.getValue()) direction = "Rotation: " + Formatting.WHITE + Yaw2 + ", " + Pitch2;
        if (!this.pitch.getValue() && !this.yaw.getValue()) direction = "Rotation: ";
        if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), direction, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
        else context.drawTextWithShadow(mc.textRenderer, Text.of(direction), (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
        rainbow.onRender();
        setWidth(mc.textRenderer.getWidth(direction));
        setHeight(mc.textRenderer.fontHeight);
    }
}