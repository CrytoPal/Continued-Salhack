package me.ionar.salhack.gui.click.component.item;

import java.util.ArrayList;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;

public class ComponentItem {
    /// Flags
    public static final int Clickable = 0x1;
    public static final int Hoverable = 0x2;
    public static final int Tooltip   = 0x4;
    public static final int HasValues = 0x8;
    public static final int RectDisplayAlways = 0x10;
    public static final int Slider = 0x20;
    public static final int Boolean = 0x40;
    public static final int Enum = 0x80;
    public static final int DontDisplayClickableHighlight = 0x100;
    public static final int RectDisplayOnClicked = 0x200;

    /// State
    public static final int Clicked = 0x1;
    public static final int Hovered = 0x2;
    public static final int Extended = 0x4;

    private final String displayText;
    private final String description;
    protected int flags;
    protected int state;
    protected ComponentItemListener listener;
    private float x;
    private float y;
    private float width;
    private float height;
    protected float currentWidth;
    public ArrayList<ComponentItem> dropdownItems;

    public ComponentItem(String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        this.displayText = displayText;
        this.description = description;
        this.flags = flags;
        this.state = state;
        this.listener = listener;
        dropdownItems = new ArrayList<>();
        x = 0;
        y = 0;
        this.width = width;
        this.height = height;
        currentWidth = this.width;
    }

    public String getDisplayText() {
        return displayText;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasFlag(int flag) {
        return (flags & flag) != 0;
    }

    public boolean hasState(int state) {
        return (this.state & state) != 0;
    }

    public void addState(int state) {
        this.state |= state;
    }

    public void removeState(int state) {
        this.state &= ~state;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getCurrentWidth() {
        return currentWidth;
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            if (listener != null) listener.OnToggled();
            if (hasState(Clicked)) removeState(Clicked);
            else addState(Clicked);
        } else if (mouseButton == 1) {
            if (hasState(Extended)) removeState(Extended);
            else addState(Extended);
        }
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {}

    public void onMouseMove(float mouseX, float mouseY, float x, float y) {}

    public void update() {}

    public void onMouseRelease(int mouseX, int mouseY) {}

    public void onMouseClickMove(int mouseX, int mouseY, int mouseButton) {}
}
