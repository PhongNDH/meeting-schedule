package com.calendlygui.main.server;

import com.calendlygui.database.Authenticate;
import com.calendlygui.utils.Validate;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.main.server.Server.ConnectionHandler.out;
import static com.calendlygui.main.server.ServerHandler.*;
import static com.calendlygui.utils.Helper.createResponse;


public class Manipulate {

    public static void register(String[] registerInfo) throws IOException {
        if (registerInfo.length == 6) {
            if ((!registerInfo[4].equals("false") && !registerInfo[4].equals("true") || !registerInfo[5].equals("false") && !registerInfo[5].equals("true")) && !Validate.checkEmailFormat(registerInfo[1])) {
                String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(INCORRECT_FORMAT)));
                out.write(error);
            } else {
                String email = registerInfo[1];
                String username = registerInfo[2];
                String password = registerInfo[3];
                boolean gender = registerInfo[4].equals("true");
                boolean isTeacher = registerInfo[5].equals("true");
                String result = Authenticate.register(email, username, password, gender, isTeacher);

                out.println(result);
            }
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(INCORRECT_FORMAT)));
            out.write(error);
        }
    }

    public static void signIn(String[] loginInfo){
        if (loginInfo.length == 3) {
            String email = loginInfo[1];
            String password = loginInfo[2];

            String result = Authenticate.signIn(email, password);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(INCORRECT_FORMAT)));
            out.write(error);
        }
    }

    //TEACHER FUNCTIONS
    public static void createMeeting(String[] data) throws ParseException {
        if (data.length == 7) {
            int tId = Integer.parseInt(data[1]);
            String name = data[2];
            String date = data[3];
            String begin = data[4];
            String end = data[5];
            String classification = data[6];

            String result = handleCreateMeeting(name, tId, date, begin, end, classification);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }


    public static void editMeeting(String[] data) throws ParseException {
        if (data.length == 8) {
            int id = Integer.parseInt(data[1]);
            String name = data[2];
            String date = data[3];
            String begin = data[4];
            String end = data[5];
            String status = data[6];
            String selectedClassification = data[7];

            String result = handleEditMeeting(id, name, date, begin, end, status, selectedClassification);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }

    public static void viewByDate(String[] data) throws ParseException {
        if(data.length == 3){
            int tId = Integer.parseInt(data[1]);
            String date = data[2];

            String result = handleViewMeetingsByDate(tId, date);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }

    public static void addMinute(String[] data) {
        if(data.length == 3){
//            int tId = Integer.parseInt(data[1]);
            int mId = Integer.parseInt(data[1]);
            String content = data[2];

            String result = handleAddMinute(mId, content);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }

    public static void viewHistory(String[] data){
        if(data.length == 2){
            int tId = Integer.parseInt(data[1]);

            String result = handleViewHistory(tId);

            System.out.println(result);

            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }


    //STUDENT FUNCTIONS
    public static void viewAvailableSlots() {
        String result = handleViewAvailableSlots();
        out.println(result);
    }

    public static void scheduleMeeting(String[] data) {
        if(data.length == 4){
            int sId = Integer.parseInt(data[1]);
            int mId = Integer.parseInt(data[2]);
            String type = data[3];

            String result = handleScheduleMeeting(sId, mId, type);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }

    public static void viewByWeek(String[] data) {
        if(data.length == 4){
            int sId = Integer.parseInt(data[1]);
            String beginDate = data[2];
            String endDate = data[3];

            String result = handleViewMeetingsByWeek(sId, beginDate, endDate);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }

    public static void cancelMeeting(String[] data) {
        if(data.length == 3){
            int sId = Integer.parseInt(data[1]);
            int mId = Integer.parseInt(data[2]);

            String result = handleCancelMeeting(sId, mId);
            out.println(result);
        } else {
            String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(MISSING_INFO)));
            out.println(error);
        }
    }

    public static void quit() {
        String result = createResponse(SUCCESS, "Quit successfully", new ArrayList<>());
        out.println(result);
    }

}