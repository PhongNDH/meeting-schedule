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

public class Client implements Runnable {
    private Socket client;
    private ObjectInputStream inObject;
    private PrintWriter out;
    BufferedReader inReader;
    private int port;
    private InetAddress host;
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
            inObject = new ObjectInputStream(this.client.getInputStream());
            InputHandler inputHandler = new InputHandler();
            Thread t = new Thread(inputHandler);
            t.start();

            Object receivedData;
            while (true) {
                receivedData = inObject.readObject();
                System.out.println("Object : "+receivedData);
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
        public void run() {
            while (!done) {
                try {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        out.println(message);
                    } else if (user != null && (message.startsWith("/login ") || message.startsWith("/register"))) {
                        System.out.println("You have signed in, please logout to login again");

                    } else {
                        out.println(message);
                    }
                } catch (IOException var3) {
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
