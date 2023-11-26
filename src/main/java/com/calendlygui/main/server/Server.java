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
        static ObjectInputStream inObject;
        static ObjectOutputStream outObject;
        private BufferedReader in;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                inObject = new ObjectInputStream(this.client.getInputStream());
                outObject = new ObjectOutputStream(this.client.getOutputStream());
                this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));

//                outObject.writeObject("Server connected");

                Request request;
                while (true){
                    request = (Request) inObject.readObject();
                    System.out.println("Server received: " + request);
                    switch (request.getMethod()){
                        case "LOGIN": {
                            System.out.println("LOGGING IN");
                            Manipulate.signIn(request.getBody());
                            break;
                        }
                        case "REGISTER": {
                            System.out.println("REGISTERING");
                            Manipulate.register(request.getBody());
                            break;
                        }
                        case "QUIT": {
                            outObject.writeObject("Quit successfully");
                            System.out.println("QUITTING");
                            break;
                        }
                        default: {
                            System.out.println(request);
                            break;
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                this.shutdown();
            }
        }

        public void shutdown() {
            try {
                if(in != null){
                    in.close();
                }
                if(outObject != null){
                    outObject.close();
                }
                if(inObject != null){
                    inObject.close();
                }
                if (!client.isClosed()) {
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