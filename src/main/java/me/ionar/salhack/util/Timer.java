package me.ionar.salhack.util;

public final class Timer {
    private long Time;

    public Timer() {
        Time = -1;
    }

    public boolean passed(double ms) {
        return System.currentTimeMillis() - Time >= ms;
    }

    public void reset() {
        Time = System.currentTimeMillis();
    }

    public void resetTimeSkipTo(long ms) {
        Time = System.currentTimeMillis() + ms;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }
}
