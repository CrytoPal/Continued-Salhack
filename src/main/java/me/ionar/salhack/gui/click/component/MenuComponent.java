package me.ionar.salhack.gui.click.component;

import java.util.ArrayList;
import java.util.Objects;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ionar.salhack.font.FontRenderers;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.util.imgs.SalDynamicTexture;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import me.ionar.salhack.gui.click.component.item.ComponentItem;
import me.ionar.salhack.main.Wrapper;
import me.ionar.salhack.module.ui.ClickGuiModule;
import me.ionar.salhack.module.ui.ColorsModule;
import net.minecraft.util.Identifier;

public class MenuComponent {
    private final String DisplayName;
    protected ArrayList<ComponentItem> Items = new ArrayList<>();
    private final float DefaultX;
    private final float DefaultY;
    private float y;
    private float x;
    private float Height;
    private final float Width;
    private boolean Dragging = false;
    private float DeltaX = 0;
    private float DeltaY = 0;
    private ComponentItem HoveredItem = null;
    private boolean Minimized = false;
    private boolean IsMinimizing = false;
    private float RemainingMinimizingY;
    private boolean IsMaximizing = false;
    private float RemainingMaximizingY;
    private int MousePlayAnim;
    private SalDynamicTexture BarTexture = null;
    private final ColorsModule Colors;
    private final ClickGuiModule ClickGUI;

    public MenuComponent(String displayName, float X, float Y, float height, float width, String image, ColorsModule colorsModule, ClickGuiModule clickGuiModule) {
        DisplayName = displayName;
        DefaultX = X;
        DefaultY = Y;
        x = X;
        y = Y;
        Height = height;
        Width = width;
        RemainingMinimizingY = 0;
        RemainingMaximizingY = 0;
        MousePlayAnim = 0;

        if (image != null) BarTexture = SalHack.getImageManager().getDynamicTexture(image);

        Colors = colorsModule;
        ClickGUI = clickGuiModule;
    }

    public void AddItem(ComponentItem componentItem) {
        Items.add(componentItem);
    }

    final float BorderLength = 15.0f;
    final float Padding = 3;

    public static void drawTexture(Identifier icon, float x, float y, int width, int height, DrawContext context) {
        context.getMatrices().push();
        context.getMatrices().translate(x, y, 0);
        RenderSystem.texParameter(3553, 10240, 9729);
        RenderSystem.texParameter(3553, 10241, 9987);
        context.drawTexture(icon, 0, 0, 0.0F, 0.0F, width, height, width, height);
        context.getMatrices().pop();
    }

    public boolean Render(int mouseX, int mouseY, boolean canHover, boolean allowsOverflow, float offsetY, DrawContext context) {
        if (Dragging) {
            x = mouseX - DeltaX;
            y = mouseY - DeltaY;
        }

        if (!allowsOverflow) {
            Window res = Wrapper.GetMC().getWindow();
            /// Don't allow too much to right, or left
            if (x+GetWidth() >= res.getScaledWidth()) x = res.getScaledWidth() - GetWidth();
            else if (x < 0) x = 0;
            if (y+GetHeight() >= res.getScaledHeight()) y = res.getScaledHeight() - GetHeight();
            else if (y < 0) y = 0;
        }

        for (ComponentItem componentItem : Items) componentItem.OnMouseMove(mouseX, mouseY, GetX(), GetY()-offsetY);

        if (IsMinimizing) {
            if (RemainingMinimizingY > 0) {

                RemainingMinimizingY -= 20;
                RemainingMinimizingY = Math.max(RemainingMinimizingY, 0);

                if (RemainingMinimizingY == 0) {
                    Minimized = true;
                    IsMinimizing = false;
                    Height = 17;
                }
            }
        } else if (IsMaximizing) {
            if (RemainingMaximizingY < 500) {

                RemainingMaximizingY += 20;
                RemainingMaximizingY = Math.min(RemainingMaximizingY, 500);

                if (RemainingMaximizingY == 500) {
                    IsMaximizing = false;
                    Height = 17;
                }
            }
        }

        context.fill((int) GetX(), (int) (GetY()+17-offsetY), (int) (GetX()+GetWidth()), (int) (GetY()+GetHeight()), 0x992A2A2A);
        context.fill((int) GetX(), (int) (GetY()-offsetY), (int) (GetX() + GetWidth()), (int) (GetY() + 17-offsetY), 0x99000000);
        FontRenderers.getTwCenMtStd28().drawString(context.getMatrices(), GetDisplayName(), (int) (GetX() + 2), (int) (GetY() + 2-offsetY), GetTextColor(), false);

        if (BarTexture != null) {
            float x = GetX()+GetWidth()-15;
            drawTexture(new Identifier(BarTexture.getResourceLocation()), (int) x, (int) (GetY()+3-offsetY), BarTexture.getWidth()/3, BarTexture.getHeight()/3, context);
        }
        if (!Minimized) {

            float Y = GetY() + 5-offsetY;
            HoveredItem = null;
            boolean Break = false;

            for (ComponentItem componentItem : Items) {
                Y = DisplayComponentItem(componentItem, Y, mouseX, mouseY, canHover, false, IsMinimizing ? RemainingMinimizingY : (IsMaximizing ? RemainingMaximizingY : 0), context);
                float menuY = Math.abs(y - Y - BorderLength);
                if (IsMinimizing && menuY >= RemainingMinimizingY) Break = true;
                else if (IsMaximizing && menuY >= RemainingMaximizingY) Break = true;
                if (Break) break;
            }

            if (!Break) {
                IsMinimizing = false;
                IsMaximizing = false;
            }

            if (HoveredItem != null && (ClickGUI != null ? ClickGUI.hoverDescriptions.getValue() : true)) {
                if (HoveredItem.GetDescription() != null && !Objects.equals(HoveredItem.GetDescription(), "")) {
                    context.fill(mouseX+15, mouseY, (int) (mouseX+19+FontRenderers.getTwCenMtStd22().getStringWidth(HoveredItem.GetDescription())), mouseY + Wrapper.GetMC().textRenderer.fontHeight+3, 0x90000000);
                    FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), HoveredItem.GetDescription(), mouseX+17, mouseY, 0xFFFFFF);
                }
            }

