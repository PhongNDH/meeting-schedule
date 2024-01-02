package com.calendlygui.utils;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.Meeting;
import com.calendlygui.model.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.createRequest;
import static com.calendlygui.utils.Helper.extractMeetingsFromResponse;

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
                            handleLogin("nguyendai060703@gmail.com", "111111");
                            break;
                        }
                        case "2": {
                            handleRegister(
                                    "Student Joe",
                                    "js@gmail.com",
                                    "111111",
                                    true,
                                    false
                            );
                            break;
                        }
                        case "3": {
                            handleCreateMeeting(
                                    "Checkpoint1",
                                    "2023-12-26",
                                    "07:00",
                                    "07:20",
                                    "individual",
                                    1);
                            break;
                        }
                        case "4": {
                            handleEditMeeting(
                                    1,
                                    "Edited checkpoint",
                                    "2023-12-30",
                                    "8:20",
                                    "8:40",
                                    "accept",
                                    "group"
                            );
                            break;
                        }
                        case "5": {
                            handleViewByDate(1, "2023-12-30");
                            break;
                        }
                        case "6": {
                            handleAddMinute(1, "No need to do that");
                            break;
                        }
                        case "7": {
                            handleViewPastMeetings(1);
                            break;
                        }

                        //student
                        case "8": {
                            handleViewAvailableSlots();
                            break;
                        }
                        case "9": {
                            handleScheduleMeeting(4, 2, GROUP);
                            break;
                        }
                        case "10": {
                            handleViewByWeek(4, "2023-1-1", "2023-12-31");
                            break;
                        }
                        default: {
                        }
                    }

                } catch (IOException var3) {
                    shutdown();
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    void handleLogin(String account, String password) throws IOException, ClassNotFoundException {
        request = createRequest(LOGIN, new ArrayList<>(List.of(account, password)));
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(DELIMITER);
                if (info[0].contains(SUCCESS)) System.out.println("Navigate to home screen");
                break;
            }
        }
    }

    void handleRegister(String username, String email, String password, boolean isMale, boolean isTeacher) throws IOException, ClassNotFoundException {
        data.clear();
        data.add(username);
        data.add(email);
        data.add(password);
        data.add(isMale ? "true" : "false");
        data.add(isTeacher ? "true" : "false");
        request = createRequest(REGISTER, data);
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(DELIMITER);
                if (info[0].contains(SUCCESS)) System.out.println("Navigate to home screen");
                break;
            }
        }
    }


    //teacher
    void handleCreateMeeting(String name, String dateTime, String begin, String end, String classification, int tId) throws IOException, ClassNotFoundException {
        request = createRequest(
                TEACHER_CREATE_MEETING,
                new ArrayList<>(List.of(String.valueOf(tId), name, dateTime, begin, end, classification)));
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) System.out.println("SHOW SOMETHING");
                break;
            }
        }
    }

    void handleEditMeeting(int id, String name, String dateTime, String begin, String end, String status, String selectedClassification) throws IOException, ClassNotFoundException {
        request = createRequest(TEACHER_EDIT_MEETING,
                new ArrayList<>(List.of(String.valueOf(id), name, dateTime, begin, end, status, selectedClassification)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) System.out.println("UPDATE DONE");
                break;
            }
        }
    }

    void handleViewByDate(int tId, String date) throws IOException {
        request = createRequest(TEACHER_VIEW_MEETING_BY_DATE,
                new ArrayList<>(List.of(String.valueOf(tId), date)));
        out.println(request);

        while (true) {
            response = in.readLine();

            if (response != null) {
                System.out.println(response);

                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    break;
                }
            }
        }
    }

    void handleAddMinute(int mId, String content) throws IOException {
//        /TEACHER_ENTER_CONTENT  teacher_id  meeting_id  content
        request = createRequest(TEACHER_ENTER_CONTENT, new ArrayList<>(List.of(String.valueOf(mId), content)));
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println("Response: " + response);
                break;
            }
        }
    }

    void handleViewPastMeetings(int tId) throws IOException {
//        TEACHER_VIEW_HISTORY  teacher_id
        request = createRequest(TEACHER_VIEW_HISTORY, new ArrayList<>(List.of(String.valueOf(tId))));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    break;
                }
            }
        }
    }


    //student
    void handleViewAvailableSlots() throws IOException {
        request = createRequest(STUDENT_VIEW_TIMESLOT, new ArrayList<>());
        out.println(request);

        //listen to response
        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    break;
                }
            }
        }
    }

    void handleScheduleMeeting(int sId, int mId, String type) throws IOException {
//        /STUDENT_SCHEDULE_INDIVIDUAL_MEETING student_id  meeting_id
        request = createRequest(STUDENT_SCHEDULE_MEETING, new ArrayList<>(List.of(String.valueOf(sId), String.valueOf(mId), type)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    break;
                }
            }
        }
    }

    void handleViewByWeek(int sId, String beginDate, String endDate) throws IOException {
//        /STUDENT_VIEW_MEETING_BY_WEEK student_id  begin_date end_date
        request = createRequest(STUDENT_VIEW_MEETING_BY_WEEK, new ArrayList<>(List.of(String.valueOf(sId), beginDate, endDate)));
        out.println(request);

        while (true) {
            response = in.readLine();
            if (response != null) {
                System.out.println(response);

                String[] info = response.split(DELIMITER);
                if (info[0].contains("SUCCESS")) {
                    ArrayList<Meeting> meetings = extractMeetingsFromResponse(response);
                    System.out.println(meetings.size());
                    break;
                }
            }
        }
    }


    public static void main(String[] args) throws UnknownHostException {
        Handler handler = new Handler(InetAddress.getLocalHost(), ConstantValue.PORT);
        handler.run();
    }
}
