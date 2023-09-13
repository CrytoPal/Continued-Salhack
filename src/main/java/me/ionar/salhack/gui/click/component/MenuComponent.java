package me.ionar.salhack.gui.click.component;

import java.util.ArrayList;
import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.util.imgs.SalDynamicTexture;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import net.minecraft.util.Identifier;

public class MenuComponent {
    private final String displayName;
    protected ArrayList<ComponentItem> componentItems = new ArrayList<>();
    private final float defaultX;
    private final float defaultY;
    private float y;
    private float x;
    private float height;
    private final float width;
    private boolean dragging = false;
    private float deltaX = 0;
    private float deltaY = 0;
    private ComponentItem hoveredItem = null;
    private boolean minimized = false;
    private boolean isMinimizing = false;
    private float remainingMinimizingY;
    private boolean isMaximizing = false;
    private float remainingMaximizingY;
    private int mousePlayAnimation;
    private SalDynamicTexture barTexture = null;
    private final ColorsModule colorsModule;
    private final ClickGuiModule clickGuiModule;
    final float BorderLength = 15.0f;
    final float Padding = 3;
    private final MinecraftClient mc = Wrapper.GetMC();

    public MenuComponent(String displayName, float x, float y, float height, float width, String image, ColorsModule colorsModule, ClickGuiModule clickGuiModule) {
        this.displayName = displayName;
        defaultX = x;
        defaultY = y;
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        remainingMinimizingY = 0;
        remainingMaximizingY = 0;
        mousePlayAnimation = 0;
        if (image != null) barTexture = SalHack.getImageManager().getDynamicTexture(image);
        this.colorsModule = colorsModule;
        this.clickGuiModule = clickGuiModule;
    }

    public void addItem(ComponentItem componentItem) {
        componentItems.add(componentItem);
    }

