package me.ionar.salhack.gui.click.component.menus.mods;

import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.gui.click.component.item.ComponentItemHUD;
import me.ionar.salhack.gui.click.component.item.componentItemValue;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.module.Value;

@SuppressWarnings("rawtypes")
public class MenuComponentHUDList extends MenuComponent {
    public MenuComponentHUDList(String displayName, float x, float y) {
        super(displayName, x, y, 100f, 105f, "", (ColorsModule) SalHack.getModuleManager().getMod(ColorsModule.class), null);
        final float Width = 105f;
        final float Height = 11f;
        for (HudComponentItem item : SalHack.getHudManager().componentItems) {
            ComponentItemListener listener = new ComponentItemListener() {
                @Override
                public void OnEnabled() {}
                @Override
                public void OnToggled() {
                    item.setHidden(!item.isHidden());
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
            if (!item.values.isEmpty()) flags |= ComponentItem.HasValues;
            int state = 0;
            if (!item.isHidden()) state |= ComponentItem.Clicked;
            ComponentItem componentItem = new ComponentItemHUD(item, item.getDisplayName(), "", flags, state, listener, Width, Height);

            for (Value value : item.values) {
                listener = new ComponentItemListener() {
                    @Override
                    public void OnEnabled() {}
                    @Override
                    public void OnToggled() {}
                    @Override
                    public void OnDisabled() {}
                    @Override
                    public void OnHover() {}
                    @Override
                    public void OnMouseEnter() {}
                    @Override
                    public void OnMouseLeave() {}
                };

                componentItemValue valueItem = new componentItemValue(value, value.getName(), value.getDescription(), ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip, 0, listener, Width, Height);
                componentItem.dropdownItems.add(valueItem);
            }

            listener = new ComponentItemListener() {
                @Override
                public void OnEnabled() {}
                @Override
                public void OnToggled() {
                    item.resetToDefaultPos();
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

            ComponentItem resetButton = new ComponentItem("Reset", "Resets the position of " + item.getDisplayName() + " to default.",  ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip | ComponentItem.Enum | ComponentItem.DontDisplayClickableHighlight | ComponentItem.RectDisplayAlways, 0, listener, Width, Height);
            componentItem.dropdownItems.add(resetButton);
            addItem(componentItem);
        }
    }
}
