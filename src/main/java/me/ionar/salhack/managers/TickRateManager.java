package me.ionar.salhack.managers;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.SalHackMod;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.main.SalHack;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.util.math.MathHelper;

import java.lang.invoke.MethodHandles;

public class TickRateManager {
    private long PreviousTime;
    private final float[] Ticks = new float[20];
    private int CurrentTick;

    public TickRateManager() {
        SalHackMod.NORBIT_EVENT_BUS.registerLambdaFactory("me.ionar.salhack.managers.TickRateManager", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
        PreviousTime = -1;
        for (int i = 0, len = Ticks.length; i < len; i++) Ticks[i] = 0.0f;
        SalHackMod.NORBIT_EVENT_BUS.subscribe(this);
    }

    public float getTickRate() {
        int tickCount = 0;
        float tickRate = 0.0f;
        for (final float tick : Ticks) {
            if (tick > 0.0f) {
                tickRate += tick;
                tickCount++;
            }
        }
        return MathHelper.clamp((tickRate / tickCount), 0.0f, 20.0f);
    }

    @EventHandler
    private void PacketEvent(PacketEvent.Receive event) {
        if (!event.isPre()) return;
        if (event.getPacket() instanceof WorldTimeUpdateS2CPacket) {
            if (PreviousTime != -1) {
                Ticks[CurrentTick % Ticks.length] = MathHelper.clamp((20.0f / ((float) (System.currentTimeMillis() - PreviousTime) / 1000.0f)), 0.0f, 20.0f);
                CurrentTick++;
            }
            PreviousTime = System.currentTimeMillis();
        }
    }

    public static TickRateManager Get() {
        return SalHack.GetTickRateManager();
    }
}
