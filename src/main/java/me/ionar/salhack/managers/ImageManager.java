package me.ionar.salhack.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import me.ionar.salhack.util.imgs.SalDynamicTexture;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class ImageManager {
    public NavigableMap<String, SalDynamicTexture> pictures = new TreeMap<String, SalDynamicTexture>();

    public ImageManager() {}

    public void init() {
        loadImage("blood_overlay");
        loadImage("rare_frame");
        loadImage("outlined_ellipse");
        loadImage("arrow");
        loadImage("blockimg");
        loadImage("blue_blur");
        loadImage("eye");
        loadImage("mouse");
        loadImage("questionmark");
        loadImage("robotimg");
        loadImage("watermark");
        loadImage("shield");
        loadImage("skull");
    }

    public void loadImage(String p_Img) {
        BufferedImage l_Image = null;

        InputStream l_Stream = ImageManager.class.getResourceAsStream("/assets/minecraft/salhack/imgs/" + p_Img + ".png");

        try {
            l_Image = ImageIO.read(l_Stream);
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (l_Image == null) {
            System.out.println("Couldn't load image: " + p_Img);
            return;
        }

        int l_Height = l_Image.getHeight();
        int l_Width = l_Image.getWidth();

        final SalDynamicTexture l_Texture = new SalDynamicTexture(l_Image, l_Height, l_Width);
        if (l_Texture != null) {
            System.out.println(l_Texture.getResourceLocation());
            l_Texture.setResourceLocation("salhack/imgs/" + p_Img + ".png");

            pictures.put(p_Img, l_Texture);

            System.out.println("Loaded Img: " + p_Img);
        }
    }

    public SalDynamicTexture getDynamicTexture(String p_Image) {
        if (pictures.containsKey(p_Image))
            return pictures.get(p_Image);

        return null;
    }

    public String getNextImage(String value, boolean p_Recursive) {
        String l_String = null;

        for (Map.Entry<String, SalDynamicTexture> l_Itr : pictures.entrySet()) {
            if (!l_Itr.getKey().equalsIgnoreCase(value))
                continue;

            if (p_Recursive) {
                l_String = pictures.lowerKey(l_Itr.getKey());

                if (l_String == null)
                    return pictures.lastKey();
            }
            else {
                l_String = pictures.higherKey(l_Itr.getKey());

                if (l_String == null)
                    return pictures.firstKey();
            }

            return l_String;
        }

        return l_String;
    }
}
