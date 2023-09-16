package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.client.MouseButtonEvent;
import net.minecraft.entity.player.PlayerEntity;

import me.ionar.salhack.managers.FriendManager;
import me.ionar.salhack.module.Module;

import static me.ionar.salhack.main.Wrapper.mc;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;

public class MiddleClickFriends extends Module {
    public MiddleClickFriends() {
        super("MiddleClick", new String[] {"MCF", "MiddleClickF"}, "Middle click friends", 0, -1, ModuleType.MISC);
    }

    @EventHandler
    private void OnMouseButton(MouseButtonEvent event) {
        if (event.getAction() == 0 || event.getButton() != GLFW_MOUSE_BUTTON_MIDDLE || mc.currentScreen != null || mc.targetedEntity == null || !(mc.targetedEntity instanceof PlayerEntity Entity)) return;
        if (FriendManager.Get().IsFriend(Entity)) {
            FriendManager.Get().RemoveFriend(Entity.getDisplayName().getString().toLowerCase());
            SendMessage(Entity.getEntityName() + " has been removed.");
        } else {
            FriendManager.Get().AddFriend(Entity.getDisplayName().getString().toLowerCase());
            SendMessage(Entity.getEntityName() + " has been added.");
        }
    }
}
