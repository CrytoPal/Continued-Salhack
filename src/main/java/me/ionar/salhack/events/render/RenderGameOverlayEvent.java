package me.ionar.salhack.events.render;

import me.ionar.salhack.events.Event;
import net.minecraft.client.gui.DrawContext;

public class RenderGameOverlayEvent extends Event {
    public final float tickDelta;
    private final DrawContext context;

    public RenderGameOverlayEvent(DrawContext context, float tickDelta) {
        this.tickDelta = tickDelta;
        this.context = context;
    }

    public DrawContext getContext() {
        return context;
    }
}
