package me.ionar.salhack.font;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FontRenderers {
    private static final List<RendererFontAdapter> fontRenderers = new ArrayList<>();
    private static FontAdapter normal;
    private static FontAdapter mono;
    private static FontAdapter twCenMtStd28;
    private static FontAdapter twCenMtStd22;
    private static FontAdapter twCenMtStd15;

    public static FontAdapter getRenderer() {
        return normal;
    }

    public static void setRenderer(FontAdapter normal) {
        FontRenderers.normal = normal;
    }

    public static FontAdapter getMono() {
        if (mono == null) {
            int v = 100;
            try {
                mono = new RendererFontAdapter(Font.createFont(
                        Font.TRUETYPE_FONT,
                        Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("Mono.ttf"))
                ).deriveFont(Font.PLAIN, v), v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return mono;
    }

    public static FontAdapter getTwCenMtStd15() {
        if (twCenMtStd15 == null) {
            int v = 30;
            try {
                twCenMtStd15 = new RendererFontAdapter(Font.createFont(
                        Font.TRUETYPE_FONT,
                        Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("assets/minecraft/salhack/fonts/tcm.TTF"))
                ).deriveFont(Font.PLAIN, v), v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return twCenMtStd15;
    }

    public static FontAdapter getTwCenMtStd22() {
        if (twCenMtStd22 == null) {
            int v = 44;
            try {
                twCenMtStd22 = new RendererFontAdapter(Font.createFont(
                        Font.TRUETYPE_FONT,
                        Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("assets/minecraft/salhack/fonts/tcm.TTF"))
                ).deriveFont(Font.PLAIN, v), v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return twCenMtStd22;
    }

    public static FontAdapter getTwCenMtStd28() {
        if (twCenMtStd28 == null) {
            int v = 56;
            try {
                twCenMtStd28 = new RendererFontAdapter(Font.createFont(
                        Font.TRUETYPE_FONT,
                        Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("assets/minecraft/salhack/fonts/TwCenMtStd.ttf"))
                ).deriveFont(Font.PLAIN, v), v);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return twCenMtStd28;
    }

    public static RendererFontAdapter getCustomSize(float size) {
        float v = size / 2f; // assuming 2x scale
        for (RendererFontAdapter fontRenderer : fontRenderers) {
            if (fontRenderer.getSize() == v) {
                return fontRenderer;
            }
        }
        try {
            RendererFontAdapter bruhAdapter = new RendererFontAdapter(Font.createFont(
                    Font.TRUETYPE_FONT,
                    Objects.requireNonNull(FontRenderers.class.getClassLoader().getResourceAsStream("Font.ttf"))
            ).deriveFont(Font.PLAIN, v), v);
            fontRenderers.add(bruhAdapter);
            return bruhAdapter;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
