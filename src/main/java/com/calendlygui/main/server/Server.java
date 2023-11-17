package com.calendlygui.main.server;

import com.calendlygui.constant.ConstantValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server;
    private boolean done = false;
    private ExecutorService pool;
    private int port;

    public Server(int port) {
        this.port = port;
        connections = new ArrayList<>();
        done = false;
    }

    public void run() {
        try {
            server = new ServerSocket(this.port);
            pool = Executors.newCachedThreadPool();

            while(!done) {
                Socket client = server.accept();
                ConnectionHandler handler = new ConnectionHandler(client);
                this.connections.add(handler);
                this.pool.execute(handler);
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
            for(ConnectionHandler ch : connections){
                ch.shutdown();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    class ConnectionHandler implements Runnable {
        private Socket client;
        ObjectOutputStream outObject;
        private BufferedReader in;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        public void run() {
            try {
                this.outObject = new ObjectOutputStream(this.client.getOutputStream());
                this.in = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
                this.outObject.writeObject("Login or signup");

                String message;
                while((message = this.in.readLine()) != null) {
                    if (message.startsWith("/register ")) {
                        Manipulate.register(message, outObject);
                    } else if (message.startsWith("/login ")) {
                        Manipulate.signIn(message, outObject);
                    } else if (message.equals("/quit")) {
                        System.out.println("Someone quit server");
                        outObject.writeObject("Quit successfully");
                    } else {
                        System.out.println("Someone try to connect by incorrect command format");
                        outObject.writeObject("Check out your command format");
                    }
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
    }

    public static void main(String[] args) {
        System.out.println("Start listening to client ...");
        Server server = new Server(ConstantValue.PORT);
        server.run();
    }
}