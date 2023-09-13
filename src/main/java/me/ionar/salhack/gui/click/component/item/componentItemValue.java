package me.ionar.salhack.gui.click.component.item;

import java.math.BigDecimal;
import java.math.RoundingMode;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;

import me.ionar.salhack.gui.click.component.listeners.ComponentItemListener;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;

@SuppressWarnings({"rawtypes", "unchecked"})
public class componentItemValue extends ComponentItem {
    final Value value;
    private boolean isDraggingSlider = false;
    private final Timer timer = new Timer();
    private String displayString = "";
    private boolean isEditingString = false;
    private final MinecraftClient mc = Wrapper.GetMC();

    public componentItemValue(final Value valueObject, String displayText, String description, int flags, int state, ComponentItemListener listener, float width, float height) {
        super(displayText, description, flags, state, listener, width, height);
        value = valueObject;
        if (valueObject.getValue() instanceof Number && !(valueObject.getValue() instanceof Enum)) {
            this.flags |= ComponentItem.Slider;
            this.flags |= ComponentItem.DontDisplayClickableHighlight;
            this.flags |= ComponentItem.RectDisplayAlways;
            this.setCurrentWidth(calculateXPositionFromValue(valueObject));
        } else if (valueObject.getValue() instanceof Boolean) {
            this.flags |= ComponentItem.Boolean;
            this.flags |= ComponentItem.RectDisplayOnClicked;
            this.flags |= ComponentItem.DontDisplayClickableHighlight;
            if ((Boolean) valueObject.getValue()) this.state |= ComponentItem.Clicked;
        } else if (valueObject.getValue() instanceof Enum) {
            this.flags |= ComponentItem.Enum;
            this.flags |= ComponentItem.DontDisplayClickableHighlight;
            this.flags |= ComponentItem.RectDisplayAlways;
        } else if (valueObject.getValue() instanceof String) this.flags |= ComponentItem.Enum;
    }

    private void setCurrentWidth(float width) {
        currentWidth = width;
    }

    @Override
    public boolean hasState(int state) {
        if ((state & ComponentItem.Clicked) != 0) return value.getValue() instanceof Boolean ? (Boolean) value.getValue() : true;
        return super.hasState(state);
    }

    public float calculateXPositionFromValue(final Value valueObject) {
        float minX = getX();
        float maxX = getX() + getWidth();
        if (valueObject.getMax() == null) return minX;
        Number value = (Number) valueObject.getValue();
        Number max = (Number) valueObject.getMax();
        return (maxX - minX) * (value.floatValue() / max.floatValue());
    }

    @Override
    public String getDisplayText() {
        if (value.getValue() instanceof Boolean) {
            String displayText = value.getName();
            if (hasState(ComponentItem.Hovered) && mc.textRenderer.getWidth(displayText) > getWidth() - 3) {
                if (displayString == null) displayString = value.getName();
                displayText = displayString;
                float width = mc.textRenderer.getWidth(displayText);
                while (width > getWidth() - 3) {
                    width = mc.textRenderer.getWidth(displayText);
                    displayText = displayText.substring(0, displayText.length() - 1);
                }
                if (timer.passed(75) && !displayString.isEmpty()) {
                    String l_FirstChar = String.valueOf(displayString.charAt(0));
                    displayString = displayString.substring(1) + l_FirstChar;
                    timer.reset();
                }
                return displayText;
            } else displayString = null;
            float width = mc.textRenderer.getWidth(displayText);
            while (width > getWidth() - 3) {
                width = mc.textRenderer.getWidth(displayText);
                displayText = displayText.substring(0, displayText.length() - 1);
            }
            return displayText;
        }
        String displayText = value.getName() + " " + (value.getValue() == null ? "null" : value.getValue().toString()) + " ";
        if (hasState(ComponentItem.Hovered) && mc.textRenderer.getWidth(displayText) > getWidth() - 3) {
            if (displayString == null) displayString = value.getName() + " " + value.getValue().toString() + " ";
            displayText = displayString;
            float width = mc.textRenderer.getWidth(displayText);
            while (width > getWidth() - 3) {
                width = mc.textRenderer.getWidth(displayText);
                displayText = displayText.substring(0, displayText.length() - 1);
            }
            if (timer.passed(75) && !displayString.isEmpty()) {
                String firstChar = String.valueOf(displayString.charAt(0));
                displayString = displayString.substring(1) + firstChar;
                timer.reset();
            }
            return displayText;
        } else displayString = null;
        float width = mc.textRenderer.getWidth(displayText);
        while (width > getWidth() - 3) {
            width = mc.textRenderer.getWidth(displayText);
            displayText = displayText.substring(0, displayText.length() - 1);
        }
        return displayText;
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        super.onMouseClick(mouseX, mouseY, mouseButton);
        if (value.getValue() instanceof Enum) value.setEnumValue(value.getNextEnum(mouseButton == 1));
        else if (value.getValue() instanceof String) {
            isEditingString = !isEditingString;
            value.setValue("");
        }
        else if (value.getValue() instanceof Boolean) value.setValue(!(Boolean) value.getValue());
        else isDraggingSlider = !isDraggingSlider;
        // SalHack.INSTANCE.getNotificationManager().addNotification(Mod.getDisplayName(), "Changed the value of " + value.getName() + " to " + value.getValue().toString());
    }

    @Override
    public void onMouseRelease(int mouseX, int mouseY) {
        if (isDraggingSlider) isDraggingSlider = false;
        //SalHack.GetNotificationManager().AddNotification("hi", "Changed the value of " + value.getName() + " to " + value.getValue().toString());
    }

    @Override
    public void onMouseMove(float mouseX, float mouseY, float X, float Y) {
        if (!hasFlag(ComponentItem.Slider)) return;
        if (!isDraggingSlider) return;
        float x = X + getX();
        if (mouseX >= x && mouseX <= X + getX() + getWidth()) x = mouseX;
        if (mouseX > X + getX() + getWidth()) x = X + getX() + getWidth();
        x -= X;
        setCurrentWidth(x - getX());
        // Slider.SetX(l_X - GetX());
        float pct = (x - getX()) / getWidth();
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
