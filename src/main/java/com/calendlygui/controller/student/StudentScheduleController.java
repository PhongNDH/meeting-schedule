package com.calendlygui.controller.student;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.constant.TimeslotMessage;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import com.calendlygui.utils.SendData;
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
import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.ConstantValue.UNDEFINED_ERROR;
import static com.calendlygui.utils.Helper.extractMeetingsFromResponse;

public class StudentScheduleController implements Initializable {

    private List<Meeting> meetings = new ArrayList<>();

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
    private TableColumn<Meeting, String> beginTableColumn;

    @FXML
    private TextField beginTextField;

    @FXML
    private TextField classificationTextField;

    @FXML
    private Button closeButton;

    @FXML
    private TextField createdTextField;

    @FXML
    private TableColumn<Meeting, String> dateTableColumn;

    @FXML
    private Text datetimeRangeText;

    @FXML
    private Pane detailPane;

    @FXML
    private TableColumn<Meeting, String> endTableColumn;

    @FXML
    private TextField endTextField;

    @FXML
    private ComboBox<String> filterCombobox;

    @FXML
    private DatePicker filterDatetime;

    @FXML
    private TableView<Meeting> meetingTable;

    @FXML
    private TextField nameTextField;

    @FXML
    private TextField statusTextField;

    @FXML
    private TableColumn<Meeting, String> teacherTableColumn;

    @FXML
    private TableColumn<Meeting, String> statusTableColumn;

    @FXML
    private TextField teacherTextField;

    @FXML
    private TableColumn<Meeting, String> typeTableColumn;

