package me.ionar.salhack.events.render;

import me.ionar.salhack.events.MinecraftEvent;
import net.minecraft.client.gui.DrawContext;

public class EventRenderGameOverlay extends MinecraftEvent
{
    public float PartialTicks;
    DrawContext context;

    public EventRenderGameOverlay(DrawContext context, float p_PartialTicks)
    {
        super();
        PartialTicks = p_PartialTicks;
        this.context = context;
    }

    public DrawContext getContext() {
        return context;
    }
}
