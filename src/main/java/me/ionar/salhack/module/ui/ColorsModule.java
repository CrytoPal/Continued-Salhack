package me.ionar.salhack.module.ui;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public final class ColorsModule extends Module {
    // 0x9933b6d7
    public final Value<Integer> red = new Value<>("Red", new String[]{"bRed"}, "Red for rendering", 0x33, 0, 255, 11);
    public final Value<Integer> green = new Value<>("Green", new String[]{"bGreen"}, "Green for rendering", 0xb6, 0, 255, 11);
    public final Value<Integer> blue = new Value<>("Blue", new String[]{"bBlue"}, "Blue for rendering", 0xd7, 0, 255, 11);
    public final Value<Integer> alpha = new Value<>("Alpha", new String[]{"bAlpha"}, "Alpha for rendering", 0x99, 0, 255, 11);

    public ColorsModule() {
        super("Colors", new String[]{ "Clrs","Colors" }, "Allows you to modify the GUI Colors", 0, -1, ModuleType.UI);
    }
}
