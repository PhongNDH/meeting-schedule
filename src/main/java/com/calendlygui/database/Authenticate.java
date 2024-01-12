package com.calendlygui.database;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.Objects;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.createResponseWithUser;

public class Authenticate {
    public static String register(String email, String name, String password, boolean gender, boolean isTeacher) {
        Connection conn = SqlConnection.connect();
        String insertUserQuery = "insert into users(name,email,gender, is_teacher) values (? ,?, ?, ?) returning register_datetime";
        String insertLoginQuery = "insert into login(email, password) values (? ,?)";
        Timestamp registerDatetime = null;
        // Update users table
        try {
            PreparedStatement insertUserPs = conn.prepareStatement(insertUserQuery);
            insertUserPs.setString(1, name);
            insertUserPs.setString(2, email);
            insertUserPs.setBoolean(3, gender);
            insertUserPs.setBoolean(4, isTeacher);
            ResultSet insertUserRs = insertUserPs.executeQuery();
            while (insertUserRs.next()) {
                registerDatetime = insertUserRs.getTimestamp("register_datetime");
            }
        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                return String.valueOf(ACCOUNT_EXIST);
            }
            return String.valueOf(SQL_ERROR);
        }

        // Update login table
        try {
            PreparedStatement insertLoginPs = conn.prepareStatement(insertLoginQuery);
            String hash = BCrypt.hashpw(password, BCrypt.gensalt());
            insertLoginPs.setString(1, email);
            insertLoginPs.setString(2, hash);
            insertLoginPs.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return String.valueOf(SQL_ERROR);
        }

        String getIdQuery = "select * from " + USERS + " where " + EMAIL + " = ?";
        int id = 0;
        try {
            PreparedStatement getIdPs = conn.prepareStatement(getIdQuery);
            getIdPs.setString(1, email);
            ResultSet getIdRs = getIdPs.executeQuery();
            while (getIdRs.next()){
                id = getIdRs.getInt(ID);
            }
        } catch (SQLException e){
            System.out.println(e.getMessage());
            return String.valueOf(SQL_ERROR);
        }


        return createResponseWithUser(
                AUTHENTICATE_SUCCESS,
                id,
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
        int id = 0;
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
                    id = rs2.getInt(ID);
                    username = rs2.getString(NAME);
                    gender = rs2.getBoolean(GENDER);
                    isTeacher = rs2.getBoolean(IS_TEACHER);
                    registerDatetime = rs2.getTimestamp(REGISTER_DATETIME);
                }
            } catch (SQLException e) {
                return String.valueOf(SQL_ERROR);
            }
        } else {
            return String.valueOf(INVALID_PASSWORD);
        }

        return createResponseWithUser(AUTHENTICATE_SUCCESS, id, username, email, registerDatetime.toString(), String.valueOf(isTeacher), String.valueOf(gender));
    }
}