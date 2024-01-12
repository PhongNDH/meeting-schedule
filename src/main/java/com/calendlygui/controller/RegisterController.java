package com.calendlygui.controller;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.SendData;
import com.calendlygui.utils.Validate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ResourceBundle;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.ConstantValue.UNDEFINED_ERROR;
import static com.calendlygui.utils.Helper.extractUserFromResponse;

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
    void navigateToSignIn(MouseEvent event) {
        Controller.navigateToOtherStage(signInLabel, "login.fxml", "Login");
    }

    @FXML
    void register(MouseEvent event) {
        String username = usernameTextField.getText();
        String email = emailTextField.getText();
        String password = passwordPasswordField.getText();
        String confirmedPassword = confirmPasswordField.getText();
        boolean isMale = gender.getSelectedToggle().equals(maleGender);
        boolean isTeacher = occupation.getSelectedToggle().equals(teacherOccupation);
        if (dealWithErrorMessageFromUI(email, username, password, confirmedPassword)) {
            try {
                SendData.register(out, email, username, password, isMale, isTeacher);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            CalendlyApplication.client = new Socket(InetAddress.getByName(ConstantValue.HOST_ADDRESS), ConstantValue.PORT);
            out = new PrintWriter(CalendlyApplication.client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(CalendlyApplication.client.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            CalendlyApplication.shutdown();
        }
        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    response = response.replaceAll(NON_PRINTABLE_CHARACTER,"");
                    System.out.println("Response: " + response);
                    String[] info = response.split(COMMAND_DELIMITER);
                    if (Integer.parseInt(info[0]) == AUTHENTICATE_SUCCESS) {
                        CalendlyApplication.user = extractUserFromResponse(response);
                        navigateToHomePage();
                        System.out.println(RegisterMessage.REGISTER_SUCCESS);
                    } else {
                        int code = Integer.parseInt(info[0]);
                        switch (code) {
                            case ACCOUNT_EXIST: {
                                //System.out.println(RegisterMessage.REGISTER_EMAIL_EXIST);
                                showErrorFromServerToUIAndConsole(RegisterMessage.REGISTER_EMAIL_EXIST);
                                break;
                            }
                            case SQL_ERROR: {
                                //System.out.println(GeneralMessage.SERVER_WRONG);
                                showErrorFromServerToUIAndConsole(GeneralMessage.SERVER_WRONG);
                                break;
                            }
                            case UNDEFINED_ERROR: {
                                //System.out.println(GeneralMessage.UNKNOWN_ERROR);
                                showErrorFromServerToUIAndConsole(GeneralMessage.UNKNOWN_ERROR);
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                CalendlyApplication.shutdown();
            } catch (ParseException | NumberFormatException e) {
                throw new RuntimeException(e);
            }
        });
        //The JVM can terminate daemon without waiting for it to complete its task if all non-daemon threads finish their execution, .
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case GeneralMessage.SERVER_WRONG -> {
                errorText.setText(GeneralMessage.SERVER_WRONG);
                Controller.setTextToEmpty(passwordText, emailText, usernameText, comfirmPasswordText);
            }
            case RegisterMessage.REGISTER_EMAIL_EXIST -> {
                emailText.setText(RegisterMessage.REGISTER_EMAIL_EXIST);
                Controller.setTextToEmpty(errorText, passwordText, usernameText, comfirmPasswordText);
            }
            default -> {
                Controller.setTextToEmpty(errorText, emailText, passwordText, usernameText, comfirmPasswordText);
            }
        }
    }

    private boolean dealWithErrorMessageFromUI(String email, String username, String password, String confirmedPassword) {
        boolean isValidEmail = false;
        boolean isValidPassword = false;
        boolean isValidPasswordConfirmation = false;
        boolean isValidUsername = false;
        if (email.trim().isEmpty()) {
            emailText.setText(GeneralMessage.REQUIRED_FIELD);
        } else if (!Validate.checkEmailFormat(email)) {
            emailText.setText(RegisterMessage.REGISTER_EMAIL_NOT_VALID);
        } else {
            emailText.setText("");
            isValidEmail = true;
        }
        if (password.isEmpty()) {
            passwordText.setText(GeneralMessage.REQUIRED_FIELD);
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
            usernameText.setText(GeneralMessage.REQUIRED_FIELD);
        } else if (!Validate.checkName(username)) {
            usernameText.setText(RegisterMessage.REGISTER_USERNAME_NOT_VALID);
        } else {
            usernameText.setText("");
            isValidUsername = true;
        }
        return isValidUsername && isValidEmail && isValidPassword && isValidPasswordConfirmation;
    }

    private void showErrorFromServerToUIAndConsole(String error) {
        System.out.println(error);
        dealWithErrorMessageFromServer(error);
    }

    private void navigateToHomePage() throws IOException {
        if (CalendlyApplication.user == null) return;
        if (CalendlyApplication.user.isTeacher())
            Controller.navigateToOtherStage(registerButton, "teacher.fxml", "Teacher");
        else
            Controller.navigateToOtherStage(registerButton, "student.fxml", "Student");
    }
}