package com.calendlygui.controller.teacher;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.constant.TimeslotMessage;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import com.calendlygui.utils.SendData;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
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
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.constant.ConstantValue.UNDEFINED_ERROR;

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
    private Text beginErrorText;

    @FXML
    private Text datetimeErrorText;

    @FXML
    private Text endErrorText;

    @FXML
    private Text durationErrorText;

    @FXML
    private Text classificationErrorText;

    @FXML
    private Text meetingNameErrorText;

    @FXML
    private Text otherErrorText;


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
    }

    @FXML
    void createTimeSlot(MouseEvent event) {
        String meetingName = meetingNameTextField.getText();
        LocalDate meetingTime = meetingTimeDatePicker.getValue();
        String beginTime = beginTextField.getText();
        String endTime = endTextField.getText();
        String classification = classificationComboBox.getValue().toLowerCase();
        if (dealWithErrorMessageFromUI(meetingTime, beginTime, endTime, meetingName)) {
            try {
                SendData.createMeeting(out, meetingName, Format.getStringFormatFromLocalDate(meetingTime), beginTime, endTime, classification, CalendlyApplication.user.getId());
            } catch (IOException | ClassNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        classificationComboBox.setValue("Group");
        Controller.changeFormatForDatepicker(meetingTimeDatePicker);
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
                        navigateToAppointment();
                        System.out.println(TimeslotMessage.TIMESLOT_SUCCESS);
                    } else {
                        int code = Integer.parseInt(info[0]);
                        switch (code) {
                            case DUPLICATE_SCHEDULE: {
                                showErrorFromServerToUIAndConsole(TimeslotMessage.TIMESLOT_TIME_CONFLICT);
                                break;
                            }
                            case SQL_ERROR: {
                                //System.out.println(GeneralMessage.SERVER_WRONG);
                                showErrorFromServerToUIAndConsole(GeneralMessage.SERVER_WRONG);
                                break;
                            }
                            case UNDEFINED_ERROR: {
                                //System.out.println(GeneralMessage.UNKNOWN_ERROR);
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


    private boolean dealWithErrorMessageFromUI(LocalDate meetingTime, String beginTime, String endTime, String meetingName) {
        boolean isNameAdmissible = false;
        boolean isDateAdmissible = false;
        boolean isBeginAdmissible = false;
        boolean isEndAdmissible = false;
        boolean isDurationAdmissible = false;

        String[] beginTimeStr = beginTime.split(":");
        LocalDateTime beginLocalDateTime = LocalDateTime.of(meetingTime.getYear(), meetingTime.getMonthValue(), meetingTime.getDayOfMonth(),Integer.parseInt(beginTimeStr[0]), Integer.parseInt(beginTimeStr[1]) );

        if (meetingName.isEmpty()) {
            meetingNameErrorText.setText(GeneralMessage.REQUIRED_FIELD);
        } else {
            meetingNameErrorText.setText("");
            isNameAdmissible = true;
        }

        if (meetingTime == null) {
            datetimeErrorText.setText(GeneralMessage.REQUIRED_FIELD);
        } else if (beginLocalDateTime.isBefore(LocalDateTime.now())) {
            datetimeErrorText.setText(TimeslotMessage.TIMESLOT_DATETIME_PAST);
        } else if (Format.getNumberOfDateFromNow(meetingTime) > MAX_TIME_WAITING) {
            datetimeErrorText.setText(TimeslotMessage.TIMESLOT_DATETIME_SURPASS);
        } else {
            isDateAdmissible = true;
            datetimeErrorText.setText("");
        }

        if (beginTime.isEmpty()) {
            beginErrorText.setText(GeneralMessage.REQUIRED_FIELD);
        } else if (!Format.CheckFormat(beginTime, TIME_FORMAT)) {
            beginErrorText.setText(TimeslotMessage.TIMESLOT_TIME_WRONG_FORMAT);
        } else {
            isBeginAdmissible = true;
            beginErrorText.setText("");
        }

        if (endTime.isEmpty()) {
            endErrorText.setText(GeneralMessage.REQUIRED_FIELD);
        } else if (!Format.CheckFormat(endTime, TIME_FORMAT)) {
            endErrorText.setText(TimeslotMessage.TIMESLOT_TIME_WRONG_FORMAT);
        } else {
            isEndAdmissible = true;
            endErrorText.setText("");
        }

        if (isBeginAdmissible && isEndAdmissible) {
            if (Format.getMinutesBetweenTwoTime(beginTime, endTime) > TIMESLOT_MAX_DURATION || Format.getMinutesBetweenTwoTime(beginTime, endTime) < TIMESLOT_MIN_DURATION) {
                durationErrorText.setText(TimeslotMessage.TIMESLOT_DURATION_WRONG);
            } else {
                isDurationAdmissible = true;
                durationErrorText.setText("");
            }
        } else {
            durationErrorText.setText("");
        }
        return isBeginAdmissible && isEndAdmissible && isNameAdmissible && isDateAdmissible && isDurationAdmissible;
    }

    private void showErrorFromServerToUIAndConsole(String error) {
        System.out.println(error);
        dealWithErrorMessageFromServer(error);
    }

    private void dealWithErrorMessageFromServer(String message) {
        switch (message) {
            case TimeslotMessage.TIMESLOT_TIME_CONFLICT -> {
                otherErrorText.setText(TimeslotMessage.TIMESLOT_TIME_CONFLICT);
                Controller.setTextToEmpty(datetimeErrorText, beginErrorText, endErrorText);
            }
            case GeneralMessage.SERVER_WRONG -> {
                otherErrorText.setText(GeneralMessage.SERVER_WRONG);
                Controller.setTextToEmpty(datetimeErrorText, beginErrorText, endErrorText);
            }
            case GeneralMessage.UNKNOWN_ERROR -> {
                otherErrorText.setText(GeneralMessage.UNKNOWN_ERROR);
                Controller.setTextToEmpty(datetimeErrorText, beginErrorText, endErrorText);
            }
            default -> {
                Controller.setTextToEmpty(datetimeErrorText, beginErrorText, endErrorText, otherErrorText);
            }
        }
    }

    private void navigateToHomePage() throws IOException {
        if (CalendlyApplication.user == null) return;
        Controller.navigateToOtherStage(meetingCreationButton, "teacher.fxml", "Teacher");
    }

    private void navigateToAppointment() {
        if (CalendlyApplication.user == null) return;
        Controller.navigateToOtherStage(meetingCreationButton, "teacher-appointment.fxml", "Appointment");
    }
}