package com.calendlygui.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.createRequest;

public class SendData {

    private static String response;
    private static String request;
    private static ArrayList<String> data = new ArrayList<>();

    public static void login(PrintWriter out, String account, String password) throws IOException {
        request = createRequest(LOGIN, new ArrayList<>(List.of(account, password)));
        out.println(request);
    }

    public static void register( PrintWriter out, String username, String email, String password, boolean isMale, boolean isTeacher) throws IOException, ClassNotFoundException {
        data.clear();
        data.add(username);
        data.add(email);
        data.add(password);

        data.add(isMale ? "true" : "false");
        data.add(isTeacher ? "true" : "false");
        request = createRequest(REGISTER, data);
        out.println(request);
    }

    public static void createMeeting(PrintWriter out, String name, String dateTime, String begin, String end, String classification, int tId) throws IOException, ClassNotFoundException {
        request = createRequest(
                TEACHER_CREATE_MEETING,
                new ArrayList<>(List.of(String.valueOf(tId), name, dateTime, begin, end, classification)));
        out.println(request);
    }

    public static void viewAvailableSlots(PrintWriter out) {
        request = createRequest(STUDENT_VIEW_TIMESLOT, new ArrayList<>());
        out.println(request);

        //listen to response
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println(response);
//
//                String[] info = response.split(COMMAND_DELIMITER);
//                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
//                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
//                    System.out.println(meetings.size());
//                    for(Meeting meeting: meetings) System.out.println(meeting);
//                    break;
//                } else handleErrorResponse(info[0]);
//            }
//        }
    }
}
