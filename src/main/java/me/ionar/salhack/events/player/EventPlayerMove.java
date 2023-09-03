package me.ionar.salhack.events.player;

import me.ionar.salhack.events.MinecraftEvent;
import net.minecraft.entity.MovementType;

public class EventPlayerMove extends MinecraftEvent
{
    public MovementType Type;
    public double X;
    public double Y;
    public double Z;

    public EventPlayerMove(MovementType p_Type, double p_X, double p_Y, double p_Z)
    {
        Type = p_Type;
        X = p_X;
        Y = p_Y;
        Z = p_Z;
    }
}
