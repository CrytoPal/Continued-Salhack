package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.EventEra;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MixinMinecraftClient {

    @Inject(method = "tick", at = @At("TAIL"))
    public void onPreTick(CallbackInfo info){
        if (Wrapper.GetMC().player == null) return;
        SalHackMod.NORBIT_EVENT_BUS.post(new TickEvent(EventEra.PRE));
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo info){
        if (Wrapper.GetMC().player == null) return;
        SalHackMod.NORBIT_EVENT_BUS.post(new TickEvent(EventEra.POST));
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    void postWindowInit(RunArgs args, CallbackInfo info) {
        SalHack.postWindowInit();
    }
}
