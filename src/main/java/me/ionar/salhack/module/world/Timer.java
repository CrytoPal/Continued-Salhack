package me.ionar.salhack.module.world;

import java.text.DecimalFormat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public final class Timer extends Module {
    public final Value<Float> speed = new Value<>("Speed", new String[]{"Spd"}, "Tick-rate multiplier. [(20tps/second) * (this value)]", 4.0f, 0.1f, 20.0f, 0.1f);
    public final Value<Boolean> accelerate = new Value<>("Accelerate", new String[]{"Acc"}, "Accelerates from 1.0 until the anti-cheat lags you back", false);
    public final Value<Boolean> tpsSync = new Value<>("TPSSync", new String[]{"TPS"}, "Syncs the game time to the current TPS", false);
    private final me.ionar.salhack.util.Timer timer = new me.ionar.salhack.util.Timer();
    private float overrideSpeed = 1.0f;
    private final DecimalFormat format = new DecimalFormat("#.#");

    public Timer() {
        super("Timer", new String[]{ "Time", "Tmr" }, "Speeds up the client tick rate", 0, 0x24DBA3, ModuleType.WORLD);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        SalHack.TICK_TIMER = 1;
    }

    @Override
    public String getMetaData() {
        if (overrideSpeed != 1.0f) return String.valueOf(overrideSpeed);
        if (tpsSync.getValue()) {
            float TPS = SalHack.getTickRateManager().getTickRate();
            return format.format((TPS/20));
        }
        return format.format(getSpeed());
    }

    @EventHandler
    private void onPlayerUpdate(TickEvent event) {
        if (event.isPre()) return;
        if (overrideSpeed != 1.0f && overrideSpeed > 0.1f) {
            SalHack.TICK_TIMER = (int) (1 * overrideSpeed);
            return;
        }
        if (tpsSync.getValue()) {
            float TPS = SalHack.getTickRateManager().getTickRate();
            SalHack.TICK_TIMER = (int) Math.min(0.1,(20/TPS));
        } else SalHack.TICK_TIMER = (int) (1 * getSpeed());
        if (accelerate.getValue() && timer.passed(2000)) {
            timer.reset();
            speed.setValue(speed.getValue() + 0.1f);
        }
    }

    @EventHandler
    private void onPacket(PacketEvent.Receive event) {
        if (!event.isPre()) return;
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket && accelerate.getValue()) speed.setValue(1.0f);
    }

    private float getSpeed() {
        return Math.max(speed.getValue(), 0.1f);
    }

    public void setOverrideSpeed(float speedOverride) {
        overrideSpeed = speedOverride;
    }

}
