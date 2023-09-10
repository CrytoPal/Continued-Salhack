package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentHUDList;
import me.ionar.salhack.gui.hud.HudComponentItem;
import net.minecraft.client.gui.DrawContext;

public class SelectorMenuComponent extends HudComponentItem {
    MenuComponentHUDList l_Component = new MenuComponentHUDList("Selector", 300, 300);

    public SelectorMenuComponent() {
        super("Selector", 300, 300);
        setHidden(false);
        AddFlag(HudComponentItem.OnlyVisibleInHudEditor);
    }

    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);

        l_Component.Render(p_MouseX, p_MouseY, true, true, 0, context);

        setWidth(l_Component.GetWidth());
        setHeight(l_Component.GetHeight());
        setX(l_Component.GetX());
        setY(l_Component.GetY());
    }

    @Override
    public boolean onMouseClick(int p_MouseX, int p_MouseY, int p_MouseButton) {
        return l_Component.MouseClicked(p_MouseX, p_MouseY, p_MouseButton, 0);
    }

    @Override
    public void onMouseRelease(int p_MouseX, int p_MouseY, int p_State) {
        super.onMouseRelease(p_MouseX, p_MouseY, p_State);
        l_Component.MouseReleased(p_MouseX, p_MouseY);
    }
}
