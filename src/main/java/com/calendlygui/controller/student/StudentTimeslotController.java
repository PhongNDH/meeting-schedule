package com.calendlygui.controller.student;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.model.Meeting;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.SimpleStringProperty;

import java.net.URL;
import java.util.ResourceBundle;

public class StudentTimeslotController implements Initializable {
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
    private TableView<Meeting> meetingTable;

    @FXML
    private TableColumn<Meeting, String> beginTableColumn;

    @FXML
    private TableColumn<Meeting, String> dateTableColumn;

    @FXML
    private TableColumn<Meeting, String> endTableColumn;

    @FXML
    private TableColumn<Meeting, String> teacherTableColumn;

    @FXML
    private TableColumn<Meeting, String> typeTableColumn;

    @FXML
    void logout(MouseEvent event) {
        if (CalendlyApplication.user != null) {
            CalendlyApplication.user = null;
        }
        Controller.navigateToOtherStage(logoutButton, "login.fxml", "Login");
    }

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
        Controller.navigateToOtherStage(profileButton, "student-profile.fxml", "Profile");
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
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        meetingTable = new TableView<>();
        teacherTableColumn = new TableColumn<>();
        dateTableColumn = new TableColumn<>();
        beginTableColumn = new TableColumn<>();
        endTableColumn = new TableColumn<>();
        typeTableColumn = new TableColumn<>();

        //meetingTable.getColumns().addAll(teacherTableColumn, dateTableColumn, beginTableColumn, endTableColumn, typeTableColumn);
        teacherTableColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTeacherId())));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.GetDateFromTimestamp(data.getValue().getFinishDatetime())));
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.GetTimeFromTimestamp(data.getValue().getOccurDatetime())));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.GetTimeFromTimestamp(data.getValue().getEstablishedDatetime())));
        typeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClassification()));

        ObservableList<Meeting> data = FXCollections.observableArrayList(
                new Meeting(1, 1, "First Meeting", Format.CreateTimestamp(2023, 12, 25, 8, 30),
                        Format.CreateTimestamp(2023, 12, 26, 8, 30),
                        Format.CreateTimestamp(2023, 12, 26, 8, 50), "Both"),
                new Meeting(2, 2, "Second Meeting", Format.CreateTimestamp(2023, 12, 25, 8, 30),
                        Format.CreateTimestamp(2023, 12, 26, 8, 30),
                        Format.CreateTimestamp(2023, 12, 26, 8, 50), "Group")
        );
        meetingTable.setItems(data);
    }
}
