package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.module.Value;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.Identifier;

public class InventoryComponent extends HudComponentItem {
    public static final Value<mode> invMode = new Value<>("Mode", new String[]{"Mode", "M"}, "Color Mode for Inventory", mode.Black);
    private static final Identifier inventory = new Identifier("minecraft","salhack/imgs/inventory.png");
    private static final Identifier inventoryblack = new Identifier("minecraft","salhack/imgs/blackcontainer.png");
    public InventoryComponent() {
        super("Inventory", 2, 170);
        setHidden(false);
    }

    @Override
    public void onRender(int mouseX, int mouseY, float partialTicks, DrawContext context) {
        super.onRender(mouseX, mouseY, partialTicks, context);
        if (mc.player != null) {
            switch (invMode.getValue()) {
                case White -> context.drawTexture(inventory, (int) getPositionX(), (int) getPositionY(), 0, 0, 0, 176, 67, 176, 67);
                case Black -> context.drawTexture(inventoryblack, (int) getPositionX(), (int) getPositionY(), 0, 0, 0, 176, 67, 176, 67);
            }

            int slot = 9;
            int x = 8;
            int y = 7;
            for (int i = 1; i<=3; i++) {
                for (int j = 1; j<=9; j++) {
                    context.drawItem(mc.player.getInventory().getStack(slot),(int) getPositionX() + x, (int) getPositionY() + y);
                    context.drawItemInSlot(mc.textRenderer,mc.player.getInventory().getStack(slot),(int) getPositionX() + x, (int) getPositionY() + y);
                    slot++;
                    x += 18;
                }
                x = 8;
                y += 18;
            }

            setWidth(176);
            setHeight(67);
        }
    }

    public enum mode {
        White, Black
    }
}
