package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.util.KeyUtil;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.util.Timer;

public class ComponentItemKeybind extends ComponentItem {
    public boolean listening = false;
    final Module module;
    private int lastKey = GLFW.GLFW_KEY_UNKNOWN;
    private final Timer timer = new Timer();
    private String displayString = "";
    private final MinecraftClient mc = Wrapper.GetMC();

    public ComponentItemKeybind(Module module, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        this.module = module;
        this.flags |= ComponentItem.RectDisplayAlways;
    }

    @Override
    public String getDisplayText() {
        if (listening) return "Press a Key...";
        String displayText = "Keybind " + KeyUtil.getKeyName(module.getKey());
        if (hasState(ComponentItem.Hovered) && mc.textRenderer.getWidth(displayText) > getWidth() - 3) {
            if (displayString == null) displayString = "Keybind " + KeyUtil.getKeyName(module.getKey()) + " ";
            displayText = displayString;
            float width = mc.textRenderer.getWidth(displayText);
            while (width > getWidth() - 3) {
                width = mc.textRenderer.getWidth(displayText);
                displayText = displayText.substring(0, displayText.length() - 1);
            }
            if (timer.passed(75) && !displayString.isEmpty()) {
                String l_FirstChar = String.valueOf(displayString.charAt(0));
                displayString = displayString.substring(1) + l_FirstChar;
                timer.reset();
            }
            return displayText;
        } else displayString = null;
        float width = mc.textRenderer.getWidth(displayText);
        while (width > getWidth() - 3) {
            width = mc.textRenderer.getWidth(displayText);
            displayText = displayText.substring(0, displayText.length() - 1);
        }
        return displayText;
    }

    @Override
    public String getDescription() {
        return "Sets the key of the Module: " + module.getDisplayName();
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        super.onMouseClick(mouseX, mouseY, mouseButton);
        lastKey = GLFW.GLFW_KEY_UNKNOWN;
        if (mouseButton == 0) listening = !listening;
        else if (mouseButton == 1) listening = false;
        else if (mouseButton == 2) {
            module.setKey(GLFW.GLFW_KEY_UNKNOWN);
            SalHack.sendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + "Unbound the module: " + Formatting.GOLD + module.getDisplayName());
            listening = false;
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (listening) {
            int key;
            if (keyCode == GLFW.GLFW_KEY_END || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) key = 0;
            else key = keyCode;
            lastKey = key;
        }
        super.keyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public void update() {
        if (listening && lastKey != GLFW.GLFW_KEY_UNKNOWN) {
            module.setKey(lastKey);
            SalHack.sendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + "Set the key of " + Formatting.GOLD + module.getDisplayName() +  Formatting.WHITE + " to " + Formatting.GREEN + KeyUtil.getKeyName(lastKey));
            listening = false;
        }
    }
}
