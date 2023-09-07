package me.ionar.salhack.gui.click.component.menus.mods;

import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.gui.click.component.item.ComponentPresetItem;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.managers.PresetsManager;
import me.ionar.salhack.module.Module.ModuleType;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.preset.Preset;

public class MenuComponentPresetsList extends MenuComponent {
    private final float Width = 105f;
    private final float Height = 11f;

    public MenuComponentPresetsList(String displayName, ModuleType moduleType, float X, float Y, String image, ColorsModule colorsModule, ClickGuiModule clickGuiModule) {
        super(displayName, X, Y, 100f, 105f, image, colorsModule, clickGuiModule);
        PresetsManager.Get().GetItems().forEach(this::AddPreset);
    }

    public void AddPreset(Preset preset) {
        ComponentItemListener listener = new ComponentItemListener() {
            @Override
            public void OnEnabled() {}
            @Override
            public void OnToggled() {
                PresetsManager.Get().SetPresetActive(preset);
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

        ComponentItem componentItem = new ComponentPresetItem(preset, flags, state, listener, Width, Height);

        // todo: add values for deleting, renaming, and copying

        AddItem(componentItem);
    }

    public void RemovePreset(Preset toRemove) {
        ComponentItem removeItem = null;
        for (ComponentItem componentItem : this.Items) {
            if (componentItem instanceof ComponentPresetItem) {
                ComponentPresetItem comp = (ComponentPresetItem) componentItem;
                if (comp.getPreset() == toRemove) {
                    removeItem = comp;
                    break;
                }
            }
        }
        if (removeItem != null) this.Items.remove(removeItem);
    }
}
