package com.calendlygui.utils;

import com.calendlygui.model.Meeting;
import com.calendlygui.model.Minute;
import com.calendlygui.model.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.ConstantValue.ESTABLISH_DATETIME;
import static org.controlsfx.glyphfont.FontAwesome.Glyph.USERS;

public class Helper {
    static String timePattern = "yyyy-MM-dd HH:mm:ss";
    static String datePattern = "EEE MMM dd HH:mm:ss zzz yyyy";
    static SimpleDateFormat formatter = new SimpleDateFormat(timePattern);
    static SimpleDateFormat dateFormatter = new SimpleDateFormat(datePattern);

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
        for (String _data : data) request.append(COMMAND_DELIMITER).append(_data);

        return request.toString();
    }

    public static String createResponse(int code, String message) {
        return code + COMMAND_DELIMITER + message;
    }

    public static String createResponseWithUser(int code, int id, String name, String email, String time, String isTeacher, String gender){
        return code + COMMAND_DELIMITER + id + COMMAND_DELIMITER + name + COMMAND_DELIMITER + email + COMMAND_DELIMITER + time + COMMAND_DELIMITER + isTeacher + COMMAND_DELIMITER + gender;
    }

    public static String createResponseWithMeetingList(int code, ArrayList<Meeting> meetings) {
        StringBuilder data = new StringBuilder();
        for (Meeting meeting : meetings) {
            data.append(COMMAND_DELIMITER)
                    .append(meeting.id).append(DOUBLE_LINE_BREAK)
                    .append(meeting.name).append(DOUBLE_LINE_BREAK)
                    .append(meeting.date).append(DOUBLE_LINE_BREAK)
                    .append(meeting.occur).append(DOUBLE_LINE_BREAK)
                    .append(meeting.finish).append(DOUBLE_LINE_BREAK)
                    .append(meeting.tId).append(DOUBLE_LINE_BREAK)
                    .append(meeting.classification).append(DOUBLE_LINE_BREAK)
                    .append(meeting.status).append(DOUBLE_LINE_BREAK)
                    .append(meeting.selectedClassification).append(DOUBLE_LINE_BREAK);

            for(Minute minute: meeting.minutes){
                data.append(minute.time).append(FIELD_DELIMITER).append(minute.content).append(LINE_BREAK);
            }

            if(!meeting.minutes.isEmpty()) data.delete(data.length() - LINE_BREAK.length(), data.length());
            data.append(DOUBLE_LINE_BREAK);

            for(User student: meeting.students){
                data
                        .append(student.getUsername()).append(FIELD_DELIMITER)
                        .append(student.getEmail()).append(FIELD_DELIMITER)
                        .append(student.getRegisterDatetime()).append(FIELD_DELIMITER)
                        .append(student.isTeacher()).append(FIELD_DELIMITER)
                        .append(student.getGender()).append(LINE_BREAK);
            }

            if(!meeting.students.isEmpty()) data.delete(data.length() - LINE_BREAK.length(), data.length());
        }
        return String.valueOf(code) + data;
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

    public static ArrayList<Meeting> extractMeetingsFromResponse(String response) throws ParseException {
        String[] data = response.split(COMMAND_DELIMITER);
        ArrayList<Meeting> meetings = new ArrayList<>();
        for (int i = 1; i < data.length; i++) {
            String[] meetingInfo = data[i].split(DOUBLE_LINE_BREAK);
            Meeting newMeeting = new Meeting(
                    Integer.parseInt(meetingInfo[0]),
                    meetingInfo[1], meetingInfo[2],
                    meetingInfo[3], meetingInfo[4],
                    Integer.parseInt(meetingInfo[5]),
                    meetingInfo[6], meetingInfo[7],
                    meetingInfo[8]);

//            List of minutes: meetingInfo[9]
            if(meetingInfo.length >= 10 && !Objects.equals(meetingInfo[9], "")){
                ArrayList<Minute> minutes = new ArrayList<>();
                String minuteField = meetingInfo[9];
                String[] minuteStrings = minuteField.split(LINE_BREAK);
                for(String minuteString: minuteStrings){
                    String[] minuteData = minuteString.split(FIELD_DELIMITER);
                    minutes.add(new Minute(dateFormatter.parse(minuteData[0]), minuteData[1]));
                }

                newMeeting.minutes = minutes;
            }

//            List of minutes: meetingInfo[10]
            if(meetingInfo.length == 11 && !Objects.equals(meetingInfo[10], "")){
                ArrayList<User> students = new ArrayList<>();
                String studentField = meetingInfo[10];
                String[] studentStrings = studentField.split(LINE_BREAK);
                for(String studentString: studentStrings){
                    String[] studentData = studentString.split(FIELD_DELIMITER);
                    students.add(new User(
                            Integer.parseInt(studentData[0]),
                            studentData[2],
                            studentData[1],
                            new Timestamp(formatter.parse(studentData[3]).getTime()),
                            Objects.equals(studentData[4], "true"),
                            Objects.equals(studentData[5], "true")
                            ));
                }

                newMeeting.students = students;
            }

            meetings.add(newMeeting);
        }
        return meetings;
    }

    public static User extractUserFromResponse(String response) throws ParseException {
        String[] data = response.split(COMMAND_DELIMITER);
        if(data.length == 7){
            return new User(
                    Integer.parseInt(data[1]),
                    data[2],
                    data[3],
                    new Timestamp(formatter.parse(data[4]).getTime()),
                    Objects.equals(data[5], "true"),
                    Objects.equals(data[6], "true")
            );
        }

        return null;
    }

    public static ArrayList<Minute> getMinutes(Connection conn, int meetingId) throws SQLException {
        ArrayList<Minute> minutes = new ArrayList<>();

        String minuteQuery;
        minuteQuery = "select * from " + MINUTE + " where " + MEETING_ID + " = ?";

        PreparedStatement ps = conn.prepareStatement(minuteQuery);
        ps.setInt(1, meetingId);

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        String content;
        Timestamp time;
        while (rs.next()) {
            content = rs.getString(CONTENT);
            time = rs.getTimestamp(ESTABLISH_DATETIME);

            Minute newMinute = new Minute(new Date(time.getTime()), content);
            minutes.add(newMinute);
        }

        System.out.println("Minutes: " + minutes.size());

        return minutes;
    }

    public static ArrayList<User> getStudentsInPastMeetings(Connection conn, int meetingId) throws SQLException {
        Set<Integer> studentIDs = new HashSet<>();

        String participateQuery = "select * from " + PARTICIPATE + " where " + MEETING_ID + " = ?";
        PreparedStatement ps = conn.prepareStatement(participateQuery);
        ps.setInt(1, meetingId);

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        while (rs.next()){
            studentIDs.add(rs.getInt(STUDENT_ID));
        }

        System.out.println("Students: " + studentIDs.size());

        if(!studentIDs.isEmpty()) return getUsers(conn, new ArrayList<>(studentIDs));
        else return new ArrayList<>();
    }

    public static ArrayList<User> getUsers(Connection conn, ArrayList<Integer> studentIDs) throws SQLException {
        ArrayList<User> users = new ArrayList<>();

        for(int id: studentIDs) System.out.println(id);

        String userQuery = "select * from " + USERS;
        for(int i = 1; i <= studentIDs.size(); i++) {
            if(i == 1) userQuery += " where " + ID + " = ?";
            else userQuery += " or " + ID + " = ?";
        }

        System.out.println(userQuery);

        PreparedStatement ps = conn.prepareStatement(userQuery);
        for(int i = 1; i <= studentIDs.size(); i++){
            ps.setInt(i, studentIDs.get(i-1));
        }

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        int id;
        String name, email;
        Timestamp registerDatetime;
        boolean gender, isTeacher;
        while (rs.next()) {
            id = rs.getInt(ID);
            name = rs.getString(NAME);
            email = rs.getString(EMAIL);
            gender = rs.getBoolean(GENDER);
            isTeacher = rs.getBoolean(IS_TEACHER);
            registerDatetime = rs.getTimestamp(REGISTER_DATETIME);

            User tmpUser = new User(id, name, email, registerDatetime, isTeacher, gender);
            users.add(tmpUser);
        }

        System.out.println("Users: " + users.size());

        return users;
    }
}
