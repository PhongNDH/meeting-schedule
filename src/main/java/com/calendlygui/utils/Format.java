package com.calendlygui.utils;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Format {
    public static String getTimeFromTimestamp(Timestamp timestamp) {
        LocalDateTime localDate = timestamp.toLocalDateTime();
        return localDate.getHour() + ":" + localDate.getMinute();
    }

    public static String getDateFromTimestamp(Timestamp timestamp) {
        LocalDateTime localDate = timestamp.toLocalDateTime();
        return localDate.getDayOfMonth() + "/" + localDate.getMonthValue() + "/" + localDate.getYear();
    }

    public static Timestamp createTimestamp(int year, int month, int day, int hour, int minute) {
        LocalDateTime dateTime = LocalDateTime.of(year, month, day, hour, minute);
        return Timestamp.valueOf(dateTime);
    }

    public static List<LocalDate> getWeekFromDate(LocalDate date) {
        List<LocalDate> list = new ArrayList<LocalDate>();
        int dayOfWeek = date.getDayOfWeek().getValue();
        for(int i = -dayOfWeek + 1; i <= - dayOfWeek + 7; i++) {
            list.add(date.plusDays(i));
        }
        return list;
    }

    public static LocalDate getLocalDateFromTimestamp(Timestamp timestamp) {
        Instant instant = timestamp.toInstant();
        return instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
    }

    public static String getStringValueOfLocalDate(LocalDate date) {
        return date.getDayOfMonth()+"/"+date.getMonthValue()+"/"+date.getYear();
    }

    public static String getStringFormatFromLocalDate(LocalDate localDate){
        // yyy-MM-dd
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return localDate.format(formatter);
    }

    public static String getStringFormatFromTimestamp(Timestamp timestamp, String format){
        // yyyy-MM-dd HH:mm:ss || HH:mm
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = timestamp.toLocalDateTime();
        return localDateTime.format(formatter);
    }

    public static int getNumberOfDateFromNow(LocalDate localDate){
        return (int) ChronoUnit.DAYS.between(LocalDate.now(), localDate);
    }

    public static boolean CheckFormat(String string, String regex){
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }



    public static int getMinutesBetweenTwoTime(String startTime, String endTime){
        String[] startParts = startTime.split(":");
        int startHour = Integer.parseInt(startParts[0]);
        int startMinute = Integer.parseInt(startParts[1]);

        String[] endParts = endTime.split(":");
        int endHour = Integer.parseInt(endParts[0]);
        int endMinute = Integer.parseInt(endParts[1]);
        // Calculate the difference in minutes
        int totalStartMinutes = startHour * 60 + startMinute;
        int totalEndMinutes = endHour * 60 + endMinute;
        return totalEndMinutes - totalStartMinutes;
    }

    public static String writeFirstCharacterInUppercase(String string){
        String firstCharacter = string.substring(0,1);
        String remainString = string.substring(1);
        return firstCharacter.toUpperCase() + remainString;
    }
}
