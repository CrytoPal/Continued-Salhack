package me.ionar.salhack.events.player;

import me.ionar.salhack.events.MinecraftEvent;

public class EventPlayerTravel extends MinecraftEvent
{
    public double Strafe;
    public double Vertical;
    public double Forward;

    public EventPlayerTravel(double p_Strafe, double p_Vertical, double p_Forward)
    {
        Strafe = p_Strafe;
        Vertical = p_Vertical;
        Forward = p_Forward;
    }
}
