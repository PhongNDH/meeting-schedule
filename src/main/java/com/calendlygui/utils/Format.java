package com.calendlygui.utils;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

public class Format {
    public static String GetTimeFromTimestamp(Timestamp timestamp) {
        LocalDateTime localDate = timestamp.toLocalDateTime();
        return localDate.getHour() + ":" + localDate.getMinute();
    }

    public static String GetDateFromTimestamp(Timestamp timestamp) {
        LocalDateTime localDate = timestamp.toLocalDateTime();
        return localDate.getDayOfMonth() + "/" + localDate.getMonthValue() + "/" + localDate.getYear();
    }

    public static Timestamp CreateTimestamp(int year, int month, int day, int hour, int minute) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute);
        return Timestamp.valueOf(dateTime);
    }

    public static void main(String[] args) {

        LocalDateTime localDateTime = LocalDateTime.now(); // Replace this with your LocalDateTime
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
    }
}
