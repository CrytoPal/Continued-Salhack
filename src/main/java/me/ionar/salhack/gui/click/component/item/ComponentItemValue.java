package me.ionar.salhack.gui.click.component.item;

import java.math.BigDecimal;
import java.math.RoundingMode;

import me.ionar.salhack.main.Wrapper;
import org.lwjgl.glfw.GLFW;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ComponentItemValue extends ComponentItem {
    final Value value;
    private boolean IsDraggingSlider = false;
    private final Timer timer = new Timer();
    private String DisplayString = "";
    private boolean isEditingString = false;

    public ComponentItemValue(final Value valueObject, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        value = valueObject;

        if (valueObject.getValue() instanceof Number && !(valueObject.getValue() instanceof Enum)) {
            Flags |= ComponentItem.Slider;
            Flags |= ComponentItem.DontDisplayClickableHighlight;
            Flags |= ComponentItem.RectDisplayAlways;

            this.SetCurrentWidth(CalculateXPositionFromValue(valueObject));
        }
        else if (valueObject.getValue() instanceof Boolean) {
            Flags |= ComponentItem.Boolean;
            Flags |= ComponentItem.RectDisplayOnClicked;
            Flags |= ComponentItem.DontDisplayClickableHighlight;

            if ((Boolean) valueObject.getValue())
                State |= ComponentItem.Clicked;
        } else if (valueObject.getValue() instanceof Enum) {
            Flags |= ComponentItem.Enum;
            Flags |= ComponentItem.DontDisplayClickableHighlight;
            Flags |= ComponentItem.RectDisplayAlways;
        } else if (valueObject.getValue() instanceof String) Flags |= ComponentItem.Enum;
    }

    private void SetCurrentWidth(float width) {
        currentWidth = width;
    }

    @Override
    public void Update() {}

    @Override
    public boolean HasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return value.getValue() instanceof Boolean ? (Boolean) value.getValue() : true;
        return super.HasState(state);
    }

    public float CalculateXPositionFromValue(final Value valueObject) {
        float minX = GetX();
        float maxX = GetX() + GetWidth();
        if (valueObject.getMax() == null) return minX;
        Number value = (Number) valueObject.getValue();
        Number max = (Number) valueObject.getMax();
        return (maxX - minX) * (value.floatValue() / max.floatValue());
    }

    @Override
    public String GetDisplayText() {
        if (value.getValue() instanceof Boolean) {
            String displayText = value.getName();

            if (HasState(ComponentItem.Hovered) && Wrapper.GetMC().textRenderer.getWidth(displayText) > GetWidth() - 3) {
                if (DisplayString == null) DisplayString = value.getName();

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

        String displayText = value.getName() + " " + (value.getValue() == null ? "null" : value.getValue().toString()) + " ";

        if (HasState(ComponentItem.Hovered) && Wrapper.GetMC().textRenderer.getWidth(displayText) > GetWidth() - 3) {
            if (DisplayString == null) DisplayString = value.getName() + " " + value.getValue().toString() + " ";

            displayText = DisplayString;
            float width = Wrapper.GetMC().textRenderer.getWidth(displayText);

            while (width > GetWidth() - 3) {
                width = Wrapper.GetMC().textRenderer.getWidth(displayText);
                displayText = displayText.substring(0, displayText.length() - 1);
            }

            if (timer.passed(75) && !DisplayString.isEmpty()) {
                String firstChar = String.valueOf(DisplayString.charAt(0));

                DisplayString = DisplayString.substring(1) + firstChar;

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
    public void OnMouseClick(int mouseX, int mouseY, int mouseButton) {
        super.OnMouseClick(mouseX, mouseY, mouseButton);
        if (value.getValue() instanceof Enum) value.setEnumValue(value.getNextEnum(mouseButton == 1));
        else if (value.getValue() instanceof String) {
            isEditingString = !isEditingString;
            value.setValue("");
        }
        else if (value.getValue() instanceof Boolean) value.setValue(!(Boolean) value.getValue());
        else IsDraggingSlider = !IsDraggingSlider;
        // SalHack.INSTANCE.getNotificationManager().addNotification(Mod.getDisplayName(), "Changed the value of " + value.getName() + " to " + value.getValue().toString());
    }

    @Override
    public void OnMouseRelease(int mouseX, int mouseY) {
        if (IsDraggingSlider) IsDraggingSlider = false;
        //SalHack.GetNotificationManager().AddNotification("hi", "Changed the value of " + value.getName() + " to " + value.getValue().toString());
    }

    @Override
    public void OnMouseMove(float mouseX, float mouseY, float X, float Y) {
        if (!HasFlag(ComponentItem.Slider)) return;
        if (!IsDraggingSlider) return;
        float x = X + GetX();
        if (mouseX >= x && mouseX <= X + GetX() + GetWidth()) x = mouseX;
        if (mouseX > X + GetX() + GetWidth()) x = X + GetX() + GetWidth();
        x -= X;
        SetCurrentWidth(x - GetX());
        // Slider.SetX(l_X - GetX());
        float pct = (x - GetX()) / GetWidth();
        // stupid hacks below because java sux it shd rly static assert or make compile error instead of crash when it reaches this point lol
        // could also fix all values but meh…
        if (value.getValue().getClass() == Float.class) {
            BigDecimal decimal = new BigDecimal((this.value.getMax().getClass() == Float.class ? (Float) this.value.getMax() : this.value.getMax().getClass() == Double.class ? (Double) this.value.getMax() : (Integer) value.getMax()) * pct);
            this.value.setValue(decimal.setScale(2, RoundingMode.HALF_EVEN).floatValue());
        } else if (value.getValue().getClass() == Double.class) {
            BigDecimal decimal = new BigDecimal((this.value.getMax().getClass() == Double.class ? (Double) this.value.getMax() : this.value.getMax().getClass() == Float.class ? (Float) this.value.getMax() : (Integer) value.getMax()) * pct);
            this.value.setValue(decimal.setScale(2, RoundingMode.HALF_EVEN).doubleValue());
        } else if (value.getValue().getClass() == Integer.class) this.value.setValue((int) ((int) this.value.getMax() * pct));
        // salhack.INSTANCE.logChat("Calculated Pct is " + (l_X-GetX())/GetWidth());
    }

    @Override
    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        if (isEditingString) {
            if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_ENTER) {
                //if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE) || Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
                isEditingString = false;
                return;
            }

            String string = (String)value.getValue();
            if (string == null) return;
            String keyName = GLFW.glfwGetKeyName(keyCode, 0);
            if (keyCode == GLFW.GLFW_KEY_BACKSPACE && !string.isEmpty()) string = string.substring(0, string.length() - 1);
                //if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
            else if (keyName != null && (Character.isDigit(keyName.charAt(0)) || Character.isLetter(keyName.charAt(0)))) string += keyName.charAt(0);

            value.setValue(string);
        }
    }
}
