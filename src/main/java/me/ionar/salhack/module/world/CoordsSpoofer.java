package me.ionar.salhack.module.world;

import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;

public class CoordsSpooferModule extends Module {
    public final Value<Integer> coordsZ = new Value<>("Z", new String[]{"Z"}, "How much to Spoof Z coord", 0, 0, 30000000, 11);
    public final Value<Integer> coordsX = new Value<>("X", new String[]{"X"}, "How much to Spoof X coord", 0, 0, 30000000, 11);
    public final Value<Integer> coordsNegativeZ = new Value<>("-Z", new String[]{"-Z"}, "How much to Spoof -Z coord", 0, 0, -30000000, 11);
    public final Value<Integer> coordsNegativeX = new Value<>("-X", new String[]{"-X"}, "How much to Spoof -X coord", 0, 0, -30000000, 11);
    public final Value<Boolean> random = new Value<>("Random", new String[]{"Random"}, "Randomly Spoofs", false);
    public final Value<Boolean> textureSpoof = new Value<>("Texture Spoof", new String[]{"TextureSpoof"}, "No terrain exploits", false);
    public final Value<Boolean> bedrockSpoof = new Value<>("Bedrock Spoof", new String[]{"BedrockSpoof"}, "Removes Bedrock textures to prevent exploits", false);
    public final Value<Boolean> cloudSpoof = new Value<>("Cloud Spoof", new String[]{"WeatherSpoof"}, "Removes Cloud to prevent exploits", false);
    public final Value<Boolean> flowerSpoof = new Value<>("Flower Spoof", new String[]{"FlowerSpoof"}, "Removes Flowers to prevent exploits", false);
    public final Value<Boolean> biomeSpoof = new Value<>("Biome Spoof", new String[]{"BiomeSpoof"}, "Prevents biome from changing water or leave color", false);

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