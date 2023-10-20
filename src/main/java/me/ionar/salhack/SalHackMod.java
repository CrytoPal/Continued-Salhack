package me.ionar.salhack;

import io.github.racoondog.norbit.EventBus;
import io.github.racoondog.norbit.IEventBus;
import me.ionar.salhack.main.SalHack;
import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;

public class SalHackMod implements ClientModInitializer {
    public static final Logger log = LoggerFactory.getLogger("SalHack");
	public static final String NAME = "SalHack";
	public static final String MOD_ID = "sal-hack";
	public static final String VERSION = "v0.7 Beta";
	public static final IEventBus NORBIT_EVENT_BUS = EventBus.threadSafe();

	@Override
	public void onInitializeClient() {
		NORBIT_EVENT_BUS.registerLambdaFactory("me.ionar.salhack", (lookupInMethod, klass) -> (MethodHandles.Lookup) lookupInMethod.invoke(null, klass, MethodHandles.lookup()));
		log.info("Welcome to " + NAME);
		SalHack.Init();
		NORBIT_EVENT_BUS.subscribe(this);
	}
}
