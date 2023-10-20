package me.ionar.salhack.module.misc;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

import static me.ionar.salhack.main.Wrapper.mc;

public final class Rotation extends Module {
    public final Value<Integer> yawLock = new Value<>("Yaw", new String[]{"Y"}, "Lock the player's rotation yaw at a point", 0, 0, 360, 1);
    public final Value<Integer> pitchLock = new Value<>("Pitch", new String[]{"P"}, "Lock the player's rotation pitch", 0, 0, 90, 1);

    public final Value<Integer> yawLockNegative = new Value<>("Negative Yaw", new String[]{"Y"}, "Lock the player's rotation yaw at a point", 0, 0, -360, 1);
    public final Value<Integer> pitchLockNegative = new Value<>("Negative Pitch", new String[]{"P"}, "Lock the player's rotation pitch in negative values", 0, 0, -90, 1);

    public Rotation() {
        super("Rotation", "Locks you rotation for precision", 0, 0xDA24DB, ModuleType.MISC);
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
    private void OnPlayerUpdate(TickEvent event) {
        if (event.isPre()) return;
        if (mc.player != null) {
            mc.player.setYaw((float) yawLock.getValue() + yawLockNegative.getValue());
            mc.player.setPitch((float) pitchLock.getValue() + pitchLockNegative.getValue());
        }
    }
}