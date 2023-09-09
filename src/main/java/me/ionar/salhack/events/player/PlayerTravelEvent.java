package me.ionar.salhack.events.player;

import me.ionar.salhack.events.Event;

public class PlayerTravelEvent extends Event {
    private double strafe;

    public double getStrafe() {
        return strafe;
    }

    public void setStrafe(double strafe) {
        this.strafe = strafe;
    }

    public double getVertical() {
        return vertical;
    }

    public void setVertical(double vertical) {
        this.vertical = vertical;
    }

    public double getForward() {
        return forward;
    }

    public void setForward(double forward) {
        this.forward = forward;
    }

    private double vertical;
    private double forward;

    public PlayerTravelEvent(double strafe, double vertical, double forward) {
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }
}
