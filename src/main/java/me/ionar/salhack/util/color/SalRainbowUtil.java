package me.ionar.salhack.util.color;

import me.ionar.salhack.util.Timer;

import java.util.ArrayList;

/// Object for rainbow handling
public class SalRainbowUtil {
    private final ArrayList<Integer> CurrentRainbowIndexes = new ArrayList<>();
    private final ArrayList<Integer> RainbowArrayList = new ArrayList<>();
    private final Timer RainbowSpeed = new Timer();
    private int Timer;
    private int i = 0;
    public SalRainbowUtil(int timer) {
        Timer = timer;

        /// Populate the RainbowArrayList
        for (int i = 0; i < 360; i++) {
            RainbowArrayList.add(ColorUtil.GetRainbowColor(i, 90.0f, 50.0f, 1.0f).getRGB());
            CurrentRainbowIndexes.add(i);
        }
    }

    public int GetRainbowColorAt(int index) {
        if (index > CurrentRainbowIndexes.size() - 1) index = CurrentRainbowIndexes.size() - 1;
        return RainbowArrayList.get(CurrentRainbowIndexes.get(index));
    }

    public void SetTimer(int newTimer) {
        Timer = newTimer;
    }

    /// Call this function in your render/update function.
    public void OnRender() {
        if (RainbowSpeed.passed(Timer)) {
            RainbowSpeed.reset();
            MoveListToNextColor();
        }
    }

    private void MoveListToNextColor() {
        if (CurrentRainbowIndexes.isEmpty()) return;

        CurrentRainbowIndexes.remove(CurrentRainbowIndexes.get(0));

        int index = CurrentRainbowIndexes.get(CurrentRainbowIndexes.size() - 1) + 1;

        if (index >= RainbowArrayList.size() - 1) index = 0;

        CurrentRainbowIndexes.add(index);
    }

    public int getRainbowColorNumber() {
        i += 1;
        if (i >= 355) {
            i = 0;
        }
        return i;
    }

}