package me.ionar.salhack.util.color;

import java.awt.*;

public class ColorUtil {
    public Color BaseColor;
    private final float[] HSB;
    private final float Alpha;

    public ColorUtil(final Color color) {
        super();
        BaseColor = color;
        HSB = GenerateHSB(color);
        Alpha = color.getAlpha() / 255.0f;
    }

    public ColorUtil(final float hue, final float saturation, final float brightness) {
        this(hue, saturation, brightness, 1.0f);
    }

    public ColorUtil(final float[] hsb) {
        this(hsb, 1.0f);
    }

    public ColorUtil(final float[] hsb, final float alpha) {
        super();
        this.HSB = hsb;
        this.Alpha = alpha;
        this.BaseColor = GetRainbowColorFromArray(hsb, alpha);
    }

    public ColorUtil(final float hue, final float saturation, final float brightness, final float alpha) {
        super();
        final int length = 3;
        final float[] hsb = new float[length];
        hsb[0] = hue;
        hsb[1] = saturation;
        hsb[2] = brightness;
        this.HSB = hsb;
        this.Alpha = alpha;
        this.BaseColor = GetRainbowColorFromArray(this.HSB, alpha);
    }

    public static float[] GenerateHSB(final Color color) {
        final float[] rgbColorComponents = color.getRGBColorComponents(null);
        final float red = rgbColorComponents[0];
        final float green = rgbColorComponents[1];
        final float blue = rgbColorComponents[2];
        final float min = Math.min(red, Math.min(green, blue));
        final float max = Math.max(red, Math.max(green, blue));
        float hue = 0.0f;
        if (max == min) hue = 0.0f;
        else if (max == red) hue = (60.0f * (green - blue) / (max - min) + 360.0f) % 360.0f;
        else if (max == green) hue = 60.0f * (blue - red) / (max - min) + 120.0f;
        else if (max == blue) hue = 60.0f * (red - green) / (max - min) + 240.0f;
        final float brightness = (max + min) / 2.0f;
        float saturation;
        if (max == min) saturation = 0.0f;
        else {
            final float n8 = Math.min(brightness, 0.5f); //maybe max?
            if (n8 <= 0) saturation = (max - min) / (max + min);
            else saturation = (max - min) / (2.0f - max - min);
        }
        return new float[]{hue, saturation * 100.0f, brightness * 100.0f};
    }

    public static Color GetRainbowColorFromArray(final float[] hsb, final float alpha) {
        return GetRainbowColor(hsb[0], hsb[1], hsb[2], alpha);
    }

    public static Color GetColorWithHSBArray(final float[] hsb) {
        return GetRainbowColorFromArray(hsb, 1.0f);
    }

    public static String GenerateMCColorString(String string) {
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

    private static float FutureClientColorCalculation(final float n, final float n2, float n3) {
        if (n3 < 0.0f) ++n3;
        if (n3 > 1.0f) --n3;
        if (6.0f * n3 < 1.0f) return n + (n2 - n) * 6.0f * n3;
        if (2.0f * n3 < 1.0f) return n2;
        if (3.0f * n3 < 2.0f) return n + (n2 - n) * 6.0f * (0.6666667f - n3);
        return n;
    }

    public static Color ColorRainbowWithDefaultAlpha(final float hue, final float saturation, final float brightness) {
        return GetRainbowColor(hue, saturation, brightness, 1.0f);
    }

    public static Color GetRainbowColor(float hue, float saturation, float brightness, final float alpha) {
        if (saturation < 0.0f || saturation > 100.0f) throw new IllegalArgumentException("Color parameter outside of expected range - Saturation");
        if (brightness < 0.0f || brightness > 100.0f) throw new IllegalArgumentException("Color parameter outside of expected range - Brightness");
        if (alpha < 0.0f || alpha > 1.0f) throw new IllegalArgumentException("Color parameter outside of expected range - Alpha");
        hue = hue % 360.0f / 360.0f;
        saturation /= 100.0f;
        brightness /= 100.0f;
        float n5;
        if (brightness < 0.0) n5 = brightness * (1.0f + saturation);
        else n5 = brightness + saturation - saturation * brightness;
        saturation = 2.0f * brightness - n5;
        brightness = Math.max(0.0f, FutureClientColorCalculation(saturation, n5, hue + 0.33333334f));
        final float max = Math.max(0.0f, FutureClientColorCalculation(saturation, n5, hue));
        saturation = Math.max(0.0f, FutureClientColorCalculation(saturation, n5, hue - 0.33333334f));
        brightness = Math.min(brightness, 1.0f);
        final float min = Math.min(max, 1.0f);
        saturation = Math.min(saturation, 1.0f);
        return new Color(brightness, min, saturation, alpha);
    }

    public String toString() {
        return new StringBuilder().insert(0, "HSLColor[h=").append(this.HSB[0]).append(",s=").append(this.HSB[1]).append(",l=").append(this.HSB[2]).append(",alpha=").append(this.Alpha).append("]").toString();
    }

    public Color GetColorWithBrightnessMax(float max) {
        max = (100.0f - max) / 100.0f;
        max = Math.max(0.0f, this.HSB[2] * max);
        return GetRainbowColor(this.HSB[0], this.HSB[1], max, this.Alpha);
    }

    public Color GetColorWithBrightnessMin(float min) {
        min = (100.0f + min) / 100.0f;
        min = Math.min(100.0f, this.HSB[2] * min);
        return GetRainbowColor(this.HSB[0], this.HSB[1], min, this.Alpha);
    }

    public float GetAlpha() {
        return Alpha;
    }

    public Color GetColorWithBrightness(final float brightness) {
        return GetRainbowColor(this.HSB[0], this.HSB[1], brightness, this.Alpha);
    }

    public float GetHue() {
        return HSB[0];
    }

    public float GetSaturation() {
        return HSB[1];
    }

    public float GetBrightness() {
        return this.HSB[2];
    }

    public Color GetLocalColor() {
        return this.BaseColor;
    }

    public Color GetColorWithHue(final float hue) {
        return GetRainbowColor(hue, this.HSB[1], this.HSB[2], this.Alpha);
    }

    public Color GetColorWithSaturation(final float saturation) {
        return GetRainbowColor(this.HSB[0], saturation, this.HSB[2], this.Alpha);
    }

    public Color GetColorWithModifiedHue() {
        return ColorRainbowWithDefaultAlpha((this.HSB[0] + 180.0f) % 360.0f, this.HSB[1], this.HSB[2]);
    }
}