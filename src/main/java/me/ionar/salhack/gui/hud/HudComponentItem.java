package me.ionar.salhack.gui.hud;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.managers.CommandManager;
import me.ionar.salhack.managers.HudManager;
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
    public ArrayList<Value> ValueList = new ArrayList<>();
    private String DisplayName;
    private float X;
    private float Y;
    private final float DefaultX;
    private final float DefaultY;
    private float Width;
    private float Height;

    protected float DeltaX;
    protected float DeltaY;
    protected float ClampX;
    protected float ClampY;
    private int Flags;

    private boolean Hidden = true;
    private boolean Dragging = false;
    protected int ClampLevel = 0;
    protected int Side = 0;
    private boolean Selected = false;
    private boolean MultiSelectedDragging = false;

    protected MinecraftClient mc = Wrapper.GetMC();

    public HudComponentItem(String displayName, float x, float y) {
        DisplayName = displayName;
        X = x;
        Y = y;
        DefaultX = x;
        DefaultY = y;
    }

    public String GetDisplayName() {
        return DisplayName;
    }

    public void SetWidth(float width) {
        Width = width;
    }

    public void SetHeight(float height) {
        Height = height;
    }

    public float GetWidth() {
        return Width;
    }

    public float GetHeight() {
        return Height;
    }

    public boolean IsHidden() {
        return Hidden;
    }

    public void SetHidden(boolean hide) {
        Hidden = hide;
        HudManager.Get().ScheduleSave(this);
    }

    public float GetX() {
        return X;
    }

    public float GetY() {
        return Y;
    }

    public void SetX(float x) {
        if (X == x) return;
        X = x;
        if (ClampLevel == 0) HudManager.Get().ScheduleSave(this);
    }

    public void SetY(float y) {
        if (Y == y) return;
        Y = y;
        if (ClampLevel == 0) HudManager.Get().ScheduleSave(this);
    }

    public boolean IsDragging() {
        return Dragging;
    }

    public void SetDragging(boolean dragging) {
        Dragging = dragging;
    }

    protected void SetClampPosition(float x, float y) {
        ClampX = x;
        ClampY = y;
    }

    protected void SetClampLevel(int clampLevel) {
        ClampLevel = clampLevel;
    }

    /// don't override unless you return this
    public boolean Render(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        boolean inside = mouseX >= GetX() && mouseX < GetX() + GetWidth() && mouseY >= GetY() && mouseY < GetY() + GetHeight();
        if (inside) context.fill((int) GetX(), (int) GetY(), (int) (GetX()+GetWidth()), (int) (GetY()+GetHeight()), 0x50384244);
        if (IsDragging()) {
            Window res = mc.getWindow();
            float x = mouseX - DeltaX;
            float y = mouseY - DeltaY;
            SetX(Math.min(Math.max(0, x), res.getScaledWidth()-GetWidth()));
            SetY(Math.min(Math.max(0, y), res.getScaledHeight()-GetHeight()));
        }
        /*else if (Clamped)
        {
            SetX(ClampX);
            SetY(ClampY);
        }*/

        render(mouseX, mouseY, partialTicks, context);

        if (IsSelected()) context.fill((int) GetX(), (int) GetY(), (int) (GetX()+GetWidth()), (int) (GetY()+GetHeight()), 0x35DDDDDD);

        return inside;
    }

    /// override for childs
    public void render(int mouseX, int mouseY, float partialTicks, DrawContext context) {}

    public boolean OnMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseX >= GetX() && mouseX < GetX() + GetWidth() && mouseY >= GetY() && mouseY < GetY() + GetHeight()) {
            if (mouseButton == 0) {
                SetDragging(true);
                DeltaX = mouseX - GetX();
                DeltaY = mouseY - GetY();
                HudManager.Get().ComponentItems.forEach(componentItem -> {
                    if (componentItem.IsMultiSelectedDragging()) {
                        componentItem.SetDragging(true);
                        componentItem.SetDeltaX(mouseX - componentItem.GetX());
                        componentItem.SetDeltaY(mouseY - componentItem.GetY());
                    }
                });
            } else if (mouseButton == 1) {
                ++Side;
                if (Side > 3) Side = 0;
                HudManager.Get().ScheduleSave(this);
            }
            else if (mouseButton == 2) {
                ++ClampLevel;
                if (ClampLevel > 2) ClampLevel = 0;
                SetClampPosition(GetX(), GetY());
                HudManager.Get().ScheduleSave(this);
            }
            return true;
        }
        return false;
    }

    public void SetDeltaX(float x) {
        DeltaX = x;
    }

    public void SetDeltaY(float y) {
        DeltaY = y;
    }

    public void OnMouseRelease(int mouseX, int mouseY, int state) {
        SetDragging(false);
    }

    public void LoadSettings() {
        File exists = new File("SalHack/HUD/"+GetDisplayName()+".json");
        if (!exists.exists()) return;
        String content = SalHack.GetFilesManager().read("SalHack/HUD/"+GetDisplayName()+".json");
        Map<?, ?> map = SalHack.gson.fromJson(content, Map.class);
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String key = (String)entry.getKey();
            String value = (String)entry.getValue();
            if (key.equalsIgnoreCase("displayname")) {
                SetDisplayName(value, false);
                continue;
            }
            if (key.equalsIgnoreCase("visible")) {
                SetHidden(value.equalsIgnoreCase("false"));
                continue;
            }
            if (key.equalsIgnoreCase("PositionX")) {
                SetX(Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("PositionY")) {
                SetY(Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("ClampLevel")) {
                SetClampLevel(Integer.parseInt(value));
                continue;
            }
            if (key.equalsIgnoreCase("ClampPositionX")) {
                ClampX = (Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("ClampPositionY")) {
                ClampY = (Float.parseFloat(value));
                continue;
            }
            if (key.equalsIgnoreCase("Side")) {
                Side = Integer.parseInt(value);
                continue;
            }
            for (Value value2 : ValueList) {
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
        return Side;
    }

    public int GetClampLevel() {
        return ClampLevel;
    }

    public boolean HasFlag(int flag) {
        return (Flags & flag) != 0;
    }

    public void AddFlag(int flags) {
        Flags |= flags;
    }

    public static int OnlyVisibleInHudEditor = 0x1;

    public void ResetToDefaultPos() {
        SetX(DefaultX);
        SetY(DefaultY);
    }

    public void SetSelected(boolean selected) {
        Selected = selected;
    }

    public boolean IsInArea(float mouseX1, float mouseX2, float mouseY1, float mouseY2) {
        return GetX() >= mouseX1 && GetX()+GetWidth() <= mouseX2 && GetY() >= mouseY1 && GetY()+GetHeight() <= mouseY2;
    }

    public boolean IsSelected() {
        return Selected;
    }

    public void SetMultiSelectedDragging(boolean multiDragging) {
        MultiSelectedDragging = multiDragging;
    }

    public boolean IsMultiSelectedDragging() {
        return MultiSelectedDragging;
    }

    public void SetDisplayName(String newName, boolean save) {
        DisplayName = newName;
        if (save) {
            HudManager.Get().ScheduleSave(this);
            CommandManager.Get().Reload();
        }
    }

    public int GetTextColor() {
        return (HudModule.Red.getValue() << 16) & 0x00FF0000 | (HudModule.Green.getValue() << 8) & 0x0000FF00 | HudModule.Blue.getValue() & 0x000000FF;
    }
}
