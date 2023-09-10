package me.ionar.salhack.command.impl;

import java.util.List;

import me.ionar.salhack.command.Command;
import me.ionar.salhack.main.SalHack;
import net.minecraft.util.Formatting;

public class HelpCommand extends Command {
    public HelpCommand() {
        super("Help", "Gives you help for commands");
    }

    public void processCommand(String p_Args) {
        String[] l_Split = p_Args.split(" ");

        if (l_Split == null || l_Split.length <= 1) {
            SendToChat(getHelp());
            return;
        }

        Command l_Command = SalHack.getCommandManager().getCommandLike(l_Split[1]);

        if (l_Command == null)
            SendToChat(String.format("Couldn't find any command named like %s", l_Split[1]));
        else
            SendToChat(l_Command.getHelp());
    }

    @Override
    public String getHelp() {
        final List<Command> l_Commands = SalHack.getCommandManager().getCommands();

        String l_CommandString = "Available commands: (" + l_Commands.size() + ")" + Formatting.WHITE + " [";

        for (int l_I = 0; l_I < l_Commands.size(); ++l_I) {
            Command l_Command = l_Commands.get(l_I);

            if (l_I == l_Commands.size() - 1)
                l_CommandString += l_Command.GetName() + "]";
            else
                l_CommandString += l_Command.GetName() + ", ";
        }

        return l_CommandString;
    }
}
