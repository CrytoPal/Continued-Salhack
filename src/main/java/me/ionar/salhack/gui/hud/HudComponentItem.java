package me.ionar.salhack.gui.hud;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

@SuppressWarnings({"rawtypes", "BooleanMethodIsAlwaysInverted", "unchecked"})
public class HudComponentItem {
    public ArrayList<Value> values = new ArrayList<>();
    private String displayName;
    private float x;
    private float y;
    private final float defaultX;
    private final float defaultY;
    private float width;
    private float height;

    protected float deltaX;
    protected float deltaY;
    protected float clampX;
    protected float clampY;
    private int flags;

    private boolean hidden = true;
    private boolean dragging = false;
    protected int clampLevel = 0;
    protected int side = 0;
    private boolean selected = false;
    private boolean multiSelectedDragging = false;

    protected MinecraftClient mc = Wrapper.GetMC();

    public HudComponentItem(String displayName, float x, float y) {
        this.displayName = displayName;
        this.x = x;
        this.y = y;
        defaultX = x;
        defaultY = y;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hide) {
        hidden = hide;
        SalHack.getHudManager().scheduleSave(this);
    }

    public float getPositionX() {
        return x;
    }

    public float getPositionY() {
        return y;
    }

    public void setX(float x) {
        if (this.x == x) return;
        this.x = x;
        if (clampLevel == 0) SalHack.getHudManager().scheduleSave(this);
    }

    public void setY(float y) {
        if (this.y == y) return;
        this.y = y;
        if (clampLevel == 0) SalHack.getHudManager().scheduleSave(this);
    }

    public boolean isDragging() {
        return dragging;
    }

    public void setDragging(boolean dragging) {
        this.dragging = dragging;
    }

    protected void setClampPosition(float x, float y) {
        clampX = x;
        clampY = y;
    }

    protected void setClampLevel(int clampLevel) {
        this.clampLevel = clampLevel;
    }

    /// don't override unless you return this
    public boolean render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        boolean inside = mouseX >= getPositionX() && mouseX < getPositionX() + getWidth() && mouseY >= getPositionY() && mouseY < getPositionY() + getHeight();
        if (inside) context.fill((int) getPositionX(), (int) getPositionY(), (int) (getPositionX()+ getWidth()), (int) (getPositionY()+ getHeight()), 0x50384244);
        if (isDragging()) {
            Window res = mc.getWindow();
            float x = mouseX - deltaX;
            float y = mouseY - deltaY;
            setX(Math.min(Math.max(0, x), res.getScaledWidth()- getWidth()));
            setY(Math.min(Math.max(0, y), res.getScaledHeight()- getHeight()));
        }
        /*else if (Clamped)
        {
            SetX(ClampX);
            SetY(ClampY);
        }*/

        onRender(mouseX, mouseY, partialTicks, context);

        if (IsSelected()) context.fill((int) getPositionX(), (int) getPositionY(), (int) (getPositionX()+ getWidth()), (int) (getPositionY()+ getHeight()), 0x35DDDDDD);

