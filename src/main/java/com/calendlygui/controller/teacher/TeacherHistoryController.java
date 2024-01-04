package com.calendlygui.controller.teacher;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.model.entity.Content;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.model.entity.User;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class TeacherHistoryController implements Initializable {

    private List<Meeting> meetings = new ArrayList<>();

    private Meeting currentMeeting = null;

    private List<User> students = new ArrayList<>();

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
    private Button addContentButton;

    @FXML
    private Button closeContentButton;

    @FXML
    private Button  closeStudentPaneButton;

    @FXML
    private Button  studentButton;

    @FXML
    private TableView<Meeting> historyTable;

    @FXML
    private TableColumn<Meeting, String> beginTableColumn;

    @FXML
    private TableColumn<Meeting, String> dateTableColumn;

    @FXML
    private TableColumn<Meeting, String> endTableColumn;

    @FXML
    private TableColumn<Meeting, String> selectedTypeTableColumn;

    @FXML
    private TableColumn<Meeting, String> statusTableColumn;

    @FXML
    private TableView<Content> contentTable;

    @FXML
    private TableColumn<Content, String> contentColumn;

    @FXML
    private TableColumn<Content, String> contentCreatedDateColumn;

    @FXML
    private TableView<User> studentTable;

    @FXML
    private TableColumn<User, String> studentNameTableColumn;

    @FXML
    private TableColumn<User, String> studentGenderTableColumn;

    @FXML
    private TableColumn<User, String> studentEmailTableColumn;

    @FXML
    private Pane detailPane;

    @FXML
    private Pane studentPane;

    @FXML
    private Pane contentPane;

    @FXML
    private TextField createdTextField;

    @FXML
    private TextField beginTextField;

    @FXML
    private TextField endTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField selectedTypeTextField;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private TextArea currentContentTextArea;


    @FXML
    void logout(MouseEvent event) {
        if (CalendlyApplication.user != null) {
            CalendlyApplication.user = null;
        }
        Controller.navigateToOtherStage(logoutButton, "login.fxml", "Login");
    }

    @FXML
    void navigateToAppointment(MouseEvent event) {
        Controller.navigateToOtherStage(appointmentButton, "teacher-appointment.fxml", "Appointment");
    }

    @FXML
    void navigateToHistory(MouseEvent event) {
    }

    @FXML
    void navigateToHome(MouseEvent event) {
        Controller.navigateToOtherStage(homeButton, "teacher.fxml", "Teacher");
    }

    @FXML
    void navigateToProfile(MouseEvent event) {
        Controller.navigateToOtherStage(profileButton, "teacher-profile.fxml", "Profile");
    }

    @FXML
    void navigateToSetting(MouseEvent event) {
        Controller.navigateToOtherStage(settingButton, "teacher-setting.fxml", "Setting");
    }

    @FXML
    void navigateToStudentList(MouseEvent event) {
        Controller.navigateToOtherStage(studentListButton, "teacher-student-list.fxml", "Student List");
    }

    @FXML
    void navigateToTimeslot(MouseEvent event) {
        Controller.navigateToOtherStage(timeslotButton, "teacher-timeslot.fxml", "Time Slots");
    }


    @FXML
    void openContentPane(MouseEvent event) {
        contentPane.setVisible(true);
    }

    @FXML
    void openStudentPane(MouseEvent event) {
        studentPane.setVisible(true);
    }

    @FXML
    void addContent(MouseEvent event) {

    }

    @FXML
    void closeContentPane(MouseEvent event) {
        contentPane.setVisible(false);
    }

    @FXML
    void closeDetailPane(MouseEvent event) {
        detailPane.setVisible(false);
    }

    @FXML
    void closeStudentPane(MouseEvent event) {
        studentPane.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        meetings.add(new Meeting(1, "First Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 50), "Individuals", "Done", new ArrayList<>() {
            {
                add(new Content(1, 1, "Content 1", Format.createTimestamp(2023, 12, 25, 8, 30)));
                add(new Content(2, 1, "Content 2", Format.createTimestamp(2023, 12, 26, 8, 30)));
            }
        }));

        meetings.add(new Meeting(2, "Second Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 50), "Not yet", "Cancelled", new ArrayList<>())
        );
        meetings.add(new Meeting(3, "Third Meeting", Format.createTimestamp(2023, 12, 27, 8, 30),
                Format.createTimestamp(2024, 1, 5, 6, 20),
                Format.createTimestamp(2024, 1, 5, 6, 50), "Individual", "Done", new ArrayList<>() {
            {
                add(new Content(3, 3, "Content 3", Format.createTimestamp(2023, 12, 28, 9, 30)));
                add(new Content(4, 3, "Content 4", Format.createTimestamp(2023, 12, 29, 15, 19)));
            }
        }));

        students.add(new User("Josko","josko@gmail.com",false,true));
        students.add(new User("Cazorla","cakop@gmail.com",false,true));
        students.add(new User("Marinai","maler@gmail.com",false,false));

        showHistory();
    }

    private void showHistory(){
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getOccurDatetime())));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getFinishDatetime())));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getDateFromTimestamp(data.getValue().getOccurDatetime())));
        selectedTypeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSelectedClassification()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        ObservableList<Meeting> data = FXCollections.observableArrayList(meetings);

        historyTable.setItems(data);

        historyTable.setRowFactory(ht -> {
            TableRow<Meeting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && !row.isEmpty()) {
                    Meeting rowData = row.getItem();
                    currentMeeting = rowData;
                    beginTextField.setText(Format.getTimeFromTimestamp(rowData.getOccurDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getOccurDatetime()));
                    nameTextField.setText(rowData.getName());
                    endTextField.setText(Format.getTimeFromTimestamp(rowData.getFinishDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getFinishDatetime()));
                    createdTextField.setText(Format.getDateFromTimestamp(rowData.getEstablishedDatetime()));
                    selectedTypeTextField.setText(rowData.getSelectedClassification());
                    statusTextField.setText(rowData.getStatus());
                    detailPane.setVisible(true);

                    if(Objects.equals(rowData.getStatus(), "Cancelled")){
                        studentButton.setDisable(true);
                    }

                    showContents();
                    showStudentList();
                }
            });

            return row;
        });

    }

    private void showStudentList() {
        studentNameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        studentGenderTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGender() ? "Male" : "Female"));
        studentEmailTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getEmail()));

        ObservableList<User> users = FXCollections.observableArrayList(students);

        studentTable.setItems(users);
    }

    private void showContents() {
        contentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContent()));
        contentCreatedDateColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getDate())+" " +Format.getDateFromTimestamp(data.getValue().getDate())));

        ObservableList<Content> contents = FXCollections.observableArrayList(currentMeeting.getContents());

        contentTable.setItems(contents);

        contentTable.setRowFactory(ct -> {
            TableRow<Content> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Content content = row.getItem();
                    currentContentTextArea.setText(content.getContent());
                }
            });
            return row;
        });
    }
}
