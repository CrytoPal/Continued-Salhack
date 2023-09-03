package me.ionar.salhack;

import me.ionar.salhack.main.SalHack;
import me.zero.alpine.fork.bus.EventManager;
import net.fabricmc.api.ModInitializer;
import me.zero.alpine.fork.bus.EventBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SalHackMod implements ModInitializer {
    public static final Logger log = LoggerFactory.getLogger("sal");
	public static final String NAME = "SalHack";
	public static final String VERSION = "v0.3 Beta";

	public static final EventBus EVENT_BUS = new EventManager();

	@Override
	public void onInitialize() {
		log.info("Welcome to " + NAME);
		SalHack.Init();
	}
}