package me.ionar.salhack.module.ui;

import me.ionar.salhack.module.Module;

public class Notification extends Module {

    public Notification() {
        super("Notification", new String[]{ "Notif" }, "Enables notifications for modules ON/OFF", 0, 0xDB9324, ModuleType.UI);
    }
}
