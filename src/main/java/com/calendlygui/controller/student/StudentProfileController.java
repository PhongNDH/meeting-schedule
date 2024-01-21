package com.calendlygui.controller.student;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.utils.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.ResourceBundle;

import static java.time.ZoneId.systemDefault;

public class StudentProfileController implements Initializable {
    @FXML
    private Button appointmentButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button scheduleButton;

    @FXML
    private Button logoutButton;

    @FXML
    private Button settingButton;

    @FXML
    private Button teacherListButton;

    @FXML
    private Button timeslotButton;

    @FXML
    private TextField emailTextField;

    @FXML
    private TextField genderTextField;

    @FXML
    private TextField registerDatetimeTextfield;

    @FXML
    private TextField roleTextField;

    @FXML
    private TextField usernameTextField;

    @FXML
    private ImageView avatarImage;

    @FXML
    void navigateTeacherList(MouseEvent event) {
        Controller.navigateToOtherStage(teacherListButton, "student-teacher-list.fxml", "Teacher List");
    }

    @FXML
    void navigateToAppointment(MouseEvent event) {
        Controller.navigateToOtherStage(appointmentButton, "student-appointment.fxml", "Appointment");
    }

    @FXML
    void navigateToHistory(MouseEvent event) {
        Controller.navigateToOtherStage(historyButton, "student-history.fxml", "History");
    }

    @FXML
    void navigateToHome(MouseEvent event) {
        Controller.navigateToOtherStage(homeButton, "student.fxml", "Student");
    }

    @FXML
    void navigateToProfile(MouseEvent event) {
    }

    @FXML
    void navigateToSchedule(MouseEvent event) {
        Controller.navigateToOtherStage(scheduleButton, "student-schedule.fxml", "Schedule");
    }

    @FXML
    void navigateToSetting(MouseEvent event) {
        Controller.navigateToOtherStage(settingButton, "student-setting.fxml", "Setting");
    }

    @FXML
    void navigateToTimeslot(MouseEvent event) {
        Controller.navigateToOtherStage(timeslotButton, "student-timeslot.fxml", "New meeting");

    }

    @FXML
    void logout(MouseEvent event) {
        if (CalendlyApplication.user != null) {
            CalendlyApplication.user = null;
        }
        Controller.navigateToOtherStage(logoutButton, "login.fxml", "Login");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Controller.initialiseProfile(usernameTextField, emailTextField, roleTextField,genderTextField, registerDatetimeTextfield, avatarImage);
    }
}

