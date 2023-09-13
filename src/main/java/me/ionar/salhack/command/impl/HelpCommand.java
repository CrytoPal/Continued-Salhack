package me.ionar.salhack.command.impl;

import java.util.List;

import me.ionar.salhack.command.Command;
import me.ionar.salhack.main.SalHack;
import net.minecraft.util.Formatting;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("Help", "Gives you help for commands");
    }

    public void processCommand(String args) {
        String[] split = args.split(" ");
        if (split.length <= 1) {
            SendToChat(getHelp());
            return;
        }
        Command command = SalHack.getCommandManager().getCommandLike(split[1]);
        if (command == null) SendToChat(String.format("Couldn't find any command named like %s", split[1]));
        else SendToChat(command.getHelp());
    }

    @Override
    public String getHelp() {
        final List<Command> commands = SalHack.getCommandManager().getCommands();
        return "Available commands: (" + commands.size() + ")" + Formatting.WHITE + commands.stream().map(Command::getName);
    }
}
