package me.ionar.salhack.util.imgs;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;

import net.minecraft.client.texture.DynamicTexture;
import net.minecraft.util.Identifier;

public class SalDynamicTexture extends BufferedImage {
    private int Height;
    private int Width;
    private BufferedImage m_BufferedImage;
    private String m_TexturedLocation;
    private ImageFrame m_Frame;

    public SalDynamicTexture(BufferedImage bufferedImage, int p_Height, int p_Width)  {
        super(p_Width, p_Height, 1);
        m_Frame = null;
        m_BufferedImage = bufferedImage;
        Height = p_Height;
        Width = p_Width;
    }

    public int GetHeight()
    {
        return Height;
    }

    public int GetWidth()
    {
        return Width;
    }

    public final DynamicTexture GetDynamicTexture()
    {
        return (DynamicTexture)this;
    }

    public void SetResourceLocation(String dynamicTextureLocation)
    {
        m_TexturedLocation = dynamicTextureLocation;
    }

    public final String GetResourceLocation()
    {
        return m_TexturedLocation;
    }

    public void SetImageFrame(final ImageFrame p_Frame)
    {
        m_Frame = p_Frame;
    }

    /// used for gifs
    public final ImageFrame GetFrame()
    {
        return m_Frame;
    }
}
