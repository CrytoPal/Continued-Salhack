package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.network.EventNetworkPacketEvent;
import me.ionar.salhack.events.network.EventNetworkPostPacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.listener.PacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> p_Packet, CallbackInfo callbackInfo)
    {
        EventNetworkPacketEvent l_Event = new EventNetworkPacketEvent(p_Packet);
        SalHackMod.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
        {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onChannelRead(Packet<T> p_Packet, PacketListener listener, CallbackInfo callbackInfo)
    {
        EventNetworkPacketEvent l_Event = new EventNetworkPacketEvent(p_Packet);
        SalHackMod.EVENT_BUS.post(l_Event);

        if (l_Event.isCancelled())
        {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("RETURN"))
    private void onPostSendPacket(Packet<?> p_Packet, CallbackInfo callbackInfo)
    {
        SalHackMod.EVENT_BUS.post(new EventNetworkPostPacketEvent(p_Packet));
    }

    @Inject(method = "handlePacket", at = @At("RETURN"))
    private static <T extends PacketListener> void onPostChannelRead(Packet<?> p_Packet, PacketListener listener, CallbackInfo callbackInfo)
    {
        SalHackMod.EVENT_BUS.post(new EventNetworkPostPacketEvent(p_Packet));
    }
}