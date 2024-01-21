package com.calendlygui.main.server;

import com.calendlygui.constant.ConstantValue;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.calendlygui.constant.ConstantValue.*;

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
                connections.add(handler);
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
                out = new PrintWriter(client.getOutputStream(), true, StandardCharsets.UTF_8);
                outObject = new ObjectOutputStream(client.getOutputStream());
                in = new BufferedReader(new InputStreamReader(client.getInputStream(), StandardCharsets.UTF_8));

                String request;
                while ((request = in.readLine()) != null) {
                    processRequest(request);
                }
            } catch (IOException | RuntimeException | ParseException e) {
                this.shutdown();
            }

        }

        public void shutdown() {
            try {
                if(in != null){
                    in.close();
                }
                if(out != null){
                    out.close();
                }
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
        private void processRequest(String request) throws IOException, ParseException {
            System.out.println("Request: " + request);
            String[] data = request.split(COMMAND_DELIMITER);
            if (data[0].contains(REGISTER))                             Manipulate.register(data, out);
            else if (data[0].contains(LOGIN))                           Manipulate.signIn(data, out);
            else if (data[0].contains(TEACHER_CREATE_MEETING))          Manipulate.createMeeting(data);
            else if (data[0].contains(TEACHER_EDIT_MEETING))            Manipulate.editMeeting(data);
            else if (data[0].contains(TEACHER_VIEW_MEETING_BY_DATE))    Manipulate.viewByDate(data);
            else if (data[0].contains(TEACHER_VIEW_MEETING))            Manipulate.viewUnscheduledAndHappeningMeetings(data);
            else if (data[0].contains(TEACHER_ENTER_CONTENT))           Manipulate.addMinute(data);
            else if (data[0].contains(TEACHER_VIEW_HISTORY))            Manipulate.viewHistory(data);

            else if (data[0].contains(STUDENT_VIEW_TIMESLOT))           Manipulate.viewAvailableSlots(data);
            else if (data[0].contains(STUDENT_SCHEDULE_MEETING))        Manipulate.scheduleMeeting(data);
            else if (data[0].contains(STUDENT_VIEW_MEETING_BY_WEEK))    Manipulate.viewByWeek(data);
            else if (data[0].contains(STUDENT_CANCEL_MEETING))          Manipulate.cancelMeeting(data);
            else if (data[0].contains(STUDENT_VIEW_SCHEDULED))          Manipulate.viewScheduled(data);

            else if (request.equals("/" + QUIT))                        Manipulate.quit();
            else {
                // String error = createResponse(FAIL, CLIENTSIDE_ERROR, new ArrayList<>(List.of(INCORRECT_FORMAT)));
                out.println(INCORRECT_FORMAT);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("Start listening to client ...");
        Server server = new Server(ConstantValue.PORT);
        server.run();
    }
}