package com.calendlygui.controller.student;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.constant.TimeslotMessage;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import com.calendlygui.utils.SendData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.beans.property.SimpleStringProperty;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.extractMeetingsFromResponse;
import static com.calendlygui.utils.Helper.extractUserFromResponse;

public class StudentTimeslotController implements Initializable {
    private ArrayList<Meeting> meetings = null;
    private Meeting currentMeeting = null;
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
    private TableColumn<Meeting, String> meetingNameTableColumn;

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

    @FXML
    private Text errorText;

    @FXML
    private Text meetingDetailErrorText;

    @FXML
    private Button reloadButton;

    @FXML
    void reload(MouseEvent event) throws IOException {
        navigateToTimeslotPage(reloadButton);
    }

    @FXML
    void closeDialog(MouseEvent event) {
        Controller.setTextFieldToEmpty(teacherTextField,beginTextField,endTextField,createdTextField, nameTextField);
        classificationCombobox.getItems().clear();
        meetingDetailErrorText.setText("");
        currentMeeting = null;
        detailPane.setVisible(false);
    }

    @FXML
    void joinMeeting(MouseEvent event) throws IOException, ParseException {
        String type = classificationCombobox.getValue().toLowerCase();
        SendData.scheduleMeeting(out, CalendlyApplication.user.getId(), currentMeeting.getId(), type);
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

        teacherTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTeacherName()));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getFinishDatetime(),"dd/MM/yyyy")));
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getOccurDatetime(),"HH:mm")));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getFinishDatetime(), "HH:mm")));
        typeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.writeFirstCharacterInUppercase(data.getValue().getClassification())));
        meetingNameTableColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));


        SendData.viewAvailableSlots(out, CalendlyApplication.user.getId());

        Thread receiveThread = getReceiveThread();
        receiveThread.start();
        
    }

    private Thread getReceiveThread() {
        Thread receiveThread = new Thread(() -> {
            try {
                String response;
                while ((response = in.readLine()) != null) {
                    response = response.replaceAll(NON_PRINTABLE_CHARACTER,"");
                    System.out.println("Response: " + response);
                    String[] info = response.split(COMMAND_DELIMITER);
                    int code = Integer.parseInt(info[0]);
                    if (code == QUERY_SUCCESS) {
                        //CalendlyApplication.user = extractUserFromResponse(response);
                        //navigateToHomePage();
                        meetings = extractMeetingsFromResponse(response);
                        System.out.println(meetings.size());
                        for(Meeting meeting: meetings) System.out.println(meeting);
                        showValueToMeetingTable(meetings);
                    }
                    else if(code == UPDATE_SUCCESS){
                        System.out.println(GeneralMessage.STUDENT_JOIN_MEETING_SUCCESS);
                        navigateToTimeslotPage(joinButton);
                    }
                    else {
                        switch (code) {
                            case SQL_ERROR: {
                                showErrorFromServerToUIAndConsole(GeneralMessage.SERVER_WRONG);
                                break;
                            }
                            case UNDEFINED_ERROR: {
                                showErrorFromServerToUIAndConsole(GeneralMessage.UNKNOWN_ERROR);
                                break;
                            }
                            case NOT_UP_TO_DATE : {
                                showErrorFromServerToUIAndConsole(GeneralMessage.NOT_UP_TO_DATE);
                                break;
                            }
                            case DUPLICATE_SCHEDULE : {
                                showErrorFromServerToUIAndConsole(TimeslotMessage.TIMESLOT_TIME_CONFLICT);
                                break;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
                CalendlyApplication.shutdown();
            } catch (ParseException | NumberFormatException e) {
                System.out.println(e.getMessage());
            }
        });

        receiveThread.setDaemon(true);
        return receiveThread;
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
            case GeneralMessage.NOT_UP_TO_DATE -> {
                meetingDetailErrorText.setText(GeneralMessage.NOT_UP_TO_DATE);
            }
            case TimeslotMessage.TIMESLOT_TIME_CONFLICT -> {
                meetingDetailErrorText.setText(TimeslotMessage.TIMESLOT_TIME_CONFLICT);
            }
            default -> {
                Controller.setTextToEmpty(errorText, meetingDetailErrorText);
            }
        }
    }

    private void showValueToMeetingTable(List<Meeting> meetings){
        ObservableList<Meeting> data = FXCollections.observableArrayList(meetings);

        meetingTable.setItems(data);

        meetingTable.setRowFactory(tv -> {
            TableRow<Meeting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Meeting rowData = row.getItem();
                    currentMeeting = rowData;
                    //System.out.println("Double click on: "+rowData.getTeacherId());
                    teacherTextField.setText(rowData.getTeacherName());
                    beginTextField.setText(Format.getStringFormatFromTimestamp(rowData.getOccurDatetime(), "HH:mm dd/MM/yyyy"));
                    endTextField.setText(Format.getStringFormatFromTimestamp(rowData.getFinishDatetime(), "HH:mm dd/MM/yyyy"));
                    nameTextField.setText(rowData.getName());
                    createdTextField.setText(Format.getStringFormatFromTimestamp(rowData.getEstablishedDatetime(), "dd/MM/yyyy"));
                    classificationCombobox.getItems().clear();
                    if(Objects.equals(rowData.getClassification(), "both")){
                        System.out.println(currentMeeting.getSelectedClassification());
                        if(Objects.equals(currentMeeting.getSelectedClassification(), "null")){
                            classificationCombobox.getItems().addAll("Group", "Individual");
                            classificationCombobox.setValue("Group");
                        }
                        else{
                            classificationCombobox.getItems().add(Format.writeFirstCharacterInUppercase(currentMeeting.getSelectedClassification()));
                            classificationCombobox.setValue(Format.writeFirstCharacterInUppercase(currentMeeting.getSelectedClassification()));
                        }

                    }else{
                        classificationCombobox.getItems().add(Format.writeFirstCharacterInUppercase(rowData.getClassification()) );
                        classificationCombobox.setValue(Format.writeFirstCharacterInUppercase(rowData.getClassification()));
                    }
                    detailPane.setVisible(true);
                }
            });
            return row ;
        });
    }

    private void navigateToHomePage() throws IOException {
        if (CalendlyApplication.user == null) return;
        Controller.navigateToOtherStage(joinButton, "teacher.fxml", "Teacher");
    }

    private void navigateToTimeslotPage(Button button) throws IOException {
        if (CalendlyApplication.user == null) return;
        Controller.navigateToOtherStage(button, "student-timeslot.fxml", "New meeting");
    }
}