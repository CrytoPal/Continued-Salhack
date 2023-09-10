package me.ionar.salhack.gui.hud;

import me.ionar.salhack.managers.HudManager;
import me.ionar.salhack.module.ui.HudEditorModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GuiHudEditor extends Screen {

    private final HudEditorModule HudEditor;
    private boolean Clicked = false;
    private boolean Dragging = false;
    private int ClickMouseX = 0;
    private int ClickMouseY = 0;

    public GuiHudEditor(HudEditorModule hudEditor) {
        super(Text.of("Hud Editor"));
        HudEditor = hudEditor;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context);
        context.getMatrices().push();
        HudComponentItem lastHovered = null;
        for (HudComponentItem componentItem : HudManager.Get().ComponentItems) {
            if (!componentItem.IsHidden() && componentItem.Render(mouseX, mouseY, delta, context)) lastHovered = componentItem;
        }
        if (lastHovered != null) {
            HudManager.Get().ComponentItems.remove(lastHovered);
            HudManager.Get().ComponentItems.add(lastHovered);
        }
        if (Clicked) {
            final float mouseX1 = Math.min(ClickMouseX, mouseX);
            final float mouseX2 = Math.max(ClickMouseX, mouseX);
            final float mouseY1 = Math.min(ClickMouseY, mouseY);
            final float mouseY2 = Math.max(ClickMouseY, mouseY);
            context.fill((int) mouseX1, (int) mouseY1, (int) mouseX2, (int) mouseY2, 0x56EC6);//205
            HudManager.Get().ComponentItems.forEach(componentItem -> {
                if (!componentItem.IsHidden()) {
                    if (componentItem.IsInArea(mouseX1, mouseX2, mouseY1, mouseY2)) componentItem.SetSelected(true);
                    else if (componentItem.IsSelected()) componentItem.SetSelected(false);
                }
            });
        }
        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (HudComponentItem componentItem : HudManager.Get().ComponentItems) {
            if (!componentItem.IsHidden()) componentItem.OnMouseClick((int) mouseX, (int) mouseY, button);
        }
        Clicked = true;
        ClickMouseX = (int) mouseX;
        ClickMouseY = (int) mouseY;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        HudManager.Get().ComponentItems.forEach(componentItem -> {
            if (!componentItem.IsHidden()) {
                componentItem.OnMouseRelease((int) mouseX, (int) mouseY, 0);
                componentItem.SetMultiSelectedDragging(componentItem.IsSelected());
            }
        });
        Clicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
        if (HudEditor.isEnabled()) HudEditor.toggle(true);
        Clicked = false;
        Dragging = false;
        ClickMouseX = 0;
        ClickMouseY = 0;
    }
}