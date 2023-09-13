package me.ionar.salhack.main;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;

public class Wrapper {

    final static MinecraftClient mc = MinecraftClient.getInstance();

    public static MinecraftClient GetMC() {
        return mc;
    }

    public static Entity GetPlayer() {
        return mc.player;
    }
}
