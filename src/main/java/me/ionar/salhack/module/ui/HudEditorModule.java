package me.ionar.salhack.module.ui;

import me.ionar.salhack.gui.hud.GuiHudEditor;
import me.ionar.salhack.module.Module;
import org.lwjgl.glfw.GLFW;

import static me.ionar.salhack.main.Wrapper.mc;

public final class HudEditorModule extends Module {
    private GuiHudEditor HudEditor;

    public HudEditorModule() {
        super("HudEditor", new String[]{"HudEditor"}, "Displays the HudEditor", GLFW.GLFW_KEY_GRAVE_ACCENT, 0xDBC824, ModuleType.UI);
    }

    @Override
    public void onToggle() {
        super.onToggle();
        if (mc.world != null) {
            if (HudEditor == null) HudEditor = new GuiHudEditor(this);
            mc.setScreen(HudEditor);
        }
    }
}
