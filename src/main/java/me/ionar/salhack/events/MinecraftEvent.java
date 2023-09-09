package me.ionar.salhack.events;

import io.github.racoondog.norbit.ICancellable;
import me.ionar.salhack.main.Wrapper;

public class MinecraftEvent implements ICancellable {
    private final float partialTicks;

    public MinecraftEvent() {
        partialTicks = Wrapper.GetMC().getTickDelta();
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
}
