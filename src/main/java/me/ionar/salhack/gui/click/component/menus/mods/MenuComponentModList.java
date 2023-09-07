package me.ionar.salhack.gui.click.component.menus.mods;

import me.ionar.salhack.gui.click.component.MenuComponent;
import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.gui.click.component.item.ComponentItemHiddenMod;
import me.ionar.salhack.gui.click.component.item.ComponentItemKeybind;
import me.ionar.salhack.gui.click.component.item.ComponentItemMod;
import me.ionar.salhack.gui.click.component.item.ComponentItemValue;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Module.ModuleType;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;

@SuppressWarnings("rawtypes")
public class MenuComponentModList extends MenuComponent {

    public MenuComponentModList(String displayName, ModuleType moduleType, float X, float Y, String image, ColorsModule colorsModule, ClickGuiModule clickGuiModule) {
        super(displayName, X, Y, 100f, 105f, image, colorsModule, clickGuiModule);
        final float Width = 105f;
        final float Height = 11f;

        for (Module module : ModuleManager.Get().GetModuleList(moduleType)) {
            ComponentItemListener listener = new ComponentItemListener() {
                @Override
                public void OnEnabled() {}
                @Override
                public void OnToggled() {
                    module.toggle();
                    //  SalHack.INSTANCE.getNotificationManager().addNotification("ClickGUI", "Toggled " + l_Mod.getDisplayName());
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
            if (!module.getValueList().isEmpty()) flags |= ComponentItem.HasValues;
            int state = 0;
            if (module.isEnabled()) state |= ComponentItem.Clicked;
            ComponentItem componentItem = new ComponentItemMod(module, module.getDisplayName(), module.getDesc(), flags, state, listener, Width, Height);

            for (Value value : module.getValueList()) {
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
                ComponentItemValue componentItemValue = new ComponentItemValue(value, value.getName(), value.getDesc(), ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip, 0, listener, Width, Height);
                componentItem.DropdownItems.add(componentItemValue);
            }

            listener = new ComponentItemListener() {
                @Override
                public void OnEnabled() {}
                @Override
                public void OnToggled() {
                    module.setHidden(!module.isHidden());
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

            ComponentItem hideButton = new ComponentItemHiddenMod(module, "Hidden", "Hides " + module.getDisplayName() + " from the arraylist",  ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip | ComponentItem.RectDisplayOnClicked | ComponentItem.DontDisplayClickableHighlight, 0, listener, Width, Height);
            componentItem.DropdownItems.add(hideButton);
            componentItem.DropdownItems.add(new ComponentItemKeybind(module, "Keybind:"+module.getDisplayName(), module.getDesc(),  ComponentItem.Clickable | ComponentItem.Hoverable | ComponentItem.Tooltip, 0, null, Width, Height));
            AddItem(componentItem);
        }
    }
}
