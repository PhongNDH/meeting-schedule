package com.calendlygui.main.server;

import com.calendlygui.constant.LoginMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.database.Authenticate;
import com.calendlygui.database.SqlConnection;
import com.calendlygui.model.ErrorMessage;
import com.calendlygui.utils.Validate;
import com.calendlygui.model.Outcome;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.calendlygui.main.server.Server.ConnectionHandler.outObject;


public class Manipulate {
    private static Connection conn = SqlConnection.connect();

    public static void register(String message) throws IOException {
        System.out.println("Someone try to register ...");
        String[] loginInfo = message.split(" ", 6);
        if (loginInfo.length == 6) {
            if ((!loginInfo[4].equals("false") && !loginInfo[4].equals("true") || !loginInfo[5].equals("false") && !loginInfo[5].equals("true")) && !Validate.checkEmailFormat(loginInfo[1])) {
                System.out.println("Someone try to connect by incorrect command format");
                outObject.writeObject("Register format is wrong");
            } else {
                String email = loginInfo[1];
                String username = loginInfo[2];
                String password = loginInfo[3];
                boolean gender = loginInfo[4].equals("true");
                boolean isTeacher = loginInfo[5].equals("true");
                Outcome result = Authenticate.register(email, username, password, gender, isTeacher);
                if (result == null) {
                    System.out.println("Someone fails at register");
                    outObject.writeObject(RegisterMessage.REGISTER_SERVER_WRONG);
                } else if (result.getUser() == null) {
                    if (result.getError().getContent().equals(RegisterMessage.REGISTER_EMAIL_EXIST)) {
                        System.out.println("Someone fails at register because of using existent email");
                        outObject.writeObject(RegisterMessage.REGISTER_EMAIL_EXIST);
                    }
                } else {
                    System.out.println("Someone register successfully with email " + email);
                    outObject.writeObject(result.getUser());
                    outObject.writeObject(RegisterMessage.REGISTER_SUCCESS);
                }
            }
        } else {
            System.out.println("Someone try to connect by incorrect command format");
            outObject.writeObject("Check out your command format");
        }

    }

    public static void signIn(String message) throws IOException {
        String[] loginInfo = message.split(" ", 3);
        if (loginInfo.length == 3) {
            String email = loginInfo[1];
            String password = loginInfo[2];
            Outcome result = Authenticate.signIn(email, password);
            if (result == null) {
                System.out.println("Someone fails at sign in");
                outObject.writeObject(LoginMessage.LOGIN_SERVER_WRONG);
            } else if (result.getUser() == null) {
                if (result.getError().getContent().equals(LoginMessage.LOGIN_EMAIL_NOT_EXIST)) {
                    System.out.println("Someone fails at sign in because of using email that does not exist");
                    outObject.writeObject(LoginMessage.LOGIN_EMAIL_NOT_EXIST);
                }

                if (result.getError().getContent().equals(LoginMessage.LOGIN_PASSWORD_NOT_MATCH)) {
                    System.out.println("Someone fails at sign in because password does not match");
                    outObject.writeObject(LoginMessage.LOGIN_PASSWORD_NOT_MATCH);
                }
            } else {
                System.out.println("Someone sign in successfully with email " + email);
                outObject.writeObject(result.getUser());
                outObject.writeObject(LoginMessage.LOGIN_SUCCESS);
            }
        } else {
            System.out.println("Someone try to connect by incorrect command format");
            outObject.writeObject("Check out your command format");
        }
    }

    public static void addSlot(String message) {
        String[] data = message.split(" ");
    }

    public static Outcome createMeeting(String request) throws IOException {
        String[] data = request.split(" ");
        System.out.print("Data received to create new meeting: ");
        for(int i=1; i<data.length; i++) System.out.print(data[i] + " ");
        System.out.println();

        String insertQuery = "insert into meeting(name, occur, finish, teacher_id, slot) values (?, ?, ?, ?, ?) returning establish_datetime";
        try{
            PreparedStatement ps = conn.prepareStatement(insertQuery);
            String[] dateTimes = convertToDateTimeStrings(data[2], data[3], data[4]);
            ps.setString(1, data[1]);
            ps.setTimestamp(2, Timestamp.valueOf(dateTimes[0]));
            ps.setTimestamp(3, Timestamp.valueOf(dateTimes[1]));
            ps.setInt(4, Integer.parseInt(data[6]));
            ps.setInt(5, Integer.parseInt(data[7]));

            ResultSet rs = ps.executeQuery();
            Timestamp establishTime = null;
            while (rs.next())
                establishTime = rs.getTimestamp("establish_datetime");

            System.out.println(establishTime.toString());
            return new Outcome(new ErrorMessage(establishTime.toString()));

        } catch (SQLException e) {
            System.out.println("SQL Exception: " + e);
            throw new RuntimeException(e);
        }
    }

    public static String[] convertToDateTimeStrings(String date, String startTime, String endTime) {
        String pattern = "MM/dd/yyyy HH:mm";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);

        // Parse the input strings to LocalDateTime
        LocalDateTime startDateTime = LocalDateTime.parse(date + " " + startTime, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(date + " " + endTime, formatter);

        // Format the LocalDateTime objects to the desired format
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedStartTime = startDateTime.format(outputFormatter);
        String formattedEndTime = endDateTime.format(outputFormatter);

        return new String[]{formattedStartTime, formattedEndTime};
    }
}