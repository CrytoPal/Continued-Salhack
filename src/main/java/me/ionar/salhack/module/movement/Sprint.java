package me.ionar.salhack.module.movement;

import me.ionar.salhack.events.MinecraftEvent.Era;
import me.ionar.salhack.events.player.EventPlayerMove;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.zero.alpine.fork.listener.EventHandler;
import me.zero.alpine.fork.listener.Listener;

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
    private Listener<EventPlayerMove> OnPlayerUpdate = new Listener<>(Event -> {
        if (Event.getEra() != Era.PRE || mc.player ==  null) return;
        switch (this.Mode.getValue()) {
            case Rage -> {
                if (!(mc.player.getHungerManager().getFoodLevel() <= 6)) mc.player.setSprinting(true);
            }
            case Legit -> {
                if (mc.player.forwardSpeed > 0 && !(mc.player.getHungerManager().getFoodLevel() <= 6f)) mc.options.sprintKey.setPressed(true);
            }
        }
    });
}