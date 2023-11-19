package com.calendlygui;

import com.calendlygui.constant.ConstantValue;
import com.calendlygui.controller.RegisterController;
import com.calendlygui.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Objects;

public class CalendlyApplication extends Application {
    public static User user = null;
    public static Socket client;
    public static PrintWriter out;
    public static ObjectInputStream inObject;

    @Override
    public void start(Stage stage){
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CalendlyApplication.class.getResource("register.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            //RegisterController registerController = fxmlLoader.getController();
            stage.setTitle("Register");
            stage.setScene(scene);
            stage.show();
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }

    }

    public static void main(String[] args) throws UnknownHostException {
        try {
            client = new Socket(InetAddress.getByName(ConstantValue.HOST_ADDRESS), ConstantValue.PORT);
            out = new PrintWriter(client.getOutputStream(), true);
            inObject = new ObjectInputStream(client.getInputStream());} catch (IOException e) {
            throw new RuntimeException(e);
        }
        launch();
    }

    public static void shutdown() {
        try {
            out.close();
            inObject.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}