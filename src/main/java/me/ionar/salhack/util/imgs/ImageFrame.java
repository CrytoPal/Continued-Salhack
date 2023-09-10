package me.ionar.salhack.util.imgs;

import net.minecraft.util.Identifier;

public class ImageFrame {
    private final int Delay;
    private final Identifier Image;
    private final String Disposal;
    private final int Width, Height;

    public ImageFrame(Identifier image, int delay, String disposal, int width, int height) {
        Image = image;
        Delay = delay;
        Disposal = disposal;
        Width = width;
        Height = height;
    }

    public ImageFrame(Identifier image) {
        Image = image;
        Delay = -1;
        Disposal = null;
        Width = -1;
        Height = -1;
    }

    public Identifier getImage() {
        return Image;
    }

    public int getDelay() {
        return Delay;
    }

    public String getDisposal() {
        return Disposal;
    }

    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }
}
