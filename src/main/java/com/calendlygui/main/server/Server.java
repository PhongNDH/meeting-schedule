package com.calendlygui.main.server;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.Request;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private final ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done = false;
    private final int port;
    private ObjectInputStream inObject;

    public Server(int port) {
        this.port = port;
        connections = new ArrayList<>();
        done = false;
    }

    public void run() {
        try {
            server = new ServerSocket(this.port);
            ExecutorService pool = Executors.newCachedThreadPool();

            while (!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                this.connections.add(handler);
                pool.execute(handler);
            }

        } catch (IOException e) {
            this.shutdown();
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        try {
            this.done = true;
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    static class ConnectionHandler implements Runnable {
        private final Socket client;
        ObjectInputStream inObject;
        ObjectOutputStream outObject;
        private BufferedReader in;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                this.inObject = new ObjectInputStream(this.client.getInputStream());
                this.outObject = new ObjectOutputStream(this.client.getOutputStream());
                this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));

                Request message;
                boolean isListening = true;
                while (isListening) {
                    Object object = this.inObject.readObject();
                    message = (Request) object;
                    System.out.println("Server received: " + message);

                    switch (message.getMethod()){
                        case "REGISTER": {
                            Manipulate.register(message.getBody(), outObject);
                            break;
                        }
                        case "LOGIN": {
                            Manipulate.signIn(message.getBody(), outObject);
                            break;
                        }
                        case "QUIT": {
                            System.out.println("Someone quit server");
                            outObject.writeObject("Quit successfully");
                            isListening = false;
                            break;
                        }
                        default: {
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                this.shutdown();
            }

        }

        public void shutdown() {
            try {
                in.close();
                outObject.close();
                if (!this.client.isClosed()) {
                    client.close();
                }

            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Start listening to client ...");
        Server server = new Server(ConstantValue.PORT);
        server.run();
    }
}