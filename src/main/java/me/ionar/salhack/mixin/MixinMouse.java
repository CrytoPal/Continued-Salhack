package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.client.MouseButtonEvent;
import me.ionar.salhack.events.client.MouseScrollEvent;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mouse.class)
public class MixinMouse {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void onMouseButton(long window, int button, int action, int mods, CallbackInfo info) {
        MouseButtonEvent event = new MouseButtonEvent(button, action);
        SalHackMod.NORBIT_EVENT_BUS.post(event);
        if (event.isCancelled()) info.cancel();
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"))
    private void onMouseScroll(long window, double horizontal, double vertical, CallbackInfo info) {
        MouseScrollEvent event = new MouseScrollEvent(horizontal, vertical);
        SalHackMod.NORBIT_EVENT_BUS.post(event);
        if (event.isCancelled()) info.cancel();
    }
}
