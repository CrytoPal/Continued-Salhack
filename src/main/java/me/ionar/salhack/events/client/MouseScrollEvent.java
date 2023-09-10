package me.ionar.salhack.events.client;

import me.ionar.salhack.events.Event;

public class MouseScrollEvent extends Event {
    private final double Horizontal;
    private final double Vertical;

    public MouseScrollEvent(double horizontal, double vertical) {
        Horizontal = horizontal;
        Vertical = vertical;
    }

    public double getHorizontal() {
        return Horizontal;
    }

    public double getVertical() {
        return Vertical;
    }
}
