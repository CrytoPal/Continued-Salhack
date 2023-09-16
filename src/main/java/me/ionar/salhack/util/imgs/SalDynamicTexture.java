package me.ionar.salhack.util.imgs;

import net.minecraft.client.texture.DynamicTexture;

import java.awt.image.BufferedImage;

public class SalDynamicTexture extends BufferedImage {
    private final int Height;
    private final int Width;
    private final BufferedImage BufferedImage;
    private String TexturedLocation;
    private ImageFrame Frame;

    public SalDynamicTexture(BufferedImage bufferedImage, int height, int width)  {
        super(width, height, 1);
        Frame = null;
        BufferedImage = bufferedImage;
        Height = height;
        Width = width;
    }

    public int GetHeight() {
        return Height;
    }

    public int GetWidth() {
        return Width;
    }

    public final DynamicTexture GetDynamicTexture() {
        return (DynamicTexture)this;
    }

    public void SetResourceLocation(String dynamicTextureLocation) {
        TexturedLocation = dynamicTextureLocation;
    }

    public final String GetResourceLocation() {
        return TexturedLocation;
    }

    public void SetImageFrame(final ImageFrame frame) {
        Frame = frame;
    }

    /// used for gifs
    public final ImageFrame GetFrame() {
        return Frame;
    }
}
