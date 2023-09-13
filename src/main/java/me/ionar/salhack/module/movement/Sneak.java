package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.module.Module;

public class Sneak extends Module {
    public Sneak() {
        super("Sneak", new String[]{"S"}, "Automatically Sneaks for you", 0, -1, ModuleType.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null && mc.player != null) mc.options.sneakKey.setPressed(false);
    }

    @EventHandler
    private void OnPlayerUpdate(TickEvent event) {
        mc.options.sneakKey.setPressed(true);
    }
}
