package me.ionar.salhack.module.ui;

import me.ionar.salhack.gui.click.ClickGuiScreen;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import org.lwjgl.glfw.GLFW;

import static me.ionar.salhack.main.Wrapper.mc;

@SuppressWarnings("SuspiciousIndentAfterControlStatement")
public final class ClickGuiModule extends Module {
    public final Value<Boolean> AllowOverflow = new Value<>("AllowOverflow", new String[]{"AllowOverflow"}, "Allows the GUI to overflow", true);
    public final Value<Boolean> Watermark = new Value<>("Watermark", new String[]{"Watermark"}, "Displays the watermark on the GUI", true);
    public final Value<Boolean> HoverDescriptions = new Value<>("HoverDescriptions", new String[]{"HD"}, "Displays hover descriptions over values and modules", true);
    public final Value<Boolean> Snowing = new Value<>("Snowing", new String[]{"SN"}, "Play a snowing animation in ClickGUI", true);
    public ClickGuiScreen ClickGui;
    //public TestScreen testScreen = new TestScreen();

    public ClickGuiModule() {
        super("ClickGui", "Displays the click gui", GLFW.GLFW_KEY_RIGHT_SHIFT, 0xDB9324, ModuleType.UI);
    }
}
