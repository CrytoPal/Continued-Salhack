package me.ionar.salhack.font;

import net.minecraft.client.util.math.MatrixStack;

import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;

public class RendererFontAdapter implements FontAdapter {
    final FontRenderer fontRenderer;
    final float size;

    public RendererFontAdapter(Font font, float size) {
        this.fontRenderer = new FontRenderer(font, (int) size);
        this.size = size;
    }

    public float getSize() {
        return size;
    }

    public FontRenderer getFontRenderer() {
        return fontRenderer;
    }

    @Override
    public void drawString(MatrixStack matrices, String text, float x, float y, int color) {
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) color1 |= 0xff000000;
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        drawString(matrices, text, x, y, r, g, b, a);
    }

    @Override
    public void drawString(MatrixStack matrices, String text, double x, double y, int color) {
        drawString(matrices, text, (float) x, (float) y, color);
    }

    public void drawString(MatrixStack stack, ColoredTextSegment coloredTextSegment, float x, float y) {
        float newX = x;
        ArrayList<ColoredTextSegment> coloredTextSegments = new ArrayList<>();
        coloredTextSegments.add(coloredTextSegment);
        while (!coloredTextSegments.isEmpty()) {
            ColoredTextSegment poll = coloredTextSegments.get(0);
            coloredTextSegments.remove(0);
            coloredTextSegments.addAll(0, Arrays.asList(poll.children()));
            String text = poll.text();
            if (text.isEmpty()) continue;
            drawString(stack, text, newX, y, poll.r(), poll.g(), poll.b(), poll.a());
            newX += getStringWidth(text);
        }
    }

    @Override
    public void drawString(MatrixStack matrices, String text, float x, float y, float r, float g, float b, float a) {
        float alpha = AlphaOverride.compute((int) (a * 255)) / 255;
        fontRenderer.drawString(matrices, text, x, y, r, g, b, alpha);
    }

    @Override
    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, int color) {
        int color1 = color;
        if ((color1 & 0xfc000000) == 0) color1 |= 0xff000000;
        float a = (float) (color1 >> 24 & 255) / 255.0F;
        float r = (float) (color1 >> 16 & 255) / 255.0F;
        float g = (float) (color1 >> 8 & 255) / 255.0F;
        float b = (float) (color1 & 255) / 255.0F;
        drawCenteredString(matrices, text, x, y, r, g, b, a);
    }

    @Override
    public void drawCenteredString(MatrixStack matrices, String text, double x, double y, float r, float g, float b, float a) {
        float alpha = AlphaOverride.compute((int) (a * 255)) / 255;
        fontRenderer.drawCenteredString(matrices, text, (float) x, (float) y, r, g, b, alpha);
    }

    @Override
    public float getStringWidth(String text) {
        return fontRenderer.getStringWidth(text);
    }

    @Override
    public float getFontHeight() {
        //return fontRenderer.getStringHeight("abcdefg123"); // we just need to trust it here
        return 1;
    }

    @Override
    public float getFontHeight(String text) {
        return getFontHeight();
    }

    @Override
    public float getMarginHeight() {
        return getFontHeight();
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, int color, boolean dropShadow) {
        drawString(matrices, s, x, y, color);
    }

    @Override
    public void drawString(MatrixStack matrices, String s, float x, float y, float r, float g, float b, float a, boolean dropShadow) {
        drawString(matrices, s, x, y, r, g, b, a);
    }

    @Override
    public String trimStringToWidth(String in, double width) {
        StringBuilder sb = new StringBuilder();
        for (char c : in.toCharArray()) {
            if (getStringWidth(sb.toString() + c) >= width) return sb.toString();
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public String trimStringToWidth(String in, double width, boolean reverse) {
        return trimStringToWidth(in, width);
    }
}
