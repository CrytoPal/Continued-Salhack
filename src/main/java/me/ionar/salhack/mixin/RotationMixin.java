package me.ionar.salhack.mixin;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.world.CoordsSpoofer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.class)
public class RotationMixin {
    @Inject(method = "getRenderingSeed", at = @At("HEAD"), cancellable = true)
    private void SeedRender(BlockState state, BlockPos pos, CallbackInfoReturnable<Long> info) {
        if (SalHack.getModuleManager().getMod(CoordsSpoofer.class) != null) {
            CoordsSpoofer coordspoof =  (CoordsSpoofer) SalHack.getModuleManager().getMod(CoordsSpoofer.class);
            if (coordspoof.isEnabled() && coordspoof.textureSpoof.getValue()) info.setReturnValue((long) coordspoof.coordsX.getValue() + (long) coordspoof.coordsZ.getValue());
        }
    }
}