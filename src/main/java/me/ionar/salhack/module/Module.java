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
    public String displayName;
    private String[] alias;
    private String description;
    public int key;
    private int color;
    public boolean hidden = false;
    private boolean enabled = false;
    private ModuleType moduleType;
    private boolean clickGuiValueUpdate;
    public List<Value> values = new ArrayList<>();
    public float remainingXAnimation = 0f;
    protected final MinecraftClient mc = Wrapper.GetMC();

    private Module(String displayName, String[] alias, int key, int color, ModuleType type) {
        this.displayName = displayName;
        this.alias = alias;
        this.key = key;
        this.color = color;
        moduleType = type;
    }

    public Module(String displayName, String[] alias, String description, int key, int color, ModuleType moduleType) {
        this(displayName, alias, key, color, moduleType);
        this.description = description;
    }

    public void onEnable() {
        Notification notification = (Notification) SalHack.getModuleManager().getMod(Notification.class);
        /// allow events to be called
        SalHackMod.NORBIT_EVENT_BUS.subscribe(this);
        SalHack.getModuleManager().onModEnable(this);
        if (mc.player != null) {
            remainingXAnimation = mc.textRenderer.getWidth(getFullArrayListDisplayName())+10f;
            if (notification.isEnabled()) mc.player.sendMessage(Text.of(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + displayName + Formatting.GREEN + " ON"));
        }
        SalHackMod.NORBIT_EVENT_BUS.post(new ModuleEvent.Enabled(this));
    }

    public void onDisable() {
        Notification notification = (Notification) SalHack.getModuleManager().getMod(Notification.class);
        /// disallow events to be called
        SalHackMod.NORBIT_EVENT_BUS.unsubscribe(this);
        SalHackMod.NORBIT_EVENT_BUS.post(new ModuleEvent.Disabled(this));
        if (mc.player != null && notification.isEnabled()) SalHack.sendMessage(Formatting.AQUA + "[Salhack] " + Formatting.WHITE + displayName + Formatting.RED + " OFF");
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
        for (Value value : getValues()) {
            for (String string : value.getAlias()) {
                if (alias.equalsIgnoreCase(string)) return value;
            }
            if (value.getName().equalsIgnoreCase(alias)) return value;
        }
        return null;
    }

    public void unload() {
        values.clear();
    }

    public enum ModuleType {
        COMBAT, EXPLOIT, MOVEMENT, RENDER, WORLD, MISC, HIDDEN, UI, BOT, LITEMATICA, HIGHWAY, DONATE
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        SalHack.getCommandManager().reload();
        saveSettings();
    }

    public String[] getAlias() {
        return alias;
    }

    public void setAlias(String[] alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getKey() {
        return key;
    }

    public boolean isKeyPressed(int KeyCode) {
        if (mc.currentScreen != null) return false;
        return key == KeyCode;
    }

    public void setKey(int key) {
        this.key = key;
        saveSettings();
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
        saveSettings();
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public ModuleType getModuleType() {
        return moduleType;
    }

    public void setModuleType(ModuleType moduleType) {
        this.moduleType = moduleType;
    }

    public List<Value> getValues() {
        return values;
    }

    public void setValues(List<Value> values) {
        this.values = values;
    }

    public float setRemainingXOffset() {
        return remainingXAnimation;
    }

    public void signalEnumChange() {}

    public void signalValueChange(Value value) {
        saveSettings();
    }

    public List<Value> getVisibleValues() {
        return values;
    }

    /// functions for updating value in an async way :)
    public void setClickGuiValueUpdate(boolean value) {
        clickGuiValueUpdate = value;
    }

    public boolean isClickGuiValueUpdate() {
        return clickGuiValueUpdate;
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
