package me.ionar.salhack.module.misc;

import me.ionar.salhack.events.player.EventPlayerTick;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

public final class Rotation extends Module {
    public final Value<Integer> yawLock = new Value<>("Yaw", new String[]{"Y"}, "Lock the player's rotation yaw at a point", 0, 0, 360, 11);
    public final Value<Integer> pitchLock = new Value<>("Pitch", new String[]{"P"}, "Lock the player's rotation pitch", 0, 0, 90, 11);

    public Rotation() {
        super("Yaw", new String[]{"RotLock", "Rotation"}, "Locks you rotation for precision", 0, 0xDA24DB, ModuleType.MISC);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @EventHandler
    private Listener<EventPlayerTick> OnPlayerUpdate = new Listener<>(Event -> {
        if (mc.player != null) {
            mc.player.setYaw((float) yawLock.getValue());
            mc.player.setPitch((float) pitchLock.getValue());
        }
    });
}