package me.ionar.salhack.util.color;

import me.ionar.salhack.util.Timer;

import java.util.ArrayList;

public class SalRainbowUtil {
    private final ArrayList<Integer> currentRainbowIndexes = new ArrayList<>();
    private final ArrayList<Integer> rainbowArrayList = new ArrayList<>();
    private final Timer rainbowSpeed = new Timer();
    private int timer;
    private int i = 0;
    public SalRainbowUtil(int timer) {
        this.timer = timer;
        for (int i = 0; i < 360; i++) {
            rainbowArrayList.add(ColorUtil.getRainbowColor(i, 90.0f, 50.0f, 1.0f).getRGB());
            currentRainbowIndexes.add(i);
        }
    }

    public int getRainbowColorAt(int index) {
        if (index > currentRainbowIndexes.size() - 1) index = currentRainbowIndexes.size() - 1;
        return rainbowArrayList.get(currentRainbowIndexes.get(index));
    }

    public void setTimer(int newTimer) {
        timer = newTimer;
    }

    public void onRender() {
        if (rainbowSpeed.passed(timer)) {
            rainbowSpeed.reset();
            moveListToNextColor();
        }
    }

    private void moveListToNextColor() {
        if (currentRainbowIndexes.isEmpty()) return;
        currentRainbowIndexes.remove(currentRainbowIndexes.get(0));
        int index = currentRainbowIndexes.get(currentRainbowIndexes.size() - 1) + 1;
        if (index >= rainbowArrayList.size() - 1) index = 0;
        currentRainbowIndexes.add(index);
    }

    public int getRainbowColorNumber() {
        i += 1;
        if (i >= 355) i = 0;
        return i;
    }
}