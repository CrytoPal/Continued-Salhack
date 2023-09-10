package me.ionar.salhack.gui.hud;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.ui.HudEditorModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GuiHudEditor extends Screen {

    private final HudEditorModule hudEditor;
    private boolean clicked = false;
    private boolean dragging = false;
    private int clickMouseX = 0;
    private int clickMouseY = 0;

    public GuiHudEditor(HudEditorModule hudEditor) {
        super(Text.of("Hud Editor"));
        this.hudEditor = hudEditor;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context);
        context.getMatrices().push();
        HudComponentItem lastHovered = null;
        for (HudComponentItem componentItem : SalHack.getHudManager().componentItems) {
            if (!componentItem.isHidden() && componentItem.render(mouseX, mouseY, delta, context)) lastHovered = componentItem;
        }
        if (lastHovered != null) {
            SalHack.getHudManager().componentItems.remove(lastHovered);
            SalHack.getHudManager().componentItems.add(lastHovered);
        }
        if (clicked) {
            final float mouseX1 = Math.min(clickMouseX, mouseX);
            final float mouseX2 = Math.max(clickMouseX, mouseX);
            final float mouseY1 = Math.min(clickMouseY, mouseY);
            final float mouseY2 = Math.max(clickMouseY, mouseY);
            context.fill((int) mouseX1, (int) mouseY1, (int) mouseX2, (int) mouseY2, 0x56EC6);//205
            SalHack.getHudManager().componentItems.forEach(componentItem -> {
                if (!componentItem.isHidden()) {
                    if (componentItem.IsInArea(mouseX1, mouseX2, mouseY1, mouseY2)) componentItem.SetSelected(true);
                    else if (componentItem.IsSelected()) componentItem.SetSelected(false);
                }
            });
        }
        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (HudComponentItem componentItem : SalHack.getHudManager().componentItems) {
            if (!componentItem.isHidden()) componentItem.onMouseClick((int) mouseX, (int) mouseY, button);
        }
        clicked = true;
        clickMouseX = (int) mouseX;
        clickMouseY = (int) mouseY;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        SalHack.getHudManager().componentItems.forEach(componentItem -> {
            if (!componentItem.isHidden()) {
                componentItem.onMouseRelease((int) mouseX, (int) mouseY, 0);
                componentItem.SetMultiSelectedDragging(componentItem.IsSelected());
            }
        });
        clicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
        if (hudEditor.isEnabled()) hudEditor.toggle(true);
        clicked = false;
        dragging = false;
        clickMouseX = 0;
        clickMouseY = 0;
    }
}