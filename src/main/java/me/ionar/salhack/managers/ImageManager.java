package me.ionar.salhack.managers;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import javax.imageio.ImageIO;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.util.imgs.SalDynamicTexture;

public class ImageManager {
    public NavigableMap<String, SalDynamicTexture> Pictures = new TreeMap<>();
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

    public void LoadImage(String img) {
        BufferedImage image = null;

        URL resource = ImageManager.class.getResource("/assets/minecraft/salhack/imgs/" + img + ".png");

        try {
            image = ImageIO.read(resource);
        } catch (IOException ignored) {}

        if (image == null) {
            System.out.println("Couldn't load image: " + img);
            return;
        }

        int height = image.getHeight();
        int width = image.getWidth();

        final SalDynamicTexture texture = new SalDynamicTexture(image, height, width);
        System.out.println(texture.GetResourceLocation());
        texture.SetResourceLocation("salhack/imgs/" + img + ".png");

        Pictures.put(img, texture);

        System.out.println("Loaded Img: " + img);
    }

    public SalDynamicTexture GetDynamicTexture(String image) {
        if (Pictures.containsKey(image)) return Pictures.get(image);
        return null;
    }

    public String GetNextImage(String value, boolean recursive) {
        String next = null;
        for (Map.Entry<String, SalDynamicTexture> entry : Pictures.entrySet()) {
            if (!entry.getKey().equalsIgnoreCase(value)) continue;
            if (recursive) {
                next = Pictures.lowerKey(entry.getKey());
                if (next == null) return Pictures.lastKey();
            } else {
                next = Pictures.higherKey(entry.getKey());
                if (next == null) return Pictures.firstKey();
            }
            return next;
        }
        return next;
    }

    public static ImageManager Get() {
        return SalHack.GetImageManager();
    }
}
