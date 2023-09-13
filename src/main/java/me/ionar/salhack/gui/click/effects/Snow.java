package me.ionar.salhack.gui.click.effects;

import me.ionar.salhack.main.SalHack;
import net.minecraft.client.util.Window;
import java.util.Random;

import net.minecraft.client.gui.DrawContext;

public class Snow {
    private int x;
    private int y;
    private int fallingSpeed;
    private int size;

    public Snow(int x, int y, int fallingSpeed, int size) {
        this.x = x;
        this.y = y;
        this.fallingSpeed = fallingSpeed;
        this.size = size;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void update(Window window, DrawContext context) {
        context.fill(getX(), getY(), getX() + size, getY() + size, 0x99C9C5C5);
        setY(getY() + fallingSpeed);
        if (getY() > window.getHeight() + 10 || getY() < -10) {
            setY(-10);
            fallingSpeed = SalHack.random.nextInt(10) + 1;
            size = SalHack.random.nextInt(4) + 1;
        }
    }
}
