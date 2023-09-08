package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;

public class ComponentItemMod extends ComponentItem {
    final Module Module;

    public ComponentItemMod(Module module, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        Module = module;
    }

    @Override
    public String GetDisplayText() {
        String displayText = Module.getDisplayName();

        float width = Wrapper.GetMC().textRenderer.getWidth(displayText);

        while (width > GetWidth()) {
            width = Wrapper.GetMC().textRenderer.getWidth(displayText);
            displayText = displayText.substring(0, displayText.length()-1);
        }

        return displayText;
    }

    @Override
    public String GetDescription() {
        return Module.getDescription();
    }

    @Override
    public void Update() {}

    @Override
    public boolean HasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return Module.isEnabled();
        return super.HasState(state);
    }
}
