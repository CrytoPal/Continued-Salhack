package me.ionar.salhack.module.ui;

import me.ionar.salhack.gui.hud.GuiHudEditor;
import me.ionar.salhack.module.Module;
import org.lwjgl.glfw.GLFW;

public final class HudEditorModule extends Module {
    private GuiHudEditor hudEditor;
    public HudEditorModule() {
        super("HudEditor", new String[]{"HudEditor"}, "Displays the HudEditor", GLFW.GLFW_KEY_GRAVE_ACCENT, 0xDBC824, ModuleType.UI);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        if (mc.world != null && mc.mouse != null) {
            if (hudEditor == null) hudEditor = new GuiHudEditor(this);
            mc.setScreen(hudEditor);
        }
    }
}
