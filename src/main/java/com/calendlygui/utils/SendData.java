package com.calendlygui.utils;

import com.calendlygui.model.entity.Meeting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.createRequest;
import static com.calendlygui.utils.Helper.extractMeetingsFromResponse;

public class SendData {

    private static String response;
    private static String request;
    private static ArrayList<String> data = new ArrayList<>();

    public static void login(PrintWriter out, String account, String password) throws IOException {
        request = createRequest(LOGIN, new ArrayList<>(List.of(account, password)));
        out.println(request);
    }

    public static void register(PrintWriter out, String username, String email, String password, boolean isMale, boolean isTeacher) throws IOException, ClassNotFoundException {
        data.clear();
        data.add(username);
        data.add(email);
        data.add(password);

        data.add(isMale ? "true" : "false");
        data.add(isTeacher ? "true" : "false");
        request = createRequest(REGISTER, data);
        out.println(request);
    }

    // STUDENT
    public static void viewAvailableSlots(PrintWriter out, int sId) {
        request = createRequest(STUDENT_VIEW_TIMESLOT, new ArrayList<>(List.of(String.valueOf(sId))));
        out.println(request);
    }

    public static void scheduleMeeting(PrintWriter out, int sId, int mId, String type) throws IOException, ParseException {
        // /STUDENT_SCHEDULE_INDIVIDUAL_MEETING student_id  meeting_id
        request = createRequest(STUDENT_SCHEDULE_MEETING, new ArrayList<>(List.of(String.valueOf(sId), String.valueOf(mId), type)));
        out.println(request);
    }

    public static void viewScheduledMeeting(PrintWriter out, int sId){
        request = createRequest(STUDENT_VIEW_SCHEDULED, new ArrayList<>(List.of(String.valueOf(sId))));
        out.println(request);
    }

    public static void cancelMeeting(PrintWriter out, int sId, int mId) {
        request = createRequest(STUDENT_CANCEL_MEETING, new ArrayList<>(List.of(String.valueOf(sId), String.valueOf(mId))));
        out.println(request);
    }


    // TEACHER
    public static void createMeeting(PrintWriter out, String name, String dateTime, String begin, String end, String classification, int tId) throws IOException, ClassNotFoundException {
        request = createRequest(
                TEACHER_CREATE_MEETING,
                new ArrayList<>(List.of(String.valueOf(tId), name, dateTime, begin, end, classification)));
        out.println(request);
    }

    public static void viewStudentScheduledMeetings(PrintWriter out, int tId) {
        request = createRequest(TEACHER_VIEW_MEETING, new ArrayList<>(List.of(String.valueOf(tId))));
        out.println(request);
    }

    public static void editMeeting(PrintWriter out, int id, String name, String dateTime, String begin, String end, String status, String classification, String selectedClassification, int tId) {
        request = createRequest(TEACHER_EDIT_MEETING,
                new ArrayList<>(List.of(String.valueOf(id), name, dateTime, begin, end, status, classification, selectedClassification, String.valueOf(tId))));
        out.println(request);

//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(COMMAND_DELIMITER);
//                if (Integer.parseInt(info[0]) == UPDATE_SUCCESS) System.out.println("UPDATE DONE");
//                else handleErrorResponse(info[0]);
//                break;
//            }
//        }
    }

    public static void addContent(PrintWriter out, int mId, String content) {
//        /TEACHER_ENTER_CONTENT  teacher_id  meeting_id  content
        request = createRequest(TEACHER_ENTER_CONTENT, new ArrayList<>(List.of(String.valueOf(mId), content)));
        out.println(request);

        //listen to response
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(COMMAND_DELIMITER);
//                if(Integer.parseInt(info[0]) == CREATE_SUCCESS){
//                    System.out.println("DO SOMETHING");
//                } else handleErrorResponse(info[0]);
//                break;
//            }
//        }
    }

    public static void viewHistory(PrintWriter out, int tId) {
        // TEACHER_VIEW_HISTORY  teacher_id
        request = createRequest(TEACHER_VIEW_HISTORY, new ArrayList<>(List.of(String.valueOf(tId))));
        out.println(request);
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println(response);
//
//                String[] info = response.split(COMMAND_DELIMITER);
//                if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
//                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
//                    System.out.println(meetings.size());
//                    for(Meeting meeting: meetings) System.out.println(meeting);
//                    break;
//                } else handleErrorResponse(info[0]);
//            }
//        }
    }

}