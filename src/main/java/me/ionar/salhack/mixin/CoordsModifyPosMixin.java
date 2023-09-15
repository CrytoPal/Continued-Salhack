package me.ionar.salhack.mixin;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.world.CoordsSpoofer;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Mixin(AbstractBlock.AbstractBlockState.class)
public class CoordsModifyPosMixin {
    @ModifyVariable(method = "getModelOffset", at = @At("HEAD"), argsOnly = true)
    private BlockPos modifyPos(BlockPos pos) {
        CoordsSpoofer coordspoof = (CoordsSpoofer) SalHack.getModuleManager().getMod(CoordsSpoofer.class);
        if (coordspoof != null && coordspoof.isEnabled() && coordspoof.textureSpoof.getValue()) return pos.multiply(coordspoof.coordsX.getValue() + coordspoof.coordsZ.getValue());
        return pos;
    }
}