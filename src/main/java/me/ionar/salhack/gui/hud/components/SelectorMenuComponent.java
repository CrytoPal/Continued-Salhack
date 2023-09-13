package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.click.component.menus.mods.MenuComponentHUDList;
import me.ionar.salhack.gui.hud.HudComponentItem;
import net.minecraft.client.gui.DrawContext;

public class SelectorMenuComponent extends HudComponentItem {
    MenuComponentHUDList component = new MenuComponentHUDList("Selector", 300, 300);

    public SelectorMenuComponent() {
        super("Selector", 300, 300);
        setHidden(false);
        addFlag(HudComponentItem.onlyVisibleInHudEditor);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        component.render(mouseX, mouseY, true, true, 0, context);
        setWidth(component.getWidth());
        setHeight(component.getHeight());
        setX(component.getX());
        setY(component.getY());
    }

    @Override
    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        return component.mouseClicked(mouseX, mouseY, mouseButton, 0);
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY, int state) {
        super.onMouseRelease(mouseX, mouseY, state);
        component.mouseReleased(mouseX, mouseY);
    }
}
