package me.ionar.salhack.module;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.salhack.ModuleEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.CommandManager;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.managers.PresetsManager;
import me.ionar.salhack.module.ui.Notification;
import me.ionar.salhack.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
@SuppressWarnings("rawtypes")
public abstract class Module {
    public String DisplayName;
    private String Description;
    public int Key;
    private int Color;
    public boolean Hidden = false;
    private boolean Enabled = false;
    private ModuleType ModuleType;
    private boolean ClickGuiValueUpdate;

    protected static MinecraftClient mc = MinecraftClient.getInstance();
    public List<Value> ValueList = new ArrayList<>();
    public float RemainingXAnimation = 0f;

    private Module(String displayName, int key, int color, ModuleType type) {
        DisplayName = displayName;
        Key = key;
        Color = color;
        ModuleType = type;
    }

    public Module(String displayName, String description, int key, int color, ModuleType moduleType) {
        this(displayName, key, color, moduleType);
        Description = description;
    }

    public void onEnable() {
        if (Hidden) return;
        Notification notification = (Notification) ModuleManager.Get().GetMod(Notification.class);
        /// allow events to be called
        SalHackMod.NORBIT_EVENT_BUS.subscribe(this);
        ModuleManager.Get().OnModEnable(this);
        if (mc.player != null) {
            RemainingXAnimation = mc.textRenderer.getWidth(GetFullArrayListDisplayName())+10f;
            if (notification.isEnabled()) ChatUtils.sendMessage(DisplayName + Formatting.GREEN + " ON");
        }
        SalHackMod.NORBIT_EVENT_BUS.post(new ModuleEvent.Enabled(this));
    }

    public void onDisable() {
        if (Hidden) return;
        Notification notification = (Notification) ModuleManager.Get().GetMod(Notification.class);
        /// disallow events to be called
        SalHackMod.NORBIT_EVENT_BUS.unsubscribe(this);
        SalHackMod.NORBIT_EVENT_BUS.post(new ModuleEvent.Disabled(this));
        if (mc.player != null && notification.isEnabled()) ChatUtils.sendMessage(DisplayName + Formatting.RED + " OFF");
    }

    public void onToggle() {}

    public void toggle(boolean save) {
        setEnabled(!isEnabled());
        if (isEnabled()) onEnable();
        else onDisable();
        onToggle();
        if (save) SaveSettings();
    }

    public void ToggleOnlySuper() {
        setEnabled(!isEnabled());
        onToggle();
    }

    public String getMetaData() {
        return null;
    }

    public void unload() {
        ValueList.clear();
    }

    public enum ModuleType {
        COMBAT, EXPLOIT, MOVEMENT, RENDER, WORLD, MISC, HIDDEN, UI, BOT, LITEMATICA, HIGHWAY, DONATE
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
        CommandManager.Get().Reload();
        SaveSettings();
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public int getKey() {
        return Key;
    }

    public boolean isKeyPressed(int KeyCode) {
        if (mc.currentScreen != null) return false;
        return Key == KeyCode;
    }

    public void setKey(int key) {
        Key = key;
        SaveSettings();
    }

    public int getColor() {
        return Color;
    }

    public void setColor(int color) {
        Color = color;
    }

    public boolean isHidden() {
        return Hidden;
    }

    public void setHidden(boolean hidden) {
        Hidden = hidden;
        SaveSettings();
    }

    public boolean isEnabled() {
        return Enabled;
    }

    public void setEnabled(boolean enabled) {
        Enabled = enabled;
    }

    public ModuleType getModuleType() {
        return ModuleType;
    }

    public void setModuleType(ModuleType moduleType) {
        ModuleType = moduleType;
    }

    public List<Value> getValueList() {
        return ValueList;
    }

    public void setValueList(List<Value> valueList) {
        ValueList = valueList;
    }

    public float GetRemainingXOffset() {
        return RemainingXAnimation;
    }

    public void SignalEnumChange() {}

    public void signalValueChange(Value value) {
        SaveSettings();
    }

    public List<Value> getVisibleValues() {
        return ValueList;
    }

    /// functions for updating value in an async way :)
    public void setClickGuiValueUpdate(boolean value) {
        ClickGuiValueUpdate = value;
    }

    public boolean needsClickGuiValueUpdate() {
        return ClickGuiValueUpdate;
    }

    public String GetNextStringValue(final Value<String> value, boolean recursive) {
        // TODO Auto-generated method stub
        return null;
    }

    public String GetArrayListDisplayName() {
        return getDisplayName();
    }

    public String GetFullArrayListDisplayName() {
        return getDisplayName() + (getMetaData() != null ? " " + Formatting.GRAY + getMetaData() : "");
    }

    public void SaveSettings() {
        PresetsManager.Get().getActivePreset().addModuleSettings(this);
    }

    public void init() {}
}
