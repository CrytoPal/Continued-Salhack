package me.ionar.salhack.module.ui;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.render.RenderGameOverlayEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.HudManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public final class HudModule extends Module {
    public static final Value<Integer> ExtraTab = new Value<>("ExtraTab", new String[]{"ET"}, "Max player slots to show in the tab list", 80, 80, 1000, 10);
    public static final Value<Boolean> Rainbow = new Value<>("Rainbow", new String[]{"RGB"}, "Give HUD items rainbow effect.", false);
    public static final Value<Boolean> CustomFont = new Value<>("Custom Font", new String[]{"CF"}, "Custom Font for Hud", true);
    public static final Value<Integer> Red = new Value<>("Red", new String[]{"bRed"}, "Red for rendering", 242, 0, 255, 11);
    public static final Value<Integer> Green = new Value<>("Green", new String[]{"bGreen"}, "Green for rendering", 216, 0, 255, 11);
    public static final Value<Integer> Blue = new Value<>("Blue", new String[]{"bBlue"}, "Blue for rendering", 0, 0, 255, 11);

    public HudModule() {
        super("HUD", new String[]{ "HUD" }, "Displays the HUD", 0, 0xD1DB24, ModuleType.UI);
    }

    @EventHandler
    private void OnRenderGameOverlay(RenderGameOverlayEvent event) {
        if (!mc.options.debugEnabled) SalHack.getHudManager().onRender(event.tickDelta, event.getContext());
    }
}
