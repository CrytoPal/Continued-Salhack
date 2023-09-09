package me.ionar.salhack.events.salhack;

import me.ionar.salhack.events.Event;
import me.ionar.salhack.module.Module;

public abstract class ModuleEvent extends Event {
    private final Module module;

    public ModuleEvent(Module module) {
        this.module = module;
    }

    public Module getModule() {
        return module;
    }

    public static class Enabled extends ModuleEvent {

        public Enabled(Module module) {
            super(module);
        }
    }

    public static class Disabled extends ModuleEvent {

        public Disabled(Module module) {
            super(module);
        }
    }

}
