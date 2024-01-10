package com.calendlygui.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.createRequest;

public class SendData {

    private static String response;
    private static String request;
    private static ArrayList<String> data = new ArrayList<>();

    public static void handleLogin(BufferedReader in, PrintWriter out, String account, String password) throws IOException {
        request = createRequest(LOGIN, new ArrayList<>(List.of(account, password)));
        out.println(request);
        //listen to response
//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(DELIMITER);
//                if (info[0].contains(SUCCESS)) System.out.println("Navigate to home screen");
//                break;
//            }
//        }
    }

    public static void handleRegister(BufferedReader in, PrintWriter out, String username, String email, String password, boolean isMale, boolean isTeacher) throws IOException, ClassNotFoundException {
        data.clear();
        data.add(username);
        data.add(email);
        data.add(password);
        data.add(isMale ? "true" : "false");
        data.add(isTeacher ? "true" : "false");
        request = createRequest(REGISTER, data);
        out.println(request);

//        while (true) {
//            response = in.readLine();
//            if (response != null) {
//                System.out.println("Response: " + response);
//                String[] info = response.split(DELIMITER);
//                if (info[0].contains(SUCCESS)) System.out.println("Navigate to home screen");
//                break;
//            }
//        }
    }
}
