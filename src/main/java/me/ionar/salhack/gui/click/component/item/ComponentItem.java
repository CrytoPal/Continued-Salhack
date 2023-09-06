package me.ionar.salhack.gui.click.component.item;

import java.util.ArrayList;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;

public class ComponentItem
{
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

    private String DisplayText;
    private String Description;
    protected int Flags;
    protected int State;
    protected ComponentItemListener Listener;
    private float X;
    private float Y;
    private float Width;
    private float Height;
    protected float currentWidth;

    public ArrayList<ComponentItem> DropdownItems;

    public ComponentItem(String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height)
    {
        DisplayText = displayText;
        Description = description;
        Flags = flags;
        State = state;
        Listener = listener;

        DropdownItems = new ArrayList<ComponentItem>();

        X = 0;
        Y = 0;
        Width = width;
        Height = height;
        currentWidth = Width;
    }

    public String GetDisplayText()
    {
        return DisplayText;
    }

    public String GetDescription()
    {
        return Description;
    }

    public boolean HasFlag(int flag)
    {
        return (Flags & flag) != 0;
    }

    public boolean HasState(int state)
    {
        return (State & state) != 0;
    }

    public void AddState(int state)
    {
        State |= state;
    }

    public void RemoveState(int state)
    {
        State &= ~state;
    }

    public float GetX()
    {
        return X;
    }

    public void SetX(float x)
    {
        X = x;
    }

    public float GetY()
    {
        return Y;
    }

    public void SetY(float y)
    {
        Y = y;
    }

    public float GetWidth()
    {
        return Width;
    }

    public void SetWidth(float width)
    {
        Width = width;
    }

    public float GetHeight()
    {
        return Height;
    }

    public void SetHeight(float height)
    {
        Height = height;
    }

    public float GetCurrentWidth()
    {
        return currentWidth;
    }

    public void OnMouseClick(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            if (Listener != null)
                Listener.OnToggled();

            if (HasState(Clicked))
                RemoveState(Clicked);
            else
                AddState(Clicked);
        }
        else if (mouseButton == 1)
        {
            if (HasState(Extended))
                RemoveState(Extended);
            else
                AddState(Extended);
        }
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers)
    {
    }

    public void OnMouseMove(float mouseX, float mouseY, float x, float y)
    {
    }

    public void Update()
    {
    }

    public void OnMouseRelease(int mouseX, int mouseY)
    {
        // TODO Auto-generated method stub

    }

    public void OnMouseClickMove(int mouseX, int mouseY, int mouseButton)
    {
        // TODO Auto-generated method stub

    }
}
