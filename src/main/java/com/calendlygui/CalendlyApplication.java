package com.calendlygui;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.model.entity.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.createRequest;

public class CalendlyApplication extends Application {
    public static User user = null;
    public static Socket client;

    public static PrintWriter out;

    public static BufferedReader in;

    public static ObjectInputStream inObject;
    public static ObjectOutputStream outObject;

    private String response;
    private String request;
    ArrayList<String> data = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CalendlyApplication.class.getResource("login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            //RegisterController registerController = fxmlLoader.getController();

            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static void shutdown() {
        try {
            if (inObject != null) {
                inObject.close();
            }
            if (outObject != null) {
                outObject.close();
            }
            if (out != null) {
                out.close();
            }
            if (in != null) {
                in.close();
            }
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}