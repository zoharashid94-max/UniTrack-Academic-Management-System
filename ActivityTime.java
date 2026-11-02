package com.unitrack.activity;

import java.time.Duration;
import java.time.LocalDateTime;

public class ActivityTime {

    private ActivityTime() {
    }

    public static String format(LocalDateTime time) {

        if (time == null) {
            return "Time unavailable";
        }

        long seconds = Duration.between(
                time, LocalDateTime.now()
        ).getSeconds();

        if (seconds < 60) {
            return "Just now";
        }

        long minutes = seconds / 60;

        if (minutes < 60) {
            return minutes + (minutes == 1
                    ? " minute ago" : " minutes ago");
        }

        long hours = minutes / 60;

        if (hours < 24) {
            return hours + (hours == 1
                    ? " hour ago" : " hours ago");
        }

        long days = hours / 24;

        return days + (days == 1 ? " day ago" : " days ago");
    }
}
