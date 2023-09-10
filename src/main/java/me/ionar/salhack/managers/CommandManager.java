package me.ionar.salhack.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import me.ionar.salhack.command.Command;
import me.ionar.salhack.command.impl.*;
import me.ionar.salhack.command.util.ModuleCommandListener;
import me.ionar.salhack.main.SalHack;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class CommandManager {
    public CommandManager() {
    }

    public void init() {
        //Commands.add(new FriendCommand());
        commands.add(new HelpCommand());
        /*
        Commands.add(new SoundReloadCommand());
        Commands.add(new HClipCommand());
        Commands.add(new VClipCommand());
        Commands.add(new ToggleCommand());
        Commands.add(new BindCommand());
        Commands.add(new UnbindCommand());
        Commands.add(new ResetGUICommand());
        Commands.add(new FontCommand());
        Commands.add(new PresetsCommand());
        Commands.add(new WaypointCommand());

         */

        SalHack.getModuleManager().getModuleList().forEach(p_Mod -> {
            ModuleCommandListener l_Listener = new ModuleCommandListener() {
                @Override
                public void OnHide()
                {
                    p_Mod.setHidden(!p_Mod.isHidden());
                }

                @Override
                public void OnToggle()
                {
                    p_Mod.toggle(true);
                }

                @Override
                public void OnRename(String p_NewName)
                {
                    p_Mod.setDisplayName(p_NewName);
                }
            };

            commands.add(new ModuleCommand(p_Mod.getDisplayName(), p_Mod.getDescription(), l_Listener, p_Mod.getValues()));
        });

        SalHack.getHudManager().componentItems.forEach(p_Item -> {
            ModuleCommandListener l_Listener = new ModuleCommandListener() {
                @Override
                public void OnHide()
                {
                    p_Item.setHidden(!p_Item.isHidden());
                }

                @Override
                public void OnToggle()
                {
                    p_Item.setHidden(!p_Item.isHidden());
                }

                @Override
                public void OnRename(String p_NewName) {
                    p_Item.setDisplayName(p_NewName, true);
                }
            };

            commands.add(new ModuleCommand(p_Item.getDisplayName(), "NYI", l_Listener, p_Item.values));
        });

        /// Sort by alphabet
        commands.sort(Comparator.comparing(Command::GetName));
    }

    private ArrayList<Command> commands = new ArrayList<Command>();

    public final ArrayList<Command> getCommands() {
        return commands;
    }

    public final List<Command> getCommandsLike(String p_Like) {
        return commands.stream()
                .filter(p_Command -> p_Command.GetName().toLowerCase().startsWith(p_Like.toLowerCase()))
                .collect(Collectors.toList());
    }

    public Command getCommandLike(String p_Like) {
        for (Command l_Command : commands) {
            if (l_Command.GetName().toLowerCase().startsWith(p_Like.toLowerCase()))
                return l_Command;
        }

        return null;
    }

    public void reload() {
        commands.clear();
        init();
    }
}
