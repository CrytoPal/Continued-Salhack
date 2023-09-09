package me.ionar.salhack.events.entity;

import me.ionar.salhack.events.Event;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends Event {
    private final Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }
}