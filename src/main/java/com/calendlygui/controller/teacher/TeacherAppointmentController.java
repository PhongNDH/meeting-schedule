package com.calendlygui.controller.teacher;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.constant.TimeslotMessage;
import com.calendlygui.model.entity.Content;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.ConstantValue.UNDEFINED_ERROR;

public class TeacherAppointmentController implements Initializable {
    private List<Meeting> meetings = new ArrayList<>();

    private Meeting currentMeeting = null;

    @FXML
    private Button appointmentButton;

    @FXML
    private Button historyButton;

    @FXML
    private Button addContentButton;

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
    private TableView<Meeting> meetingTable;

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
    private TableColumn<Meeting, String> typeTableColumn;

    @FXML
    private ComboBox<String> filterCombobox;

    @FXML
    private DatePicker filterDatetime;

    @FXML
    private Pane detailPane;

    @FXML
    private Pane contentPane;

    @FXML
    private Button editButton;

    @FXML
    private TextField selectedTypeTextField;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField endTextField;

    @FXML
    private TextField beginTextField;

    @FXML
    private TextField createdTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TextField typeTextField;

    @FXML
    private Button closeContentButton;

    @FXML
    private Button contentButton;

    @FXML
    private Button closeButton;

    @FXML
    private TableView<Content> contentTable;

    @FXML
    private TextArea contentTextArea;

    @FXML
    private TextArea currentContentTextArea;

    @FXML
    private TableColumn<Content, String> contentCreatedDateColumn;

    @FXML
    private TableColumn<Content, String> contentColumn;

    @FXML
    private Text errorText;


    @FXML
    void logout(MouseEvent event) {
        if (CalendlyApplication.user != null) {
            CalendlyApplication.user = null;
        }
        Controller.navigateToOtherStage(logoutButton, "login.fxml", "Login");
    }

    @FXML
    void navigateToAppointment(MouseEvent event) {
    }

