package me.ionar.salhack.events.client;

import me.ionar.salhack.events.MinecraftEvent;

public class EventMouseButton extends MinecraftEvent {
    int button;
    int action;

    public EventMouseButton(int button, int action) {
        this.button = button;
        this.action = action;
    }

    public int getButton() {
        return this.button;
    }

    public int getAction() {
        return this.action;
    }
}
