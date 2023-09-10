package me.ionar.salhack.main;

import net.minecraft.client.MinecraftClient;

public class Wrapper {
    final static MinecraftClient mc = MinecraftClient.getInstance();
    public static MinecraftClient GetMC() {
        return mc;
    }
}
