package me.ionar.salhack.managers;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.network.PacketEvent;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

import java.lang.invoke.MethodHandles;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class TickRateManager {
    private long prevTime;
    private float[] ticks = new float[20];
    private int currentTick;

    public TickRateManager() {
        SalHackMod.NORBIT_EVENT_BUS.registerLambdaFactory("me.ionar.salhack.managers.TickRateManager", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        this.prevTime = -1;

        for (int i = 0, len = this.ticks.length; i < len; i++) {
            this.ticks[i] = 0.0f;
        }
        SalHackMod.NORBIT_EVENT_BUS.subscribe(this);
    }

    public float getTickRate() {
        int tickCount = 0;
        float tickRate = 0.0f;

        for (int i = 0; i < this.ticks.length; i++) {
            final float tick = this.ticks[i];

            if (tick > 0.0f) {
                tickRate += tick;
                tickCount++;
            }
        }

        return MathHelper.clamp((tickRate / tickCount), 0.0f, 20.0f);
    }

    @EventHandler
    private void onPacket(PacketEvent.Receive event) {
        if (!event.isPre()) return;

        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            if (this.prevTime != -1) {
                this.ticks[this.currentTick % this.ticks.length] = MathHelper.clamp((20.0f / ((float) (System.currentTimeMillis() - this.prevTime) / 1000.0f)), 0.0f, 20.0f);
                this.currentTick++;
            }

            this.prevTime = System.currentTimeMillis();
        }
    }
}
