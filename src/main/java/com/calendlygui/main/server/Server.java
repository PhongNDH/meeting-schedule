package com.calendlygui.main.server;

import com.calendlygui.constant.ConstantValue;

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
        public static ObjectOutputStream outObject;
        private BufferedReader in;
        public static PrintWriter out;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                out = new PrintWriter(this.client.getOutputStream(), true);
                outObject = new ObjectOutputStream(this.client.getOutputStream());
                this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
                outObject.writeObject("Server connected");

                String request;
                while ((request = this.in.readLine()) != null) {
                    handleRequest(request);
                }
            } catch (IOException e) {
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
        private void handleRequest(String request) throws IOException {
            System.out.println("Request: " + request);
            if (request.startsWith("/REGISTER"))                        Manipulate.register(request);
            else if (request.startsWith("/LOGIN"))                      Manipulate.signIn(request);
            else if (request.startsWith("/ADDSLOT"))                    Manipulate.addSlot(request);
            else if (request.startsWith("/TEACHER_CREATE_MEETING"))     Manipulate.createMeeting(request);
            else if (request.equals("/quit")) {
                System.out.println("Someone quit server");
                outObject.writeObject("Quit successfully");
            } else {
                System.out.println("Someone try to connect by incorrect command format");
                outObject.writeObject("Check out your command format");
            }
        }
    }


    public static void main(String[] args) {
        System.out.println("Start listening to client ...");
        Server server = new Server(ConstantValue.PORT);
        server.run();
    }
}