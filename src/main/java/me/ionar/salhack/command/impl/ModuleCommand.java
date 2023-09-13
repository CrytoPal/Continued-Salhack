package me.ionar.salhack.command.impl;

import java.util.List;

import me.ionar.salhack.command.Command;
import me.ionar.salhack.command.util.ModuleCommandListener;
import me.ionar.salhack.module.Value;

public class ModuleCommand extends Command {
    private ModuleCommandListener listener;
    private final List<Value> values;

    public ModuleCommand(String name, String description, ModuleCommandListener listener, final List<Value> values) {
        super(name, description);
        this.listener = listener;
        this.values = values;
        commandChunks.add("hide");
        commandChunks.add("toggle");
        commandChunks.add("rename <newname>");
        /// TODO: Add enum names, etc
        for (Value value : this.values) commandChunks.add(String.format("%s <%s>", value.getName(), "value"));
    }

    @Override
    public void processCommand(String args) {
        String[] split = args.split(" ");
        if (split.length <= 1) {
            for (Value value : values) SendToChat(String.format("%s : %s",value.getName(), value.getValue()));
            return;
        }
        if (split[1].equalsIgnoreCase("hide")) {
            listener.onHide();
            return;
        }
        if (split[1].equalsIgnoreCase("toggle")) {
            listener.onHide();
            return;
        }
        if (split[1].equalsIgnoreCase("rename")) {
            if (split.length <= 3) listener.onRename(split[2]);
            return;
        }
        for (Value value : values) {
            if (value.getName().toLowerCase().startsWith(split[1].toLowerCase())) {
                if (split.length == 2) break;
                String value2 = split[2].toLowerCase();
                if (value.getValue() instanceof Number && !(value.getValue() instanceof Enum)) {
                    if (value.getValue() instanceof Integer) value.setForcedValue(Integer.parseInt(value2));
                    else if (value.getValue() instanceof Float) value.setForcedValue(Float.parseFloat(value2));
                    else if (value.getValue() instanceof Double) value.setForcedValue(Double.parseDouble(value2));
                } else if (value.getValue() instanceof Boolean) value.setForcedValue(value2.equalsIgnoreCase("true"));
                else if (value.getValue() instanceof Enum) value.setForcedValue(value.getEnumReal(value2));
                else if (value.getValue() instanceof String) value.setForcedValue(value2);
                SendToChat(String.format("Set the value of %s to %s", value.getName(), value.getValue()));
                break;
            }
        }
    }

    @Override
    public String getHelp() {
        return getDescription();
    }
}
