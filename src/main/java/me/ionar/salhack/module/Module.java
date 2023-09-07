package me.ionar.salhack.module;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.salhack.EventSalHackModuleDisable;
import me.ionar.salhack.events.salhack.EventSalHackModuleEnable;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.CommandManager;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.managers.PresetsManager;
import me.ionar.salhack.module.ui.Notifcation;
import me.zero.alpine.fork.listener.Listenable;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public abstract class Module implements Listenable
{
    public String displayName;
    private String[] alias;
    private String desc;
    public int key;
    private int color;
    public boolean hidden = false;
    private boolean enabled = false;
    private ModuleType type;
    private boolean m_NeedsClickGuiValueUpdate;
    protected final MinecraftClient mc = MinecraftClient.getInstance();

    public List<Value> valueList = new ArrayList<Value>();
    public float RemainingXAnimation = 0f;

    private Module(String displayName, String[] alias, int key, int color, ModuleType type)
    {
        this.displayName = displayName;
        this.alias = alias;
        this.key = key;
        this.color = color;
        this.type = type;
    }

    public Module(String displayName, String[] alias, String desc, int key, int color, ModuleType type)
    {
        this(displayName, alias, key, color, type);
        this.desc = desc;
    }

    public void onEnable()
    {
        Notifcation notifcation =  (Notifcation) ModuleManager.Get().GetMod(Notifcation.class);
        /// allow events to be called
        SalHackMod.EVENT_BUS.subscribe(this);

        if (mc.player != null) RemainingXAnimation = mc.textRenderer.getWidth(GetFullArrayListDisplayName())+10f;

        ModuleManager.Get().OnModEnable(this);
        if (mc.player != null) {
            if (notifcation.isEnabled()) {
                mc.player.sendMessage(Text.of(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + displayName + Formatting.GREEN + " ON"));
            }
        }
        SalHackMod.EVENT_BUS.post(new EventSalHackModuleEnable(this));
    }

    public void onDisable()
    {
        Notifcation notifcation =  (Notifcation) ModuleManager.Get().GetMod(Notifcation.class);
        /// disallow events to be called
        SalHackMod.EVENT_BUS.unsubscribe(this);
        SalHackMod.EVENT_BUS.post(new EventSalHackModuleDisable(this));
        if (mc.player != null) {
            if (notifcation.isEnabled()) {
                SalHack.SendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + displayName + Formatting.RED + " OFF");
            }
        }
    }

    public void onToggle()
    {

    }

    public void toggle()
    {
        this.setEnabled(!this.isEnabled());
        if (this.isEnabled())
        {
            this.onEnable();
        }
        else
        {
            this.onDisable();
        }
        this.onToggle();

        SaveSettings();
    }

    public void toggleNoSave()
    {
        this.setEnabled(!this.isEnabled());
        if (this.isEnabled())
        {
            this.onEnable();
        }
        else
        {
            this.onDisable();
        }
        this.onToggle();
    }

    public void ToggleOnlySuper()
    {
        this.setEnabled(!this.isEnabled());
        this.onToggle();
    }

    public String getMetaData()
    {
        return null;
    }

    public Value find(String alias)
    {
        for (Value v : this.getValueList())
        {
            for (String s : v.getAlias())
            {
                if (alias.equalsIgnoreCase(s))
                {
                    return v;
                }
            }

            if (v.getName().equalsIgnoreCase(alias))
            {
                return v;
            }
        }
        return null;
    }

    public void unload()
    {
        this.valueList.clear();
    }

    public enum ModuleType
    {
        COMBAT, EXPLOIT, MOVEMENT, RENDER, WORLD, MISC, HIDDEN, UI, BOT, LITEMATICA, HIGHWAY, DONATE
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
        CommandManager.Get().Reload();
        SaveSettings();
    }

    public String[] getAlias()
    {
        return alias;
    }

    public void setAlias(String[] alias)
    {
        this.alias = alias;
    }

    public String getDesc()
    {
        return desc;
    }

    public void setDesc(String desc)
    {
        this.desc = desc;
    }

    public int getKey()
    {
        return key;
    }

    public boolean IsKeyPressed(int p_KeyCode) {
        if (mc.currentScreen != null) return false;
        return key == p_KeyCode;
    }

    public void setKey(int key)
    {
        this.key = key;
        SaveSettings();
    }

    public int getColor()
    {
        return color;
    }

    public void setColor(int color)
    {
        this.color = color;
    }

    public boolean isHidden()
    {
        return hidden;
    }

    public void setHidden(boolean hidden)
    {
        this.hidden = hidden;
        SaveSettings();
    }

    public boolean isEnabled()
    {
        return enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public ModuleType getType()
    {
        return type;
    }

    public void setType(ModuleType type)
    {
        this.type = type;
    }

    public List<Value> getValueList()
    {
        return valueList;
    }

    public void setValueList(List<Value> valueList)
    {
        this.valueList = valueList;
    }

    public float GetRemainingXArraylistOffset()
    {
        return RemainingXAnimation;
    }

    public void SignalEnumChange()
    {
    }

    public void SignalValueChange(Value p_Val)
    {
        SaveSettings();
    }

    public List<Value> GetVisibleValueList()
    {
        return valueList;
    }

    /// functions for updating value in an async way :)
    public void SetClickGuiValueUpdate(boolean p_Val)
    {
        m_NeedsClickGuiValueUpdate = p_Val;
    }

    public boolean NeedsClickGuiValueUpdate()
    {
        return m_NeedsClickGuiValueUpdate;
    }

    public String GetNextStringValue(final Value<String> p_Val, boolean p_Recursive)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public String GetArrayListDisplayName()
    {
        return getDisplayName();
    }

    public String GetFullArrayListDisplayName()
    {
        return getDisplayName() + (getMetaData() != null ? " " + Formatting.GRAY + getMetaData() : "");
    }

    public void SendMessage(String p_Message)
    {
        if (mc.player != null) SalHack.SendMessage(Formatting.AQUA + "[" + GetArrayListDisplayName() + "]: " + Formatting.RESET + p_Message);
    }

    public void SaveSettings()
    {
        PresetsManager.Get().getActivePreset().addModuleSettings(this);
    }

    public void init() {

    }
}
