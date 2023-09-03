package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.managers.TickRateManager;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class TPSComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);
    private final int i = 0;
    public TPSComponent() {
        super("TPS", 2, 33);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        final String tickrate = String.format("TPS%s %.2f", Formatting.WHITE, TickRateManager.Get().getTickRate());

        context.drawTextWithShadow(mc.textRenderer, Text.of(tickrate), (int) GetX(), (int) GetY(), 0x2ACCED);

        SetWidth(Wrapper.GetMC().textRenderer.getWidth(tickrate));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

}