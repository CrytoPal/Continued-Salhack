package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.Timer;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

import java.text.DecimalFormat;

public class SpeedComponent extends HudComponentItem {
    public final Value<unitList> speedUnit = new Value<>("Speed Unit", new String[]{"SpeedUnit"}, "Unit of speed. Note that 1 metre = 1 block", unitList.BPS);
    final DecimalFormat formatterBPS = new DecimalFormat("#.#");
    final DecimalFormat formatterKMH = new DecimalFormat("#.#");
    private double prevPosX;
    private double prevPosZ;
    private final Timer timer = new Timer();
    private String speed = "";
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public SpeedComponent() {
        super("Speed", 2, 93);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.player == null) return;
        if (timer.passed(1000)) {
            prevPosX = mc.player.prevX;
            prevPosZ = mc.player.prevZ;
        }
        final double deltaX = mc.player.getX() - prevPosX;
        final double deltaZ = mc.player.getZ() - prevPosZ;
        float distance = MathHelper.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));
        double bPS = distance * 20;
        double kMH = Math.floor((distance / 1000.0f) / (0.05f / 3600.0f));
        if (speedUnit.getValue() == unitList.BPS) {
            String formatterBPS = this.formatterBPS.format(bPS);
            speed = "Speed: " + Formatting.WHITE + formatterBPS + " BPS";
        } else if (speedUnit.getValue() == unitList.KMH) {
            String formatterKMH = this.formatterKMH.format(kMH);
            speed = hud.rainbow.getValue() ? "Speed: " + Formatting.WHITE + formatterKMH + "km/h" : "Speed " + Formatting.WHITE + formatterKMH + "km/h";
        }
        if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), speed, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
        else context.drawTextWithShadow(mc.textRenderer, Text.of(speed), (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
        rainbow.onRender();
        setWidth(mc.textRenderer.getWidth(speed));
        setHeight(mc.textRenderer.fontHeight);
    }

    public enum unitList {
        BPS,
        KMH,
    }
}