package me.ionar.salhack.mixin;

import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.client.EventClientTick;
import me.ionar.salhack.events.player.EventPlayerJump;
import me.ionar.salhack.events.player.EventPlayerTravel;
import me.ionar.salhack.main.Wrapper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({PlayerEntity.class})
public abstract class MixinPlayerEntity extends LivingEntity {

    public MixinPlayerEntity(World worldIn) {
        super(EntityType.PLAYER, worldIn);
    }

    @Inject(method = {"travel"}, at = {@At("HEAD")}, cancellable = true)
    private void travel(Vec3d movement, CallbackInfo info) {
        EventPlayerTravel l_Event = new EventPlayerTravel(movement.getX(), movement.getY(), movement.getZ());
        SalHackMod.EVENT_BUS.post(l_Event);
        if (l_Event.isCancelled()) info.cancel();
    }

    @Inject(method = {"jump"}, at = {@At("HEAD")}, cancellable = true)
    private void jump(CallbackInfo callback) {
        if (Wrapper.GetMC().player == null) return;

        SalHackMod.EVENT_BUS.post(new EventPlayerJump());
    }
}
