package me.ionar.salhack.managers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.ionar.salhack.main.SalHack;
import me.ionar.salhack.util.Timer;

public class NotificationManager {
    public final List<Notification> Notifications = new CopyOnWriteArrayList<>();

    public void AddNotification(String title, String description) {
        Notifications.add(new Notification(title, description));
    }

    public static NotificationManager Get() {
        return SalHack.GetNotificationManager();
    }

    public static class Notification {
        public Notification(String title, String description) {
            Title = title;
            Description = description;
            DecayTime = 2500;
            Timer.reset();
            DecayTimer.reset();
        }

        private final String Title;
        private final String Description;
        private final Timer Timer = new Timer();
        private final Timer DecayTimer = new Timer();
        private final int DecayTime;

        private int X;
        private int Y;

        public void OnRender() {
            if (Timer.passed(DecayTime-500)) --Y;
        }

        public boolean IsDecayed() {
            return DecayTimer.passed(DecayTime);
        }

        /**
         * @return the description
         */
        public String GetDescription() {
            return Description;
        }

        /**
         * @return the title
         */
        public String GetTitle() {
            return Title;
        }

        /**
         * @return the x
         */
        public int GetX() {
            return X;
        }

        /**
         * @param x the x to set
         */
        public void SetX(int x) {
            X = x;
        }

        /**
         * @return the y
         */
        public int GetY() {
            return Y;
        }

        /**
         * @param y the y to set
         */
        public void SetY(int y) {
            Y = y;
        }
    }
}