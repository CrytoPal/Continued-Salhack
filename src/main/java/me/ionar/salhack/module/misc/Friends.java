package me.ionar.salhack.module.misc;

import me.ionar.salhack.module.Module;

public class Friends extends Module {
    public Friends() {
        super("Friends", new String[] {"Homies"}, "Allows the friend system to function, disabling this ignores friend requirements, useful for dueling friends.", 0, -1, ModuleType.MISC);
        setEnabled(true);
    }
}
