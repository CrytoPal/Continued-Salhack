package me.ionar.salhack.events.client;

import me.ionar.salhack.events.Event;

public class MouseScrollEvent extends Event {
    private final double horizontal;
    private final double vertical;

    public MouseScrollEvent(double horizontal, double vertical) {
        this.horizontal = horizontal;
        this.vertical = vertical;
    }

    public double getHorizontal() {
        return horizontal;
    }

    public double getVertical() {
        return vertical;
    }
}
