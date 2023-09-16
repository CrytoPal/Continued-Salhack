package me.ionar.salhack.util.imgs;

import net.minecraft.client.texture.DynamicTexture;

import java.awt.image.BufferedImage;

public class SalDynamicTexture extends BufferedImage {
    private final int height, width;
    private final BufferedImage bufferedImage;
    private String texturedLocation;
    private ImageFrame frame;

    public SalDynamicTexture(BufferedImage bufferedImage, int height, int width)  {
        super(width, height, 1);
        frame = null;
        this.bufferedImage = bufferedImage;
        this.height = height;
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public final DynamicTexture getDynamicTexture() {
        return (DynamicTexture)this;
    }

    public void setResourceLocation(String dynamicTextureLocation) {
        texturedLocation = dynamicTextureLocation;
    }

    public final String getResourceLocation() {
        return texturedLocation;
    }

    public void setImageFrame(final ImageFrame frame) {
        this.frame = frame;
    }

    /// used for gifs
    public final ImageFrame getFrame() {
        return frame;
    }
}
