package me.ionar.salhack.module.ui;

import me.ionar.salhack.gui.click.ClickGuiScreen;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import org.lwjgl.glfw.GLFW;

public final class ClickGuiModule extends Module {
    public final Value<Boolean> AllowOverflow = new Value<>("AllowOverflow", new String[]{"AllowOverflow"}, "Allows the GUI to overflow", true);
    public final Value<Boolean> Watermark = new Value<>("Watermark", new String[]{"Watermark"}, "Displays the watermark on the GUI", true);
    public final Value<Boolean> HoverDescriptions = new Value<>("HoverDescriptions", new String[]{"HD"}, "Displays hover descriptions over values and modules", true);
    public final Value<Boolean> Snowing = new Value<>("Snowing", new String[]{"SN"}, "Play a snowing animation in ClickGUI", true);
    public ClickGuiScreen ClickGui;

    public ClickGuiModule() {
        super("ClickGui", new String[]{ "Gui", "ClickGui" }, "Displays the click gui", GLFW.GLFW_KEY_RIGHT_SHIFT, 0xDB9324, ModuleType.UI);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (ClickGui == null) ClickGui = new ClickGuiScreen(this, (ColorsModule) SalHack.getModuleManager().getMod(ColorsModule.class));
        if (mc.world != null && mc.mouse != null) {
            System.out.println("open clickgui");
            mc.setScreen(ClickGui);
        }
    }
}
