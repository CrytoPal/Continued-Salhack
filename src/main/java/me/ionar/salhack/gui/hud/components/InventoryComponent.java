package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class InventoryComponent extends HudComponentItem {
    public InventoryComponent() {
        super("Inventory", 2, 160);
        setHidden(false);
    }

    public static final Value<Mode> InvMode = new Value<Mode>("Mode", new String[]
            { "Mode", "M" }, "Color Mode for Inventory", Mode.Black);

    private static final Identifier inventory = new Identifier("minecraft","salhack/imgs/inventory.png");
    private static final Identifier inventoryblack = new Identifier("minecraft","salhack/imgs/blackcontainer.png");


    @Override
    public void onRender(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.onRender(p_MouseX, p_MouseY, p_PartialTicks, context);
        if (mc.player != null) {
            // Inventory Gui
            switch (InvMode.getValue()) {
                case White -> {
                    context.drawTexture(inventory, (int) getPositionX(), (int) getPositionY(), 0, 0, 0, 176, 67, 176, 67);
                }
                case Black -> {
                    context.drawTexture(inventoryblack, (int) getPositionX(), (int) getPositionY(), 0, 0, 0, 176, 67, 176, 67);
                }
            }

            // Even more Cursed code but uh too lazy to fix it ¯\_(ツ)_/¯ (Your chance to pr a fix for this)

            // Row 1
            context.drawItem(mc.player.getInventory().getStack(9),(int) getPositionX() + 8, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(10),(int) getPositionX() + 26, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(11),(int) getPositionX() + 44, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(12),(int) getPositionX() + 62, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(13),(int) getPositionX() + 80, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(14),(int) getPositionX() + 98, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(15),(int) getPositionX() + 116, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(16),(int) getPositionX() + 134, (int) getPositionY() + 7);
            context.drawItem(mc.player.getInventory().getStack(17),(int) getPositionX() + 152, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(9),(int) getPositionX() + 8, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(10),(int) getPositionX() + 26, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(11),(int) getPositionX() + 44, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(12),(int) getPositionX() + 62, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(13),(int) getPositionX() + 80, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(14),(int) getPositionX() + 98, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(15),(int) getPositionX() + 116, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(16),(int) getPositionX() + 134, (int) getPositionY() + 7);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(17),(int) getPositionX() + 152, (int) getPositionY() + 7);
            // Row 2
            context.drawItem(mc.player.getInventory().getStack(18),(int) getPositionX() + 8, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(19),(int) getPositionX() + 26, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(20),(int) getPositionX() + 44, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(21),(int) getPositionX() + 62, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(22),(int) getPositionX() + 80, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(23),(int) getPositionX() + 98, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(24),(int) getPositionX() + 116, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(25),(int) getPositionX() + 134, (int) getPositionY() + 25);
            context.drawItem(mc.player.getInventory().getStack(26),(int) getPositionX() + 152, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(18),(int) getPositionX() + 8, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(19),(int) getPositionX() + 26, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(20),(int) getPositionX() + 44, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(21),(int) getPositionX() + 62, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(22),(int) getPositionX() + 80, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(23),(int) getPositionX() + 98, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(24),(int) getPositionX() + 116, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(25),(int) getPositionX() + 134, (int) getPositionY() + 25);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(26),(int) getPositionX() + 152, (int) getPositionY() + 25);
            // Row 3
            context.drawItem(mc.player.getInventory().getStack(27),(int) getPositionX() + 8, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(28),(int) getPositionX() + 26, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(29),(int) getPositionX() + 44, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(30),(int) getPositionX() + 62, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(31),(int) getPositionX() + 80, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(32),(int) getPositionX() + 98, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(33),(int) getPositionX() + 116, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(34),(int) getPositionX() + 134, (int) getPositionY() + 43);
            context.drawItem(mc.player.getInventory().getStack(35),(int) getPositionX() + 152, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(27),(int) getPositionX() + 8, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(28),(int) getPositionX() + 26, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(29),(int) getPositionX() + 44, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(30),(int) getPositionX() + 62, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(31),(int) getPositionX() + 80, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(32),(int) getPositionX() + 98, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(33),(int) getPositionX() + 116, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(34),(int) getPositionX() + 134, (int) getPositionY() + 43);
            context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(35),(int) getPositionX() + 152, (int) getPositionY() + 43);

            setWidth(176);
            setHeight(67);
        }
    }


    private enum Mode
    {
        White, Black
    }
}
