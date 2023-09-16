package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.network.PacketEvent;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class MixinClientConnection {

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        PacketEvent.Send event = new PacketEvent.Send(EventEra.PRE, packet);
        SalHackMod.NORBIT_EVENT_BUS.post(event);

        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true)
    private static <T extends PacketListener> void onChannelRead(Packet<T> packet, PacketListener listener, CallbackInfo callbackInfo) {
        PacketEvent.Receive event = new PacketEvent.Receive(EventEra.PRE, packet);
        SalHackMod.NORBIT_EVENT_BUS.post(event);

        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "send(Lnet/minecraft/network/packet/Packet;)V", at = @At("RETURN"))
    private void onPostSendPacket(Packet<?> packet, CallbackInfo callbackInfo) {
        SalHackMod.NORBIT_EVENT_BUS.post(new PacketEvent.Send(EventEra.POST, packet));
    }

    @Inject(method = "handlePacket", at = @At("RETURN"))
    private static <T extends PacketListener> void onPostChannelRead(Packet<?> packet, PacketListener listener, CallbackInfo callbackInfo) {
        SalHackMod.NORBIT_EVENT_BUS.post(new PacketEvent.Receive(EventEra.POST, packet));
    }
}