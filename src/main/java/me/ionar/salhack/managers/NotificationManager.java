package me.ionar.salhack.managers;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import me.ionar.salhack.util.Timer;
// DO NOT TOUCH THESE THEY MAY BREAK OPENING THE GUI
public class NotificationManager {
    public final List<Notification> notifications = new CopyOnWriteArrayList<>();

    public void addNotification(String p_Title, String p_Description) {
        notifications.add(new Notification(p_Title, p_Description));
    }

    public class Notification {
        public Notification(String p_Title, String p_Description) {
            title = p_Title;
            description = p_Description;
            decayTime = 2500;

            timer.reset();
            decayTimer.reset();
        }

        private String title;
        private String description;
        private Timer timer = new Timer();
        private Timer decayTimer = new Timer();
        private int decayTime;

        private int x;
        private int y;

        public void onRender() {
            if (timer.passed(decayTime -500))
                --y;
        }

        public boolean isDecayed() {
            return decayTimer.passed(decayTime);
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @return the x
         */
        public int getX() {
            return x;
        }

        /**
         * @param x the x to set
         */
        public void setX(int x) {
            this.x = x;
        }

        /**
         * @return the y
         */
        public int getY() {
            return y;
        }

        /**
         * @param y the y to set
         */
        public void setY(int y) {
            this.y = y;
        }
    }
}