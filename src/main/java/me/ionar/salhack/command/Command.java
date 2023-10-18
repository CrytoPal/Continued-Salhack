package me.ionar.salhack.command;

import java.util.ArrayList;
import java.util.List;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.util.ChatUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

public class Command {
    private final String Name;
    private final String Description;
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected final List<String> CommandChunks = new ArrayList<String>();

    public Command(String CName, String CDescription) {
        Name = CName;
        Description = CDescription;
    }

    public String GetName() {
        return Name;
    }

    public String GetDescription() {
        return Description;
    }

    public void ProcessCommand(String p_Args) {
    }


    protected void SendToChat(String p_Desc) {
        ChatUtils.sendMessage(String.format("%s[%s]: %s", Formatting.LIGHT_PURPLE, GetName(), Formatting.YELLOW + p_Desc));
    }

    public List<String> GetChunks() {
        return CommandChunks;
    }

    public String GetHelp() {
        return Description;
    }
}
