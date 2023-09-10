package me.ionar.salhack.mixin;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.world.CoordsSpooferModule;
import net.minecraft.block.AbstractBlock;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
@Mixin(AbstractBlock.AbstractBlockState.class)
public class CoordsModifyPosMixin {
    @ModifyVariable(method = "getModelOffset", at = @At("HEAD"), argsOnly = true)
    private BlockPos modifyPos(BlockPos pos) {
        CoordsSpooferModule coordspoof = (CoordsSpooferModule) SalHack.getModuleManager().getMod(CoordsSpooferModule.class);
        if (coordspoof != null) {
            if (coordspoof.isEnabled() && coordspoof.textureSpoof.getValue()) return pos.multiply(coordspoof.coordsX.getValue() + coordspoof.coordsZ.getValue());
        }
        return pos;
    }
}