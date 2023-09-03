package me.ionar.salhack.mixin;

import me.ionar.salhack.main.SalHack;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.data.Main;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RenderTickCounter.class)
public class MixinRenderTickCounter {
    @Shadow
    public float lastFrameDuration;

    @Inject(method = "beginRenderTick", at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/RenderTickCounter;tickDelta:F", ordinal = 0))
    private void onBeingRenderTick(long a, CallbackInfoReturnable<Integer> info) {
        lastFrameDuration *= SalHack.TICK_TIMER;
    }
}
