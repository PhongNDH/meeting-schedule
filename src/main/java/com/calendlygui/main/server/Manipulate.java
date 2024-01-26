package com.calendlygui.main.server;

import com.calendlygui.database.Authenticate;
import com.calendlygui.utils.Validate;

import java.io.PrintWriter;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.main.server.Server.ConnectionHandler.out;
import static com.calendlygui.main.server.ServerHandler.*;
import static com.calendlygui.utils.Helper.createResponse;


public class Manipulate {

    public static void register(String[] registerInfo, PrintWriter out) {
        if (registerInfo.length == 6) {
            if ((!registerInfo[4].equals("false") && !registerInfo[4].equals("true") || !registerInfo[5].equals("false") && !registerInfo[5].equals("true")) && !Validate.checkEmailFormat(registerInfo[1])) {
                out.println(INCORRECT_FORMAT);
            } else {
                String email = registerInfo[1];
                String username = registerInfo[2];
                String password = registerInfo[3];
                boolean gender = registerInfo[4].equals("true");
                boolean isTeacher = registerInfo[5].equals("true");

                String result = Authenticate.register(email, username, password, gender, isTeacher);
                System.out.println("Result: " + result);
                out.println(result);
            }
        } else {
            out.println(INCORRECT_FORMAT);
        }
    }

    public static void signIn(String[] loginInfo, PrintWriter out) {
        if (loginInfo.length == 3) {
            String email = loginInfo[1];
            String password = loginInfo[2];

            String result = Authenticate.signIn(email, password);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(INCORRECT_FORMAT);
        }
    }

    //TEACHER FUNCTIONS
    public static void createMeeting(String[] data) {
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
            out.println(CLIENT_MISSING_INFO);
        }
    }


    public static void editMeeting(String[] data) {
        if (data.length == 10) {
            int id = Integer.parseInt(data[1]);
            String name = data[2];
            String date = data[3];
            String begin = data[4];
            String end = data[5];
            String status = data[6];
            String classification = data[7];
            String selectedClassification = data[8];
            int tId = Integer.parseInt(data[9]);

            String result = handleEditMeeting(id, name, date, begin, end, status, classification, selectedClassification, tId);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void viewByDate(String[] data) {
        if (data.length == 3) {
            int tId = Integer.parseInt(data[1]);
            String date = data[2];

            String result = handleViewMeetingsByDate(tId, date);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void viewUnscheduledAndHappeningMeetings(String[] data) {
        if (data.length == 2) {
            int tId = Integer.parseInt(data[1]);

            String result = handleTeacherViewUnscheduledAndHappeningMeetings(tId);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void addMinute(String[] data) {
        if (data.length == 3) {
//            int tId = Integer.parseInt(data[1]);
            int mId = Integer.parseInt(data[1]);
            String content = data[2];

            String result = handleAddMinute(mId, content);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void viewHistory(String[] data) {
        if (data.length == 2) {
            int tId = Integer.parseInt(data[1]);

            String result = handleViewHistory(tId);

            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }


    //STUDENT FUNCTIONS
    public static void viewAvailableSlots(String[] data) {
        if (data.length == 2) {
            int sId = Integer.parseInt(data[1]);
            String result = handleViewAvailableSlots(sId);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void scheduleMeeting(String[] data) {
        if (data.length == 4) {
            int sId = Integer.parseInt(data[1]);
            int mId = Integer.parseInt(data[2]);
            String type = data[3];

            String result = handleScheduleMeeting(sId, mId, type);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void viewByWeek(String[] data) {
        if (data.length == 4) {
            int sId = Integer.parseInt(data[1]);
            String beginDate = data[2];
            String endDate = data[3];

            String result = handleViewMeetingsByWeek(sId, beginDate, endDate);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void cancelMeeting(String[] data) {
        if (data.length == 3) {
            int sId = Integer.parseInt(data[1]);
            int mId = Integer.parseInt(data[2]);

            String result = handleCancelMeeting(sId, mId);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void viewScheduled(String[] data) {
        if (data.length == 2) {
            int sId = Integer.parseInt(data[1]);

            String result = handleViewScheduled(sId);
            System.out.println("Result: " + result);
            out.println(result);
        } else {
            out.println(CLIENT_MISSING_INFO);
        }
    }

    public static void quit() {
        String result = createResponse(CREATE_SUCCESS, "Quit successfully");
        System.out.println("Result: " + result);
        out.println(result);
    }

}