package me.ionar.salhack.events.player;

import me.ionar.salhack.events.Event;
import net.minecraft.entity.MovementType;

public class PlayerMoveEvent extends Event {
    private MovementType type;
    private double x;
    private double y;
    private double z;

    public PlayerMoveEvent(MovementType type, double x, double y, double z) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public MovementType getType() {
        return type;
    }

    public void setType(MovementType type) {
        this.type = type;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }


}
