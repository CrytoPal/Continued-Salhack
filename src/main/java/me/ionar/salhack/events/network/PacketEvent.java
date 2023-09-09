package me.ionar.salhack.events.network;

import me.ionar.salhack.events.Event;
import me.ionar.salhack.events.EventEra;
import net.minecraft.network.packet.Packet;

public abstract class PacketEvent extends Event {
    private final Packet<?> packet;

    public PacketEvent(EventEra era, Packet<?> packet) {
        super(era);
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    public static class Send extends PacketEvent {
        public Send(EventEra era, Packet<?> packet) {
            super(era, packet);
        }
    }

    public static class Receive extends PacketEvent {
        public Receive(EventEra era, Packet<?> packet) {
            super(era, packet);
        }
    }

}