            Height = Math.abs(y - Y - 12);
        }

        if (MousePlayAnim > 0) {
            MousePlayAnim--;
            //RenderUtil.DrawPolygon(p_MouseX, p_MouseY, MousePlayAnim, 360, 0x99FFFFFF);
        }

        return canHover && mouseX > GetX() && mouseX < GetX() + GetWidth() && mouseY > GetY()-offsetY && mouseY < GetY()+GetHeight()-offsetY;
    }

    public float DisplayComponentItem(ComponentItem componentItem, float Y, int mouseX, int mouseY, boolean canHover, boolean displayExtendedLine, final float maxY, DrawContext context) {

        Y += componentItem.GetHeight();
        componentItem.OnMouseMove(mouseX, mouseY, GetX(), GetY());
        componentItem.Update();
        if (componentItem.HasState(ComponentItem.Extended)) context.fill((int) (x+1), (int) Y, (int) (x+componentItem.GetWidth()-3), (int) (Y + Wrapper.GetMC().textRenderer.fontHeight + 3),0x080808);
        int color = 0xFFFFFF;
        boolean hovered = canHover && mouseX > x && mouseX < x+componentItem.GetWidth() && mouseY > Y && mouseY < Y+componentItem.GetHeight();
        boolean dropDown = componentItem.HasState(ComponentItem.Extended);

        if (hovered) {
            if (!dropDown) context.fill((int) GetX(), (int) Y, (int) (GetX()+componentItem.GetWidth()), (int) (Y+11), 0x99040404);
                //RenderUtil.drawGradientRect(GetX(), p_Y, GetX()+p_Item.GetWidth(), p_Y+11, 0x99040404, 0x99000000);
            color = (componentItem.HasState(ComponentItem.Clicked) && !componentItem.HasFlag(ComponentItem.DontDisplayClickableHighlight)) ? GetTextColor() : color;// - commented for issue #27
            HoveredItem = componentItem;
            componentItem.AddState(ComponentItem.Hovered);
        } else {
            if (componentItem.HasState(ComponentItem.Clicked) && !componentItem.HasFlag(ComponentItem.DontDisplayClickableHighlight)) color = GetTextColor();
            componentItem.RemoveState(ComponentItem.Hovered);
        }

        if (dropDown) context.fill((int) GetX(), (int) Y, (int) (GetX()+componentItem.GetWidth()), (int) (Y+11), 0x99040404);
            //RenderUtil.drawGradientRect(GetX(), p_Y, GetX()+p_Item.GetWidth(), p_Y+11, 0x99040404, 0x99000000);

        if (componentItem.HasFlag(ComponentItem.RectDisplayAlways) || (componentItem.HasFlag(ComponentItem.RectDisplayOnClicked) && componentItem.HasState(ComponentItem.Clicked))) context.fill((int) GetX(), (int) Y, (int) (GetX()+componentItem.GetCurrentWidth()), (int) (Y+11), GetColor());
        FontRenderers.getTwCenMtStd22().drawString(context.getMatrices(), componentItem.GetDisplayText(), x + Padding, Y, color);

        /*if (p_Item.HasFlag(ComponentItem.HasValues))
        {
            RenderUtil.drawLine(X + p_Item.GetWidth() - 1, p_Y, X + p_Item.GetWidth() - 1, p_Y + 11, 5, 0x9945B5E4);
        }*/

        if (componentItem.HasState(ComponentItem.Extended) || displayExtendedLine) {
            //RenderUtil.drawLine(X + p_Item.GetWidth() - 1, p_Y, X + p_Item.GetWidth() - 1, p_Y + 11, 3, GetColor());
        }

        if (componentItem.HasState(ComponentItem.Extended)) {
            for (ComponentItem component : componentItem.DropdownItems) {
                Y = DisplayComponentItem(component, Y, mouseX, mouseY, canHover, true, maxY, context);
                if (maxY > 0) {
                    float menuY = Math.abs(y - Y - BorderLength);
                    if (menuY >= maxY) break;
                }
            }
        }

        return Y;
    }

    public boolean MouseClicked(int mouseX, int mouseY, int mouseButton, float offsetY) {
        if (mouseX > GetX() && mouseX < GetX() + GetWidth() && mouseY > GetY()-offsetY && mouseY < GetY()+BorderLength-offsetY) {
            /// Dragging (Top border)
            if (mouseButton == 0) {
                Dragging = true;
                DeltaX = mouseX-x;
                DeltaY = mouseY-y;
            } else if (mouseButton == 1) {
                /// Right click
                if (!Minimized) {
                    IsMinimizing = true;
                    RemainingMinimizingY = Height;
                    IsMaximizing = false;
                } else {
                    Minimized = false;
                    IsMinimizing = false;
                    RemainingMinimizingY = 0;
                    IsMaximizing = true;
                }
                RemainingMaximizingY = 0;
            }
        }

        if (HoveredItem != null) {
            HoveredItem.OnMouseClick(mouseX, mouseY, mouseButton);
            if (mouseButton == 0) MousePlayAnim = 20;
            return true;
        }

        return Dragging;
    }

    public void MouseReleased(int mouseX, int mouseY) {
        if (Dragging) Dragging = false;
        for (ComponentItem componentItem : Items) HandleMouseReleaseCompItem(componentItem, mouseX, mouseY);
    }

    public void HandleMouseReleaseCompItem(ComponentItem componentItem, int mouseX, int mouseY) {
        componentItem.OnMouseRelease(mouseX, mouseY);
        for (ComponentItem component : componentItem.DropdownItems) component.OnMouseRelease(mouseX, mouseY);
    }

    public void MouseClickMove(int mouseX, int mouseY, int mouseButton) {
        for (ComponentItem componentItem : Items) HandleMouseClickMoveCompItem(componentItem, mouseX, mouseY, mouseButton);
    }

    private void HandleMouseClickMoveCompItem(ComponentItem componentItem, int mouseX, int mouseY, int mouseButton) {
        componentItem.OnMouseClickMove(mouseX, mouseY, mouseButton);
        for (ComponentItem component : componentItem.DropdownItems) component.OnMouseClickMove(mouseX, mouseY, mouseButton);
    }

    public String GetDisplayName() {
        return DisplayName;
    }

    public float GetX() {
        return x;
    }

    public float GetY() {
        return y;
    }

    public float GetWidth() {
        return Width;
    }

    public float GetHeight() {
        return Height;
    }

    public void SetX(float X) {
        x = X;
    }

    public void SetY(float Y) {
        y = Y;
    }

    public void keyTyped(int keyCode, int scanCode, int modifiers) {
        for (ComponentItem componentItem : Items) HandleKeyTypedForItem(componentItem, keyCode, scanCode, modifiers);
    }

    public void HandleKeyTypedForItem(ComponentItem componentItem, int keyCode, int scanCode, int modifiers) {
        componentItem.keyTyped(keyCode, scanCode, modifiers);
        for (ComponentItem component : componentItem.DropdownItems) HandleKeyTypedForItem(component, keyCode, scanCode, modifiers);
    }

    private int GetColor() {
        return (Colors.alpha.getValue() << 24) & 0xFF000000 | (Colors.red.getValue() << 16) & 0x00FF0000 | (Colors.green.getValue() << 8) & 0x0000FF00 | Colors.blue.getValue() & 0x000000FF;
    }

    public int GetTextColor() {
        return (Colors.red.getValue() << 16) & 0x00FF0000 | (Colors.green.getValue() << 8) & 0x0000FF00 | Colors.blue.getValue() & 0x000000FF;
    }

    public void Default() {
        x = DefaultX;
        y = DefaultY;
        Items.forEach(comp -> {if (comp.HasState(ComponentItem.Extended)) comp.RemoveState(ComponentItem.Extended);});
    }
}