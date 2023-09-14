package me.ionar.salhack.events.entity;

import me.ionar.salhack.events.Event;
import net.minecraft.entity.Entity;

public class EntityRemovedEvent extends Event {
    private Entity entity;

    public EntityRemovedEvent(Entity entity){
        this.entity = entity;
    }


    public Entity GetEntity() {
        return entity;
    }
}
