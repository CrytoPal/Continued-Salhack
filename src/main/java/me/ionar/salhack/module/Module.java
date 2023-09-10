package me.ionar.salhack.module;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.salhack.ModuleEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.Notification;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public abstract class Module {
    public String DisplayName;
    private String[] Alias;
    private String Description;
    public int Key;
    private int Color;
    public boolean Hidden = false;
    private boolean Enabled = false;
    private ModuleType ModuleType;
    private boolean ClickGuiValueUpdate;
    public List<Value> ValueList = new ArrayList<>();
    public float RemainingXAnimation = 0f;
    protected final MinecraftClient mc = Wrapper.GetMC();

    private Module(String displayName, String[] alias, int key, int color, ModuleType type) {
        DisplayName = displayName;
        Alias = alias;
        Key = key;
        Color = color;
        ModuleType = type;
    }

    public Module(String displayName, String[] alias, String description, int key, int color, ModuleType moduleType) {
        this(displayName, alias, key, color, moduleType);
        Description = description;
    }

    public void onEnable() {
        Notification notification = (Notification) SalHack.getModuleManager().getMod(Notification.class);
        /// allow events to be called
        SalHackMod.NORBIT_EVENT_BUS.subscribe(this);
        SalHack.getModuleManager().onModEnable(this);
        if (mc.player != null) {
            RemainingXAnimation = mc.textRenderer.getWidth(getFullArrayListDisplayName())+10f;
            if (notification.isEnabled()) mc.player.sendMessage(Text.of(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + DisplayName + Formatting.GREEN + " ON"));
        }
        SalHackMod.NORBIT_EVENT_BUS.post(new ModuleEvent.Enabled(this));
    }

    public void onDisable() {
        Notification notification = (Notification) SalHack.getModuleManager().getMod(Notification.class);
        /// disallow events to be called
        SalHackMod.NORBIT_EVENT_BUS.unsubscribe(this);
        SalHackMod.NORBIT_EVENT_BUS.post(new ModuleEvent.Disabled(this));
        if (mc.player != null && notification.isEnabled()) SalHack.sendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + DisplayName + Formatting.RED + " OFF");
    }

    public void onToggle() {}

    public void toggle(boolean save) {
        setEnabled(!isEnabled());
        if (isEnabled()) onEnable();
        else onDisable();
        onToggle();
        if (save) saveSettings();
    }

    public void toggleOnlySuper() {
        setEnabled(!isEnabled());
        onToggle();
    }

    public String getMetaData() {
        return null;
    }

    public Value find(String alias) {
        for (Value value : getValueList()) {
            for (String string : value.getAlias()) {
                if (alias.equalsIgnoreCase(string)) return value;
            }
            if (value.getName().equalsIgnoreCase(alias)) return value;
        }
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
        SalHack.getCommandManager().reload();
        saveSettings();
    }

    public String[] getAlias() {
        return Alias;
    }

    public void setAlias(String[] alias) {
        Alias = alias;
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
        saveSettings();
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
        saveSettings();
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

    public float setRemainingXOffset() {
        return RemainingXAnimation;
    }

    public void SignalEnumChange() {}

    public void signalValueChange(Value value) {
        saveSettings();
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

    public String getNextStringValue(final Value<String> value, boolean recursive) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getArrayListDisplayName() {
        return getDisplayName();
    }

    public String getFullArrayListDisplayName() {
        return getDisplayName() + (getMetaData() != null ? " " + Formatting.GRAY + getMetaData() : "");
    }

    public void sendMessage(String message) {
        if (mc.player != null) SalHack.sendMessage(Formatting.AQUA + "[" + getArrayListDisplayName() + "]: " + Formatting.RESET + message);
    }

    public void saveSettings() {
        SalHack.getPresetsManager().getActivePreset().addModuleSettings(this);
    }

    public void init() {}
}
