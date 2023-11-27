package com.calendlygui.controller.student;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.utils.Controller;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

public class StudentScheduleController {
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
    private Button settingButton;

    @FXML
    private Button teacherListButton;

    @FXML
    private Button timeslotButton;

    @FXML
    private Button logoutButton;

    @FXML
    void logout(MouseEvent event) {
        if(CalendlyApplication.user != null){
            CalendlyApplication.user = null;
        }
        Controller.navigateToOtherStage(logoutButton, "login.fxml","Login");
    }

    @FXML
    void navigateTeacherList(MouseEvent event) {
        Controller.navigateToOtherStage(teacherListButton,"student-teacher-list.fxml","Teacher List");
    }

    @FXML
    void navigateToAppointment(MouseEvent event) {
        Controller.navigateToOtherStage(appointmentButton,"student-appointment.fxml","Appointment");
    }

    @FXML
    void navigateToHistory(MouseEvent event) {
        Controller.navigateToOtherStage(historyButton,"student-history.fxml","History");
    }

    @FXML
    void navigateToHome(MouseEvent event) {
        Controller.navigateToOtherStage(homeButton,"student.fxml","Student");
    }

    @FXML
    void navigateToProfile(MouseEvent event) {
        Controller.navigateToOtherStage(profileButton,"student-profile.fxml","Profile");
    }

    @FXML
    void navigateToSchedule(MouseEvent event) {
    }

    @FXML
    void navigateToSetting(MouseEvent event) {
        Controller.navigateToOtherStage(settingButton,"student-setting.fxml","Setting");
    }

    @FXML
    void navigateToTimeslot(MouseEvent event) {
        Controller.navigateToOtherStage(timeslotButton,"student-timeslot.fxml","Time slots");

    }
}
