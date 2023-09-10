package me.ionar.salhack.managers;

import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;
import me.ionar.salhack.friend.Friend;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.module.misc.FriendsModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

import java.io.File;

public class FriendManager {
    public static FriendManager Get() {
        return SalHack.GetFriendManager();
    }
    private FriendsModule FriendsModule;
    public FriendManager() {}

    /// Loads the friends from the JSON
    public void LoadFriends() {
        File exists = new File("SalHack/FriendList.json");
        if (!exists.exists()) return;
        String content = SalHack.GetFilesManager().read(exists.getPath());
        FriendList = SalHack.gson.fromJson(content, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType());
    }

    public void SaveFriends() {
        SalHack.GetFilesManager().write("SalHack/FriendList.json", SalHack.gson.toJson(FriendList, new TypeToken<LinkedTreeMap<String, Friend>>(){}.getType()));
    }

    private LinkedTreeMap<String, Friend> FriendList = new LinkedTreeMap<>();

    public String GetFriendName(Entity entity) {
        if (!FriendList.containsKey(entity.getEntityName())) return entity.getEntityName();
        return FriendList.get(entity.getEntityName()).GetAlias();
    }

    public boolean IsFriend(Entity entity) {
        return entity instanceof PlayerEntity && FriendList.containsKey(entity.getEntityName());
    }

    public void AddFriend(String name) {
        if (FriendList.containsKey(name)) return;
        Friend friend = new Friend(name, name, null);
        FriendList.put(name, friend);
        SaveFriends();
    }

    public void RemoveFriend(String name) {
        if (!FriendList.containsKey(name)) return;
        FriendList.remove(name);
        SaveFriends();
    }

    public final LinkedTreeMap<String, Friend> GetFriends() {
        return FriendList;
    }

    public boolean IsFriend(String name) {
        if (!FriendsModule.isEnabled()) return false;
        return FriendList.containsKey(name.toLowerCase());
    }

    public Friend GetFriend(Entity entity) {
        if (!FriendsModule.isEnabled()) return null;
        if (!FriendList.containsKey(entity.getEntityName())) return null;
        return FriendList.get(entity.getEntityName());
    }

    public void Load() {
        LoadFriends();
        FriendsModule = (FriendsModule)ModuleManager.Get().GetMod(FriendsModule.class);
    }
}