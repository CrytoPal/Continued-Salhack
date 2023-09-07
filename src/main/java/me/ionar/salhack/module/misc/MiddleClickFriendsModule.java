package me.ionar.salhack.module.misc;

import me.ionar.salhack.events.client.EventMouseButton;
import net.minecraft.entity.player.PlayerEntity;

import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriendsModule extends Module {
    public MiddleClickFriendsModule() {
        super("MiddleClick", new String[] {"MCF", "MiddleClickF"}, "Middle click friends", 0, -1, ModuleType.MISC);
    }

    @EventHandler
    private Listener<EventMouseButton> OnMouseButton = new Listener<>(Event -> {
        if (Event.getAction() == 0 || Event.getButton() != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity Entity)) return;
        if (FriendManager.Get().IsFriend(Entity)) {
            FriendManager.Get().RemoveFriend(Entity.getName().getString().toLowerCase());
            SendMessage(String.format("%s has been removed.", Entity.getName().getString()));
        } else {
            FriendManager.Get().AddFriend(Entity.getName().getString().toLowerCase());
            SendMessage(String.format("%s has been added.", Entity.getName()));
        }
    });
}
