package com.calendlygui.main.client;

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

public class Client implements Runnable {
    private Socket client;
    private ObjectInputStream inObject;
    private PrintWriter out;
    BufferedReader inReader;
    private int port;
    private InetAddress host;
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
            out = new PrintWriter(this.client.getOutputStream(), true);
            inReader = new BufferedReader(new InputStreamReader(System.in));
            inObject = new ObjectInputStream(this.client.getInputStream());
            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();

            Object receivedData;
            while (true) {
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
                        case "/LOGIN": {
                            handleLogin("nguyendai060703@gmail.com", "111111");
                            break;
                        }
                        case "/REGISTER": {
                            handleRegister("a1@gmail.com", "111111");
                            break;
                        }
                        case "/TEACHER_CREATE_MEETING": {

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
                        default: {}
                    }
                    out.println(request);
                } catch (IOException var3) {
                    shutdown();
                }
            }
        }
    }

    String createRequest(String command, ArrayList<String> data) {
        StringBuilder request = new StringBuilder();
        request.append(command);
        for (String _data : data) request.append(" ").append(_data);

        return request.toString();
    }

    void handleCreateMeeting(String name, String dateTime, String begin, String end, String type, String tId, String slot) {
        data.clear();
        data.add(name);
        data.add(dateTime);
        data.add(begin);
        data.add(end);
        data.add(type);
        data.add(tId);
        data.add(slot);
        request = createRequest("/TEACHER_CREATE_MEETING", data);
    }

    void handleLogin(String account, String password) {
        data.clear();
        data.add(account);
        data.add(password);
        request = createRequest("/LOGIN", data);
    }

    void handleRegister(String account, String password) {
        data.clear();
        data.add(account);
        data.add(password);
        request = createRequest("/REGISTER", data);
    }

    public static void main(String[] args) throws UnknownHostException {
        Client client = new Client(InetAddress.getLocalHost(), ConstantValue.PORT);
        client.run();
    }
}
