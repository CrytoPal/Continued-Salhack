package me.ionar.salhack.module.misc;

import me.ionar.salhack.events.client.EventMouseButton;
import me.zero.alpine.listener.Listener;
import me.zero.alpine.listener.Subscribe;
import net.minecraft.entity.player.PlayerEntity;

import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriendsModule extends Module {
    public MiddleClickFriendsModule() {
        super("MiddleClick", new String[] {"MCF", "MiddleClickF"}, "Middle click friends", 0, -1, ModuleType.MISC);
    }

    @Subscribe
    private Listener<EventMouseButton> OnMouseButton = new Listener<>(Event -> {
        if (Event.getAction() == 0 || Event.getButton() != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity Entity)) return;
        if (FriendManager.Get().IsFriend(Entity)) {
            FriendManager.Get().RemoveFriend(Entity.getDisplayName().getString().toLowerCase());
            SendMessage(Entity.getEntityName() + " has been removed.");
        } else {
            FriendManager.Get().AddFriend(Entity.getDisplayName().getString().toLowerCase());
            SendMessage(Entity.getEntityName() + " has been added.");
        }
    });
}
