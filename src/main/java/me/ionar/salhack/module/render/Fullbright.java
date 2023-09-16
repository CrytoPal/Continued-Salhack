package me.ionar.salhack.module.render;

import io.github.racoondog.norbit.EventHandler;
import me.ionar.salhack.events.world.TickEvent;
import me.ionar.salhack.managers.ModuleManager;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.text.Text;

import static me.ionar.salhack.main.Wrapper.mc;

public class Fullbright extends Module {


    public final Value<Mode> mode = new Value<>("Mode", new String[]{"Mode", "M"}, "Mode to use for 2b2t flight.", Mode.NightVision);
    public Fullbright() {
        super("Fullbright", new String[]{"FB"}, "Brights up your world", 0, -1, ModuleType.RENDER);
    }

    public enum Mode {
        NightVision, Gamma
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            if (mode.getValue() == Mode.NightVision) {
                mc.player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, 999999999, 5));
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            if (mode.getValue() == Mode.NightVision) {
                mc.player.removeStatusEffect(StatusEffects.NIGHT_VISION);
            }
        }
    }
}
