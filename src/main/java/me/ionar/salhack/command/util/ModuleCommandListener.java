package me.ionar.salhack.command.util;

public interface ModuleCommandListener {
    public void onHide();
    public void onToggle();
    public void onRename(String p_NewName);
}
