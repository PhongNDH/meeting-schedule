package com.calendlygui.controller;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.LoginMessage;
import com.calendlygui.constant.RegisterMessage;
import com.calendlygui.model.ErrorMessage;
import com.calendlygui.model.entity.User;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.SendData;
import com.calendlygui.utils.Validate;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
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
                SendData.handleRegister(in, out, email, username, password, isMale, isTeacher);
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
            CalendlyApplication.inObject = new ObjectInputStream(CalendlyApplication.client.getInputStream());
            CalendlyApplication.outObject = new ObjectOutputStream(CalendlyApplication.client.getOutputStream());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            CalendlyApplication.shutdown();
        }
        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    System.out.println("Response: " + response);
                    String[] info = response.split(COMMAND_DELIMITER);
                    if (Integer.parseInt(info[0]) == OPERATION_SUCCESS) {
                        CalendlyApplication.user = extractUserFromResponse(response);
                        navigateToHomePage();
                        System.out.println("Navigate to home screen");
                    } else {
                        int code = Integer.parseInt(info[0]);
                        switch (code) {
                            case ACCOUNT_EXIST: {
                                System.out.println("This account already exist");
                                showErrorFromServerToUIAndConsole("This account already exist");
                                break;
                            }
                            case SQL_ERROR: {
                                System.out.println("Sql error");
                                showErrorFromServerToUIAndConsole("Sql error");
                                break;
                            }
                            case UNDEFINED_ERROR: {
                                System.out.println("Unknown error");
                                System.out.println("Unknown error");
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
//                                System.out.println(((ErrorMessage) response.getBody()).getContent());
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
//                                showErrorFromServerToUIAndConsole((ErrorMessage) response.getBody());
//                                break;
//                            }
//                        }
//                    }
//                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                CalendlyApplication.shutdown();
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
        //The JVM can terminate daemon without waiting for it to complete its task if all non-daemon threads finish their execution, .
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    private void dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case RegisterMessage.REGISTER_SERVER_WRONG -> {
                errorText.setText(LoginMessage.LOGIN_SERVER_WRONG);
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