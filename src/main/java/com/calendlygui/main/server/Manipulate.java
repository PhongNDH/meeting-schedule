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
import java.time.LocalTime;

public class Manipulate {
    public static void register(String message, ObjectOutputStream outObject) throws IOException {
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

                Response result = Authenticate.register(email, username, password, gender, isTeacher);
                outObject.writeObject(result);
//                if (result.getCode() != 0) {
//                    System.out.println("Someone fails at register");
//                    outObject.writeObject(RegisterMessage.REGISTER_SERVER_WRONG);
//                } else if (result.getUser() == null) {
//                    if (result.getError().getContent().equals(RegisterMessage.REGISTER_EMAIL_EXIST)) {
//                        System.out.println("Someone fails at register because of using existed email");
//                        outObject.writeObject(RegisterMessage.REGISTER_EMAIL_EXIST);
//                    }
//                } else {
//                    System.out.println("Someone register successfully with email " + email);
//                    outObject.writeObject(result.getUser());
//                    outObject.writeObject(RegisterMessage.REGISTER_SUCCESS);
//                }
            }
        } else {
            System.out.println("Incorrect command format or error. Try again");
            outObject.writeObject(new Response(1, LocalTime.now(), new ErrorMessage("Incorrect command format")));
        }

    }

    public static void signIn(String message, ObjectOutputStream outObject) throws IOException {
        String[] loginInfo = message.split(" ", 3);
        if (loginInfo.length == 3) {
            String email = loginInfo[1];
            String password = loginInfo[2];
            Response result = Authenticate.signIn(email, password);
            outObject.writeObject(result);
//            if (result.getCode() != 0) {
//                String error = result.getError().getContent();
//                System.out.println(error);
//                outObject.writeObject(error);
//            } else {
//                System.out.println("Someone sign in successfully with email " + email);
//                outObject.writeObject(result.getUser());
//                outObject.writeObject(LoginMessage.LOGIN_SUCCESS);
//            }
        } else {
            System.out.println("Someone try to connect by incorrect command format");
            outObject.writeObject(new Response(1, LocalTime.now(), new ErrorMessage("Incorrect command format")));
        }
    }

    public static void addSlot(String message, ObjectOutputStream outObject) {
        String[] data = message.split(" ");

    }
}