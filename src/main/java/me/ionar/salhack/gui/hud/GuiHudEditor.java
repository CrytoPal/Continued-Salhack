package me.ionar.salhack.gui.hud;

import me.ionar.salhack.managers.HudManager;
import me.ionar.salhack.module.ui.HudEditorModule;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class GuiHudEditor extends Screen {

    private HudEditorModule HudEditor;
    private boolean Clicked = false;
    private boolean Dragging = false;
    private int ClickMouseX = 0;
    private int ClickMouseY = 0;

    public GuiHudEditor(HudEditorModule p_HudEditor) {
        super(Text.of("Hud Editor"));
        HudEditor = p_HudEditor;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        this.renderBackground(context);

        context.getMatrices().push();

        HudComponentItem l_LastHovered = null;

        for (HudComponentItem l_Item : HudManager.Get().Items)
        {
            if (!l_Item.IsHidden() && l_Item.Render(mouseX, mouseY, delta, context))
                l_LastHovered = l_Item;
        }

        if (l_LastHovered != null)
        {
            /// Add to the back of the list for rendering
            HudManager.Get().Items.remove(l_LastHovered);
            HudManager.Get().Items.add(l_LastHovered);
        }

        if (Clicked)
        {
            final float l_MouseX1 = Math.min(ClickMouseX, mouseX);
            final float l_MouseX2 = Math.max(ClickMouseX, mouseX);
            final float l_MouseY1 = Math.min(ClickMouseY, mouseY);
            final float l_MouseY2 = Math.max(ClickMouseY, mouseY);

            //RenderUtil.drawOutlineRect(l_MouseX2, l_MouseY2, l_MouseX1, l_MouseY1, 1, 0x75056EC6);
            context.fill((int) l_MouseX1, (int) l_MouseY1, (int) l_MouseX2, (int) l_MouseY2, 0x56EC6);//205

            HudManager.Get().Items.forEach(p_Item ->
            {
                if (!p_Item.IsHidden())
                {
                    if (p_Item.IsInArea(l_MouseX1, l_MouseX2, l_MouseY1, l_MouseY2))
                        p_Item.SetSelected(true);
                    else if (p_Item.IsSelected())
                        p_Item.SetSelected(false);
                }
            });
        }

        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (HudComponentItem l_Item : HudManager.Get().Items)
        {
            if (!l_Item.IsHidden())
            {
                if (l_Item.OnMouseClick((int) mouseX, (int) mouseY, button));
            }
        }

        Clicked = true;
        ClickMouseX = (int) mouseX;
        ClickMouseY = (int) mouseY;
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        HudManager.Get().Items.forEach(p_Item ->
        {
            if (!p_Item.IsHidden())
            {
                p_Item.OnMouseRelease((int) mouseX, (int) mouseY, 0);

                if (p_Item.IsSelected())
                    p_Item.SetMultiSelectedDragging(true);
                else
                    p_Item.SetMultiSelectedDragging(false);
            }
        });

        Clicked = false;
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void close() {
        super.close();
        if (HudEditor.isEnabled())
            HudEditor.toggle();

        Clicked = false;
        Dragging = false;
        ClickMouseX = 0;
        ClickMouseY = 0;
    }
}