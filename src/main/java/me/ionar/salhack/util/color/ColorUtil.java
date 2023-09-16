package me.ionar.salhack.util.color;

import java.awt.*;

public class ColorUtil {
    public Color baseColor;
    private final float[] hsb;
    private final float alpha;

    public ColorUtil(final Color colorBase) {
        super();
        baseColor = colorBase;
        hsb = generateHSB(colorBase);
        alpha = colorBase.getAlpha() / 255.0f;
    }

    public ColorUtil(final float hue, final float saturation, final float brightness) {
        this(hue, saturation, brightness, 1.0f);
    }

    public ColorUtil(final float[] hsb) {
        this(hsb, 1.0f);
    }

    public ColorUtil(final float[] hsb, final float alpha) {
        super();
        this.hsb = hsb;
        this.alpha = alpha;
        this.baseColor = getRainbowColorFromArray(hsb, alpha);
    }

    public ColorUtil(final float hue, final float saturation, final float brightness, final float alpha) {
        super();
        final float[] hsb = new float[3];
        hsb[0] = hue;
        hsb[1] = saturation;
        hsb[2] = brightness;
        this.hsb = hsb;
        this.alpha = alpha;
        this.baseColor = getRainbowColorFromArray(this.hsb, alpha);
    }

    public static float[] generateHSB(final Color color) {
        final float[] rgbColorComponents = color.getRGBColorComponents(null);
        final float n = rgbColorComponents[0];
        final float n2 = rgbColorComponents[1];
        final float n3 = rgbColorComponents[2];
        final float min = Math.min(n, Math.min(n2, n3));
        final float max = Math.max(n, Math.max(n2, n3));
        float n4 = 0.0f;
        float n5;
        if (max == min) {
            n4 = 0.0f;
            n5 = max;
        } else if (max == n) {
            n4 = (60.0f * (n2 - n3) / (max - min) + 360.0f) % 360.0f;
            n5 = max;
        } else if (max == n2) {
            n4 = 60.0f * (n3 - n) / (max - min) + 120.0f;
            n5 = max;
        } else {
            if (max == n3) n4 = 60.0f * (n - n2) / (max - min) + 240.0f;
            n5 = max;
        }
        final float n6 = (n5 + min) / 2.0f;
        float n7;
        if (max == min) n7 = 0.0f;
        else {
            final float n8 = Math.min(n6, 0.5f); //maybe max?
            if (n8 <= 0) n7 = (max - min) / (max + min);
            else n7 = (max - min) / (2.0f - max - min);
        }
        return new float[]{n4, n7 * 100.0f, n6 * 100.0f};
    }

    public static Color getRainbowColorFromArray(final float[] hSB, final float alpha) {
        return getRainbowColor(hSB[0], hSB[1], hSB[2], alpha);
    }

    public static Color getColorWithHSBArray(final float[] HSB) {
        return getRainbowColorFromArray(HSB, 1.0f);
    }

    public static String generateMCColorString(String string) {
        final char c = 'q';
        final char c2 = '\u0018';
        final int length = string.length();
        final char[] array = new char[length];
        int n;
        int i = n = length - 1;
        while (i >= 0) {
            final int n2 = n;
            final char char1 = string.charAt(n2);
            --n;
            array[n2] = (char) (char1 ^ c);
            if (n < 0) break;
            final int n3 = n--;
            array[n3] = (char) (string.charAt(n3) ^ c2);
            i = n;
        }
        return new String(array);
    }

    private static float futureClientColorCalculation(final float hue, final float saturation, float brightness) {
        if (brightness < 0.0f) ++brightness;
        if (brightness > 1.0f) --brightness;
        if (6.0f * brightness < 1.0f) return hue + (saturation - hue) * 6.0f * brightness;
        if (2.0f * brightness < 1.0f) return saturation;
        if (3.0f * brightness < 2.0f) return hue + (saturation - hue) * 6.0f * (0.6666667f - brightness);
        return hue;
    }

    public static Color colorRainbowWithDefaultAlpha(final float hue, final float saturation, final float brightness) {
        return getRainbowColor(hue, saturation, brightness, 1.0f);
    }

    public static Color getRainbowColor(float hue, float saturation, float lightness, final float alpha) {
        if (saturation < 0.0f || saturation > 100.0f) throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
        if (lightness < 0.0f || lightness > 100.0f) throw new IllegalArgumentException("Color parameter outside of expected range - Lightness");
        if (alpha < 0.0f || alpha > 1.0f) throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
        hue = (hue %= 360.0f) / 360.0f;
        saturation /= 100.0f;
        lightness /= 100.0f;
        float n5;
        if (lightness < 0.0) n5 = lightness * (1.0f + saturation);
        else n5 = lightness + saturation - saturation * lightness;
        saturation = 2.0f * lightness - n5;
        lightness = Math.max(0.0f, futureClientColorCalculation(saturation, n5, hue + 0.33333334f));
        final float max = Math.max(0.0f, futureClientColorCalculation(saturation, n5, hue));
        saturation = Math.max(0.0f, futureClientColorCalculation(saturation, n5, hue - 0.33333334f));
        lightness = Math.min(lightness, 1.0f);
        final float min = Math.min(max, 1.0f);
        saturation = Math.min(saturation, 1.0f);
        return new Color(lightness, min, saturation, alpha);
    }

    public String toString() {
        return new StringBuilder().insert(0, "HSLColor[h=").append(this.hsb[0]).append(",s=").append(this.hsb[1]).append(",l=").append(this.hsb[2]).append(",alpha=").append(this.alpha).append("]").toString();
    }

    public Color getColorWithLightnessMax(float max) {
        max = (100.0f - max) / 100.0f;
        max = Math.max(0.0f, this.hsb[2] * max);
        return getRainbowColor(this.hsb[0], this.hsb[1], max, this.alpha);
    }

    public Color getColorWithLightnessMin(float min) {
        min = (100.0f + min) / 100.0f;
        min = Math.min(100.0f, this.hsb[2] * min);
        return getRainbowColor(this.hsb[0], this.hsb[1], min, this.alpha);
    }

    public float getAlpha() {
        return alpha;
    }

    public Color getColorWithBrightness(final float brightness) {
        return getRainbowColor(this.hsb[0], this.hsb[1], brightness, this.alpha);
    }

    public float getHue() {
        return hsb[0];
    }

    public float getSaturation() {
        return hsb[1];
    }

    public float getLightness() {
        return this.hsb[2];
    }

    public Color getLocalColor() {
        return this.baseColor;
    }

    public Color getColorWithHue(final float hue) {
        return getRainbowColor(hue, this.hsb[1], this.hsb[2], this.alpha);
    }

    public Color getColorWithSaturation(final float saturation) {
        return getRainbowColor(this.hsb[0], saturation, this.hsb[2], this.alpha);
    }

    public Color getColorWithModifiedHue() {
        return colorRainbowWithDefaultAlpha((this.hsb[0] + 180.0f) % 360.0f, this.hsb[1], this.hsb[2]);
    }
}