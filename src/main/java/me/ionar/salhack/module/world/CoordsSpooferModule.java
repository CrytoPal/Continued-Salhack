package me.ionar.salhack.module.world;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public class CoordsSpooferModule extends Module {
    public final Value<Integer> CoordsZ = new Value<>("Z", new String[]{"Z"}, "How much to Spoof Z coord", 0, 0, 30000000, 11);
    public final Value<Integer> CoordsX = new Value<>("X", new String[]{"X"}, "How much to Spoof X coord", 0, 0, 30000000, 11);
    public final Value<Integer> CoordsNegativeZ = new Value<>("-Z", new String[]{"-Z"}, "How much to Spoof -Z coord", 0, 0, -30000000, 11);
    public final Value<Integer> CoordsNegativeX = new Value<>("-X", new String[]{"-X"}, "How much to Spoof -X coord", 0, 0, -30000000, 11);
    public final Value<Boolean> Random = new Value<>("Random", new String[]{"Random"}, "Randomly Spoofs", false);
    public final Value<Boolean> TextureSpoof = new Value<>("Texture Spoof", new String[]{"TextureSpoof"}, "No terrain exploits", false);
    public final Value<Boolean> BedrockSpoof = new Value<>("Bedrock Spoof", new String[]{"BedrockSpoof"}, "Removes Bedrock textures to prevent exploits", false);
    public final Value<Boolean> CloudSpoof = new Value<>("Cloud Spoof", new String[]{"WeatherSpoof"}, "Removes Cloud to prevent exploits", false);
    public final Value<Boolean> FlowerSpoof = new Value<>("Flower Spoof", new String[]{"FlowerSpoof"}, "Removes Flowers to prevent exploits", false);
    public final Value<Boolean> BiomeSpoof = new Value<>("Biome Spoof", new String[]{"BiomeSpoof"}, "Prevents biome from changing water or leave color", false);

    public CoordsSpooferModule() {
        super("CoordSpoofer", new String[]{"CoordSpoofer"}, "Removes any way for players to get your coords", 0, 0x96DB24, ModuleType.WORLD);
    }

    @Override
    public void onEnable() {
        if (mc.worldRenderer != null) {
            super.onEnable();
            mc.worldRenderer.reload();
        }
    }
}