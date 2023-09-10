package me.ionar.salhack.module.world;

import java.text.DecimalFormat;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.network.PacketEvent;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.managers.TickRateManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import me.ionar.salhack.util.Timer;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;

public final class TimerModule extends Module {
    public final Value<Float> Speed = new Value<>("Speed", new String[]{"Spd"}, "Tick-rate multiplier. [(20tps/second) * (this value)]", 4.0f, 0.1f, 20.0f, 0.1f);
    public final Value<Boolean> Accelerate = new Value<>("Accelerate", new String[]{"Acc"}, "Accelerates from 1.0 until the anti-cheat lags you back", false);
    public final Value<Boolean> TPSSync = new Value<>("TPSSync", new String[]{"TPS"}, "Syncs the game time to the current TPS", false);
    private final Timer Timer = new Timer();

    public TimerModule() {
        super("Timer", new String[]{ "Time", "Tmr" }, "Speeds up the client tick rate", 0, 0x24DBA3, ModuleType.WORLD);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        SalHack.TICK_TIMER = 1;
    }

    private float OverrideSpeed = 1.0f;

    /// store this as member to save cpu
    private final DecimalFormat Format = new DecimalFormat("#.#");

    @Override
    public String getMetaData() {
        if (OverrideSpeed != 1.0f) return String.valueOf(OverrideSpeed);
        if (TPSSync.getValue()) {
            float TPS = TickRateManager.Get().getTickRate();
            return Format.format((TPS/20));
        }
        return Format.format(GetSpeed());
    }

    @EventHandler
    private void OnPlayerUpdate(TickEvent event) {
        if (event.isPre()) return;
        if (OverrideSpeed != 1.0f && OverrideSpeed > 0.1f) {
            SalHack.TICK_TIMER = (int) (1 * OverrideSpeed);
            return;
        }
        if (TPSSync.getValue()) {
            float TPS = TickRateManager.Get().getTickRate();
            SalHack.TICK_TIMER = (int) Math.min(0.1,(20/TPS));
        } else SalHack.TICK_TIMER = (int) (1 * GetSpeed());
        if (Accelerate.getValue() && Timer.passed(2000)) {
            Timer.reset();
            Speed.setValue(Speed.getValue() + 0.1f);
        }
    }

    @EventHandler
    private void PacketEvent(PacketEvent.Receive event) {
        if (!event.isPre()) return;
        if (event.getPacket() instanceof PlayerPositionLookS2CPacket && Accelerate.getValue()) Speed.setValue(1.0f);
    }

    private float GetSpeed() {
        return Math.max(Speed.getValue(), 0.1f);
    }

    public void SetOverrideSpeed(float speedOverride) {
        OverrideSpeed = speedOverride;
    }

}
