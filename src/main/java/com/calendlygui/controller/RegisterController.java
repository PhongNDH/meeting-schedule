package com.calendlygui.controller;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.model.User;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Validate;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {

    @FXML
    private Button closeButton;

    @FXML
    private Label signInLabel;

    @FXML
    private Text comfirmPasswordText;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Text emailText;

    @FXML
    private TextField emailTextField;

    @FXML
    private ToggleGroup gender;

    @FXML
    private ToggleGroup occupation;

    @FXML
    private RadioButton femaleGender;

    @FXML
    private RadioButton maleGender;

    @FXML
    private RadioButton studentOccupation;

    @FXML
    private RadioButton teacherOccupation;

    @FXML
    private PasswordField passwordPasswordField;

    @FXML
    private Text passwordText;

    @FXML
    private Button registerButton;

    @FXML
    private Text usernameText;

    @FXML
    private TextField usernameTextField;

    @FXML
    private Text errorText;

    @FXML
    void close(MouseEvent event) {

    }
    @FXML
    void navigateToSignIn(MouseEvent event) throws IOException {
        Controller.navigateToOtherStage(signInLabel,"login.fxml","Login");
    }

    @FXML
    void register(MouseEvent event) {
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordPasswordField.getText();
        String confirmedPassword = confirmPasswordField.getText();
        boolean isMale = gender.getSelectedToggle().equals(maleGender);
        boolean isTeacher = occupation.getSelectedToggle().equals(teacherOccupation);
        String request = "/register " + email + " " + username + " " + password + " " + isMale + " " + isTeacher;
        if (dealWithErrorMessageFromUI(email, username, password, confirmedPassword)) {
            CalendlyApplication.out.println(request);
        }

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
                        if (dealWithErrorMessageFromServer(receivedMessage)) {
                            navigateToHomePage();
                        }
                    } else if (receivedData instanceof User receivedUser) {
                        CalendlyApplication.user = receivedUser;
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

    private boolean dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case RegisterMessage.REGISTER_SERVER_WRONG -> {
                errorText.setText(LoginMessage.LOGIN_SERVER_WRONG);
                Controller.setTextToEmpty(passwordText, emailText, usernameText, comfirmPasswordText);
            }
            case RegisterMessage.REGISTER_EMAIL_EXIST -> {
                emailText.setText(RegisterMessage.REGISTER_EMAIL_EXIST);
                Controller.setTextToEmpty(errorText, passwordText, usernameText, comfirmPasswordText);
            }
            case RegisterMessage.REGISTER_SUCCESS -> {
                Controller.setTextToEmpty(errorText, emailText, passwordText, usernameText, comfirmPasswordText);
                return true;
            }
        }
        return false;
    }

    private boolean dealWithErrorMessageFromUI(String email, String username, String password, String confirmedPassword) {
        boolean isValidEmail = false;
        boolean isValidPassword = false;
        boolean isValidPasswordConfirmation = false;
        boolean isValidUsername = false;
        if (email.trim().isEmpty()) {
            emailText.setText(RegisterMessage.REGISTER_REQUIRED_FIELD);
        } else if (!Validate.checkEmailFormat(email)) {
            emailText.setText(RegisterMessage.REGISTER_EMAIL_NOT_VALID);
        } else {
            emailText.setText("");
            isValidEmail = true;
        }
        if (password.isEmpty()) {
            passwordText.setText(RegisterMessage.REGISTER_REQUIRED_FIELD);
        } else if (password.length() < 6) {
            passwordText.setText(RegisterMessage.REGISTER_PASSWORD_NOT_STRONG);
        } else {
            passwordText.setText("");
            isValidPassword = true;
        }
        if (isValidPassword && !password.equals(confirmedPassword)) {
            comfirmPasswordText.setText(RegisterMessage.REGISTER_PASSWORD_CONFIRMATION_NOT_MATCH);
        } else {
            comfirmPasswordText.setText("");
            isValidPasswordConfirmation = true;
        }
        if (username.trim().isEmpty()) {
            usernameText.setText(RegisterMessage.REGISTER_REQUIRED_FIELD);
        } else if (!Validate.checkName(username)) {
            usernameText.setText(RegisterMessage.REGISTER_USERNAME_NOT_VALID);
        } else {
            usernameText.setText("");
            isValidUsername = true;
        }
        return isValidUsername && isValidEmail && isValidPassword && isValidPasswordConfirmation;
    }

    private void navigateToHomePage() throws IOException {
        //Allow to execute a Runnable object in the JavaFX Application Thread asynchronously
        if (CalendlyApplication.user == null) return;
        if (CalendlyApplication.user.isTeacher())
            Controller.navigateToOtherStage(registerButton, "teacher.fxml", "Teacher");
        else
            Controller.navigateToOtherStage(registerButton, "student.fxml", "Student");
    }


}