package me.ionar.salhack.main;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public class Wrapper {

    final static MinecraftClient mc = MinecraftClient.getInstance();

    public static MinecraftClient GetMC()
    {
        return mc;
    }

    public static ClientPlayerEntity GetPlayer()
    {
        return mc.player;
    }
}
