package me.ionar.salhack.events;

import io.github.racoondog.norbit.ICancellable;
import me.ionar.salhack.main.Wrapper;

public class MinecraftEvent implements ICancellable {
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

    @Override
    public void setCancelled(boolean b) {

    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    public enum Era {
        PRE,
        PERI,
        POST
    }

}
