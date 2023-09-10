package me.ionar.salhack.mixin;

import com.mojang.authlib.GameProfile;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.player.PlayerMoveEvent;
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

    @Inject(method = "move", at = @At("HEAD"), cancellable = true)
    public void onMove(MovementType type, Vec3d movement, CallbackInfo info) {
        PlayerMoveEvent event = new PlayerMoveEvent(type, movement.x, movement.y, movement.z);
        SalHackMod.NORBIT_EVENT_BUS.post(event);
        if (event.isCancelled()) {
            super.move(type, new Vec3d(event.getX(), event.getY(), event.getZ()));
            info.cancel();
        }
    }
}
