package com.calendlygui.utils;

import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

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

    public static void changeDatePickerFormat(DatePicker date){
        //dd/MM/yyyy
        date.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate != null) {
                    return dateFormatter.format(localDate);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String s) {
                if (s != null && !s.isEmpty()) {
                    return LocalDate.parse(s, dateFormatter);
                } else {
                    return null;
                }
            }
            final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        });
    }

    public static void main(String[] args) {
    }
}
