package me.ionar.salhack.util.imgs;

import net.minecraft.util.Identifier;

public class ImageFrame {
    private final int delay;
    private final Identifier image;
    private final String disposal;
    private final int width, height;

    public ImageFrame(Identifier image, int delay, String disposal, int width, int height) {
        this.image = image;
        this.delay = delay;
        this.disposal = disposal;
        this.width = width;
        this.height = height;
    }

    public ImageFrame(Identifier image) {
        this.image = image;
        delay = -1;
        disposal = null;
        width = -1;
        height = -1;
    }

    public Identifier getImage() {
        return image;
    }

    public int getDelay() {
        return delay;
    }

    public String getDisposal() {
        return disposal;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
