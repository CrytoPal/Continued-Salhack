package me.ionar.salhack.command;

import java.util.ArrayList;
import java.util.List;

import me.ionar.salhack.main.SalHack;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Formatting;

public class Command {
    private final String name;
    private final String description;
    protected final MinecraftClient mc = MinecraftClient.getInstance();
    protected final List<String> commandChunks = new ArrayList<>();

    public Command(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void processCommand(String args) {}


    protected void SendToChat(String description) {
        SalHack.sendMessage(String.format("%s[%s]: %s", Formatting.LIGHT_PURPLE, getName(), Formatting.YELLOW + description));
    }

    public List<String> GetChunks() {
        return commandChunks;
    }

    public String getHelp() {
        return description;
    }
}
