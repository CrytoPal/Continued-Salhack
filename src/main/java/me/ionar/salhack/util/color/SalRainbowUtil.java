package me.ionar.salhack.util.color;

import me.ionar.salhack.util.Timer;

import java.util.ArrayList;

/// Object for rainbow handling
public class SalRainbowUtil {
    private final ArrayList<Integer> currentRainbowIndexes = new ArrayList<>();
    private final ArrayList<Integer> rainbow = new ArrayList<>();
    private final Timer rainbowSpeed = new Timer();
    private int timer;
    private int index = 0;
    public SalRainbowUtil(int timer) {
        this.timer = timer;
        /// Populate the RainbowArrayList
        for (int i = 0; i < 360; i++) {
            rainbow.add(ColorUtil.getRainbowColor(i, 90.0f, 50.0f, 1.0f).getRGB());
            currentRainbowIndexes.add(i);
        }
    }

    public int getRainbowColorAt(int index) {
        if (index > currentRainbowIndexes.size() - 1) index = currentRainbowIndexes.size() - 1;
        return rainbow.get(currentRainbowIndexes.get(index));
    }

    public void setTimer(int newTimer) {
        timer = newTimer;
    }

    /// Call this function in your render/update function.
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
        if (index >= rainbow.size() - 1) index = 0;
        currentRainbowIndexes.add(index);
    }

    public int getRainbowColorNumber() {
        index += 1;
        if (index >= 355) index = 0;
        return index;
    }
}