package com.calendlygui.controller;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.model.entity.User;
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

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.DELIMITER;
import static com.calendlygui.constant.ConstantValue.SUCCESS;

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
    void signIn(MouseEvent event) {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();

        if (dealWithErrorMessageFromUI(email, password)) {
            try {
                SendData.handleLogin(in, out, email, password);
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
            out = new PrintWriter(CalendlyApplication.client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(CalendlyApplication.client.getInputStream()));
//            CalendlyApplication.inObject = new ObjectInputStream(CalendlyApplication.client.getInputStream());
//            CalendlyApplication.outObject = new ObjectOutputStream(CalendlyApplication.client.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            CalendlyApplication.shutdown();
        }

        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("Response: " + response);
                    String[] info = response.split(DELIMITER);
                    if (info[0].contains(SUCCESS) && info.length == 7) {
                        CalendlyApplication.user = new User(info[2], info[3], Timestamp.valueOf(info[4]), Objects.equals(info[5], "true"), Objects.equals(info[6], "true"));
                        navigateToHomePage();
                        System.out.println("Navigate to home screen");
                    } else {
                        switch (info[1]) {
                            case ConstantValue.CLIENTSIDE_ERROR: {
                                System.out.println("CLIENT ERROR");
                                showErrorFromServerToUIAndConsole(info[2]);
                                break;
                            }
                            case ConstantValue.SERVERSIDE_ERROR: {
                                System.out.println("SERVER ERROR");
                                showErrorFromServerToUIAndConsole(info[2]);
                                break;
                            }
                            case ConstantValue.SQL_ERROR: {
                                System.out.println("SQL ERROR");
                                showErrorFromServerToUIAndConsole(info[2]);
                                break;
                            }
                            default: {
                                System.out.println("UNKNOWN ERROR");
                                System.out.println((info[2]));
                                break;
                            }
                        }
                    }
                }
//                Object responseObject;
//                Response response = null;
//                while (true) {
//                    responseObject = CalendlyApplication.inObject.readObject();
//                    if(responseObject instanceof Response) response = (Response) responseObject;
//                    System.out.println("Response received : " + response);
//                    if(response == null) return;
//                    if(response.getBody() instanceof String){
//                        System.out.println((String) response.getBody());
//                    }
//                    else{
//                        switch (response.getCode()) {
//                            case 0: {
//                                User user = ((User) response.getBody());
//                                System.out.println("SUCCESS");
//                                System.out.println(user.getUsername());
//                                CalendlyApplication.user = user;
//                                navigateToHomePage();
//                                break;
//                            }
//                            case 1: {
//                                System.out.println("CLIENT ERROR");
//                                showErrorFromServerToUIAndConsole((ErrorMessage) response.getBody());
//                                break;
//                            }
//                            case 2: {
//                                System.out.println("SERVER ERROR");
//                                showErrorFromServerToUIAndConsole((ErrorMessage) response.getBody());
//                                break;
//                            }
//                            case 3: {
//                                System.out.println("SQL ERROR");
//                                showErrorFromServerToUIAndConsole((ErrorMessage) response.getBody());
//                                break;
//                            }
//                            default: {
//                                System.out.println("UNKNOWN ERROR");
//                                System.out.println(((ErrorMessage) response.getBody()).getContent());
//                                break;
//                            }
//                        }
//                    }
//                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                CalendlyApplication.shutdown();
            }
        });
        //The JVM can terminate daemon without waiting for it to complete its task if all non-daemon threads finish their execution.
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void dealWithErrorMessageFromServer(String message) {
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
            default -> {
                Controller.setTextToEmpty(errorText, emailText, passwordText);
            }
        }
    }

    private boolean dealWithErrorMessageFromUI(String email, String password) {
        boolean isValidEmail = false;
        boolean isValidPassword = false;
        if (email.trim().isEmpty()) {
            emailText.setText(LoginMessage.LOGIN_REQUIRED_FIELD);
        } else {
            emailText.setText("");
            isValidEmail = true;
        }
        if (password.isEmpty()) {
            passwordText.setText(LoginMessage.LOGIN_REQUIRED_FIELD);
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
