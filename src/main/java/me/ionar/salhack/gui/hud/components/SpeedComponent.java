package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
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
    public final Value<UnitList> SpeedUnit = new Value<UnitList>("Speed Unit", new String[]{"SpeedUnit"}, "Unit of speed. Note that 1 metre = 1 block", UnitList.BPS);
    final DecimalFormat FormatterBPS = new DecimalFormat("#.#");
    final DecimalFormat FormatterKMH = new DecimalFormat("#.#");
    private double PrevPosX;
    private double PrevPosZ;
    private double deltaX;
    private double deltaZ;
    private final Timer timer = new Timer();
    private String speed = "Speed: ";
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    private final SalRainbowUtil Rainbow = new SalRainbowUtil(9);
    public SpeedComponent() {
        super("Speed", 2, 93);
        SetHidden(false);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        if (mc.player != null) {
            if (timer.passed(1000)) {
                PrevPosX = mc.player.prevX;
                PrevPosZ = mc.player.prevZ;
            }

            deltaX = mc.player.getX() - PrevPosX;
            deltaZ = mc.player.getZ() - PrevPosZ;
        }

        float distance = MathHelper.sqrt((float) (deltaX * deltaX + deltaZ * deltaZ));

        double bPS = distance * 20;
        double kMH = Math.floor((distance / 1000.0f) / (0.05f / 3600.0f));

        if (SpeedUnit.getValue() == UnitList.BPS) {
            String formatterBPS = FormatterBPS.format(bPS);

            speed = hud.Rainbow.getValue() ? "Speed: " + Formatting.WHITE + formatterBPS + " BPS" : "Speed: " + Formatting.WHITE + formatterBPS + " BPS";

        } else if (SpeedUnit.getValue() == UnitList.KMH) {
            String formatterKMH = FormatterKMH.format(kMH);

            speed = hud.Rainbow.getValue() ? "Speed: " + Formatting.WHITE + formatterKMH + "km/h" : "Speed: " + Formatting.WHITE + formatterKMH + "km/h";

        }

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), speed, (int) (GetX()), (int) (GetY()), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(speed), (int) GetX(), (int) GetY(), hud.Rainbow.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
        }

        Rainbow.OnRender();
        SetWidth(mc.textRenderer.getWidth(speed));
        SetHeight(mc.textRenderer.fontHeight);
    }

    public enum UnitList {
        BPS,
        KMH,
    }
}