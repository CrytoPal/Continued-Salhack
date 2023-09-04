package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);
    private final int i = 0;
    public TimeComponent() {
        super("Time", 2, 13);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        final String time = "Time " + Formatting.WHITE + new SimpleDateFormat("h:mm a").format(new Date());

        if (HudModule.CustomFont.getValue()) {
            FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), time, (int) (GetX()), (int) (GetY()), GetTextColor(), true);
        } else {
            context.drawTextWithShadow(mc.textRenderer, Text.of(time), (int) GetX(), (int) GetY(), GetTextColor());
        }

        SetWidth(Wrapper.GetMC().textRenderer.getWidth(time));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }
}