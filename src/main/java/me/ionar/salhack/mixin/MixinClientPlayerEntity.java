package me.ionar.salhack.mixin;

import com.mojang.authlib.GameProfile;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.events.player.EventPlayerMove;
import me.ionar.salhack.events.player.EventPlayerTick;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.MovementType;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({ClientPlayerEntity.class})
public class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    private MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onPlayerTick(CallbackInfo ci){
        if (Wrapper.GetMC().player == null) return;

        SalHackMod.EVENT_BUS.post(new EventPlayerTick());
    }

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void move$Inject$HEAD(MovementType type, Vec3d movement, CallbackInfo p_Info) {
        EventPlayerMove event = new EventPlayerMove(type, movement.x, movement.y, movement.z);
        SalHackMod.EVENT_BUS.post(event);
        if (event.isCancelled())
        {
            super.move(type, new Vec3d(event.X, event.Y, event.Z));
            p_Info.cancel();
        }
    }
}
