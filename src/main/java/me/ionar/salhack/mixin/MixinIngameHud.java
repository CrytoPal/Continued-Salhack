package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.player.EventPlayerJump;
import me.ionar.salhack.events.render.EventRenderGameOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinIngameHud {

    @Inject(at = @At(value = "RETURN"), method = "render", cancellable = true)
    public void render(DrawContext context, float tickDelta, CallbackInfo callback) {
        EventRenderGameOverlay event = new EventRenderGameOverlay(context, tickDelta);
        SalHackMod.EVENT_BUS.post(event);
        if (event.isCancelled()) callback.cancel();
    }
}
