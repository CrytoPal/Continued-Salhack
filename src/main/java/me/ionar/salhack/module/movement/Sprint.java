package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public final class Sprint extends Module {
    public final Value<Modes> Mode = new Value<>("Mode", new String[]{"Mode", "M"}, "The sprint mode to use.", Modes.Legit);

    public enum Modes {
        Rage,
        Legit
    }

    public Sprint() {
        super("Sprint", new String[]{ "AutoSprint", "Spr" }, "Automatically sprints for you", 0, 0xDB2450, ModuleType.MOVEMENT);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world != null && mc.player != null) mc.player.setSprinting(false);
    }

    @Override
    public String getMetaData() {
        return String.valueOf(Mode.getValue());
    }

    @EventHandler
    private void OnPlayerUpdate(TickEvent event) {
        if (event.isPre() || mc.player == null) return;
        switch (Mode.getValue()) {
            case Rage -> {
                if (!(mc.player.getHungerManager().getFoodLevel() <= 6)) mc.player.setSprinting(true);
            } case Legit -> {
                if (mc.player.forwardSpeed > 0 && !(mc.player.getHungerManager().getFoodLevel() <= 6f)) mc.options.sprintKey.setPressed(true);
            }
        }
    }
}