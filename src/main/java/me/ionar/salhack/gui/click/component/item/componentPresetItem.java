package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.preset.Preset;

public class componentPresetItem extends ComponentItem {
    private final Preset preset;

    public componentPresetItem(Preset preset, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(preset.getName(), "", flags, state, listener, width, height);
        this.preset = preset;
    }

    @Override
    public boolean hasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return preset.isActive();
        return super.hasState(state);
    }

    public Preset getPreset() {
        return preset;
    }
}
