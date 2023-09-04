package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.mixin.MinecraftClientAccessor;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.text.DecimalFormat;

public class FPSComponent extends HudComponentItem {
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    MinecraftClient mc = MinecraftClient.getInstance();
    private final int i = 0;

    private int fps = mc.getCurrentFps();
    public FPSComponent() {
        super("FPS", 2, 23);
    }



    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);



        final String fPS = "FPS " + Formatting.WHITE + MinecraftClientAccessor.getCurrentFps();

        context.drawTextWithShadow(mc.textRenderer, Text.of(fPS), (int) GetX(), (int) GetY(), GetTextColor());

        SetWidth(Wrapper.GetMC().textRenderer.getWidth(fPS));
        SetHeight(Wrapper.GetMC().textRenderer.fontHeight);
    }

}