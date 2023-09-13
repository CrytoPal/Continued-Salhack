package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Module;
import net.minecraft.client.MinecraftClient;

public class ComponentItemMod extends ComponentItem {
    final Module module;
    private final MinecraftClient mc = Wrapper.GetMC();

    public ComponentItemMod(Module module, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        this.module = module;
    }

    @Override
    public String getDisplayText() {
        String displayText = module.getDisplayName();
        float width = mc.textRenderer.getWidth(displayText);
        while (width > getWidth()) {
            width = mc.textRenderer.getWidth(displayText);
            displayText = displayText.substring(0, displayText.length()-1);
        }
        return displayText;
    }

    @Override
    public String getDescription() {
        return module.getDescription();
    }

    @Override
    public boolean hasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return module.isEnabled();
        return super.hasState(state);
    }
}
