package me.ionar.salhack.mixin;

import me.ionar.salhack.managers.ModuleManager;
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
        if (ModuleManager.Get().GetMod(CoordsSpooferModule.class) != null) {
            CoordsSpooferModule coordspoof =  (CoordsSpooferModule) ModuleManager.Get().GetMod(CoordsSpooferModule.class);
            if (coordspoof.isEnabled()) {
                if (coordspoof.TextureSpoof.getValue()) {
                    return pos.multiply(coordspoof.CoordsX.getValue() + coordspoof.CoordsZ.getValue());
                }
            }
        }
        return pos;
    }
}