package com.calendlygui.database;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.model.ErrorMessage;
import com.calendlygui.model.Outcome;
import com.calendlygui.model.Response;
import com.calendlygui.model.User;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Objects;

public class Authenticate {
    public static Response register(String email, String name, String password, boolean gender, boolean isTeacher) {
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
//                return new Outcome(new ErrorMessage(RegisterMessage.REGISTER_EMAIL_EXIST));
                return new Response(2, new ErrorMessage(RegisterMessage.REGISTER_EMAIL_EXIST));
            }
            return new Response(3, new ErrorMessage(e.getMessage()));
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
            return new Response(3, new ErrorMessage(e.getMessage()));
        }
//        return new Outcome(new User(name, email, registerDatetime, isTeacher, gender, "register"));
        return new Response(0, new User(name, email, registerDatetime, isTeacher, gender, "register"));
    }

    public static Response signIn(String email, String password) {
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
            if (Objects.equals(hash, null))
                return new Response(2, new ErrorMessage(LoginMessage.LOGIN_EMAIL_NOT_EXIST));
        } catch (SQLException e) {
            return new Response(3, new ErrorMessage(e.getMessage()));
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
                return new Response(3, new ErrorMessage(e.getMessage()));
            }
        }
        else{
            return new Response(2, new ErrorMessage(LoginMessage.LOGIN_PASSWORD_NOT_MATCH));
//            return new Outcome(new ErrorMessage(LoginMessage.LOGIN_PASSWORD_NOT_MATCH));
        }
//        return new Outcome(new User(username, email, registerDatetime, isTeacher, gender, "register"));
        return new Response(0, new User(username, email, registerDatetime, isTeacher, gender, "register"));
    }

    public static void Logout(User user){

    }
}
