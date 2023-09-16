package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.gui.hud.HudComponentItem;

public class ComponentItemHUD extends ComponentItem {
    final HudComponentItem HudComponent;

    public ComponentItemHUD(HudComponentItem hudComponent, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        HudComponent = hudComponent;
    }

    @Override
    public String GetDisplayText() {
        String displayText = HudComponent.GetDisplayName();
        float width = FontRenderers.getTwCenMtStd22().getStringWidth(displayText);
        while (width > GetWidth()) {
            width = FontRenderers.getTwCenMtStd22().getStringWidth(displayText);
            displayText = displayText.substring(0, displayText.length()-1);
        }
        return displayText;
    }

    @Override
    public String GetDescription() {
        return "";
    }

    @Override
    public void Update() {
        super.Update();
    }

    @Override
    public boolean HasState(int p_State) {
        if ((p_State & ComponentItem.Clicked) != 0) return !HudComponent.IsHidden();
        return super.HasState(p_State);
    }
}
