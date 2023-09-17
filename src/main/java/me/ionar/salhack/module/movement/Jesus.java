package me.ionar.salhack.module.movement;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class Jesus extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", new String[]
            {"Mode", "M"}, "The current Jesus/WaterWalk mode to use.", Mode.NCP);
    public final Value<Float> offSet = new Value<Float>("Offset", new String[]
            {"Off", "O"}, "Amount to offset the player into the water's bounding box.", 0.18f, 0.0f, 0.9f, 0.01f);

    // public final Value<Boolean> Speed = new Value<>("Speed", new String[]{""}, "", true);
    private final Timer timer = new Timer();

    private boolean isInLiquid(Vec3d pos) {
        BlockPos bp = BlockPos.ofFloored(pos);
        FluidState state = mc.world.getFluidState(bp);

        return !state.isEmpty() && pos.y - bp.getY() <= state.getHeight();
    }

    public Jesus() {
        super("Jesus", new String[]
                {"LiquidWalk", "WaterWalk"}, "Allows you to walk on water", 0, 0x24DB6E, ModuleType.MOVEMENT);
    }

    @Override
    public String getMetaData() {
        return this.mode.getValue().name();
    }

    @EventHandler
    public void onTick(TickEvent event) {
        if (mode.getValue() == Mode.NCP) {
            Entity entity = mc.player.getRootVehicle();

            if (entity.isSneaking() || entity.fallDistance > 3f)
                return;

            if (isInLiquid(entity.getPos().add(0, 0.3, 0))) {
                entity.setVelocity(entity.getVelocity().x, 0.08, entity.getVelocity().z);
            } else if (isInLiquid(entity.getPos().add(0, 0.1, 0))) {
                entity.setVelocity(entity.getVelocity().x, 0.05, entity.getVelocity().z);
            } else if (isInLiquid(entity.getPos().add(0, 0.05, 0))) {
                entity.setVelocity(entity.getVelocity().x, 0.01, entity.getVelocity().z);
            } else if (isInLiquid(entity.getPos())) {
                entity.setVelocity(entity.getVelocity().x, -0.01, entity.getVelocity().z);
                entity.setOnGround(true);
            }
        }
    }

    private enum Mode {
        NCP
    }

}