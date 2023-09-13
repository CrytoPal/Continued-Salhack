package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.MathHelper;

public class DirectionComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) SalHack.getModuleManager().getMod(HudModule.class);
    private final SalRainbowUtil rainbow = new SalRainbowUtil(9);
    public DirectionComponent() {
        super("Direction", 2, 506);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.player == null) return;
        final String direction = this.getFacing(mc.player) + " " + Formatting.GRAY + this.getTowards(mc.player);
        if (HudModule.customFont.getValue()) FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), direction, (int) (getPositionX()), (int) (getPositionY()), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor(), true);
        else context.drawTextWithShadow(mc.textRenderer, Text.of(direction), (int) getPositionX(), (int) getPositionY(), hud.rainbow.getValue() ? rainbow.getRainbowColorAt(rainbow.getRainbowColorNumber()) : getTextColor());
        rainbow.onRender();
        setWidth(Wrapper.GetMC().textRenderer.getWidth(direction));
        setHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

    private String getFacing(PlayerEntity player) {
        float yaw = player.getYaw();
        return switch (MathHelper.floor((double) (yaw * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0 -> "South";
            case 1 -> "South West";
            case 2 -> "West";
            case 3 -> "North West";
            case 4 -> "North";
            case 5 -> "North East";
            case 6 -> "East";
            case 7 -> "South East";
            default -> "Invalid";
        };
    }

    private String getTowards(PlayerEntity player) {
        float yaw = player.getYaw();
        return switch (MathHelper.floor((double) (yaw * 8.0F / 360.0F) + 0.5D) & 7) {
            case 0 -> "+Z";
            case 1 -> "-X +Z";
            case 2 -> "-X";
            case 3 -> "-X -Z";
            case 4 -> "-Z";
            case 5 -> "+X -Z";
            case 6 -> "+X";
            case 7 -> "+X +Z";
            default -> "Invalid";
        };
    }
}