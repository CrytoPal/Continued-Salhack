package me.ionar.salhack.events.render;

import me.ionar.salhack.events.Event;
import net.minecraft.client.util.math.MatrixStack;

public class RenderEvent extends Event {

    private final MatrixStack stack;
    public RenderEvent(MatrixStack stack){
        this.stack = stack;
    }

    public MatrixStack getMatrixStack(){
        return stack;
    }
}
