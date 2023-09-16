package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.module.Module;
public class ComponentItemHiddenMod extends ComponentItem {
    final Module Module;

    public ComponentItemHiddenMod(Module module, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        Module = module;
    }

    @Override
    public boolean HasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return Module.isHidden();
        return super.HasState(state);
    }
}
