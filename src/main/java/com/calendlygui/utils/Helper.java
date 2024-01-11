package com.calendlygui.utils;

import com.calendlygui.model.entity.Content;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.model.entity.User;

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
//    static String datePattern = "EEE MMM dd HH:mm:ss zzz yyyy";
    static SimpleDateFormat formatter = new SimpleDateFormat(timePattern);

    public static Timestamp toTimeStamp(String date, String time) throws ParseException {
        return new Timestamp(formatter.parse(date + " " + time).getTime());
    }

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
                    .append(meeting.getId()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getName()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getEstablishedDatetime()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getOccurDatetime()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getFinishDatetime()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getTeacherId()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getTeacherName()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getClassification()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getStatus()).append(DOUBLE_LINE_BREAK)
                    .append(meeting.getSelectedClassification()).append(DOUBLE_LINE_BREAK);

            for(Content content: meeting.getContents()){
                data.append(content.getDate()).append(FIELD_DELIMITER).append(content.getContent()).append(LINE_BREAK);
            }

            if(!meeting.getContents().isEmpty()) data.delete(data.length() - LINE_BREAK.length(), data.length());
            data.append(DOUBLE_LINE_BREAK);

            for(User student: meeting.getStudents()){
                data
                        .append(student.getId()).append(FIELD_DELIMITER)
                        .append(student.getUsername()).append(FIELD_DELIMITER)
                        .append(student.getEmail()).append(FIELD_DELIMITER)
                        .append(student.getRegisterDatetime()).append(FIELD_DELIMITER)
                        .append(student.isTeacher()).append(FIELD_DELIMITER)
                        .append(student.getGender()).append(LINE_BREAK);
            }

            if(!meeting.getStudents().isEmpty()) data.delete(data.length() - LINE_BREAK.length(), data.length());
        }
        return String.valueOf(code) + data;
    }

    public static ArrayList<Meeting> getMeetings(ResultSet rs) throws SQLException {
        ArrayList<Meeting> meetings = new ArrayList<>();

        int id, teacherId;
        String name, status, classification, selectedClassification;
        String teacher_name;
        Timestamp occur, finish, established;

        while (rs.next()) {
            id = rs.getInt(ID);
            teacherId = rs.getInt(MEETING_TEACHER_ID);
            teacher_name = rs.getString(TEACHER_NAME);
            name = rs.getString(NAME);
            occur = rs.getTimestamp(MEETING_OCCUR);
            finish = rs.getTimestamp(MEETING_FINISH);
            established = rs.getTimestamp(ESTABLISH_DATETIME);
            status = rs.getString(STATUS);
            classification = rs.getString(CLASSIFICATION);
            selectedClassification = rs.getString(SELECTED_CLASSIFICATION);


            Meeting newMeeting = new Meeting(id, teacherId, teacher_name,  name, established, occur, finish, classification, selectedClassification, status);
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
                    Integer.parseInt(meetingInfo[5]),
                    meetingInfo[6],
                    meetingInfo[1],
                    new Timestamp(formatter.parse(meetingInfo[2]).getTime()),
                    new Timestamp(formatter.parse(meetingInfo[3]).getTime()),
                    new Timestamp(formatter.parse(meetingInfo[4]).getTime()),
                    meetingInfo[7],
                    meetingInfo[9],
                    meetingInfo[8]);

//            List of minutes: meetingInfo[9]
            if(meetingInfo.length >= 11 && !Objects.equals(meetingInfo[10], "")){
                ArrayList<Content> contents = new ArrayList<>();
                String contentField = meetingInfo[10];
                String[] contentStrings = contentField.split(LINE_BREAK);
                for(String minuteString: contentStrings){
                    String[] minuteData = minuteString.split(FIELD_DELIMITER);
                    contents.add(new Content(minuteData[1],new Timestamp((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(minuteData[0])).getTime())));
                }

                newMeeting.setContents(contents);
            }

//            List of minutes: meetingInfo[10]
            if(meetingInfo.length == 12 && !Objects.equals(meetingInfo[11], "")){
                ArrayList<User> students = new ArrayList<>();
                String studentField = meetingInfo[11];
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

                newMeeting.setStudents(students);
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

    public static ArrayList<Content> getMinutes(Connection conn, int meetingId) throws SQLException {
        ArrayList<Content> contents = new ArrayList<>();

        String contentQuery;
        contentQuery = "select * from " + MINUTE + " where " + MEETING_ID + " = ?";

        PreparedStatement ps = conn.prepareStatement(contentQuery);
        ps.setInt(1, meetingId);

        System.out.println(ps);

        ResultSet rs = ps.executeQuery();

        String content;
        Timestamp time;
        while (rs.next()) {
            content = rs.getString(CONTENT);
            time = rs.getTimestamp(ESTABLISH_DATETIME);

            Content newContent = new Content(content, time);
            contents.add(newContent);
        }

        System.out.println("Contents: " + contents.size());

        return contents;
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

    public static boolean checkDuplicateMeetingTime(Timestamp desiredOccur, Timestamp desiredFinish, ResultSet rs) throws SQLException {
        //true means duplicated, false means available

        Timestamp scheduledOccur, scheduledFinish;
        while (rs.next()){
            scheduledOccur = rs.getTimestamp(MEETING_OCCUR);
            scheduledFinish = rs.getTimestamp(MEETING_FINISH);

            if((desiredOccur.before(scheduledOccur) && desiredFinish.after(scheduledOccur))
                    || (desiredOccur.after(scheduledOccur) && desiredFinish.before(scheduledFinish)))
                return true;
        }
        return false;
    }
}