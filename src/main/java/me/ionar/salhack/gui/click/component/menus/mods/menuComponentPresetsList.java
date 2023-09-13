package me.ionar.salhack.gui.click.component.menus.mods;

import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.gui.click.component.item.componentPresetItem;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.preset.Preset;

public class menuComponentPresetsList extends MenuComponent {
    public menuComponentPresetsList(String displayName, float x, float y, String image, ColorsModule colorsModule, ClickGuiModule clickGuiModule) {
        super(displayName, x, y, 100f, 105f, image, colorsModule, clickGuiModule);
        SalHack.getPresetsManager().getItems().forEach(this::addPreset);
    }

    public void addPreset(Preset preset) {
        ComponentItemListener listener = new ComponentItemListener() {
            @Override
            public void OnEnabled() {}
            @Override
            public void OnToggled() {
                SalHack.getPresetsManager().setPresetActive(preset);
            }
            @Override
            public void OnDisabled() {}
            @Override
            public void OnHover() {}
            @Override
            public void OnMouseEnter() {}
            @Override
            public void OnMouseLeave() {}
        };

        int flags = ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip;
        int state = 0;
        if (preset.isActive()) state |= ComponentItem.Clicked;
        float width = 105f;
        float height = 11f;
        ComponentItem componentItem = new componentPresetItem(preset, flags, state, listener, width, height);

        // todo: add values for deleting, renaming, and copying

        addItem(componentItem);
    }

    public void removePreset(Preset toRemove) {
        ComponentItem removeItem = null;
        for (ComponentItem componentItem : this.componentItems) {
            if (componentItem instanceof componentPresetItem comp) {
                if (comp.getPreset() == toRemove) {
                    removeItem = comp;
                    break;
                }
            }
        }
        if (removeItem != null) this.componentItems.remove(removeItem);
    }
}
