package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.preset.Preset;

public class ComponentPresetItem extends ComponentItem {
    private final Preset Preset;

    public ComponentPresetItem(Preset preset, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(preset.getName(), "", flags, state, listener, width, height);
        Preset = preset;
    }

    @Override
    public boolean HasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return Preset.isActive();
        return super.HasState(state);
    }

    public Preset getPreset() {
        return Preset;
    }
}
