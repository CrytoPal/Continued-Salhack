package me.ionar.salhack.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import me.ionar.salhack.main.Wrapper;
import net.minecraft.util.Identifier;
import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.util.imgs.SalDynamicTexture;

public class ImageManager
{
    public NavigableMap<String, SalDynamicTexture> Pictures = new TreeMap<String, SalDynamicTexture>();

    public ImageManager() {}

    public void Load() {
        LoadImage("blood_overlay");
        LoadImage("rare_frame");
        LoadImage("outlined_ellipse");
        LoadImage("arrow");
        LoadImage("blockimg");
        LoadImage("blue_blur");
        LoadImage("eye");
        LoadImage("mouse");
        LoadImage("questionmark");
        LoadImage("robotimg");
        LoadImage("watermark");
        LoadImage("shield");
        LoadImage("skull");
    }

    public void LoadImage(String p_Img)
    {
        BufferedImage l_Image = null;

        InputStream l_Stream = ImageManager.class.getResourceAsStream("/assets/minecraft/salhack/imgs/" + p_Img + ".png");

        try
        {
            l_Image = ImageIO.read(l_Stream);
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (l_Image == null)
        {
            System.out.println("Couldn't load image: " + p_Img);
            return;
        }

        int l_Height = l_Image.getHeight();
        int l_Width = l_Image.getWidth();

        final SalDynamicTexture l_Texture = new SalDynamicTexture(l_Image, l_Height, l_Width);
        if (l_Texture != null)
        {
            System.out.println(l_Texture.GetResourceLocation());
            l_Texture.SetResourceLocation("salhack/imgs/" + p_Img + ".png");

            Pictures.put(p_Img, l_Texture);

            System.out.println("Loaded Img: " + p_Img);
        }
    }

    public SalDynamicTexture GetDynamicTexture(String p_Image)
    {
        if (Pictures.containsKey(p_Image))
            return Pictures.get(p_Image);

        return null;
    }

    public String GetNextImage(String value, boolean p_Recursive)
    {
        String l_String = null;

        for (Map.Entry<String, SalDynamicTexture> l_Itr : Pictures.entrySet())
        {
            if (!l_Itr.getKey().equalsIgnoreCase(value))
                continue;

            if (p_Recursive)
            {
                l_String = Pictures.lowerKey(l_Itr.getKey());

                if (l_String == null)
                    return Pictures.lastKey();
            }
            else
            {
                l_String = Pictures.higherKey(l_Itr.getKey());

                if (l_String == null)
                    return Pictures.firstKey();
            }

            return l_String;
        }

        return l_String;
    }

    public static ImageManager Get() {return SalHack.GetImageManager();}

}
