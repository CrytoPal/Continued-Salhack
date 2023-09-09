package me.ionar.salhack.events.client;

import me.ionar.salhack.events.Event;

public class MouseButtonEvent extends Event {
    private final int button;
    private final int action;

    public MouseButtonEvent(int button, int action) {
        this.button = button;
        this.action = action;
    }

    public int getButton() {
        return this.button;
    }

    public int getAction() {
        return this.action;
    }
}
