package me.ionar.salhack.util;

public final class Timer {
    private long time;

    public Timer() {
        time = -1;
    }

    public boolean passed(double ms) {
        return System.currentTimeMillis() - time >= ms;
    }

    public void reset() {
        time = System.currentTimeMillis();
    }

    public void resetTimeSkipTo(long ms) {
        time = System.currentTimeMillis() + ms;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
