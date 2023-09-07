package me.ionar.salhack.gui.click.component.menus.mods;

import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.gui.click.component.item.ComponentItemHUD;
import me.ionar.salhack.gui.click.component.item.ComponentItemValue;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.managers.HudManager;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.ui.ColorsModule;
import me.ionar.salhack.module.Value;

@SuppressWarnings("rawtypes")
public class MenuComponentHUDList extends MenuComponent {
    public MenuComponentHUDList(String displayName, float X, float Y) {
        super(displayName, X, Y, 100f, 105f, "", (ColorsModule)ModuleManager.Get().GetMod(ColorsModule.class), null);

        final float Width = 105f;
        final float Height = 11f;

        for (HudComponentItem item : HudManager.Get().Items) {
            ComponentItemListener listener = new ComponentItemListener() {
                @Override
                public void OnEnabled() {}
                @Override
                public void OnToggled() {
                    item.SetHidden(!item.IsHidden());
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
            if (!item.ValueList.isEmpty()) flags |= ComponentItem.HasValues;
            int state = 0;
            if (!item.IsHidden()) state |= ComponentItem.Clicked;
            ComponentItem componentItem = new ComponentItemHUD(item, item.GetDisplayName(), "", flags, state, listener, Width, Height);

            for (Value value : item.ValueList) {
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

                ComponentItemValue valueItem = new ComponentItemValue(value, value.getName(), value.getDesc(), ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip, 0, listener, Width, Height);
                componentItem.DropdownItems.add(valueItem);
            }

            listener = new ComponentItemListener() {
                @Override
                public void OnEnabled() {}
                @Override
                public void OnToggled() {
                    item.ResetToDefaultPos();
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

            ComponentItem resetButton = new ComponentItem("Reset", "Resets the position of " + item.GetDisplayName() + " to default.",  ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip | ComponentItem.Enum | ComponentItem.DontDisplayClickableHighlight | ComponentItem.RectDisplayAlways, 0, listener, Width, Height);
            componentItem.DropdownItems.add(resetButton);
            AddItem(componentItem);
        }
    }
}
