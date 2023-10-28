package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.color.SalRainbowUtil;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.*;

public class ModuleListComponent extends HudComponentItem {

    public final Value<Boolean> RainbowVal = new Value<Boolean>("Rainbow", new String[]{""}, "Makes a dynamic rainbow", true);

    public static List<Module> moduleArrayList = new ArrayList<>();
    public static List<String> moduleDisplayNames = new ArrayList<>();
    private final SalRainbowUtil Rainbow = new SalRainbowUtil(100);

    public ModuleListComponent() {
        super("ModuleList", 50, 20);
        SetHidden(false);
        ClampLevel = 1;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.render(mouseX, mouseY, partialTicks, context);

        ModuleManager.Get().Update();
        if (!moduleArrayList.isEmpty()) {
            for (int i = 0; i < moduleArrayList.size(); i++) {
                if (HudModule.CustomFont.getValue()) {
                    Collections.sort(moduleDisplayNames, Comparator.comparingInt(String::length).reversed());
                    if (GetX() > 400) {
                        FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), moduleDisplayNames.get(i), (int) GetX() + (mc.textRenderer.getWidth(moduleDisplayNames.get(0)) - mc.textRenderer.getWidth(moduleDisplayNames.get(i))), (int) GetY() + (i * 10), RainbowVal.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
                    } else {
                        FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), moduleDisplayNames.get(i), (int) GetX(), (int) GetY() + (i * 10) , RainbowVal.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
                    }
                } else {
                    Collections.sort(moduleDisplayNames, Comparator.comparingInt(String::length).reversed());
                    if (GetX() > 400) {
                        context.drawTextWithShadow(mc.textRenderer, Text.of(moduleDisplayNames.get(i)), (int) GetX() + (mc.textRenderer.getWidth(moduleDisplayNames.get(0)) - mc.textRenderer.getWidth(moduleDisplayNames.get(i))), (int) GetY() + (i * 10), RainbowVal.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
                    } else {
                        context.drawTextWithShadow(mc.textRenderer, Text.of(moduleDisplayNames.get(i)), (int) GetX(), (int) GetY() + (i * 10), RainbowVal.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
                    }
                }
                Rainbow.OnRender();
                SetWidth(mc.textRenderer.getWidth(moduleDisplayNames.get(0)));
                SetHeight(mc.textRenderer.fontHeight + (i * 10));
            }
        }

        /*
        for (int i = 0; i < moduleArrayList.size(); i++) {
                if (HudModule.CustomFont.getValue()) {
                    Collections.sort(moduleDisplayNames, Comparator.comparingInt(String::length).reversed());
                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), moduleDisplayNames.get(i), (int) GetX(), (int) GetY() + (i * 10) , RainbowVal.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor(), true);
                } else {
                    Collections.sort(moduleDisplayNames, Comparator.comparingInt(String::length).reversed());
                    context.drawTextWithShadow(mc.textRenderer, Text.of(moduleDisplayNames.get(i)), (int) GetX(), (int) GetY() + (i * 10), RainbowVal.getValue() ? Rainbow.GetRainbowColorAt(Rainbow.getRainbowColorNumber()) : GetTextColor());
                }
                Rainbow.OnRender();
                SetWidth(mc.textRenderer.getWidth(moduleDisplayNames.get(0)));
                SetHeight(mc.textRenderer.fontHeight + (i * 10));
            }
         */
    }
}
