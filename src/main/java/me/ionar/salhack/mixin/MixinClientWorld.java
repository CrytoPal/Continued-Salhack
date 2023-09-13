package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.entity.EntityRemovedEvent;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ClientWorld.class)
public class MixinClientWorld {
    @Inject(method = "removeEntity", at = @At("HEAD"))
    public void onRemoveEntity$Inject$HEAD(int entityId, Entity.RemovalReason removalReason, CallbackInfo ci) {
        if(Wrapper.GetMC() == null) return;
        EntityRemovedEvent event = new EntityRemovedEvent(Wrapper.GetMC().world.getEntityById(entityId));
        SalHackMod.NORBIT_EVENT_BUS.post(event);
    }
}
