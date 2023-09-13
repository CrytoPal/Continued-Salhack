package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.gui.hud.HudComponentItem;

public class ComponentItemHUD extends ComponentItem {
    final HudComponentItem hudComponent;

    public ComponentItemHUD(HudComponentItem hudComponent, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        this.hudComponent = hudComponent;
    }

    @Override
    public String getDisplayText() {
        String displayText = hudComponent.getDisplayName();
        float width = FontRenderers.getTwCenMtStd22().getStringWidth(displayText);
        while (width > getWidth()) {
            width = FontRenderers.getTwCenMtStd22().getStringWidth(displayText);
            displayText = displayText.substring(0, displayText.length()-1);
        }
        return displayText;
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public void update() {
        super.update();
    }

    @Override
    public boolean hasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return !hudComponent.isHidden();
        return super.hasState(state);
    }
}
