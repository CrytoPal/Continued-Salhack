package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

import static me.ionar.salhack.main.Wrapper.mc;


public class Flight extends Module {

    private boolean flying;

    // public final Value<Modes> Mode = new Value<Modes>("Mode", new String[]{"M"}, "Modes of the speed to use", Modes.Vanilla);
    public final Value<Float> Speed = new Value<Float>("Speed", new String[]{""}, "Speed to use", 0.1f, 0.0f, 1.0f, 0.1f);
    // public final Value<Boolean> AntiKick = new Value<Boolean>("AntiKick", new String[]{""}, "Prevents you from getting kicked while flying by vanilla anticheat", true);
    public Flight() {
        super("Flight", new String[]{ "fly" }, "Lets you fly like a bird", 0, -1, ModuleType.MOVEMENT);
    }


    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            flying = true;
            mc.player.getAbilities().flying = true;
            if (mc.player.getAbilities().creativeMode) return;
            mc.player.getAbilities().allowFlying = true;
        }
    }

    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            flying = false;
            mc.player.getAbilities().flying = false;
            if (mc.player.getAbilities().creativeMode) return;
            mc.player.getAbilities().allowFlying = false;
        }
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (event.isPre()) return;
        if (mc.player != null) {
            if (flying) {
                mc.player.getAbilities().setFlySpeed(Speed.getValue());
            }
        }
    }

    public enum Modes {
        Vanilla,
        Creative,
    }
}
