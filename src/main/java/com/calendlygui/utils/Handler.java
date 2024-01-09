package com.calendlygui.utils;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.Meeting;
import com.calendlygui.model.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.*;

public class Handler implements Runnable {
    private Socket client;
    private ObjectInputStream inObject;
    private PrintWriter out;
    private BufferedReader in;
    BufferedReader inReader;
    private final int port;
    private final InetAddress host;
    private boolean done;
    private User user;

    private String response;
    private String request;
    ArrayList<String> data = new ArrayList<>();

    public Handler(InetAddress host, int port) {
        this.port = port;
        this.host = host;
        this.user = null;
    }

    public void run() {
        try {
            client = new Socket(this.host, this.port);
            in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
            out = new PrintWriter(this.client.getOutputStream(), true);
            inReader = new BufferedReader(new InputStreamReader(System.in));
            inObject = new ObjectInputStream(this.client.getInputStream());

            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();
        } catch (
                IOException e) {
            this.shutdown();
        }
    }

    private void shutdown() {
        done = true;
        try {
            inReader.close();
            inObject.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    class InputHandler implements Runnable {
        public void run() {
            while (!done) {
                //input from console
                try {
                    String message = inReader.readLine();
                    switch (message) {
                        case "1": {
                            handleLogin(in, out, "Vanh LEG", "111111");
                            break;
                        }
                        case "2": {
                            handleRegister(
                                    in, out,
                                    "vl@gmail.com",
                                    "Vanh LEG",
                                    "111111",
                                    true,
                                    false
                            );
                            handleRegister(
                                    in, out,
                                    "s1@gmail.com",
                                    "Student one",
                                    "111111",
                                    true,
                                    false
                            );
                            handleRegister(
                                    in, out,
                                    "s2@gmail.com",
                                    "Student two",
                                    "111111",
                                    false,
                                    false
                            );
                            handleRegister(
                                    in, out,
                                    "t1@gmail.com",
                                    "Teacher 1",
                                    "111111",
                                    true,
                                    true
                            );
                            handleRegister(
                                    in, out,
                                    "t2@gmail.com",
                                    "Teacher 2",
                                    "111111",
                                    true,
                                    true
                            );
                            break;
                        }
                        case "3": {
                            handleCreateMeeting(
                                    in, out,
                                    "Checkpoint 1",
                                    "2024-1-9",
                                    "08:00",
                                    "08:30",
                                    "individual",
                                    18);

                            handleCreateMeeting(
                                    in, out,
                                    "GR1",
                                    "2024-1-9",
                                    "08:15",
                                    "09:00",
                                    "group",
                                    19);
                            break;
                        }
                        case "4": {
                            handleEditMeeting(
                                    in, out,
                                    10,
                                    "Check point 1 (Edited)",
                                    "2024-1-9",
                                    "08:00",
                                    "08:30",
                                    "accept",
                                    "group"
                            );
                            break;
                        }
                        case "5": {
                            handleViewByDate(in, out, 18, "2024-1-9");
                            handleViewByDate(in, out, 19, "2024-1-9");
                            break;
                        }
                        case "6": {
                            handleAddMinute(in, out, 10, "Check student 1");
                            handleAddMinute(in, out, 10, "Check student 2");
                            handleAddMinute(in, out, 10, "Check student 3");
                            handleAddMinute(in, out, 11, "Assign project");
                            handleAddMinute(in, out, 11, "Check project's progress");
                            break;
                        }
                        case "7": {
                            try {
                                handleViewPastMeetings(in, out, 18);
//                                handleViewPastMeetings(19);
                            } catch (ParseException e){
                                System.out.println(e.getMessage());
                            }
                            break;
                        }

                        //student
                        case "8": {
                            handleViewAvailableSlots(in, out);
                            break;
                        }
                        case "9": {
                            handleScheduleMeeting(in, out, 16, 10, INDIVIDUAL);
//                            handleScheduleMeeting(in, out, 17, 11, GROUP);
                            break;
                        }
                        case "10": {
                            handleViewByWeek(in, out, 4, "2023-1-1", "2023-12-31");
                            break;
                        }
                        case "11": {
                            handleCancelMeeting(in, out, 16, 11);
                            break;
                        }
                        default: {
                        }
                    }

                } catch (IOException | ParseException var3) {
                    shutdown();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    void handleLogin(BufferedReader in, PrintWriter out, String account, String password) throws IOException, ClassNotFoundException, ParseException {
        request = createRequest(LOGIN, new ArrayList<>(List.of(account, password)));
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                    User currentUser = extractUserFromResponse(response);
                    System.out.println(currentUser);
                    System.out.println("Navigate to home screen");
                } else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleRegister(BufferedReader in, PrintWriter out, String username, String email, String password, boolean isMale, boolean isTeacher) throws IOException, ClassNotFoundException, ParseException {
        data.clear();
        data.add(username);
        data.add(email);
        data.add(password);
        data.add(String.valueOf(isMale));
        data.add(String.valueOf(isTeacher));
        request = createRequest(REGISTER, data);
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                    User currentUser = extractUserFromResponse(response);
                    System.out.println(currentUser);
                    System.out.println("Navigate to home screen");
                } else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    private void handleCancelMeeting(BufferedReader in, PrintWriter out, int sId, int mId) throws IOException {
        request = createRequest(STUDENT_CANCEL_MEETING, new ArrayList<>(List.of(String.valueOf(sId), String.valueOf(mId))));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) System.out.println("UPDATE DONE");
                else handleErrorResponse(info[0]);
                break;
            }
        }
    }



    //teacher
    void handleCreateMeeting(BufferedReader in, PrintWriter out, String name, String dateTime, String begin, String end, String classification, int tId) throws IOException, ClassNotFoundException {
        request = createRequest(
                TEACHER_CREATE_MEETING,
                new ArrayList<>(List.of(String.valueOf(tId), name, dateTime, begin, end, classification)));
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) System.out.println("SHOW SOMETHING");
                else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleEditMeeting(BufferedReader in, PrintWriter out, int id, String name, String dateTime, String begin, String end, String status, String selectedClassification) throws IOException, ClassNotFoundException {
        request = createRequest(TEACHER_EDIT_MEETING,
                new ArrayList<>(List.of(String.valueOf(id), name, dateTime, begin, end, status, selectedClassification)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) System.out.println("UPDATE DONE");
                else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleViewByDate(BufferedReader in, PrintWriter out, int tId, String date) throws IOException, ParseException {
        request = createRequest(TEACHER_VIEW_MEETING_BY_DATE,
                new ArrayList<>(List.of(String.valueOf(tId), date)));
        out.println(request);

        while (true) {
            response = in.readLine();

            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    for(Meeting meeting: meetings) System.out.println(meeting);
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }

    void handleAddMinute(BufferedReader in, PrintWriter out, int mId, String content) throws IOException {
//        /TEACHER_ENTER_CONTENT  teacher_id  meeting_id  content
        request = createRequest(TEACHER_ENTER_CONTENT, new ArrayList<>(List.of(String.valueOf(mId), content)));
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if(Integer.parseInt(info[0]) == OPERATION_SUCCESS){
                    System.out.println("DO SOMETHING");
                } else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleViewPastMeetings(BufferedReader in, PrintWriter out, int tId) throws IOException, ParseException {
//        TEACHER_VIEW_HISTORY  teacher_id
        request = createRequest(TEACHER_VIEW_HISTORY, new ArrayList<>(List.of(String.valueOf(tId))));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    for(Meeting meeting: meetings) System.out.println(meeting);
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }


    //student
    void handleViewAvailableSlots(BufferedReader in, PrintWriter out) throws IOException, ParseException {
        request = createRequest(STUDENT_VIEW_TIMESLOT, new ArrayList<>());
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    for(Meeting meeting: meetings) System.out.println(meeting);
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }

    void handleScheduleMeeting(BufferedReader in, PrintWriter out, int sId, int mId, String type) throws IOException, ParseException {
//        /STUDENT_SCHEDULE_INDIVIDUAL_MEETING student_id  meeting_id
        request = createRequest(STUDENT_SCHEDULE_MEETING, new ArrayList<>(List.of(String.valueOf(sId), String.valueOf(mId), type)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if(Integer.parseInt(info[0]) == OPERATION_SUCCESS){
                    System.out.println("Do something");
                } else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleViewByWeek(BufferedReader in, PrintWriter out, int sId, String beginDate, String endDate) throws IOException, ParseException {
//        /STUDENT_VIEW_MEETING_BY_WEEK student_id  begin_date end_date
        request = createRequest(STUDENT_VIEW_MEETING_BY_WEEK, new ArrayList<>(List.of(String.valueOf(sId), beginDate, endDate)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }

    void handleErrorResponse(String codeString){
        int code = Integer.parseInt(codeString);
        switch (code){
            case CLIENT_MISSING_INFO: {
                System.out.println("Missing information in request");
                break;
            }
            case INCORRECT_FORMAT: {
                System.out.println("Incorrect message format");
                break;
            }
            case ACCOUNT_NOT_EXIST: {
                System.out.println("This account does not exist");
                break;
            }
            case INVALID_PASSWORD: {
                System.out.println("Invalid password");
                break;
            }
            case ACCOUNT_EXIST: {
                System.out.println("This account already exist");
                break;
            }
            case UNDEFINED_ERROR: {
                System.out.println("Unknown error");
                break;
            }
            case IO_ERROR: {
                System.out.println("Input/Output error");
                break;
            }
            case PARSE_ERROR: {
                System.out.println("Error parsing numbers and strings");
                break;
            }
            case NULL_ERROR: {
                System.out.println("Null pointer error");
                break;
            }
            case NOT_UP_TO_DATE: {
                System.out.println("Someone interacted with the data source, reload to update");
                break;
            }
            case SQL_ERROR: {
                System.out.println("Sql error");
                break;
            }
            default: {
                System.out.println("Unknown error");
            }
        }
    }


    public static void main(String[] args) throws UnknownHostException {
        Handler handler = new Handler(InetAddress.getLocalHost(), ConstantValue.PORT);
        handler.run();
    }
}
