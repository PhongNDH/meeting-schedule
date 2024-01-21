package com.calendlygui.utils;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.model.entity.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

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
                            handleLogin(in, out, "t1@gmail.com", "111111");
                            break;
                        }
                        case "2": {
                            handleRegister(in, out, "Teacher 1", "t1@gmail.com", "111111", true, true);
                            handleRegister(in, out, "Teacher 2", "t2@gmail.com", "111111", true, true);
                            handleRegister(in, out, "Teacher 3", "t3@gmail.com", "111111", true, true);

                            handleRegister(in, out, "Student 1", "s1@gmail.com", "111111", true, false);
                            handleRegister(in, out, "Student 2", "s2@gmail.com", "111111", true, false);
                            handleRegister(in, out, "Student 3", "s3@gmail.com", "111111", true, false);
                            break;
                        }
                        case "3": {
                            handleCreateMeeting(in, out,
                                    "Upcoming checkpoint",
                                    "2024-1-14",
                                    "09:15",
                                    "09:30",
                                    "group",
                                    100);
                            break;
                        }
                        case "4": {
                            handleEditMeeting(
                                    in, out,
                                    54,
                                    "Check point 1 (Edited)",
                                    "2024-1-13",
                                    "09:29",
                                    "10:00",
                                    "accept",
                                    "group",
                                    "group",
                                    100
                            );
                            break;
                        }
                        case "4A": {
                            handleEditMeeting(
                                    in, out,
                                    54,
                                    "Check point 1 (Edited)",
                                    "2024-1-13",
                                    "09:01",
                                    "09:14",
                                    "accept",
                                    "group",
                                    "group",
                                    100
                            );
                            break;
                        }
                        case "5": {
                            handleViewByDate(in, out, 100, "2024-1-13");
                            handleViewByDate(in, out, 101, "2024-1-13");
                            break;
                        }
                        case "6": {
                            handleAddMinute(in, out, 54, "Check student 1");
                            handleAddMinute(in, out, 54, "Check student 2");
                            handleAddMinute(in, out, 54, "Check student 3");
                            break;
                        }
                        case "7": {
                            //history
                            handleViewPastMeetings(in, out, 118);
                            break;
                        }

                        //student
                        case "8": {
                            handleStudentViewAvailableSlots(in, out, 103);
                            handleStudentViewAvailableSlots(in, out, 104);
                            break;
                        }
                        case "9": {
                            handleStudentScheduleMeeting(in, out, 103, 57, INDIVIDUAL);
                            handleStudentScheduleMeeting(in, out, 104, 57, GROUP);
                            break;
                        }
                        case "10": {
                            handleStudentViewByWeek(in, out, 103, "2024-1-1", "2024-12-31");
                            handleStudentViewByWeek(in, out, 104, "2024-1-1", "2024-12-31");
                            handleStudentViewByWeek(in, out, 105, "2024-1-1", "2024-12-31");
                            break;
                        }
                        case "11": {
                            //not happening
                            handleCancelMeeting(in, out, 105, 57);

                            handleCancelMeeting(in, out, 104, 57);
                            break;
                        }
                        case "11A": {
                            handleCancelMeeting(in, out, 103, 57);
                            break;
                        }
                        case "12": {
                            //teacher view all scheduled meeting
                            handleTeacherViewScheduledMeetings(in, out, 100);
                            break;
                        }
                        case "12A": {
                            handleStudentViewScheduledMeeting(103);
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

    void handleStudentViewScheduledMeeting(int sId) throws IOException, ParseException {
        request = createRequest(STUDENT_VIEW_SCHEDULED, new ArrayList<>(List.of(String.valueOf(sId))));
        out.println(request);

        while (true) {
            response = in.readLine();

            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    for (Meeting meeting : meetings) System.out.println(meeting);
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }

    void handleTeacherViewScheduledMeetings(BufferedReader in, PrintWriter out, int tId) throws IOException, ParseException {
        request = createRequest(TEACHER_VIEW_MEETING, new ArrayList<>(List.of(String.valueOf(tId))));
        out.println(request);

        while (true) {
            response = in.readLine();

            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    for (Meeting meeting : meetings) System.out.println(meeting);
                    break;
                } else handleErrorResponse(info[0]);
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
                if (Integer.parseInt(info[0]) == AUTHENTICATE_SUCCESS) {
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
        data.add(email);
        data.add(username);
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
                if (Integer.parseInt(info[0]) == AUTHENTICATE_SUCCESS) {
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
                if (Integer.parseInt(info[0]) == UPDATE_SUCCESS) System.out.println("UPDATE DONE");
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
                if (Integer.parseInt(info[0]) == CREATE_SUCCESS) System.out.println("SHOW SOMETHING");
                else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleEditMeeting(BufferedReader in, PrintWriter out, int id, String name, String dateTime, String begin, String end, String status, String classification, String selectedClassification, int tId) throws IOException, ClassNotFoundException {
        request = createRequest(TEACHER_EDIT_MEETING,
                new ArrayList<>(List.of(String.valueOf(id), name, dateTime, begin, end, status, classification, selectedClassification, String.valueOf(tId))));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == UPDATE_SUCCESS) System.out.println("UPDATE DONE");
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
                if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    for (Meeting meeting : meetings) System.out.println(meeting);
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
                if (Integer.parseInt(info[0]) == CREATE_SUCCESS) {
                    System.out.println("DO SOMETHING");
                } else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleViewPastMeetings(BufferedReader in, PrintWriter out, int tId) {
        // TEACHER_VIEW_HISTORY  teacher_id
        request = createRequest(TEACHER_VIEW_HISTORY, new ArrayList<>(List.of(String.valueOf(tId))));
        out.println(request);

        try {

            while (true) {
                response = in.readLine();
                if (response != null) {
                    System.out.println(response);

                    String[] info = response.split(COMMAND_DELIMITER);
                    if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                        ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                        System.out.println(meetings.size());
                        for (Meeting meeting : meetings) System.out.println(meeting);
                        break;
                    } else handleErrorResponse(info[0]);
                }
            }
        } catch (ParseException e) {
            System.out.println("Parse exception from client: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO exception from client: " + e.getMessage());
        }
    }

    //student
    void handleStudentViewAvailableSlots(BufferedReader in, PrintWriter out, int sId) throws IOException, ParseException {
        request = createRequest(STUDENT_VIEW_TIMESLOT, new ArrayList<>(List.of(String.valueOf(sId))));
        out.println(request);
        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    for (Meeting meeting : meetings) System.out.println(meeting);
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }

    void handleStudentScheduleMeeting(BufferedReader in, PrintWriter out, int sId, int mId, String type) throws IOException, ParseException {
//        /STUDENT_SCHEDULE_INDIVIDUAL_MEETING student_id  meeting_id
        request = createRequest(STUDENT_SCHEDULE_MEETING, new ArrayList<>(List.of(String.valueOf(sId), String.valueOf(mId), type)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == UPDATE_SUCCESS) {
                    System.out.println("Do something");
                } else handleErrorResponse(info[0]);
                break;
            }
        }
    }

    void handleStudentViewByWeek(BufferedReader in, PrintWriter out, int sId, String beginDate, String endDate) throws IOException, ParseException {
//        /STUDENT_VIEW_MEETING_BY_WEEK student_id  begin_date end_date
        request = createRequest(STUDENT_VIEW_MEETING_BY_WEEK, new ArrayList<>(List.of(String.valueOf(sId), beginDate, endDate)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(COMMAND_DELIMITER);
                if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    for (Meeting meeting : meetings) System.out.println(meeting);
                    System.out.println();
                    break;
                } else handleErrorResponse(info[0]);
            }
        }
    }

    void handleErrorResponse(String codeString) {
        int code = Integer.parseInt(codeString);
        switch (code) {
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
            case DUPLICATE_SCHEDULE: {
                System.out.println("Already existed another scheduled meeting. Choose another time");
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