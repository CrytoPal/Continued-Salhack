package me.ionar.salhack.events;

import me.ionar.salhack.main.Wrapper;
import me.zero.alpine.event.CancellableEvent;

public class MinecraftEvent extends CancellableEvent {
    private Era era = Era.PRE;
    private final float partialTicks;

    public MinecraftEvent() {
        partialTicks = Wrapper.GetMC().getTickDelta();
    }

    public MinecraftEvent(Era p_Era) {
        partialTicks = Wrapper.GetMC().getTickDelta();
        era = p_Era;
    }

    public Era getEra() {
        return era;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public enum Era {
        PRE,
        PERI,
        POST
    }

}
