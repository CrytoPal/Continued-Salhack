package me.ionar.salhack.managers;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import me.ionar.salhack.friend.Friend;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.misc.FriendsModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class FriendManager {

    private FriendsModule m_FriendsModule;

    public FriendManager() {
    }

    /// Loads the friends from the JSON
    public void loadFriends() {
        File l_Exists = new File("SalHack/FriendList.json");
        if (!l_Exists.exists())
            return;

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("SalHack/" + "FriendList" + ".json"));

            // convert JSON file to map
            friendList = gson.fromJson(reader, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType());

            // close reader
            reader.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void saveFriends() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.setPrettyPrinting().create();

        Writer writer;
        try {
            writer = Files.newBufferedWriter(Paths.get("SalHack/" + "FriendList" + ".json"));

            gson.toJson(friendList, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType(), writer);
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private LinkedTreeMap<String, Friend> friendList = new LinkedTreeMap<>();

    public String getFriendName(Entity p_Entity) {
        if (!friendList.containsKey(p_Entity.getName().getString().toLowerCase()))
            return p_Entity.getName().getString();

        return friendList.get(p_Entity.getName().getString().toLowerCase()).GetAlias();
    }

    public boolean isFriend(Entity p_Entity) {
        return p_Entity instanceof PlayerEntity && friendList.containsKey(p_Entity.getName().getString().toLowerCase());
    }

    public boolean addFriend(String p_Name) {
        if (friendList.containsKey(p_Name))
            return false;

        Friend l_Friend = new Friend(p_Name, p_Name, null);

        friendList.put(p_Name, l_Friend);
        saveFriends();
        return true;
    }

    public boolean removeFriend(String p_Name) {
        if (!friendList.containsKey(p_Name))
            return false;

        friendList.remove(p_Name);
        saveFriends();
        return true;
    }

    public final LinkedTreeMap<String, Friend> getFriends() {
        return friendList;
    }

    public boolean isFriend(String p_Name) {
        if (!m_FriendsModule.isEnabled())
            return false;

        return friendList.containsKey(p_Name.toLowerCase());
    }

    public Friend getFriend(Entity e) {
        if (!m_FriendsModule.isEnabled())
            return null;

        if (!friendList.containsKey(e.getName().getString().toLowerCase()))
            return null;

        return friendList.get(e.getName().getString().toLowerCase());
    }

    public void init() {
        loadFriends();

        m_FriendsModule = (FriendsModule)SalHack.getModuleManager().getMod(FriendsModule.class);
    }
}