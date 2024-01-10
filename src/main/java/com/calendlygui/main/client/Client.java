//package com.calendlygui.main.client;
//
//import com.calendlygui.constant.ConstantValue;
//import com.calendlygui.model.entity.User;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.ObjectInputStream;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.UnknownHostException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static com.calendlygui.constant.ConstantValue.TEACHER_CREATE_MEETING;
//
//public class Client implements Runnable {
//    private Socket client;
//    private ObjectInputStream inObject;
//    private PrintWriter out;
//    private BufferedReader in;
//    BufferedReader inReader;
//    private final int port;
//    private final InetAddress host;
//    private boolean done;
//    private User user;
//
//    private String request;
//    ArrayList<String> data = new ArrayList<>();
//
//    public Client(InetAddress host, int port) {
//        this.port = port;
//        this.host = host;
//        this.user = null;
//    }
//
//    public void run() {
//        try {
//            client = new Socket(this.host, this.port);
//            in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
//            out = new PrintWriter(this.client.getOutputStream(), true);
//            inReader = new BufferedReader(new InputStreamReader(System.in));
//            inObject = new ObjectInputStream(this.client.getInputStream());
//
//            InputHandler inputHandler = new InputHandler();
//            Thread t = new Thread(inputHandler);
//            t.start();
//        } catch (
//                IOException e) {
//            this.shutdown();
//        }
//    }
//
//    private void shutdown() {
//        done = true;
//        try {
//            inReader.close();
//            inObject.close();
//            out.close();
//            if (!client.isClosed()) {
//                client.close();
//            }
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());
//        }
//    }
//
//    class InputHandler implements Runnable {
//        public void run() {
//            while (!done) {
//                //input from console
//                try {
//                    String message = inReader.readLine();
//                    switch (message) {
//                        case "1": {
//                            handleLogin("nguyendai060703@gmail.com", "111111");
//                            break;
//                        }
//                        case "2": {
//                            handleRegister(
//                                    "a2@gmail.com",
//                                    "111111",
//                                    "111111",
//                                    true,
//                                    true
//                            );
//                            break;
//                        }
//                        case "3": {
//                            handleCreateMeeting(
//                                    "Checkpoint1",
//                                    "2023-12-26",
//                                    "07:00",
//                                    "07:20",
//                                    "individual",
//                                    1);
//                            break;
//                        }
//                        case "4": {
////                            /TEACHER_EDIT_MEETING id  name  date  begin  end  classification
////                            handleEditMeeting();
//                        }
//                        default: {
//                        }
//                    }
//
//                } catch (IOException var3) {
//                    shutdown();
//                } catch (ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }
//
//    String createRequest(String command, ArrayList<String> data) {
//        StringBuilder request = new StringBuilder();
//        request.append("/").append(command);
//        for (String _data : data) request.append(DELIMITER).append(_data);
//
//        return request.toString();
//    }
//
//
//    void handleLogin(String account, String password) throws IOException, ClassNotFoundException {
//        request = createRequest("LOGIN", new ArrayList<>(List.of(account, password)));
//        out.println(request);
//
//        //listen to response
//        String response;
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(DELIMITER);
//                if (info[0].contains("SUCCESS")) System.out.println("Navigate to home screen");
//                else System.out.println("Error: " + info[1] + " " + info[2]);
//                break;
//            }
//        }
//    }
//
//    void handleRegister(String username, String email, String password, boolean isMale, boolean isTeacher) throws IOException, ClassNotFoundException {
//        data.clear();
//        data.add(username);
//        data.add(email);
//        data.add(password);
//        data.add(isMale ? "male" : "female");
//        data.add(isTeacher ? "true" : "false");
//        request = createRequest("REGISTER", data);
//        out.println(request);
//
//        //listen to response
//        String response;
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(DELIMITER);
//                if (info[0].contains("SUCCESS")) System.out.println("Navigate to home screen");
//                else System.out.println("Error: " + info[1] + " " + info[2]);
//                break;
//            }
//        }
//    }
//
//
//    //teacher
//    void handleCreateMeeting(String name, String dateTime, String begin, String end, String classification, int tId) throws IOException, ClassNotFoundException {
//        request = createRequest(
//                TEACHER_CREATE_MEETING,
//                new ArrayList<>(List.of(String.valueOf(tId), name, dateTime, begin, end, classification)));
//        out.println(request);
//
//        //listen to response
//        String response;
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(DELIMITER);
//                if (info[0].contains("SUCCESS")) System.out.println("SHOW SOMETHING");
//                else System.out.println("Error: " + info[1] + " " + info[2]);
//                break;
//            }
//        }
//    }
//
//    void handleEditMeeting(int id, String name, String dateTime, String begin, String end, String classification, String status, String selectedClassification) throws IOException, ClassNotFoundException {
//        request = createRequest("TEACHER_EDIT_MEETING",
//                new ArrayList<>(List.of(String.valueOf(id), name, dateTime, begin, end, classification, status, selectedClassification)));
//
//        String response;
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(DELIMITER);
//                if (info[0].contains("SUCCESS")) System.out.println("UPDATE DONE");
//                else System.out.println("Error: " + info[1] + " " + info[2]);
//                break;
//            }
//        }
//    }
//
//    void handleViewByDate() {
//    }
//
//    void handleAddMinute() {
//    }
//
//    void handleViewPastMeetings() {
//    }
//
//    //student
//    void handleViewSlots() {
//    }
//
//
//    public static void main(String[] args) throws UnknownHostException {
//        Client client = new Client(InetAddress.getLocalHost(), ConstantValue.PORT);
//        client.run();
//    }
//}
