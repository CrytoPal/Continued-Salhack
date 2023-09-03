package me.ionar.salhack.events.network;

import net.minecraft.network.packet.Packet;

public class EventNetworkPostPacketEvent extends EventNetworkPacketEvent
{
    public EventNetworkPostPacketEvent(Packet p_Packet)
    {
        super(p_Packet);
    }
}