    @FXML
    private Button cancelButton;

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
    }

    @FXML
    void navigateToSetting(MouseEvent event) {
        Controller.navigateToOtherStage(settingButton, "student-setting.fxml", "Setting");
    }

    @FXML
    void navigateToTimeslot(MouseEvent event) {
        Controller.navigateToOtherStage(timeslotButton, "student-timeslot.fxml", "Time slots");
    }

    @FXML
    void cancelMeeting(MouseEvent event) {

    }

    @FXML
    void closeDialog(MouseEvent event) {
        detailPane.setVisible(false);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            CalendlyApplication.client = new Socket(InetAddress.getByName(ConstantValue.HOST_ADDRESS), ConstantValue.PORT);
            out = new PrintWriter(CalendlyApplication.client.getOutputStream(), true, StandardCharsets.UTF_8);
            in = new BufferedReader(new InputStreamReader(CalendlyApplication.client.getInputStream(), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            CalendlyApplication.shutdown();
        }

        Controller.changeFormatForDatepicker(filterDatetime);
        SendData.viewScheduledMeeting(out, CalendlyApplication.user.getId());

        Thread receiveThread = getReceiveThread();
        receiveThread.start();

//        meetings.add(new Meeting(1, 1, "First Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
//                Format.createTimestamp(2023, 12, 26, 8, 30),
//                Format.createTimestamp(2023, 12, 26, 8, 50), "Both","Pending"));
//        meetings.add(new Meeting(2, 2, "Second Meeting", Format.createTimestamp(2023, 12, 25, 8, 30),
//                        Format.createTimestamp(2023, 12, 26, 8, 30),
//                        Format.createTimestamp(2023, 12, 26, 8, 50), "Group", "Pending")
//                );
//        meetings.add(new Meeting(3, 1, "Third Meeting", Format.createTimestamp(2023, 12, 27, 8, 30),
//                Format.createTimestamp(2024, 1, 5, 6, 20),
//                Format.createTimestamp(2024, 1, 5, 6, 50), "Individual", "Pending"));
//        filterCombobox.setValue("All");
//        filterDatetime.setVisible(false);
//        showScheduledMeeting();
//        filterCombobox();
    }

    private Thread getReceiveThread(){
        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    response = response.replaceAll(NON_PRINTABLE_CHARACTER, "");
                    System.out.println("Response: " + response);
                    String[] info = response.split(COMMAND_DELIMITER);
                    if (Integer.parseInt(info[0]) == QUERY_SUCCESS) {
                        //navigateToHomePage();
                        meetings = extractMeetingsFromResponse(response);
                        for(Meeting meeting: meetings) System.out.println(meeting);
                        filterCombobox.setValue("All");
                        filterDatetime.setVisible(false);
                        showScheduledMeeting();
                        filterCombobox();
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
            } catch (NumberFormatException | ParseException e) {
                throw new RuntimeException(e);
            }
        });

        receiveThread.setDaemon(true);
        return receiveThread;
    }

    private void filterCombobox() {
        filterCombobox.setOnAction(e -> {
            String selectedFilter = filterCombobox.getValue();
            switch (selectedFilter) {
                case "All":
                    filterDatetime.setVisible(false);
                    datetimeRangeText.setText("");
                    datetimeRangeText.setVisible(false);
                    ObservableList<Meeting> allData = FXCollections.observableArrayList(meetings);
                    meetingTable.setItems(allData);
                    break;
                case "By week":
                    filterDatetime.setVisible(true);
                    filterDatetime.setValue(LocalDate.now());
                    datetimeRangeText.setVisible(true);
                    List<LocalDate> dates = Format.getWeekFromDate(LocalDate.now());
                    datetimeRangeText.setText(Format.getStringValueOfLocalDate(dates.get(0)) + " to " + Format.getStringValueOfLocalDate(dates.get(6)));
                    showWeekFromDate();
                    List<Meeting> meetingsByWeek = meetings.stream()
                            .filter(meeting -> dates.contains(Format.getLocalDateFromTimestamp(meeting.getOccurDatetime())))
                            .toList();
                    ObservableList<Meeting> dataByWeek = FXCollections.observableArrayList(meetingsByWeek);
                    meetingTable.setItems(dataByWeek);

            }
        });
    }

    private void showWeekFromDate() {
        filterDatetime.setOnAction(event -> {
            LocalDate selectedDate = filterDatetime.getValue();
            List<LocalDate> dates = Format.getWeekFromDate(selectedDate);
            datetimeRangeText.setText(Format.getStringValueOfLocalDate(dates.get(0)) + " to " + Format.getStringValueOfLocalDate(dates.get(6)));
            List<Meeting> meetingsByWeek = meetings.stream()
                    .filter(meeting -> dates.contains(Format.getLocalDateFromTimestamp(meeting.getOccurDatetime())))
                    .toList();
            ObservableList<Meeting> dataByWeek = FXCollections.observableArrayList(meetingsByWeek);
            meetingTable.setItems(dataByWeek);
        });
    }

    private void showScheduledMeeting() {
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getDateFromTimestamp(data.getValue().getFinishDatetime())));
        teacherTableColumn.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getTeacherId())));
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getOccurDatetime())));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getTimeFromTimestamp(data.getValue().getFinishDatetime())));
        typeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSelectedClassification()));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getStatus()));

        ObservableList<Meeting> data = FXCollections.observableArrayList(meetings);

        meetingTable.setItems(data);

        meetingTable.setRowFactory(tv -> {
            TableRow<Meeting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Meeting rowData = row.getItem();
                    //System.out.println("Double click on: "+rowData.getTeacherId());
                    teacherTextField.setText(String.valueOf(rowData.getTeacherId()));
                    endTextField.setText(Format.getTimeFromTimestamp(rowData.getFinishDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getFinishDatetime()));
                    beginTextField.setText(Format.getTimeFromTimestamp(rowData.getOccurDatetime()) +  " " +Format.getDateFromTimestamp(rowData.getOccurDatetime()));
                    nameTextField.setText(rowData.getName());
                    createdTextField.setText(Format.getDateFromTimestamp(rowData.getEstablishedDatetime()));
                    classificationTextField.setText(rowData.getSelectedClassification());
                    statusTextField.setText(rowData.getStatus());
                    detailPane.setVisible(true);
                }
            });
            return row ;
        });
    }

    private void showErrorFromServerToUIAndConsole(String error) {
        System.out.println(error);
        dealWithErrorMessageFromServer(error);
    }

    private void dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case GeneralMessage.SERVER_WRONG -> {
                errorText.setText(GeneralMessage.SERVER_WRONG);
            }
            case GeneralMessage.UNKNOWN_ERROR -> {
                errorText.setText(GeneralMessage.UNKNOWN_ERROR);
            }
            default -> {
                Controller.setTextToEmpty(errorText);
            }
        }
    }

    private void navigateToTimeslotPage() throws IOException {
        if (CalendlyApplication.user == null) return;
        //Controller.navigateToOtherStage(joinButton, "student-timeslot.fxml", "Time slots");
    }

}
