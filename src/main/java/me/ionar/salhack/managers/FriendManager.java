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
import me.ionar.salhack.module.misc.Friends;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class FriendManager {
    public static FriendManager Get() {
        return SalHack.GetFriendManager();
    }

    private Friends m_FriendsModule;

    public FriendManager() {
    }

    /// Loads the friends from the JSON
    public void LoadFriends() {
        File l_Exists = new File("SalHack/FriendList.json");
        if (!l_Exists.exists())
            return;

        try {
            // create Gson instance
            Gson gson = new Gson();

            // create a reader
            Reader reader = Files.newBufferedReader(Paths.get("SalHack/" + "FriendList" + ".json"));

            // convert JSON file to map
            FriendList = gson.fromJson(reader, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType());

            // close reader
            reader.close();

        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void SaveFriends() {
        GsonBuilder builder = new GsonBuilder();

        Gson gson = builder.setPrettyPrinting().create();

        Writer writer;
        try {
            writer = Files.newBufferedWriter(Paths.get("SalHack/" + "FriendList" + ".json"));

            gson.toJson(FriendList, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType(), writer);
            writer.close();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private LinkedTreeMap<String, Friend> FriendList = new LinkedTreeMap<>();

    public String GetFriendName(Entity p_Entity) {
        if (!FriendList.containsKey(p_Entity.getName().getString().toLowerCase()))
            return p_Entity.getName().getString();

        return FriendList.get(p_Entity.getName().getString().toLowerCase()).GetAlias();
    }

    public boolean IsFriend(Entity p_Entity) {
        return p_Entity instanceof PlayerEntity && FriendList.containsKey(p_Entity.getName().getString().toLowerCase());
    }

    public boolean AddFriend(String p_Name) {
        if (FriendList.containsKey(p_Name))
            return false;

        Friend l_Friend = new Friend(p_Name, p_Name, null);

        FriendList.put(p_Name, l_Friend);
        SaveFriends();
        return true;
    }

    public boolean RemoveFriend(String p_Name) {
        if (!FriendList.containsKey(p_Name))
            return false;

        FriendList.remove(p_Name);
        SaveFriends();
        return true;
    }

    public final LinkedTreeMap<String, Friend> GetFriends() {
        return FriendList;
    }

    public boolean IsFriend(String p_Name) {
        if (!m_FriendsModule.isEnabled())
            return false;

        return FriendList.containsKey(p_Name.toLowerCase());
    }

    public Friend GetFriend(Entity e) {
        if (!m_FriendsModule.isEnabled())
            return null;

        if (!FriendList.containsKey(e.getName().getString().toLowerCase()))
            return null;

        return FriendList.get(e.getName().getString().toLowerCase());
    }

    public void Load() {
        LoadFriends();

        m_FriendsModule = (Friends)ModuleManager.Get().GetMod(Friends.class);
    }
}