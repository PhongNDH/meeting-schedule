package com.calendlygui.controller;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.SendData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
import static com.calendlygui.utils.Helper.extractUserFromResponse;

public class LoginController implements Initializable {
    @FXML
    private Button closeButton;

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
        Controller.closeApplication(closeButton);
    }

    @FXML
    void signIn(MouseEvent event) {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        if (dealWithErrorMessageFromUI(email, password)) {
            try {
                SendData.login(out, email, password);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @FXML
    void navigateToRegister(MouseEvent event) throws IOException {
        Controller.navigateToOtherStage(registerLabel, "register.fxml", "Register");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            CalendlyApplication.client = new Socket(InetAddress.getByName(ConstantValue.HOST_ADDRESS), ConstantValue.PORT);
            out = new PrintWriter(CalendlyApplication.client.getOutputStream(), true,StandardCharsets.UTF_8);
            in = new BufferedReader(new InputStreamReader(CalendlyApplication.client.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            CalendlyApplication.shutdown();
        }

        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while (!(response = in.readLine()).isEmpty()) {
                    response = response.replaceAll(NON_PRINTABLE_CHARACTER,"");
                    System.out.println("Response: " + response);
                    String[] info = response.split(COMMAND_DELIMITER);
                    int code = Integer.parseInt(info[0]);
                    if (code == AUTHENTICATE_SUCCESS) {
                        CalendlyApplication.user = extractUserFromResponse(response);
                        navigateToHomePage();
                        System.out.println(LoginMessage.LOGIN_SUCCESS);
                    } else {
                        switch (code) {
                            case ACCOUNT_NOT_EXIST: {
                                //System.out.println(LoginMessage.LOGIN_EMAIL_NOT_EXIST);
                                showErrorFromServerToUIAndConsole(LoginMessage.LOGIN_EMAIL_NOT_EXIST);
                                break;
                            }
                            case INVALID_PASSWORD: {
                                //System.out.println(LoginMessage.LOGIN_PASSWORD_NOT_MATCH);
                                showErrorFromServerToUIAndConsole(LoginMessage.LOGIN_PASSWORD_NOT_MATCH);
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
                System.out.println(e.getMessage());
            }
        });
        //The JVM can terminate daemon without waiting for it to complete its task if all non-daemon threads finish their execution.
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case GeneralMessage.SERVER_WRONG -> {
                errorText.setText(GeneralMessage.SERVER_WRONG);
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
            default -> {
                Controller.setTextToEmpty(errorText, emailText, passwordText);
            }
        }
    }

    private boolean dealWithErrorMessageFromUI(String email, String password) {
        boolean isValidEmail = false;
        boolean isValidPassword = false;
        if (email.trim().isEmpty()) {
            emailText.setText(GeneralMessage.REQUIRED_FIELD);
        } else {
            emailText.setText("");
            isValidEmail = true;
        }
        if (password.isEmpty()) {
            passwordText.setText(GeneralMessage.REQUIRED_FIELD);
        } else {
            passwordText.setText("");
            isValidPassword = true;
        }
        return isValidEmail && isValidPassword;
    }

    private void showErrorFromServerToUIAndConsole(String error) {
        System.out.println(error);
        dealWithErrorMessageFromServer(error);
    }

    private void navigateToHomePage() throws IOException {
        //Allow to execute a Runnable object in the JavaFX Application Thread asynchronously
        if (CalendlyApplication.user == null) return;
        if (CalendlyApplication.user.isTeacher())
            Controller.navigateToOtherStage(signInButton, "teacher.fxml", "Teacher");
        else Controller.navigateToOtherStage(signInButton, "student.fxml", "Student");
    }
}
