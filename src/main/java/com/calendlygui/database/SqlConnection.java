package com.calendlygui.database;

import java.sql.*;

public class SqlConnection {
    public static Connection connect() {
        String url = "jdbc:postgresql://localhost:5432/networkprog";
        String user = "postgres";
        String password = "Spyderpyg59@";
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException var5) {
            throw new RuntimeException(var5);
        }
    }

    public static void disconnect(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException var2) {
                throw new RuntimeException(var2);
            }
        }

    }

    public static void main(String[] args) {
        String query = "select * from users";
        Connection conn = SqlConnection.connect();
        try {
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                System.out.println(rs.getString("email"));
                System.out.println(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}