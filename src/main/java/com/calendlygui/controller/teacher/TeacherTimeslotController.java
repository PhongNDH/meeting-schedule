package com.calendlygui.controller.teacher;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.utils.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class TeacherTimeslotController implements Initializable {
    @FXML
    private Button appointmentButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button homeButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button settingButton;

    @FXML
    private Button studentListButton;

    @FXML
    private Button timeslotButton;

    @FXML
    private Button logoutButton;

    @FXML
    private TextField beginTextField;

    @FXML
    private ComboBox<String> classificationComboBox;


    @FXML
    private TextField endTextField;

    @FXML
    private Button meetingCreationButton;

    @FXML
    private TextField meetingNameTextField;

    @FXML
    private DatePicker meetingTimeDatePicker;


    @FXML
    void logout(MouseEvent event) {
        if(CalendlyApplication.user != null){
            CalendlyApplication.user = null;
        }
        Controller.navigateToOtherStage(logoutButton, "login.fxml","Login");
    }

    @FXML
    void navigateToAppointment(MouseEvent event) {
        Controller.navigateToOtherStage(appointmentButton,"teacher-appointment.fxml","Appointment");
    }

    @FXML
    void navigateToHistory(MouseEvent event) {
        Controller.navigateToOtherStage(historyButton,"teacher-history.fxml","History");
    }

    @FXML
    void navigateToHome(MouseEvent event) {
        Controller.navigateToOtherStage(homeButton,"teacher.fxml","Teacher");
    }

    @FXML
    void navigateToProfile(MouseEvent event) {
        Controller.navigateToOtherStage(profileButton,"teacher-profile.fxml","Profile");
    }

    @FXML
    void navigateToSetting(MouseEvent event) {
        Controller.navigateToOtherStage(settingButton,"teacher-setting.fxml","Setting");
    }

    @FXML
    void navigateToStudentList(MouseEvent event) {
        Controller.navigateToOtherStage(studentListButton,"teacher-student-list.fxml","Student List");
    }

    @FXML
    void navigateToTimeslot(MouseEvent event) {
    }

    @FXML
    void createTimeSlot(MouseEvent event) {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        classificationComboBox.setValue("Group");
    }
}