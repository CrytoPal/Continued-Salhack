package me.ionar.salhack.module.combat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.client.MouseButtonEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.module.misc.MiddleClickFriends;
import me.ionar.salhack.util.ChatUtils;
import me.ionar.salhack.util.entity.ItemUtil;
import net.minecraft.client.Mouse;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickPearl extends Module {

    public final Value<Boolean> MiddleClickFriend = new Value<Boolean>("MiddleClickFriend", new String[]{"MDF"}, "Throw a pearl if middle click friend module is on.", false);
    private boolean clicked;
    private MiddleClickFriends _mcf;
    @EventHandler
    private void onMousePress(MouseButtonEvent event) {
        if (mc.world != null) {
            if (event.getAction() == 0 || event.getButton() != GLFW_MOUSE_BUTTON_MIDDLE) return;
            if (findPearlInHotbar() != -1) {
                final int oldSlot = mc.player.getInventory().selectedSlot;
                mc.player.getInventory().selectedSlot = findPearlInHotbar();
                mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                mc.player.getInventory().selectedSlot = oldSlot;
            } else ChatUtils.errorMessage("No Ender Pearls found in Hotbar!");
        }
    }

    public MiddleClickPearl() {
        super("MiddleClickPearl", "Throws a when if you middle-click.", 0, -1, ModuleType.COMBAT);
    }

    private boolean isItemStackPearl(final ItemStack itemStack) {
        return itemStack.getItem() instanceof EnderPearlItem;
    }

    private int findPearlInHotbar() {
        for (int i = 0; i < 8; i++) {
            if (mc.player.getInventory().getStack(i).getItem() == Items.ENDER_PEARL) return i;
        }
        return -1;
    }

    @Override
    public void init() {
        _mcf = (MiddleClickFriends) ModuleManager.Get().GetMod(MiddleClickFriends.class);
    }

    private boolean mcfEnabled() {
        return _mcf.isEnabled();
    }
}