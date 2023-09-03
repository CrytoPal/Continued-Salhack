package me.ionar.salhack.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.module.ui.HudModule;
import me.ionar.salhack.util.render.TransformPositionUtil;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {

    @Inject(method = {"tiltViewWhenHurt"}, at = {@At("HEAD")}, cancellable = true)
    private void bobView(MatrixStack matrixStack, float f, CallbackInfo ci) {
    }

    @SuppressWarnings("SpellCheckingInspection")
    @Inject(at = @At(value = "FIELD", target = "Lnet/minecraft/client/render/GameRenderer;renderHand:Z", opcode = Opcodes.GETFIELD, ordinal = 0), method = "renderWorld")
    void renderer_postWorldRender(float tickDelta, long limitTime, MatrixStack matrix, CallbackInfo ci) {
        TransformPositionUtil.lastProjMat.set(RenderSystem.getProjectionMatrix());
        TransformPositionUtil.lastModMat.set(RenderSystem.getModelViewMatrix());
        TransformPositionUtil.lastWorldSpaceMatrix.set(matrix.peek().getPositionMatrix());
    }
}
