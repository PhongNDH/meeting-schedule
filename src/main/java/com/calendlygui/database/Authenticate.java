package com.calendlygui.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.RegisterMessage.REGISTER_EMAIL_EXIST;
import static com.calendlygui.utils.Helper.createResponse;
import static com.calendlygui.utils.Helper.createResponseWithUser;

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
                return String.valueOf(ACCOUNT_EXIST);
            }
            return String.valueOf(SQL_ERROR);
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
            return String.valueOf(SQL_ERROR);
        }
//        return new Outcome(new User(name, email, registerDatetime, isTeacher, gender, "register"));
        return createResponseWithUser(
                OPERATION_SUCCESS,
                name,
                email,
                registerDatetime.toString(),
                String.valueOf(isTeacher),
                String.valueOf(gender));
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
                return String.valueOf(ACCOUNT_NOT_EXIST);
            }
        } catch (SQLException e) {
            return String.valueOf(SQL_ERROR);
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
                return String.valueOf(SQL_ERROR);
            }
        } else {
            return String.valueOf(INVALID_PASSWORD);
        }

        return createResponseWithUser(OPERATION_SUCCESS, username, email, registerDatetime.toString(), String.valueOf(isTeacher), String.valueOf(gender));
    }
}
