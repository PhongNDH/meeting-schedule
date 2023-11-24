package com.calendlygui.main.server;

import com.calendlygui.constant.LoginMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.database.Authenticate;
import com.calendlygui.model.ErrorMessage;
import com.calendlygui.model.Response;
import com.calendlygui.utils.Validate;
import com.calendlygui.model.Outcome;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import static com.calendlygui.main.server.Server.ConnectionHandler.outObject;

public class Manipulate {
    public static void register(ArrayList<String> body) throws IOException {
        System.out.println("Someone try to register ...");
        if (body.size() == 5) {
//            if ((!body.get(3).equals("false") && !body.get(3).equals("true") || !body.get(4).equals("false") && !body.get(4).equals("true")) && !Validate.checkEmailFormat(body.get(0))) {
//                System.out.println("Someone try to connect by incorrect command format");
//                outObject.writeObject("Register format is wrong");
//            } else {
                String email = body.get(0);
                String username = body.get(1);
                String password = body.get(2);
                boolean gender = body.get(3).equals("true");
                boolean isTeacher = body.get(4).equals("true");
                Response result = Authenticate.register(email, username, password, gender, isTeacher);
                outObject.writeObject(result);
//                if (result == null) {
//                    System.out.println("Someone fails at register");
//                    outObject.writeObject(RegisterMessage.REGISTER_SERVER_WRONG);
//                } else if (result.getUser() == null) {
//                    if (result.getError().getContent().equals(RegisterMessage.REGISTER_EMAIL_EXIST)) {
//                        System.out.println("Someone fails at register because of using existent email");
//                        outObject.writeObject(RegisterMessage.REGISTER_EMAIL_EXIST);
//                    }
//                } else {
//                    System.out.println("Someone register successfully with email " + email);
//                    outObject.writeObject(result.getUser());
//                    outObject.writeObject(RegisterMessage.REGISTER_SUCCESS);
//                }
//            }
        } else {
            System.out.println("Someone try to connect by incorrect command format");
            outObject.writeObject("Check out your command format");
        }

    }

    public static void signIn(ArrayList<String> body) throws IOException {
        System.out.println("Body size: " + body.size());
        if (body.size() == 2) {
            String email = body.get(0);
            String password = body.get(1);
            System.out.println("email: " + email + " password: " + password);
            Response result = Authenticate.signIn(email, password);
            outObject.writeObject(result);

//            if (result == null) {
//                System.out.println("Someone fails at sign in");
//                outObject.writeObject(LoginMessage.LOGIN_SERVER_WRONG);
//            } else if (result.getUser() == null) {
//                if (result.getError().getContent().equals(LoginMessage.LOGIN_EMAIL_NOT_EXIST)) {
//                    System.out.println("Someone fails at sign in because of using email that does not exist");
//                    outObject.writeObject(LoginMessage.LOGIN_EMAIL_NOT_EXIST);
//                }
//
//                if (result.getError().getContent().equals(LoginMessage.LOGIN_PASSWORD_NOT_MATCH)) {
//                    System.out.println("Someone fails at sign in because password does not match");
//                    outObject.writeObject(LoginMessage.LOGIN_PASSWORD_NOT_MATCH);
//                }
//            } else {
//                System.out.println("Someone sign in successfully with email " + email);
//                outObject.writeObject(result.getUser());
//                outObject.writeObject(LoginMessage.LOGIN_SUCCESS);
//            }
        } else {
            System.out.println("Someone try to connect by incorrect command format");
            outObject.writeObject(new Response(1, new ErrorMessage("Incorrect command format")));
        }
    }

    public static void addSlot(String message, ObjectOutputStream outObject) {
        String[] data = message.split(" ");

    }
}