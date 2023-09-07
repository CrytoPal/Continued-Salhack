package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.util.KeyUtil;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.util.Timer;

public class ComponentItemKeybind extends ComponentItem {
    public boolean Listening = false;
    final Module Module;
    private int LastKey = GLFW.GLFW_KEY_UNKNOWN;
    private final Timer timer = new Timer();
    private String DisplayString = "";

    public ComponentItemKeybind(Module module, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        Module = module;

        Flags |= ComponentItem.RectDisplayAlways;
    }

    @Override
    public String GetDisplayText() {
        if (Listening) return "Press a Key...";

        String displayText = "Keybind " + KeyUtil.getKeyName(Module.getKey());

        if (HasState(ComponentItem.Hovered) && Wrapper.GetMC().textRenderer.getWidth(displayText) > GetWidth() - 3) {
            if (DisplayString == null) DisplayString = "Keybind " + KeyUtil.getKeyName(Module.getKey()) + " ";

            displayText = DisplayString;
            float width = Wrapper.GetMC().textRenderer.getWidth(displayText);

            while (width > GetWidth() - 3) {
                width = Wrapper.GetMC().textRenderer.getWidth(displayText);
                displayText = displayText.substring(0, displayText.length() - 1);
            }

            if (timer.passed(75) && !DisplayString.isEmpty()) {
                String l_FirstChar = String.valueOf(DisplayString.charAt(0));
                DisplayString = DisplayString.substring(1) + l_FirstChar;
                timer.reset();
            }

            return displayText;
        } else DisplayString = null;

        float width = Wrapper.GetMC().textRenderer.getWidth(displayText);

        while (width > GetWidth() - 3) {
            width = Wrapper.GetMC().textRenderer.getWidth(displayText);
            displayText = displayText.substring(0, displayText.length() - 1);
        }

        return displayText;
    }

    @Override
    public String GetDescription() {
        return "Sets the key of the Module: " + Module.getDisplayName();
    }

    @Override
    public void OnMouseClick(int mouseX, int mouseY, int mouseButton) {
        super.OnMouseClick(mouseX, mouseY, mouseButton);

        LastKey = GLFW.GLFW_KEY_UNKNOWN;

        if (mouseButton == 0) Listening = !Listening;
        else if (mouseButton == 1) Listening = false;
        else if (mouseButton == 2) {
            Module.setKey(GLFW.GLFW_KEY_UNKNOWN);
            SalHack.SendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + "Unbinded the module: " + Formatting.GOLD + Module.getDisplayName());
            Listening = false;
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (Listening) {
            int key;
            if (keyCode == GLFW.GLFW_KEY_END || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) key = 0;
            else key = keyCode;
            LastKey = key;
        }
        super.keyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public void Update() {
        if (Listening && LastKey != GLFW.GLFW_KEY_UNKNOWN) {
            Module.setKey(LastKey);
            SalHack.SendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + "Set the key of " + Formatting.GOLD + Module.getDisplayName() +  Formatting.WHITE + " to " + Formatting.GREEN + KeyUtil.getKeyName(LastKey));
            Listening = false;
        }
    }
}
