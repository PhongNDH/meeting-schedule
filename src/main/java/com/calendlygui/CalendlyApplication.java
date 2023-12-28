package com.calendlygui;

import com.calendlygui.model.entity.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class CalendlyApplication extends Application {
    public static User user = null;
    public static Socket client;
    public static PrintWriter out;
    public static ObjectInputStream inObject;
    public static ObjectOutputStream outObject;

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
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}