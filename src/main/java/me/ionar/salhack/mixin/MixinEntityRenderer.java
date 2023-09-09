package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.render.NametagRenderEvent;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({EntityRenderer.class})
public class MixinEntityRenderer {

    @Inject(method = "renderLabelIfPresent", at = @At("HEAD"), cancellable = true)
    private void renderLabelIfPresent(Entity entity, Text text, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo p_Info) {
        NametagRenderEvent l_Event = new NametagRenderEvent();
        SalHackMod.NORBIT_EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) p_Info.cancel();
    }
}
