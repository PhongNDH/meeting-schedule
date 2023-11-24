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
import java.util.List;

public class Manipulate {
    public static void register(List<String> data, ObjectOutputStream outObject) throws IOException {
        System.out.println("Someone try to register ...");
        if (data.size() == 5) {
//            if (
//                    (!loginInfo[4].equals("false") && !loginInfo[4].equals("true") || !loginInfo[5].equals("false") && !loginInfo[5].equals("true"))
//                            && !Validate.checkEmailFormat(loginInfo[1])) {
//                System.out.println("Someone try to connect by incorrect command format");
//                outObject.writeObject("Register format is wrong");
//            } else {
                String email = data.get(0);
                String username = data.get(1);
                String password = data.get(2);
                boolean gender = data.get(3).equals("true");
                boolean isTeacher = data.get(4).equals("true");

                Response result = Authenticate.register(email, username, password, gender, isTeacher);
                outObject.writeObject(result);
//            }
        } else {
            System.out.println("Incorrect command format or error. Try again");
            outObject.writeObject(new Response(1, LocalTime.now(), new ErrorMessage("Incorrect command format")));
        }

    }

    public static void signIn(List<String> data, ObjectOutputStream outObject) throws IOException {
        if (data.size() == 2) {
            String email = data.get(0);
            String password = data.get(1);
            Response result = Authenticate.signIn(email, password);
            outObject.writeObject(result);
        } else {
            System.out.println("Someone try to connect by incorrect command format");
            outObject.writeObject(new Response(1, LocalTime.now(), new ErrorMessage("Incorrect command format")));
        }
    }

    public static void addSlot(String message, ObjectOutputStream outObject) {
        String[] data = message.split(" ");

    }
}