package com.calendlygui.main.client;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.ErrorMessage;
import com.calendlygui.model.Request;
import com.calendlygui.model.Response;
import com.calendlygui.model.User;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class Client implements Runnable {
    private Socket client;
    private ObjectOutputStream outObject;
    private ObjectInputStream inObject;
    private PrintWriter out;
    BufferedReader inReader;
    private final int port;
    private final InetAddress host;
    private boolean done;
    private User user;

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
            outObject = new ObjectOutputStream(this.client.getOutputStream());
            inObject = new ObjectInputStream(this.client.getInputStream());
            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();

            Object responseObject;
            Response response = null;
            while (true) {
                responseObject = inObject.readObject();
                if(responseObject instanceof Response) response = (Response) responseObject;
                System.out.println("Response received : " + response);

                if(response != null) {
                    switch (response.getCode()) {
                        case 0: {
                            System.out.println("SUCCESS");
                            System.out.println(((User) response.getBody()).getUsername());
                            break;
                        }
                        case 1: {
                            System.out.println("CLIENT ERROR");
                            System.out.println(((ErrorMessage) response.getBody()).getContent());
                            break;
                        }
                        case 2: {
                            System.out.println("SERVER ERROR");
                            System.out.println(((ErrorMessage) response.getBody()).getContent());
                            break;
                        }
                        case 3: {
                            System.out.println("SQL ERROR");
                            System.out.println(((ErrorMessage) response.getBody()).getContent());
                            break;
                        }
                        default: {
                            System.out.println("UNKNOWN ERROR");
                            System.out.println(((ErrorMessage) response.getBody()).getContent());
                            break;
                        }
                    }
                }
//                if (receivedData instanceof String receivedString) {
//                    System.out.println(receivedString);
//                    if (receivedString.equals("Quit successfully")) {
//                        this.shutdown();
//                        return;
//                    }
//                } else if (receivedData instanceof User) {
//                    user = (User) receivedData;
//                    System.out.println(user);
//                }
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

//        INSERT INTO your_table_name (timestamp_column_name, other_column1, other_column2, ...)
//        VALUES (TIMESTAMP '2023-11-20 12:34:56', 'value1', 'value2', ...);

        //this line is new

        public void run() {
            String[] data;
            Request request = null;
            ArrayList<String> body = new ArrayList<>();
            while (!done) {
                try {
                    String message = inReader.readLine();
                    System.out.println("Message: " + message);
                    data = message.split(" ");
                    System.out.println("Data length: " + data.length);
                    if(message.contains("login")){
                        body.clear();
                        body.addAll(Arrays.asList(data).subList(1, 3));
                        request = new Request("LOGIN", body);
                    } else if (message.contains("register")) {
                        body.clear();
                        body.addAll(Arrays.asList(data).subList(1, 6));
                        request = new Request("REGISTER", body);
                    } else if (message.contains("quit")) {
                        done = true;
                        request = new Request("QUIT");
                        break;
                    }
                    outObject.writeObject(request);
                } catch (IOException e) {
                    shutdown();
                }
            }
        }
    }

    public static void main(String[] args) throws UnknownHostException {
        Client client = new Client(InetAddress.getLocalHost(), ConstantValue.PORT);
        client.run();
    }
}
