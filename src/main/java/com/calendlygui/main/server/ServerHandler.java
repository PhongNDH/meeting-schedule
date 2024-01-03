package com.calendlygui.main.server;

import com.calendlygui.database.SqlConnection;
import com.calendlygui.model.Meeting;
import com.calendlygui.model.Minute;
import com.calendlygui.model.User;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.*;

public class ServerHandler {
    private static final Connection conn = SqlConnection.connect();
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static String handleCreateMeeting(String name, int tId, String date, String begin, String end, String classification) throws ParseException {
        Date[] convertedTime = convertToDate(date, begin, end);
        String insertQuery = "insert into " + MEETING + "(" +
                NAME + ", " + MEETING_OCCUR + ", " + MEETING_FINISH + ", " + MEETING_TEACHER_ID + ", " + CLASSIFICATION + ") values (?, ?, ?, ?, ?) returning " + ESTABLISH_DATETIME;
        try {
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setString(1, name);
            ps.setTimestamp(2, new Timestamp(convertedTime[0].getTime()));
            ps.setTimestamp(3, new Timestamp(convertedTime[1].getTime()));
            ps.setInt(4, tId);
            ps.setString(5, classification);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            Timestamp establishTime = null;
            while (rs.next())
                establishTime = rs.getTimestamp(ESTABLISH_DATETIME);

            System.out.println("Created: " + establishTime.toString());
            return createResponse(SUCCESS, "New meeting created", new ArrayList<>(List.of(establishTime.toString())));

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    static String handleEditMeeting(int id, String name, String date, String begin, String end, String status, String selectedClassification) throws ParseException {
        Date[] convertedTime = convertToDate(date, begin, end);
        String insertQuery = "update table " + MEETING +
                " set " + NAME + " = ?," +
                MEETING_OCCUR + " = ?," +
                MEETING_FINISH + " = ?," +
                STATUS + " = ?," +
                SELECTED_CLASSIFICATION + " = ?" +
                " where " + ID + " = ?" +
                " returning " + ESTABLISH_DATETIME;
        try {
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setString(1, name);
            ps.setTimestamp(2, new Timestamp(convertedTime[0].getTime()));
            ps.setTimestamp(3, new Timestamp(convertedTime[1].getTime()));
            ps.setString(4, status);
            ps.setString(5, selectedClassification);
            ps.setInt(6, id);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            Timestamp establishTime = null;
            while (rs.next())
                establishTime = rs.getTimestamp(ESTABLISH_DATETIME);

            System.out.println("Created: " + establishTime.toString());
            return createResponse(SUCCESS, "Meeting edited", new ArrayList<>(List.of(establishTime.toString())));
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    static String handleViewMeetingsByDate(int tId, String date) throws ParseException {
        Date fromDate = formatter.parse(date + " 00:00:00");
        Date toDate = formatter.parse(date + " 23:59:59");

        String query = "select * from " + MEETING + " where " + MEETING_TEACHER_ID + " = ? and " + MEETING_OCCUR + " between ? and ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, tId);
            ps.setTimestamp(2, new Timestamp(fromDate.getTime()));
            ps.setTimestamp(3, new Timestamp(toDate.getTime()));

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            return createResponseWithMeetingList(SUCCESS, DATA_FOUND + ": " + meetings.size(), meetings);
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    public static String handleAddMinute(int mId, String content) {
        String insertQuery = "insert into " + MINUTE + "(" +
                MEETING_ID + ", " + CONTENT + ") values (?, ?) returning " + ESTABLISH_DATETIME;
        try {
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setInt(1, mId);
            ps.setString(2, content);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            Timestamp establishTime = null;
            while (rs.next())
                establishTime = rs.getTimestamp(ESTABLISH_DATETIME);

            System.out.println("Created: " + establishTime.toString());
            return createResponse(SUCCESS, "New minute added", new ArrayList<>(List.of(establishTime.toString())));

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    public static String handleViewHistory(int tId) {
        String query = "select * from " + MEETING + " where " + MEETING_TEACHER_ID + " = ? and " + STATUS + " = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, tId);
            ps.setString(2, ACCEPT);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            System.out.println("Found " + meetings.size() + " in history");

            for(Meeting meeting: meetings){
                ArrayList<Minute> minutes = getMinutes(conn, meeting.id);
                ArrayList<User> students = getStudentsInPastMeetings(conn, meeting.id);
                meeting.minutes = minutes;
                meeting.students = students;
            }

            return createResponseWithMeetingList(SUCCESS, DATA_FOUND + ": " + meetings.size(), meetings);

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    public static String handleViewAvailableSlots(){
        String query = "select * from " + MEETING + " where " + STATUS + " = ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, PENDING);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            return createResponseWithMeetingList(SUCCESS, DATA_FOUND + ": " + meetings.size(), meetings);

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    public static String handleScheduleMeeting(int sId, int mId, String type){
        String participateQuery = "insert into " + PARTICIPATE + "(" + STUDENT_ID + ", " + MEETING_ID + ") values (?, ?) returning " + PARTICIPATE_DATETIME;
        String updateMeetingQuery = "update " + MEETING + " set " + STATUS + " = ?, " + SELECTED_CLASSIFICATION + " = ? where " + ID + " = ? returning " + ESTABLISH_DATETIME ;

        try {
            PreparedStatement participatePs = conn.prepareStatement(participateQuery);
            participatePs.setInt(1, sId);
            participatePs.setInt(2, mId);

            PreparedStatement updateMeetingPs = conn.prepareStatement(updateMeetingQuery);
            updateMeetingPs.setString(1, ACCEPT);
            updateMeetingPs.setString(2, type);
            updateMeetingPs.setInt(3, mId);

            System.out.println(participatePs);
            System.out.println(updateMeetingPs);

            updateMeetingPs.executeQuery();
            ResultSet participateRs = participatePs.executeQuery();

            Timestamp participateTime = null;
            while (participateRs.next()) {
                participateTime = participateRs.getTimestamp(PARTICIPATE_DATETIME);
                break;
            }

            return createResponse(SUCCESS, "Insert & update successfully", new ArrayList<>(List.of(participateTime.toString())));
        } catch (SQLException e){
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }

    public static String handleViewMeetingsByWeek(int sId, String beginDate, String endDate){
        String query = "select * from " + PARTICIPATE + " join " + MEETING +
                " on " + PARTICIPATE + "." + MEETING_ID + " = " + MEETING + "." + ID +
                " where " + STUDENT_ID + " = ?" +
                " and " + MEETING_OCCUR + " >= ? and " + MEETING_OCCUR + " <= ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);

            Date beginTimeStamp = formatter.parse(beginDate + " 00:00:00");
            Date endTimeStamp = formatter.parse(endDate + " 23:59:59");
            ps.setInt(1, sId);
            ps.setTimestamp(2, new Timestamp(beginTimeStamp.getTime()));
            ps.setTimestamp(3, new Timestamp(endTimeStamp.getTime()));

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            return createResponseWithMeetingList(SUCCESS, DATA_FOUND + ": " + meetings.size(), meetings);

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        } catch (ParseException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return createResponse(FAIL, SERVERSIDE_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
    }
}
