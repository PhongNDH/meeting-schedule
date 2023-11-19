package com.calendlygui.controller;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.model.User;
import com.calendlygui.utils.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.tools.Utils;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Button CloseButton;

    @FXML
    private Text emailText;

    @FXML
    private TextField emailTextField;

    @FXML
    private Text passwordText;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private Button signInButton;

    @FXML
    private Label registerLabel;

    @FXML
    private Text errorText;

    @FXML
    void close(MouseEvent event) {

    }

    @FXML
    void signIn(MouseEvent event) throws IOException {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        String request = "/login " + email + " " + password;
        if (dealWithErrorMessageFromUI(email, password)) {
            CalendlyApplication.out.println(request);
        }
    }

    @FXML
    void navigateToRegister(MouseEvent event) throws IOException {
        Controller.navigateToOtherStage(registerLabel,"register.fxml","Register");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            CalendlyApplication.client = new Socket(InetAddress.getByName(ConstantValue.HOST_ADDRESS), ConstantValue.PORT);
            CalendlyApplication.out = new PrintWriter(CalendlyApplication.client.getOutputStream(), true);
            CalendlyApplication.inObject = new ObjectInputStream(CalendlyApplication.client.getInputStream());
        } catch (IOException e) {
            CalendlyApplication.shutdown();
        }
        Thread receiveThread = new Thread(() -> {
            try {
                CalendlyApplication.client = new Socket(InetAddress.getByName(ConstantValue.HOST_ADDRESS), ConstantValue.PORT);
                CalendlyApplication.out = new PrintWriter(CalendlyApplication.client.getOutputStream(), true);
                CalendlyApplication.inObject = new ObjectInputStream(CalendlyApplication.client.getInputStream());
                Object receivedData;
                while (true) {
                    receivedData = CalendlyApplication.inObject.readObject();
                    if (receivedData instanceof String receivedMessage) {
                        System.out.println(receivedMessage);
                        if (dealWithErrorMessageFromDatabase(receivedMessage)) {
                            navigateToHomePage();
                        }
                    } else if (receivedData instanceof User receivedUser) {
                        CalendlyApplication.user = receivedUser;
                        //System.out.println(CalendlyApplication.user);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                CalendlyApplication.shutdown();
            }
        });
        //The JVM can terminate daemon without waiting for it to complete its task if all non-daemon threads finish their execution, .
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private boolean dealWithErrorMessageFromDatabase(String message) {
        switch (message) {
            case LoginMessage.LOGIN_SERVER_WRONG -> {
                errorText.setText(LoginMessage.LOGIN_SERVER_WRONG);
                Controller.setTextToEmpty(passwordText, emailText);
            }
            case LoginMessage.LOGIN_PASSWORD_NOT_MATCH -> {
                passwordText.setText(LoginMessage.LOGIN_PASSWORD_NOT_MATCH);
                Controller.setTextToEmpty(errorText, emailText);
            }
            case LoginMessage.LOGIN_EMAIL_NOT_EXIST -> {
                emailText.setText(LoginMessage.LOGIN_EMAIL_NOT_EXIST);
                Controller.setTextToEmpty(errorText, passwordText);
            }
            case LoginMessage.LOGIN_SUCCESS -> {
                Controller.setTextToEmpty(errorText, emailText, passwordText);
                return true;
            }
        }
        return false;
    }

    private boolean dealWithErrorMessageFromUI(String email, String password) {
        boolean isValidEmail = false;
        boolean isValidPassword = false;
        if (email.trim().isEmpty()) {
            emailText.setText(LoginMessage.LOGIN_REQUIRED_FIELD);
        }else{
            emailText.setText("");
            isValidEmail = true;
        }
        if (password.isEmpty()) {
            passwordText.setText(LoginMessage.LOGIN_REQUIRED_FIELD);
        }else{
            passwordText.setText("");
            isValidPassword = true;
        }
        return isValidEmail && isValidPassword;
    }

    private void navigateToHomePage() throws IOException {
        //Allow to execute a Runnable object in the JavaFX Application Thread asynchronously
        if (CalendlyApplication.user == null) return;
        if (CalendlyApplication.user.isTeacher())
            Controller.navigateToOtherStage(signInButton, "teacher.fxml", "Teacher");
        else
            Controller.navigateToOtherStage(signInButton, "student.fxml", "Student");

    }
}
