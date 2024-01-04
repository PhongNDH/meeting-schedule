package com.calendlygui.controller.student;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.Objects;
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

    @FXML
    private TextField createdTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField beginTextField;

    @FXML
    private Button closeButton;

    @FXML
    private TextField endTextField;

    @FXML
    private Button joinButton;

    @FXML
    private TextField teacherTextField;

    @FXML
    private Pane detailPane;

    @FXML
    private ComboBox<String> classificationCombobox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        teacherTableColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTeacherId())));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getDateFromTimestamp(data.getValue().getFinishDatetime())));
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getOccurDatetime())));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getFinishDatetime())));
        typeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClassification()));

        ObservableList<Meeting> data = FXCollections.observableArrayList(
                new Meeting(1, 1, "First Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
                        Format.createTimestamp(2023, 12, 26, 8, 30),
                        Format.createTimestamp(2023, 12, 26, 8, 50), "Both"),
                new Meeting(2, 2, "Second Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
                        Format.createTimestamp(2023, 12, 26, 8, 30),
                        Format.createTimestamp(2023, 12, 26, 8, 50), "Group")
        );

        meetingTable.setItems(data);

        meetingTable.setRowFactory(tv -> {
            TableRow<Meeting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Meeting rowData = row.getItem();
                    //System.out.println("Double click on: "+rowData.getTeacherId());
                    teacherTextField.setText(String.valueOf(rowData.getTeacherId()));
                    beginTextField.setText(Format.getTimeFromTimestamp(rowData.getOccurDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getOccurDatetime()));
                    endTextField.setText(Format.getTimeFromTimestamp(rowData.getFinishDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getFinishDatetime()));
                    nameTextField.setText(rowData.getName());
                    createdTextField.setText(Format.getDateFromTimestamp(rowData.getEstablishedDatetime()));
                    if(Objects.equals(rowData.getClassification(), "Both")){
                        classificationCombobox.getItems().addAll("Group", "Individual");
                        classificationCombobox.setValue("Group");
                    }else{
                        classificationCombobox.getItems().add(rowData.getClassification());
                        classificationCombobox.setValue(rowData.getClassification());
                    }
                    detailPane.setVisible(true);;
                }
            });
            return row ;
        });
    }

    @FXML
    void closeDialog(MouseEvent event) {
        Controller.setTextFieldToEmpty(teacherTextField,beginTextField,endTextField,createdTextField, nameTextField);
        classificationCombobox.getItems().clear();
        detailPane.setVisible(false);
    }

    @FXML
    void joinMeeting(MouseEvent event) {

    }
}