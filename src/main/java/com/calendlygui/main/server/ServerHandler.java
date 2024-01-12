package com.calendlygui.main.server;

import com.calendlygui.database.SqlConnection;
import com.calendlygui.model.entity.Content;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.model.entity.User;

import java.security.PrivilegedAction;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.*;

public class ServerHandler {
    private static final Connection conn = SqlConnection.connect();
    private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    static String handleCreateMeeting(String name, int tId, String date, String begin, String end, String classification) throws ParseException {
        //query meetings in a day to check duplicate
        Timestamp filterDateStart = toTimeStamp(date, "00:00:00");
        Timestamp filterDateEnd = toTimeStamp(date, "23:59:59");

        //format data to compare amd insert if not duplicated
        Date[] convertedTime = convertToDate(date, begin, end);
        Timestamp occur = new Timestamp(convertedTime[0].getTime());
        Timestamp finish = new Timestamp(convertedTime[1].getTime());
        try {
            String checkQuery = "select * from " + MEETING + " where " + MEETING_OCCUR + " >= ? and " + MEETING_OCCUR + " <= ?";
            PreparedStatement checkPs = conn.prepareStatement(checkQuery);
            checkPs.setTimestamp(1, filterDateStart);
            checkPs.setTimestamp(2, filterDateEnd);

            ResultSet checkRs = checkPs.executeQuery();
            while (checkRs.next()) {
                Timestamp alreadyOccur = checkRs.getTimestamp(MEETING_OCCUR);
                Timestamp alreadyFinish = checkRs.getTimestamp(MEETING_FINISH);

                if (occur.before(alreadyOccur) && finish.after(alreadyFinish)
                        || occur.after(alreadyOccur) && occur.before(alreadyFinish))
                    return String.valueOf(DUPLICATE_SCHEDULE);
            }

            //if not duplicate then can insert
            String insertQuery = "insert into " + MEETING + "(" +
                    NAME + ", " + MEETING_OCCUR + ", " + MEETING_FINISH + ", " + MEETING_TEACHER_ID + ", " + CLASSIFICATION + ") values (?, ?, ?, ?, ?) returning " + ESTABLISH_DATETIME;
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            ps.setString(1, name);
            ps.setTimestamp(2, occur);
            ps.setTimestamp(3, finish);
            ps.setInt(4, tId);
            ps.setString(5, classification);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            Timestamp establishTime = null;
            while (rs.next())
                establishTime = rs.getTimestamp(ESTABLISH_DATETIME);

            assert establishTime != null;
            System.out.println("Created: " + establishTime);
            return createResponse(CREATE_SUCCESS, establishTime.toString());

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    static String handleEditMeeting(int id, String name, String date, String begin, String end, String status, String selectedClassification) throws ParseException {
        Date[] convertedTime = convertToDate(date, begin, end);
        String insertQuery = "update " + MEETING +
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
            return createResponse(UPDATE_SUCCESS, establishTime.toString());
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    static String handleViewMeetings(int tId) {

        String query = "select m.*, u." + NAME + " as " + TEACHER_NAME + " from " + MEETING + " m join " + USERS + " u on m." + MEETING_TEACHER_ID + " = u." + ID + " where " + MEETING_TEACHER_ID + " = ? and " + STATUS + " = ? or (" + STATUS + " = ? and " + MEETING_OCCUR + " > CURRENT_TIMESTAMP)";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, tId);
            ps.setString(2, PENDING);
            ps.setString(3, ACCEPT);

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            return createResponseWithMeetingList(QUERY_SUCCESS, meetings);
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    static String handleViewMeetingsByDate(int tId, String date) throws ParseException {
        Date fromDate = formatter.parse(date + " 00:00:00");
        Date toDate = formatter.parse(date + " 23:59:59");

        String query = "select m.*, u." + NAME + " as " + TEACHER_NAME + " from " + MEETING + " m join " + USERS + " u on m." + MEETING_TEACHER_ID + " = u." + ID + " where " + MEETING_TEACHER_ID + " = ? and " + MEETING_OCCUR + " between ? and ?";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, tId);
            ps.setTimestamp(2, new Timestamp(fromDate.getTime()));
            ps.setTimestamp(3, new Timestamp(toDate.getTime()));

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            return createResponseWithMeetingList(QUERY_SUCCESS, meetings);
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
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
            return createResponse(CREATE_SUCCESS, establishTime.toString());

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    public static String handleViewHistory(int tId) {
        String query = "select m.*, u." + NAME + " as " + TEACHER_NAME + " from " + MEETING + " m join " + USERS + " u on m." + MEETING_TEACHER_ID + " = u." + ID + " where " + MEETING_TEACHER_ID + " = ? and " + STATUS + " = ? and " + MEETING_FINISH + " < CURRENT_TIMESTAMP";
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, tId);
            ps.setString(2, ACCEPT);

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            System.out.println("Found " + meetings.size() + " in history");

            for (Meeting meeting : meetings) {
                ArrayList<Content> contents = getMinutes(conn, meeting.getId());
                ArrayList<User> students = getStudentsInPastMeetings(conn, meeting.getId());
                meeting.setContents(contents);
                meeting.setStudents(students);
            }

            return createResponseWithMeetingList(QUERY_SUCCESS, meetings);

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    public static String handleViewAvailableSlots(int sId) {
        try {
            String getAlreadyScheduledMeetingIds = "select * from " + PARTICIPATE + " where " + STUDENT_ID + " = ?";
            PreparedStatement getAlreadyScheduledMeetingIdPs = conn.prepareStatement(getAlreadyScheduledMeetingIds);
            getAlreadyScheduledMeetingIdPs.setInt(1, sId);
            System.out.println(getAlreadyScheduledMeetingIdPs);

            ResultSet getAlreadyScheduledMeetingIdRs = getAlreadyScheduledMeetingIdPs.executeQuery();
            ArrayList<Integer> scheduledMeetingIds = new ArrayList<>();
            while (getAlreadyScheduledMeetingIdRs.next()){
                scheduledMeetingIds.add(getAlreadyScheduledMeetingIdRs.getInt(MEETING_ID));
            }
            System.out.println("Scheduled: " + scheduledMeetingIds.size());

            String query = "select m.*, u." + NAME + " as " + TEACHER_NAME + " from " + MEETING + " m join " + USERS + " u on m." + MEETING_TEACHER_ID + " = u." + ID + " where " + STATUS + " = ? or (" + STATUS + " = ? and " + SELECTED_CLASSIFICATION + " = ? and " + MEETING_OCCUR + " > CURRENT_TIMESTAMP)";
            for(int i=0; i<scheduledMeetingIds.size(); i++){
                query += " and m." + ID + " != ?";
            }
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, PENDING);
            ps.setString(2, ACCEPT);
            ps.setString(3, GROUP);
            for(int i=0; i<scheduledMeetingIds.size(); i++){
                ps.setInt(i + 4, scheduledMeetingIds.get(i));
            }

            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            ArrayList<Meeting> meetings = getMeetings(rs);

            return createResponseWithMeetingList(QUERY_SUCCESS, meetings);
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    public static String handleScheduleMeeting(int sId, int mId, String type) {
        try {
            //get student's participated meeting ids
            String checkDuplicateTimeQuery = "select * from " + PARTICIPATE + " where " + STUDENT_ID + " = ?";
            PreparedStatement checkDuplicatePs = conn.prepareStatement(checkDuplicateTimeQuery);
            checkDuplicatePs.setInt(1, sId);
            ResultSet checkDuplicateRs = checkDuplicatePs.executeQuery();
            ArrayList<Integer> meetingIds = new ArrayList<>();
            while (checkDuplicateRs.next()){
                meetingIds.add(checkDuplicateRs.getInt(MEETING_ID));
            }

            //Get meetings from ids
            String findScheduledTimeQuery = "select * from " + MEETING;
            for (int i=0; i<meetingIds.size(); i++){
                if(i == 0) findScheduledTimeQuery += " where " + ID + " = ?";
                else findScheduledTimeQuery += " or " + ID + " = ?";
            }
            PreparedStatement findScheduledTimePs = conn.prepareStatement(findScheduledTimeQuery);
            for(int i = 0; i<meetingIds.size(); i++){
                findScheduledTimePs.setInt(i + 1, meetingIds.get(i));
            }
            ResultSet findScheduledTimeRs = findScheduledTimePs.executeQuery();

            //get desired meeting info
            String checkMeetingQuery = "select * from " + MEETING + " where " + ID + " = ?";
            PreparedStatement checkMeetingPs = conn.prepareStatement(checkMeetingQuery);
            checkMeetingPs.setInt(1, mId);
            System.out.println(checkMeetingPs);

            ResultSet checkMeetingRs = checkMeetingPs.executeQuery();

            Timestamp desiredOccur = null, desiredFinish = null;
            String selectedClassification = null;
            while (checkMeetingRs.next()) {
                desiredOccur = checkMeetingRs.getTimestamp(MEETING_OCCUR);
                desiredFinish = checkMeetingRs.getTimestamp(MEETING_FINISH);
                selectedClassification = checkMeetingRs.getString(SELECTED_CLASSIFICATION);
            }

            //if an individual selected or a group selected but user want individual
            if (selectedClassification != null && (selectedClassification.equals(INDIVIDUAL)
                    || selectedClassification.equals(GROUP) && type.equals(INDIVIDUAL)))
                return String.valueOf(NOT_UP_TO_DATE);

            //if there already have meetings at that time
            if (checkDuplicateMeetingTime(desiredOccur, desiredFinish, findScheduledTimeRs)) return String.valueOf(DUPLICATE_SCHEDULE);

            //otherwise, schedule is available
            String participateQuery = "insert into " + PARTICIPATE + "(" + STUDENT_ID + ", " + MEETING_ID + ") values (?, ?) returning " + PARTICIPATE_DATETIME;
            String updateMeetingQuery = "update " + MEETING + " set " + STATUS + " = ?, " + SELECTED_CLASSIFICATION + " = ? where " + ID + " = ? returning " + ESTABLISH_DATETIME;

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

            return createResponse(UPDATE_SUCCESS, participateTime.toString());
        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        }
    }

    public static String handleViewMeetingsByWeek(int sId, String beginDate, String endDate) {
        String query = "select m.*, p.*, u." + NAME + " as " + TEACHER_NAME + " from " + PARTICIPATE + " p join " + MEETING +
                " m on p." + MEETING_ID + " = m." + ID + " join " + USERS + " u on u." + ID + " = m." + MEETING_TEACHER_ID +
                " where " + STUDENT_ID + " = ? and " + MEETING_OCCUR + " >= ? and " + MEETING_OCCUR + " <= ?";
        System.out.println(query);
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

            return createResponseWithMeetingList(QUERY_SUCCESS, meetings);

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        } catch (ParseException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(PARSE_ERROR);
        }
    }

    public static String handleCancelMeeting(int sId, int mId) {
        String meetingQuery = "select * from " + MEETING + " where " + ID + " = ?";

        try {
            PreparedStatement ps = conn.prepareStatement(meetingQuery);
            ps.setInt(1, mId);
            System.out.println(ps);

            ResultSet rs = ps.executeQuery();
            String selectedClassification = null;

            Timestamp executedTime = null;
            while (rs.next()) {
                selectedClassification = rs.getString(SELECTED_CLASSIFICATION);
            }
            if (selectedClassification.equals(GROUP)) {
                String countStudentQuery = "select count(*) from " + PARTICIPATE + " where " + MEETING_ID + " = ?";
                PreparedStatement countPs = conn.prepareStatement(countStudentQuery);
                countPs.setInt(1, mId);
                System.out.println(countPs);

                ResultSet countRs = countPs.executeQuery();
                int studentNum = 0;
                while (countRs.next()) studentNum = countRs.getInt("count");
                System.out.println("Number of participated students: " + studentNum);

                executedTime = studentNum == 1 ? cancelAndUpdateMeeting(sId, mId) : cancelMeeting(sId, mId);
            } else executedTime = cancelAndUpdateMeeting(sId, mId);

            return createResponse(UPDATE_SUCCESS, executedTime.toString());

        } catch (SQLException e) {
            System.out.println(SQL_EXCEPTION + ": " + e.getMessage());
            return String.valueOf(SQL_ERROR);
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
            return String.valueOf(NULL_ERROR);
        }
    }

    private static Timestamp cancelAndUpdateMeeting(int sId, int mId) throws SQLException {
        String updateMeetingQuery = "update " + MEETING + " set " + STATUS + " = '" + PENDING + "', " + SELECTED_CLASSIFICATION + " = null where " + ID + " = ? returning " + ESTABLISH_DATETIME;
        PreparedStatement updatePs = conn.prepareStatement(updateMeetingQuery);
        updatePs.setInt(1, mId);
        System.out.println(updatePs);

        ResultSet updateRs = updatePs.executeQuery();

        Timestamp establishDatetime = null;
        while (updateRs.next()) {
            establishDatetime = updateRs.getTimestamp(ESTABLISH_DATETIME);
        }

        String deleteQuery = "delete from " + PARTICIPATE + " where " + STUDENT_ID + " = ? and " + MEETING_ID + " = ? returning " + PARTICIPATE_DATETIME;
        PreparedStatement deletePs = conn.prepareStatement(deleteQuery);
        deletePs.setInt(1, sId);
        deletePs.setInt(2, mId);
        System.out.println(deletePs);

        deletePs.executeQuery();

        return establishDatetime;
    }

    private static Timestamp cancelMeeting(int sId, int mId) throws SQLException {
        String deleteQuery = "delete from " + PARTICIPATE + " where " + STUDENT_ID + " = ? and " + MEETING_ID + " = ? returning " + PARTICIPATE_DATETIME;
        PreparedStatement deletePs = conn.prepareStatement(deleteQuery);
        deletePs.setInt(1, sId);
        deletePs.setInt(2, mId);
        System.out.println(deletePs);

        deletePs.executeQuery();

        Timestamp deleteTime = new Timestamp(System.currentTimeMillis());
        return deleteTime;
    }
}