    @FXML
    void navigateToHistory(MouseEvent event) {
        Controller.navigateToOtherStage(historyButton, "teacher-history.fxml", "History");
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
    void closeDialog(MouseEvent event) {
        detailPane.setVisible(false);
        currentMeeting = null;
    }

    @FXML
    void editMeeting(MouseEvent event) {

    }

    @FXML
    void addContent(MouseEvent event) {

    }

    @FXML
    void closeContentPane(MouseEvent event) {
        contentPane.setVisible(false);
    }

    @FXML
    void openContentPane(MouseEvent event) {
        contentPane.setVisible(true);
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
        Thread receiveThread = getReceiveThread();
        receiveThread.start();


        initializeScheduleMeeting();
    }

    private Thread getReceiveThread() {
        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    response = response.replaceAll(NON_PRINTABLE_CHARACTER, "");
                    System.out.println("Response: " + response);
                    String[] info = response.split(COMMAND_DELIMITER);
                    if (Integer.parseInt(info[0]) == CREATE_SUCCESS) {
                        //CalendlyApplication.user = extractUserFromResponse(response);
                        //navigateToHomePage();
                        System.out.println(TimeslotMessage.TIMESLOT_SUCCESS);
                    } else {
                        int code = Integer.parseInt(info[0]);
                        switch (code) {
                            case SQL_ERROR: {
                                showErrorFromServerToUIAndConsole(GeneralMessage.SERVER_WRONG);
                                break;
                            }
                            case UNDEFINED_ERROR: {
                                showErrorFromServerToUIAndConsole(GeneralMessage.UNKNOWN_ERROR);
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                CalendlyApplication.shutdown();
            } catch (NumberFormatException e) {
                throw new RuntimeException(e);
            }
        });
        //The JVM can terminate daemon without waiting for it to complete its task if all non-daemon threads finish their execution, .
        receiveThread.setDaemon(true);
        return receiveThread;
    }

    private void showErrorFromServerToUIAndConsole(String error) {
        System.out.println(error);
        dealWithErrorMessageFromServer(error);
    }

    private void dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case GeneralMessage.UNKNOWN_ERROR -> {
                errorText.setText(GeneralMessage.UNKNOWN_ERROR);
//                Controller.setTextToEmpty(datetimeErrorText, beginErrorText, endErrorText);
            }
            case GeneralMessage.SERVER_WRONG -> {
                errorText.setText(GeneralMessage.SERVER_WRONG);
//                Controller.setTextToEmpty(datetimeErrorText, beginErrorText, endErrorText);
            }
            default -> {
                Controller.setTextToEmpty(errorText);
            }
        }
    }

    private void initializeScheduleMeeting(){
        meetings.add(new Meeting(1, "First Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 50), "Both", "Individuals", "Ready", new ArrayList<>() {
            {
                add(new Content(1, 1, "Content 1", Format.createTimestamp(2023, 12, 25, 8, 30)));
                add(new Content(2, 1, "Content 2", Format.createTimestamp(2023, 12, 26, 8, 30)));
            }
        }));

        meetings.add(new Meeting(2, "Second Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 30),
                Format.createTimestamp(2023, 12, 26, 8, 50), "Group", "Group", "Available", new ArrayList<>())
        );
        meetings.add(new Meeting(3, "Third Meeting", Format.createTimestamp(2023, 12, 27, 8, 30),
                Format.createTimestamp(2024, 1, 5, 6, 20),
                Format.createTimestamp(2024, 1, 5, 6, 50), "Individual", "Individual", "Available", new ArrayList<>() {
            {
                add(new Content(3, 3, "Content 3", Format.createTimestamp(2023, 12, 28, 9, 30)));
                add(new Content(4, 3, "Content 4", Format.createTimestamp(2023, 12, 29, 15, 19)));
            }
        }));

        meetings.add(new Meeting(4, "Forth Meeting", Format.createTimestamp(2023, 12, 28, 8, 30),
                Format.createTimestamp(2024, 1, 4, 6, 20),
                Format.createTimestamp(2024, 1, 4, 6, 50), "Individual", "Not yet", "Available", new ArrayList<>() {
            {
                add(new Content(5, 4, "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.", Format.createTimestamp(2023, 12, 28, 9, 30)));
            }
        }));

        showScheduledMeeting();

        filterCombobox.setValue("All");
        filterDatetime.setVisible(false);
        filterCombobox();
        showMeetingByDate();
    }

    private void showScheduledMeeting() {
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getDateFromTimestamp(data.getValue().getFinishDatetime())));
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getOccurDatetime())));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getFinishDatetime())));
        selectedTypeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSelectedClassification()));
        typeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getClassification()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        ObservableList<Meeting> data = FXCollections.observableArrayList(meetings);

        meetingTable.setItems(data);

        meetingTable.setRowFactory(tv -> {
            TableRow<Meeting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Meeting rowData = row.getItem();
                    currentMeeting = rowData;
                    beginTextField.setText(Format.getTimeFromTimestamp(rowData.getOccurDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getOccurDatetime()));
                    endTextField.setText(Format.getTimeFromTimestamp(rowData.getFinishDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getFinishDatetime()));
                    nameTextField.setText(rowData.getName());
                    createdTextField.setText(Format.getDateFromTimestamp(rowData.getEstablishedDatetime()));
                    typeTextField.setText(rowData.getClassification());
                    selectedTypeTextField.setText(rowData.getSelectedClassification());
                    statusTextField.setText(rowData.getStatus());
                    detailPane.setVisible(true);

                    if(!Objects.equals(rowData.getStatus(), "Ready")){
                        editButton.setDisable(false);
                        typeTextField.setEditable(true);
                        endTextField.setEditable(true);
                        beginTextField.setEditable(true);
                    }else{
                        editButton.setDisable(true);
                    }
                    showContent();
                }
            });
            return row;
        });
    }

    private void filterCombobox() {
        filterCombobox.setOnAction(e -> {
            String selectedFilter = filterCombobox.getValue();
            switch (selectedFilter) {
                case "All":
                    filterDatetime.setVisible(false);
                    ObservableList<Meeting> allData = FXCollections.observableArrayList(meetings);
                    meetingTable.setItems(allData);
                    break;
                case "By date":
                    filterDatetime.setVisible(true);
                    filterDatetime.setValue(LocalDate.now());
                    List<Meeting> meetingsByDate= meetings.stream()
                            .filter(meeting -> Objects.equals(Format.getLocalDateFromTimestamp(meeting.getOccurDatetime()), LocalDate.now()))
                            .toList();
                    ObservableList<Meeting> dataByDate = FXCollections.observableArrayList(meetingsByDate);
                    meetingTable.setItems(dataByDate);
            }
        });
    }

    private void showMeetingByDate() {
        filterDatetime.setOnAction(event -> {
            LocalDate selectedDate = filterDatetime.getValue();
            List<Meeting> meetingsByDate= meetings.stream()
                    .filter(meeting -> Objects.equals(Format.getLocalDateFromTimestamp(meeting.getOccurDatetime()) ,selectedDate))
                    .toList();
            ObservableList<Meeting> dataByDate = FXCollections.observableArrayList(meetingsByDate);
            meetingTable.setItems(dataByDate);
        });
    }

    private void showContent() {
        contentCreatedDateColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getDate())+" " +Format.getDateFromTimestamp(data.getValue().getDate())));
        contentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContent()));
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
