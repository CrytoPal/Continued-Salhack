package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.world.EventTickPost;
import me.ionar.salhack.events.world.EventTickPre;
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

    @Inject(method = "tick", at = @At("TAIL"), cancellable = true)
    public void onPreTick(CallbackInfo ci){
        if (Wrapper.GetMC().player == null) return;
        SalHackMod.NORBIT_EVENT_BUS.post(new EventTickPre());
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void onTick(CallbackInfo ci){
        if (Wrapper.GetMC().player == null) return;
        SalHackMod.NORBIT_EVENT_BUS.post(new EventTickPost());
    }


    @Inject(method = "<init>", at = @At("TAIL"))
    void postWindowInit(RunArgs args, CallbackInfo ci) {
        SalHack.postWindowInit();
    }
}
