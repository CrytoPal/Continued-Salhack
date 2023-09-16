package me.ionar.salhack.events;

import io.github.racoondog.norbit.ICancellable;

public class Event implements ICancellable {
    private EventEra era = EventEra.PRE;
    private boolean cancelled;

    public Event() {}

    public Event(EventEra era) {
        this.era = era;
    }

    public EventEra getEra() {
        return era;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isPre() {
        return era == EventEra.PRE;
    }

}
