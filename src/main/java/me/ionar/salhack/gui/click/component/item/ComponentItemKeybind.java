package me.ionar.salhack.gui.click.component.item;

import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.util.KeyUtil;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.util.Timer;

public class ComponentItemKeybind extends ComponentItem
{
    public boolean Listening = false;
    final Module Mod;
    private int LastKey = GLFW.GLFW_KEY_UNKNOWN;
    private Timer timer = new Timer();
    private String DisplayString = "";

    public ComponentItemKeybind(Module p_Mod, String p_DisplayText, String p_Description, int p_Flags, int p_State, ComponentItemListener p_Listener, float p_Width, float p_Height)
    {
        super(p_DisplayText, p_Description, p_Flags, p_State, p_Listener, p_Width, p_Height);
        Mod = p_Mod;

        Flags |= ComponentItem.RectDisplayAlways;
    }

    @Override
    public String GetDisplayText()
    {
        if (Listening)
            return "Press a Key...";

        String l_DisplayText = "Keybind " + KeyUtil.getKeyName(Mod.getKey());

        if (HasState(ComponentItem.Hovered) && Wrapper.GetMC().textRenderer.getWidth(l_DisplayText) > GetWidth() - 3)
        {
            if (DisplayString == null)
                DisplayString = "Keybind " + KeyUtil.getKeyName(Mod.getKey()) + " ";

            l_DisplayText = DisplayString;
            float l_Width = Wrapper.GetMC().textRenderer.getWidth(l_DisplayText);

            while (l_Width > GetWidth() - 3)
            {
                l_Width = Wrapper.GetMC().textRenderer.getWidth(l_DisplayText);
                l_DisplayText = l_DisplayText.substring(0, l_DisplayText.length() - 1);
            }

            if (timer.passed(75) && DisplayString.length() > 0)
            {
                String l_FirstChar = String.valueOf(DisplayString.charAt(0));

                DisplayString = DisplayString.substring(1) + l_FirstChar;

                timer.reset();
            }

            return l_DisplayText;
        }
        else
            DisplayString = null;

        float l_Width = Wrapper.GetMC().textRenderer.getWidth(l_DisplayText);

        while (l_Width > GetWidth() - 3)
        {
            l_Width = Wrapper.GetMC().textRenderer.getWidth(l_DisplayText);
            l_DisplayText = l_DisplayText.substring(0, l_DisplayText.length() - 1);
        }

        return l_DisplayText;
    }

    @Override
    public String GetDescription()
    {
        return "Sets the key of the Module: " + Mod.getDisplayName();
    }

    @Override
    public void OnMouseClick(int p_MouseX, int p_MouseY, int p_MouseButton)
    {
        super.OnMouseClick(p_MouseX, p_MouseY, p_MouseButton);

        LastKey = GLFW.GLFW_KEY_UNKNOWN;

        if (p_MouseButton == 0)
            Listening = !Listening;
        else if (p_MouseButton == 1)
            Listening = false;
        else if (p_MouseButton == 2)
        {
            Mod.setKey(GLFW.GLFW_KEY_UNKNOWN);
            SalHack.SendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + "Unbinded the module: " + Formatting.GOLD + Mod.getDisplayName());
            Listening = false;
        }
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (Listening)
        {

            int l_key = 0;

            if (keyCode == GLFW.GLFW_KEY_END || keyCode == GLFW.GLFW_KEY_BACKSPACE || keyCode == GLFW.GLFW_KEY_DELETE) {
                l_key = 0;
            } else {
                l_key = keyCode;
            }

            LastKey = l_key;
        }
        super.keyTyped(keyCode, scanCode, modifiers);
    }

    @Override
    public void Update()
    {
        if (Listening && LastKey != GLFW.GLFW_KEY_UNKNOWN)
        {
            Mod.setKey(LastKey);
            SalHack.SendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + "Set the key of " + Formatting.GOLD + Mod.getDisplayName() +  Formatting.WHITE + " to " + Formatting.GREEN + KeyUtil.getKeyName(LastKey));
            Listening = false;
        }
    }
}