        return inside;
    }

    /// override for childs
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {}

    public boolean onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= getPositionX() && mouseX < getPositionX() + getWidth() && mouseY >= getPositionY() && mouseY < getPositionY() + getHeight()) {
            if (mouseButton == 0) {
                setDragging(true);
                deltaX = mouseX - getPositionX();
                deltaY = mouseY - getPositionY();
                SalHack.getHudManager().componentItems.forEach(componentItem -> {
                    if (componentItem.IsMultiSelectedDragging()) {
                        componentItem.setDragging(true);
                        componentItem.setDeltaX(mouseX - componentItem.getPositionX());
                        componentItem.setDeltaY(mouseY - componentItem.getPositionY());
                    }
                });
            } else if (mouseButton == 1) {
                ++side;
                if (side > 3) side = 0;
                SalHack.getHudManager().scheduleSave(this);
            }
            else if (mouseButton == 2) {
                ++clampLevel;
                if (clampLevel > 2) clampLevel = 0;
                setClampPosition(getPositionX(), getPositionY());
                SalHack.getHudManager().scheduleSave(this);
            }
            return true;
        }
        return false;
    }

    public void setDeltaX(float x) {
        deltaX = x;
    }

    public void setDeltaY(float y) {
        deltaY = y;
    }

    public void onMouseRelease(int mouseX, int mouseY, int state) {
        setDragging(false);
    }

    public void loadSettings() {
        File exists = new File("SalHack/HUD/"+ getDisplayName()+".json");
        if (!exists.exists()) return;
        String content = SalHack.getFilesManager().read("SalHack/HUD/"+ getDisplayName()+".json");
        Map<?, ?> map = SalHack.gson.fromJson(content, Map.class);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (key.equalsIgnoreCase("displayname")) {
                SetDisplayName(value, false);
                continue;
            }
            if (key.equalsIgnoreCase("visible")) {
                setHidden(value.equalsIgnoreCase("false"));
                continue;
            }
            if (key.equalsIgnoreCase("PositionX")) {
                setX(Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("PositionY")) {
                setY(Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("ClampLevel")) {
                setClampLevel(Integer.parseInt(value));
                continue;
            }
            if (key.equalsIgnoreCase("ClampPositionX")) {
                clampX = (Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("ClampPositionY")) {
                clampY = (Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("Side")) {
                side = Integer.parseInt(value);
                continue;
            }
            for (Value value2 : values) {
                if (value2.getName().equalsIgnoreCase((String) entry.getKey())) {
                    if (value2.getValue() instanceof Number && !(value2.getValue() instanceof Enum)) {
                        if (value2.getValue() instanceof Integer) value2.SetForcedValue(Integer.parseInt(value));
                        else if (value2.getValue() instanceof Float) value2.SetForcedValue(Float.parseFloat(value));
                        else if (value2.getValue() instanceof Double) value2.SetForcedValue(Double.parseDouble(value));
                    }
                    else if (value2.getValue() instanceof Boolean) value2.SetForcedValue(value.equalsIgnoreCase("true"));
                    else if (value2.getValue() instanceof Enum) value2.SetForcedValue(value2.GetEnumReal(value));
                    else if (value2.getValue() instanceof String) value2.SetForcedValue(value);
                    break;
                }
            }
        }
    }

    public int GetSide() {
        return side;
    }

    public int GetClampLevel() {
        return clampLevel;
    }

    public boolean HasFlag(int flag) {
        return (flags & flag) != 0;
    }

    public void AddFlag(int flags) {
        this.flags |= flags;
    }

    public static int OnlyVisibleInHudEditor = 0x1;

    public void ResetToDefaultPos() {
        setX(defaultX);
        setY(defaultY);
    }

    public void SetSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean IsInArea(float mouseX1, float mouseX2, float mouseY1, float mouseY2) {
        return getPositionX() >= mouseX1 && getPositionX()+ getWidth() <= mouseX2 && getPositionY() >= mouseY1 && getPositionY()+ getHeight() <= mouseY2;
    }

    public boolean IsSelected() {
        return selected;
    }

    public void SetMultiSelectedDragging(boolean multiDragging) {
        multiSelectedDragging = multiDragging;
    }

    public boolean IsMultiSelectedDragging() {
        return multiSelectedDragging;
    }

    public void SetDisplayName(String newName, boolean save) {
        displayName = newName;
        if (save) {
            SalHack.getHudManager().scheduleSave(this);
            SalHack.getCommandManager().reload();
        }
    }

    public int GetTextColor() {
        return (HudModule.Red.getValue() << 16) & 0x00FF0000 | (HudModule.Green.getValue() << 8) & 0x0000FF00 | HudModule.Blue.getValue() & 0x000000FF;
    }
}