    public static void drawTexture(Identifier icon, float x, float y, int width, int height, DrawContext context) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10241, 9987);
        context.drawTexture(icon, 0, 0, 0.0F, 0.0F, width, height, width, height);
        context.getMatrices().pop();
    }

    public boolean render(int mouseX, int mouseY, boolean canHover, boolean allowsOverflow, float offsetY, DrawContext context) {
        if (dragging) {
            x = mouseX - deltaX;
            y = mouseY - deltaY;
        }

        if (!allowsOverflow) {
            Window res = mc.getWindow();
            if (x+ getWidth() >= res.getScaledWidth()) x = res.getScaledWidth() - getWidth();
            else if (x < 0) x = 0;
            if (y+ getHeight() >= res.getScaledHeight()) y = res.getScaledHeight() - getHeight();
            else if (y < 0) y = 0;
        }

        for (ComponentItem componentItem : componentItems) componentItem.onMouseMove(mouseX, mouseY, getX(), getY()-offsetY);

        if (isMinimizing) {
            if (remainingMinimizingY > 0) {
                remainingMinimizingY -= 20;
                remainingMinimizingY = Math.max(remainingMinimizingY, 0);
                if (remainingMinimizingY == 0) {
                    minimized = true;
                    isMinimizing = false;
                    height = 17;
                }
            }
        } else if (isMaximizing) {
            if (remainingMaximizingY < 500) {
                remainingMaximizingY += 20;
                remainingMaximizingY = Math.min(remainingMaximizingY, 500);
                if (remainingMaximizingY == 500) {
                    isMaximizing = false;
                    height = 17;
                }
            }
        }

        context.fill((int) getX(), (int) (getY()+17-offsetY), (int) (getX()+ getWidth()), (int) (getY()+ getHeight()), 0x992A2A2A);
        context.fill((int) getX(), (int) (getY()-offsetY), (int) (getX() + getWidth()), (int) (getY() + 17-offsetY), 0x99000000);
        FontRenderers.getTwCenMtStd28().drawString(context.getMatrices(), getDisplayName(), (int) (getX() + 2), (int) (getY() + 2-offsetY), getTextColor(), false);

        if (barTexture != null) {
            float x = getX()+ getWidth()-15;
            drawTexture(new Identifier(barTexture.getResourceLocation()), (int) x, (int) (getY()+3-offsetY), barTexture.getWidth()/3, barTexture.getHeight()/3, context);
        }
        if (!minimized) {
            float Y = getY() + 5-offsetY;
            hoveredItem = null;
            boolean Break = false;
            for (ComponentItem componentItem : componentItems) {
                Y = displayComponentItem(componentItem, Y, mouseX, mouseY, canHover, false, isMinimizing ? remainingMinimizingY : (isMaximizing ? remainingMaximizingY : 0), context);
                float menuY = Math.abs(y - Y - BorderLength);
                if (isMinimizing && menuY >= remainingMinimizingY) Break = true;
                else if (isMaximizing && menuY >= remainingMaximizingY) Break = true;
                if (Break) break;
            }
            if (!Break) {
                isMinimizing = false;
                isMaximizing = false;
            }
            if (hoveredItem != null && (clickGuiModule != null ? clickGuiModule.hoverDescriptions.getValue() : true)) {
                if (hoveredItem.getDescription() != null && !Objects.equals(hoveredItem.getDescription(), "")) {
                    context.fill(mouseX+15, mouseY, (int) (mouseX+19+FontRenderers.getTwCenMtStd22().getStringWidth(hoveredItem.getDescription())), mouseY + mc.textRenderer.fontHeight+3, 0x90000000);
                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), hoveredItem.getDescription(), mouseX+17, mouseY, 0xFFFFFF);
                }
            }
            height = Math.abs(y - Y - 12);
        }
        if (mousePlayAnimation > 0) {
            mousePlayAnimation--;
            //RenderUtil.DrawPolygon(p_MouseX, p_MouseY, MousePlayAnim, 360, 0x99FFFFFF);
        }
        return canHover && mouseX > getX() && mouseX < getX() + getWidth() && mouseY > getY()-offsetY && mouseY < getY()+ getHeight()-offsetY;
    }

    public float displayComponentItem(ComponentItem componentItem, float Y, int mouseX, int mouseY, boolean canHover, boolean displayExtendedLine, final float maxY, DrawContext context) {
        Y += componentItem.getHeight();
        componentItem.onMouseMove(mouseX, mouseY, getX(), getY());
        componentItem.update();
        if (componentItem.hasState(ComponentItem.Extended)) context.fill((int) (x+1), (int) Y, (int) (x+componentItem.getWidth()-3), (int) (Y + mc.textRenderer.fontHeight + 3),0x080808);
        int color = 0xFFFFFF;
        boolean hovered = canHover && mouseX > x && mouseX < x+componentItem.getWidth() && mouseY > Y && mouseY < Y+componentItem.getHeight();
        boolean dropDown = componentItem.hasState(ComponentItem.Extended);
        if (hovered) {
            if (!dropDown) context.fill((int) getX(), (int) Y, (int) (getX()+componentItem.getWidth()), (int) (Y+11), 0x99040404);
                //RenderUtil.drawGradientRect(GetX(), p_Y, GetX()+p_Item.GetWidth(), p_Y+11, 0x99040404, 0x99000000);
            color = (componentItem.hasState(ComponentItem.Clicked) && !componentItem.hasFlag(ComponentItem.DontDisplayClickableHighlight)) ? getTextColor() : color;// - commented for issue #27
            hoveredItem = componentItem;
            componentItem.addState(ComponentItem.Hovered);
        } else {
            if (componentItem.hasState(ComponentItem.Clicked) && !componentItem.hasFlag(ComponentItem.DontDisplayClickableHighlight)) color = getTextColor();
            componentItem.removeState(ComponentItem.Hovered);
        }
        if (dropDown) context.fill((int) getX(), (int) Y, (int) (getX()+componentItem.getWidth()), (int) (Y+11), 0x99040404);
            //RenderUtil.drawGradientRect(GetX(), p_Y, GetX()+p_Item.GetWidth(), p_Y+11, 0x99040404, 0x99000000);
        if (componentItem.hasFlag(ComponentItem.RectDisplayAlways) || (componentItem.hasFlag(ComponentItem.RectDisplayOnClicked) && componentItem.hasState(ComponentItem.Clicked))) context.fill((int) getX(), (int) Y, (int) (getX()+componentItem.getCurrentWidth()), (int) (Y+11), getColor());
        FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), componentItem.getDisplayText(), x + Padding, Y, color);
        /*if (p_Item.HasFlag(ComponentItem.HasValues))
        {
            RenderUtil.drawLine(X + p_Item.GetWidth() - 1, p_Y, X + p_Item.GetWidth() - 1, p_Y + 11, 5, 0x9945B5E4);
        }

        if (componentItem.HasState(ComponentItem.Extended) || displayExtendedLine) {
            RenderUtil.drawLine(X + p_Item.GetWidth() - 1, p_Y, X + p_Item.GetWidth() - 1, p_Y + 11, 3, GetColor());
        }*/
        if (componentItem.hasState(ComponentItem.Extended)) {
            for (ComponentItem component : componentItem.dropdownItems) {
                Y = displayComponentItem(component, Y, mouseX, mouseY, canHover, true, maxY, context);
                if (maxY > 0) {
                    float menuY = Math.abs(y - Y - BorderLength);
                    if (menuY >= maxY) break;
                }
            }
        }
        return Y;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton, float offsetY) {
        if (mouseX > getX() && mouseX < getX() + getWidth() && mouseY > getY()-offsetY && mouseY < getY()+BorderLength-offsetY) {
            /// Dragging (Top border)
            if (mouseButton == 0) {
                dragging = true;
                deltaX = mouseX-x;
                deltaY = mouseY-y;
            } else if (mouseButton == 1) {
                /// Right click
                if (!minimized) {
                    isMinimizing = true;
                    remainingMinimizingY = height;
                    isMaximizing = false;
                } else {
                    minimized = false;
                    isMinimizing = false;
                    remainingMinimizingY = 0;
                    isMaximizing = true;
                }
                remainingMaximizingY = 0;
            }
        }
        if (hoveredItem != null) {
            hoveredItem.onMouseClick(mouseX, mouseY, mouseButton);
            if (mouseButton == 0) mousePlayAnimation = 20;
            return true;
        }
        return dragging;
    }

    public void mouseReleased(int mouseX, int mouseY) {
        if (dragging) dragging = false;
        for (ComponentItem componentItem : componentItems) handleMouseReleaseCompItem(componentItem, mouseX, mouseY);
    }

    public void handleMouseReleaseCompItem(ComponentItem componentItem, int mouseX, int mouseY) {
        componentItem.onMouseRelease(mouseX, mouseY);
        for (ComponentItem component : componentItem.dropdownItems) component.onMouseRelease(mouseX, mouseY);
    }

    public void mouseClickMove(int mouseX, int mouseY, int mouseButton) {
        for (ComponentItem componentItem : componentItems) handleMouseClickMoveCompItem(componentItem, mouseX, mouseY, mouseButton);
    }

    private void handleMouseClickMoveCompItem(ComponentItem componentItem, int mouseX, int mouseY, int mouseButton) {
        componentItem.onMouseClickMove(mouseX, mouseY, mouseButton);
        for (ComponentItem component : componentItem.dropdownItems) component.onMouseClickMove(mouseX, mouseY, mouseButton);
    }

    public String getDisplayName() {
        return displayName;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public void setX(float X) {
        x = X;
    }

    public void setY(float Y) {
        y = Y;
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        for (ComponentItem componentItem : componentItems) handleKeyTypedForItem(componentItem, keyCode, scanCode, modifiers);
    }

    public void handleKeyTypedForItem(ComponentItem componentItem, int keyCode, int scanCode, int modifiers) {
        componentItem.keyTyped(keyCode, scanCode, modifiers);
        for (ComponentItem component : componentItem.dropdownItems) handleKeyTypedForItem(component, keyCode, scanCode, modifiers);
    }

    private int getColor() {
        return (colorsModule.alpha.getValue() << 24) & 0xFF000000 | (colorsModule.red.getValue() << 16) & 0x00FF0000 | (colorsModule.green.getValue() << 8) & 0x0000FF00 | colorsModule.blue.getValue() & 0x000000FF;
    }

    public int getTextColor() {
        return (colorsModule.red.getValue() << 16) & 0x00FF0000 | (colorsModule.green.getValue() << 8) & 0x0000FF00 | colorsModule.blue.getValue() & 0x000000FF;
    }

    public void reset() {
        x = defaultX;
        y = defaultY;
        componentItems.forEach(comp -> {if (comp.hasState(ComponentItem.Extended)) comp.removeState(ComponentItem.Extended);});
    }
}