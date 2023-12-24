package com.calendlygui.main.client;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.DELIMINATOR;

public class Client implements Runnable {
    private Socket client;
    private ObjectInputStream inObject;
    private PrintWriter out;
    private BufferedReader in;
    BufferedReader inReader;
    private final int port;
    private final InetAddress host;
    private boolean done;
    private User user;

    private String request;
    ArrayList<String> data = new ArrayList<>();

    public Client(InetAddress host, int port) {
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

            Object receivedData;
            while (true) {
//                String response = in.readLine();
//                System.out.println(response);
                receivedData = inObject.readObject();
                System.out.println("Object : " + receivedData);
                if (receivedData instanceof String receivedString) {
                    System.out.println(receivedString);
                    if (receivedString.equals("Quit successfully")) {
                        this.shutdown();
                        return;
                    }
                } else if (receivedData instanceof User) {
                    user = (User) receivedData;
                    System.out.println(user);
                }
            }

        } catch (ClassNotFoundException | IOException e) {
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
        String[] promts = {
                "/login nguyendai060703@gmail.com 111111",
                "/addslot "
        };

//        INSERT INTO your_table_name (timestamp_column_name, other_column1, other_column2, ...)
//        VALUES (TIMESTAMP '2023-11-20 12:34:56', 'value1', 'value2', ...);

        //this line is new

        public void run() {
            while (!done) {
                try {
                    String message = inReader.readLine();
                    switch (message) {
                        case "1": {
                            handleLogin("nguyendai060703@gmail.com", "111111");
                            break;
                        }
                        case "2": {
                            handleRegister(
                                    "a2@gmail.com",
                                    "111111",
                                    "111111",
                                    true,
                                    true
                            );
                            break;
                        }
                        case "3": {
                            handleCreateMeeting(
                                    "Checkpoint1",
                                    "06/07/2003",
                                    "07:00",
                                    "07:20",
                                    "Individual",
                                    "1",
                                    "1");
                            break;
                        }
                        default: {
                        }
                    }

                } catch (IOException var3) {
                    shutdown();
                }
            }
        }
    }

    String createRequest(String command, ArrayList<String> data) {
        StringBuilder request = new StringBuilder();
        request.append("/").append(command);
        for (String _data : data) request.append(DELIMINATOR).append(_data);

        return request.toString();
    }

    void handleCreateMeeting(String name, String dateTime, String begin, String end, String type, String tId, String slot) {
        request = createRequest(
                "TEACHER_CREATE_MEETING",
                new ArrayList<>(List.of(
                        name,
                        dateTime,
                        begin,
                        end,
                        type,
                        tId,
                        slot
                )));
        out.println(request);

        while (true) {
            //        String response = "/SUCCESS;Login successfully;dainn;nguyendai060703@gmail.com;2023-11-18 15:35:22.924218;true;male";
            String response = "/FAIL;SQL_CONNECTION;SOME STUPID ERROR";
            if (response != null) {
                String[] info = response.split(DELIMINATOR);
                if (info[0].contains("SUCCESS")) System.out.println("SHOW SOMETHING");
                else System.out.println("Error: " + info[1] + " " + info[2]);
                break;
            }
        }
    }

    void handleLogin(String account, String password) {
        data.clear();
        data.add(account);
        data.add(password);
        request = createRequest("LOGIN", data);
        out.println(request);

        //listen to changes
        while (true) {
            //        String response = "/SUCCESS;Login successfully;dainn;nguyendai060703@gmail.com;2023-11-18 15:35:22.924218;true;male";
            String response = "/FAIL;SQL_CONNECTION;MOTHERFUC";
            if (response != null) {
                String[] info = response.split(DELIMINATOR);
                if (info[0].contains("SUCCESS")) System.out.println("Navigate to home screen");
                else System.out.println("Error: " + info[1] + " " + info[2]);
                break;
            }
        }
    }

    void handleRegister(String username, String email, String password, boolean isMale, boolean isTeacher) {
        data.clear();
        data.add(username);
        data.add(email);
        data.add(password);
        data.add(isMale ? "male" : "female");
        data.add(isTeacher ? "true" : "false");
        request = createRequest("REGISTER", data);
        out.println(request);

//        String response = "/FAIL;SQL Connection Error;Email has existed";
        while (true) {
            String response = "/SUCCESS;Register successfully;111111;a2@gmail.com;2023-12-25 03:16:41.495112;true;female";
            if (response != null) {
                String[] info = response.split(DELIMINATOR);
                if (info[0].contains("SUCCESS")) System.out.println("Navigate to home screen");
                else System.out.println("Error: " + info[1] + " " + info[2]);
                break;
            }
        }
    }

    //teacher
    void handleEditSlot() {
    }

    void handleViewByDate() {
    }

    void handleAddMinute() {
    }

    void handleViewPastMeetings() {
    }

    //student
    void handleViewSlots() {
    }


    public static void main(String[] args) throws UnknownHostException {
        Client client = new Client(InetAddress.getLocalHost(), ConstantValue.PORT);
        client.run();
    }
}
