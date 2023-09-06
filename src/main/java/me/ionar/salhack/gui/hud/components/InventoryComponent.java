package me.ionar.salhack.gui.hud.components;

import me.ionar.salhack.gui.hud.HudComponentItem;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.ui.HudModule;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class InventoryComponent extends HudComponentItem
{
    private final HudModule hud = (HudModule) ModuleManager.Get().GetMod(HudModule.class);

    public InventoryComponent()
    {
        super("Inventory", 2, 160);
        SetHidden(false);
    }

    public static final Value<Mode> InvMode = new Value<Mode>("Mode", new String[]
            { "Mode", "M" }, "Color Mode for Inventory", Mode.Black);

    private static final Identifier inventory = new Identifier("minecraft","salhack/imgs/inventory.png");
    private static final Identifier inventoryblack = new Identifier("minecraft","salhack/imgs/blackcontainer.png");


    @Override
    public void render(int p_MouseX, int p_MouseY, float p_PartialTicks, DrawContext context) {
        super.render(p_MouseX, p_MouseY, p_PartialTicks, context);
        if (mc.player != null) {
            // Inventory Gui
            switch (InvMode.getValue()) {
                case White -> {
                    context.drawTexture(inventory, (int) GetX(), (int) GetY(), 0, 0, 0, 176, 67, 176, 67);
                }
                case Black -> {
                    context.drawTexture(inventoryblack, (int) GetX(), (int) GetY(), 0, 0, 0, 176, 67, 176, 67);
                }
            }

            // Cursed code but uh too lazy to fix it ¯\_(ツ)_/¯

            // TODO: Show Item count

            // Row 1
            context.drawItem(mc.player.getInventory().getStack(9),(int) GetX() + 8, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(10),(int) GetX() + 26, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(11),(int) GetX() + 44, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(12),(int) GetX() + 62, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(13),(int) GetX() + 80, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(14),(int) GetX() + 98, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(15),(int) GetX() + 116, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(16),(int) GetX() + 134, (int) GetY() + 7);
            context.drawItem(mc.player.getInventory().getStack(17),(int) GetX() + 152, (int) GetY() + 7);
            // Row 2
            context.drawItem(mc.player.getInventory().getStack(18),(int) GetX() + 8, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(19),(int) GetX() + 26, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(20),(int) GetX() + 44, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(21),(int) GetX() + 62, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(22),(int) GetX() + 80, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(23),(int) GetX() + 98, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(24),(int) GetX() + 116, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(25),(int) GetX() + 134, (int) GetY() + 25);
            context.drawItem(mc.player.getInventory().getStack(26),(int) GetX() + 152, (int) GetY() + 25);
            // Row 3
            context.drawItem(mc.player.getInventory().getStack(27),(int) GetX() + 8, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(28),(int) GetX() + 26, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(29),(int) GetX() + 44, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(30),(int) GetX() + 62, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(31),(int) GetX() + 80, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(32),(int) GetX() + 98, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(33),(int) GetX() + 116, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(34),(int) GetX() + 134, (int) GetY() + 43);
            context.drawItem(mc.player.getInventory().getStack(35),(int) GetX() + 152, (int) GetY() + 43);

            SetWidth(176);
            SetHeight(67);
        }
    }


    private enum Mode
    {
        White, Black
    }
}
