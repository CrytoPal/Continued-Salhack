package me.ionar.salhack.module.misc;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import me.ionar.salhack.module.Module;
import me.ionar.salhack.module.Value;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import org.apache.commons.io.IOUtils;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class FakePlayer extends Module {
    public static final Value<String> name = new Value<>("Name", new String[] {"Name"}, "Name of the fake player", "bluegooon");

    public FakePlayer() {
        super("FakePlayer", new String[] {"Fake"}, "Summons a fake player", 0, 0xDADB25, ModuleType.MISC);
    }

    private OtherClientPlayerEntity fakePlayer;

    @Override
    public void onEnable() {
        super.onEnable();
        fakePlayer = null;
        if (mc.world == null || mc.player == null) {
            this.toggle(true);
            return;
        }
        // If getting uuid from mojang doesn't work we use another uuid
        try {
            fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.fromString(getUuid(name.getValue())), name.getValue()));
        } catch (Exception e) {
            fakePlayer = new OtherClientPlayerEntity(mc.world, new GameProfile(UUID.fromString(getUuid(mc.player.getEntityName())), name.getValue()));
            sendMessage("Failed to load uuid, setting another one.");
        }
        sendMessage(String.format("%s has been spawned.", name.getValue()));
        fakePlayer.copyFrom(mc.player);
        fakePlayer.headYaw = mc.player.getHeadYaw();
        mc.world.addEntity(-100, fakePlayer);
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.world == null) return;
        mc.world.removeEntity(fakePlayer.getId(), Entity.RemovalReason.UNLOADED_WITH_PLAYER);
    }

    // Getting uuid from a Name
    public static String getUuid(String name) {
        Gson gson = new Gson();
        String url = "https://api.mojang.com/users/profiles/minecraft/" + name;
        try {
            String UUIDJson = IOUtils.toString(new URL(url), StandardCharsets.UTF_8);
            if(UUIDJson.isEmpty()) return "invalid name";
            JsonObject UUIDObject = gson.fromJson(UUIDJson, JsonObject.class);
            return reformatUuid(UUIDObject.get("id").toString());
        } catch (Exception ignored) {}
        return "error";
    }

    // Reformating a short uuid type into a long uuid type
    private static String reformatUuid(String uuid) {
        String longUuid = "";
        longUuid += uuid.substring(1, 9) + "-";
        longUuid += uuid.substring(9, 13) + "-";
        longUuid += uuid.substring(13, 17) + "-";
        longUuid += uuid.substring(17, 21) + "-";
        longUuid += uuid.substring(21, 33);
        return longUuid;
    }
}
