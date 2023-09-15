package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.render.RenderGameOverlayEvent;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class MixinInGameHud {

    @Inject(method = "render", at = @At(value = "RETURN"), cancellable = true)
    public void render(DrawContext context, float tickDelta, CallbackInfo info) {
        RenderGameOverlayEvent event = new RenderGameOverlayEvent(context, tickDelta);
        SalHackMod.NORBIT_EVENT_BUS.post(event);
        if (event.isCancelled()) info.cancel();
    }
}
