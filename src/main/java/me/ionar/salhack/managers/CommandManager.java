package me.ionar.salhack.managers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import me.ionar.salhack.command.Command;
import me.ionar.salhack.command.impl.*;
import me.ionar.salhack.command.util.ModuleCommandListener;
import me.ionar.salhack.main.SalHack;

public class CommandManager {
    public CommandManager() {}

    public void InitializeCommands() {
        //Commands.add(new FriendCommand());
        Commands.add(new HelpCommand());
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

        ModuleManager.Get().GetModuleList().forEach(module -> {
            ModuleCommandListener listener = new ModuleCommandListener() {
                @Override
                public void OnHide() {
                    module.setHidden(!module.isHidden());
                }

                @Override
                public void OnToggle() {
                    module.toggle(true);
                }

                @Override
                public void OnRename(String newName) {
                    module.setDisplayName(newName);
                }
            };

            Commands.add(new ModuleCommand(module.getDisplayName(), module.getDescription(), listener, module.getValueList()));
        });

        HudManager.Get().ComponentItems.forEach(componentItem -> {
            ModuleCommandListener listener = new ModuleCommandListener() {
                @Override
                public void OnHide() {
                    componentItem.SetHidden(!componentItem.IsHidden());
                }

                @Override
                public void OnToggle() {
                    componentItem.SetHidden(!componentItem.IsHidden());
                }

                @Override
                public void OnRename(String newName) {
                    componentItem.SetDisplayName(newName, true);
                }
            };

            Commands.add(new ModuleCommand(componentItem.GetDisplayName(), "NYI", listener, componentItem.ValueList));
        });

        /// Sort by alphabet
        Commands.sort(Comparator.comparing(Command::GetName));
    }

    private final ArrayList<Command> Commands = new ArrayList<>();

    public final ArrayList<Command> GetCommands() {
        return Commands;
    }

    public final List<Command> GetCommandsLike(String like) {
        return Commands.stream().filter(command -> command.GetName().toLowerCase().startsWith(like.toLowerCase())).collect(Collectors.toList());
    }

    public static CommandManager Get() {
        return SalHack.GetCommandManager();
    }

    public Command GetCommandLike(String like) {
        for (Command command : Commands) {
            if (command.GetName().toLowerCase().startsWith(like.toLowerCase())) return command;
        }
        return null;
    }

    public void Reload() {
        Commands.clear();
        InitializeCommands();
    }
}
