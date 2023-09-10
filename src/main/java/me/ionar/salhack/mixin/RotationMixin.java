package me.ionar.salhack.mixin;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.world.CoordsSpooferModule;
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
    private void SeedRender(BlockState state, BlockPos pos, CallbackInfoReturnable<Long> cir) {
        //if (SalHack.getModuleManager().getMod(CoordsSpooferModule.class) != null) {
        //    CoordsSpooferModule coordspoof =  (CoordsSpooferModule) SalHack.getModuleManager().getMod(CoordsSpooferModule.class);
        //    if (coordspoof.isEnabled() && coordspoof.TextureSpoof.getValue()) cir.setReturnValue((long) coordspoof.CoordsX.getValue() + (long) coordspoof.CoordsZ.getValue());
        //}
    }
}