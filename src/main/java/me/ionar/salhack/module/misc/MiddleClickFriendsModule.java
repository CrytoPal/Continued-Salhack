package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.client.MouseButtonEvent;
import me.ionar.salhack.main.SalHack;
import net.minecraft.entity.player.PlayerEntity;

import me.ionar.salhack.module.Module;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriendsModule extends Module {
    public MiddleClickFriendsModule() {
        super("MiddleClick", new String[] {"MCF", "MiddleClickF"}, "Middle click friends", 0, -1, ModuleType.MISC);
    }

    @EventHandler
    private void OnMouseButton(MouseButtonEvent event) {
        if (event.getAction() == 0 || event.getButton() != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity Entity)) return;
        if (SalHack.getFriendManager().isFriend(Entity)) {
            SalHack.getFriendManager().removeFriend(Entity.getEntityName());
            sendMessage(Entity.getEntityName() + " has been removed.");
        } else {
            SalHack.getFriendManager().addFriend(Entity.getEntityName());
            sendMessage(Entity.getEntityName() + " has been added.");
        }
    }
}
