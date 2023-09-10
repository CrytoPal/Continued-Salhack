package me.ionar.salhack.util.color;

import java.awt.*;

public class ColorUtil {
    public Color baseColor;
    private final float[] hsb;
    private final float alpha;

    public ColorUtil(final Color color) {
        super();
        baseColor = color;
        hsb = generateHSB(color);
        alpha = color.getAlpha() / 255.0f;
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
        final int length = 3;
        final float[] hsb = new float[length];
        hsb[0] = hue;
        hsb[1] = saturation;
        hsb[2] = brightness;
        this.hsb = hsb;
        this.alpha = alpha;
        this.baseColor = getRainbowColorFromArray(this.hsb, alpha);
    }

    public static float[] generateHSB(final Color color) {
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

    public static Color getRainbowColorFromArray(final float[] hsb, final float alpha) {
        return getRainbowColor(hsb[0], hsb[1], hsb[2], alpha);
    }

    public static Color getColorWithHSBArray(final float[] hsb) {
        return getRainbowColorFromArray(hsb, 1.0f);
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

    private static float futureClientColorCalculation(final float n, final float n2, float n3) {
        if (n3 < 0.0f) ++n3;
        if (n3 > 1.0f) --n3;
        if (6.0f * n3 < 1.0f) return n + (n2 - n) * 6.0f * n3;
        if (2.0f * n3 < 1.0f) return n2;
        if (3.0f * n3 < 2.0f) return n + (n2 - n) * 6.0f * (0.6666667f - n3);
        return n;
    }

    public static Color colorRainbowWithDefaultAlpha(final float hue, final float saturation, final float brightness) {
        return getRainbowColor(hue, saturation, brightness, 1.0f);
    }

    public static Color getRainbowColor(float hue, float saturation, float brightness, final float alpha) {
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
        brightness = Math.max(0.0f, futureClientColorCalculation(saturation, n5, hue + 0.33333334f));
        final float max = Math.max(0.0f, futureClientColorCalculation(saturation, n5, hue));
        saturation = Math.max(0.0f, futureClientColorCalculation(saturation, n5, hue - 0.33333334f));
        brightness = Math.min(brightness, 1.0f);
        final float min = Math.min(max, 1.0f);
        saturation = Math.min(saturation, 1.0f);
        return new Color(brightness, min, saturation, alpha);
    }

    public String toString() {
        return new StringBuilder().insert(0, "HSLColor[h=").append(this.hsb[0]).append(",s=").append(this.hsb[1]).append(",l=").append(this.hsb[2]).append(",alpha=").append(this.alpha).append("]").toString();
    }

    public Color getColorWithBrightnessMax(float max) {
        max = (100.0f - max) / 100.0f;
        max = Math.max(0.0f, this.hsb[2] * max);
        return getRainbowColor(this.hsb[0], this.hsb[1], max, this.alpha);
    }

    public Color getColorWithBrightnessMin(float min) {
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

    public float getBrightness() {
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