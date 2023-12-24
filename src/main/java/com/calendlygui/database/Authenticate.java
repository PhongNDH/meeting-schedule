package com.calendlygui.database;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.main.server.Manipulate;
import com.calendlygui.model.ErrorMessage;
import com.calendlygui.model.Outcome;
import com.calendlygui.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.RegisterMessage.REGISTER_EMAIL_EXIST;

public class Authenticate {
    public static String register(String email, String name, String password, boolean gender, boolean isTeacher) {
        Connection conn = SqlConnection.connect();
        String query1 = "insert into users(name,email,gender, is_teacher) values (? ,?, ?, ?) returning register_datetime";
        String query2 = "insert into login(email, password) values (? ,?)";
        Timestamp registerDatetime = null;
        // Update users table
        try {
            PreparedStatement ps = conn.prepareStatement(query1);
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setBoolean(3, gender);
            ps.setBoolean(4, isTeacher);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                registerDatetime = rs.getTimestamp("register_datetime");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return Manipulate.createResponse("FAIL", SQL_ERROR, new ArrayList<>(List.of(REGISTER_EMAIL_EXIST)));
            }
            return Manipulate.createResponse("FAIL", SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }

        // Update login table
        try {
            PreparedStatement ps2 = conn.prepareStatement(query2);
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            ps2.setString(1, email);
            ps2.setString(2, hash);
            ps2.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return Manipulate.createResponse("FAIL", SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
//        return new Outcome(new User(name, email, registerDatetime, isTeacher, gender, "register"));
        return Manipulate.createResponse("SUCCESS", "Register successfully", new ArrayList<>(List.of(name, email, registerDatetime.toString(), isTeacher ? "true" : "false", gender ? "male" : "female")));
    }

    public static String signIn(String email, String password) {
        Connection conn = SqlConnection.connect();
        String query = "select password from login where email = ?";
        String query2 = "select * from users where email = ?";
        String hash = null;
        String username = null;
        boolean gender = false, isTeacher = false;
        Timestamp registerDatetime = null;
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                hash = rs.getString("password");
            }
            if (Objects.equals(hash, null)) {
                return Manipulate.createResponse("FAIL", SERVERSIDE_ERROR, new ArrayList<>(List.of(LOGIN_EMAIL_NOT_EXIST)));
            }
        } catch (SQLException e) {
            return Manipulate.createResponse("FAIL", SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
        }
        if (BCrypt.checkpw(password, hash)) {
            try {
                PreparedStatement ps2 = conn.prepareStatement(query2);
                ps2.setString(1, email);
                ResultSet rs2 = ps2.executeQuery();
                while (rs2.next()) {
                    username = rs2.getString("name");
                    gender = rs2.getBoolean("gender");
                    isTeacher = rs2.getBoolean("is_teacher");
                    registerDatetime = rs2.getTimestamp("register_datetime");
                }
            } catch (SQLException e) {
                return Manipulate.createResponse("FAIL", SQL_ERROR, new ArrayList<>(List.of(e.getMessage())));
            }
        } else {
            return Manipulate.createResponse("FAIL", SERVERSIDE_ERROR, new ArrayList<>(List.of(LOGIN_PASSWORD_NOT_MATCH)));
        }
//        return new Outcome(new User(username, email, registerDatetime, isTeacher, gender, "register"));
        ArrayList<String> userData = new ArrayList<>(List.of(username, email, registerDatetime.toString(), isTeacher ? "true" : "false", gender ? "male" : "famale"));
        return Manipulate.createResponse("SUCCESS", LOGIN_SUCCESS, userData);
    }
}
