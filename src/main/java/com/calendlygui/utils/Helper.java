package com.calendlygui.utils;

import com.calendlygui.model.Meeting;
import com.calendlygui.model.Minute;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.ConstantValue.ESTABLISH_DATETIME;

public class Helper {
    static String pattern = "yyyy-MM-dd HH:mm:ss";
    static SimpleDateFormat formatter = new SimpleDateFormat(pattern);

    public static Date[] convertToDate(String date, String startTime, String endTime) throws ParseException {

        // Parse the input strings to LocalDateTime
        Date startDateTime = formatter.parse(date + " " + startTime + ":00");
        Date endDateTime = formatter.parse(date + " " + endTime + ":00");

        return new Date[]{startDateTime, endDateTime};
    }

    public static String convertFromDateToString(Timestamp timestamp) {
        String res = "";
        res += timestamp.toLocalDateTime().getHour() + ":" + timestamp.toLocalDateTime().getMinute();
        return res;
    }

    public static String createRequest(String command, ArrayList<String> data) {
        StringBuilder request = new StringBuilder();
        request.append("/").append(command);
        for (String _data : data) request.append(DELIMITER).append(_data);

        return request.toString();
    }

    public static String createResponse(String status, String type, ArrayList<String> message) {
        StringBuilder data = new StringBuilder();
        for (int i = 0; i < message.size(); i++) {
            if (i == 0) data.append(message.get(i));
            else data.append(DELIMITER).append(message.get(i));
        }
        return "/" + status + DELIMITER + type + DELIMITER + data;
    }

    public static String createResponseWithMeetingList(String status, String type, ArrayList<Meeting> meetings) {
        //minutes are not required
        StringBuilder data = new StringBuilder();
        for (Meeting meeting : meetings) {
            data.append(LINE_BREAK)
                    .append(meeting.id).append(DELIMITER)
                    .append(meeting.name).append(DELIMITER)
                    .append(meeting.date).append(DELIMITER)
                    .append(meeting.occur).append(DELIMITER)
                    .append(meeting.finish).append(DELIMITER)
                    .append(meeting.tId).append(DELIMITER)
                    .append(meeting.classification).append(DELIMITER)
                    .append(meeting.status).append(DELIMITER)
                    .append(meeting.selectedClassification).append(DELIMITER);
        }
        String res = "/" + status + LINE_BREAK + type + data;
        return res;
    }

    public static ArrayList<Meeting> getMeetings(ResultSet rs) throws SQLException {
        ArrayList<Meeting> meetings = new ArrayList<>();

        int id, teacherId;
        String name, status, classification, selectedClassification, occurString, finishString;
        Timestamp occur, finish;

        while (rs.next()) {
            id = rs.getInt(ID);
            teacherId = rs.getInt(MEETING_TEACHER_ID);
            name = rs.getString(NAME);
            occur = rs.getTimestamp(MEETING_OCCUR);
            finish = rs.getTimestamp(MEETING_FINISH);
            status = rs.getString(STATUS);
            classification = rs.getString(CLASSIFICATION);
            selectedClassification = rs.getString(SELECTED_CLASSIFICATION);

            String date = formatter.format(occur.getTime());

            occurString = convertFromDateToString(occur);
            finishString = convertFromDateToString(finish);
            Meeting newMeeting = new Meeting(id, name, date, occurString, finishString, teacherId, classification, status, selectedClassification);

            meetings.add(newMeeting);
        }

        return meetings;
    }

    public static ArrayList<Meeting> extractMeetingsFromResponse(String response) {
        String[] data = response.split(LINE_BREAK);
        ArrayList<Meeting> meetings = new ArrayList<>();
        for (int i = 2; i < data.length; i++) {
            String[] meetingInfo = data[i].split(DELIMITER);
            meetings.add(new Meeting(
                    Integer.parseInt(meetingInfo[0]),
                    meetingInfo[1], meetingInfo[2],
                    meetingInfo[3], meetingInfo[4],
                    Integer.parseInt(meetingInfo[5]),
                    meetingInfo[6], meetingInfo[7],
                    meetingInfo[8]));
        }
        return meetings;
    }

    public static ArrayList<Minute> getMinutes(Connection conn, int meetingId) throws SQLException {
        ArrayList<Minute> minutes = new ArrayList<>();

        String minuteQuery;
        minuteQuery = "select * from " + MINUTE + " where " + MEETING_ID + " = ?";

        PreparedStatement ps = conn.prepareStatement(minuteQuery);
        ps.setInt(1, meetingId);
        ResultSet rs = ps.executeQuery();

        String content;
        Timestamp time;
        while (rs.next()) {
            content = rs.getString(CONTENT);
            time = rs.getTimestamp(ESTABLISH_DATETIME);

            Minute newMinute = new Minute(new Date(time.getTime()), content);
            minutes.add(newMinute);
        }

        return minutes;
    }
}
