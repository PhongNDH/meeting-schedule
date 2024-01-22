package com.calendlygui.controller.teacher;

import com.calendlygui.CalendlyApplication;
import com.calendlygui.constant.ConstantValue;
import com.calendlygui.constant.GeneralMessage;
import com.calendlygui.model.entity.Content;
import com.calendlygui.model.entity.Meeting;
import com.calendlygui.model.entity.User;
import com.calendlygui.utils.Controller;
import com.calendlygui.utils.Format;
import com.calendlygui.utils.SendData;
import javafx.beans.property.ObjectProperty;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static com.calendlygui.CalendlyApplication.in;
import static com.calendlygui.CalendlyApplication.out;
import static com.calendlygui.constant.ConstantValue.*;
import static com.calendlygui.utils.Helper.extractMeetingsFromResponse;

public class TeacherHistoryController implements Initializable {

    private List<Meeting> meetings = new ArrayList<>();

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
    private Button contentButton;

    @FXML
    private Button reloadButton;

    @FXML
    private Button closeContentButton;

    @FXML
    private Button closeStudentPaneButton;

    @FXML
    private Button studentButton;

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
    private Text errorText;

    @FXML
    private Text contentErrorText;


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
        Controller.navigateToOtherStage(timeslotButton, "teacher-timeslot.fxml", "New meeting");
    }

    @FXML
    void reload(MouseEvent event) throws IOException {
        navigateToHistoryPage(reloadButton);
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
        if (contentTextArea.getText().isEmpty()) {
            contentErrorText.setText(GeneralMessage.REQUIRED_FIELD);
        } else {
            contentErrorText.setText("");
            SendData.addContent(out, currentMeeting.getId(), contentTextArea.getText());
        }
    }

    @FXML
    void closeContentPane(MouseEvent event) {
        contentPane.setVisible(false);
        Controller.setTextToEmpty(contentErrorText);
    }

    @FXML
    void closeDetailPane(MouseEvent event) {
        detailPane.setVisible(false);
        Controller.setTextToEmpty(errorText, contentErrorText);
    }

    @FXML
    void closeStudentPane(MouseEvent event) {
        studentPane.setVisible(false);
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

        SendData.viewHistory(out, CalendlyApplication.user.getId());

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
                    int code = Integer.parseInt(info[0]);
                    if (code == QUERY_SUCCESS) {
                        //navigateToHomePage();
                        meetings = extractMeetingsFromResponse(response);
                        for (Meeting meeting : meetings) System.out.println(meeting);
                        showHistory();
                    } else if (code == CREATE_SUCCESS) {
                        System.out.println(GeneralMessage.TEACHER_ADD_CONTENT_SUCCESS);
                        navigateToHistoryPage(addContentButton);
                    } else {
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
            }
            case GeneralMessage.SERVER_WRONG -> {
                errorText.setText(GeneralMessage.SERVER_WRONG);
            }
            case GeneralMessage.REQUIRED_FIELD -> {
                contentErrorText.setText(GeneralMessage.REQUIRED_FIELD);
            }
            default -> {
                Controller.setTextToEmpty(errorText, contentErrorText);
            }
        }
    }

    private void showHistory() {
        beginTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getOccurDatetime(), "HH:mm")));
        endTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getFinishDatetime(), "HH:mm")));
        selectedTypeTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Objects.equals(data.getValue().getSelectedClassification(), "null") ? "Not yet" : Format.writeFirstCharacterInUppercase(data.getValue().getSelectedClassification())));
        dateTableColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getOccurDatetime(), "dd/MM/yyyy")));
        statusTableColumn.setCellValueFactory(data -> new SimpleStringProperty(specifyStatus(data.getValue().getStudents())));

        ObservableList<Meeting> data = FXCollections.observableArrayList(meetings);

        historyTable.setItems(data);

        historyTable.setRowFactory(ht -> {
            TableRow<Meeting> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Meeting rowData = row.getItem();
                    currentMeeting = rowData;
                    beginTextField.setText(Format.getStringFormatFromTimestamp(rowData.getOccurDatetime(), "dd/MM/yyyy HH:mm"));
                    nameTextField.setText(rowData.getName());
                    endTextField.setText(Format.getStringFormatFromTimestamp(rowData.getOccurDatetime(), "dd/MM/yyyy HH:mm"));
                    createdTextField.setText(Format.getStringFormatFromTimestamp(rowData.getEstablishedDatetime(),"dd/MM/yyyy"));
                    selectedTypeTextField.setText(Objects.equals(rowData.getSelectedClassification(), "null") ? "Not yet" : Format.writeFirstCharacterInUppercase(rowData.getSelectedClassification()));
                    statusTextField.setText(Format.writeFirstCharacterInUppercase(rowData.getStatus()));
                    detailPane.setVisible(true);

                    if (currentMeeting.getStudents().isEmpty()) {
                        studentButton.setDisable(true);
                        contentButton.setDisable(true);
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

        ObservableList<User> users = FXCollections.observableArrayList(currentMeeting.getStudents());

        studentTable.setItems(users);
    }

    private void showContents() {
        contentColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContent()));
        contentCreatedDateColumn.setCellValueFactory(data -> new SimpleStringProperty(Format.getStringFormatFromTimestamp(data.getValue().getDate(), "dd/MM/yyyy HH:mm")));

        ObservableList<Content> contents = FXCollections.observableArrayList(currentMeeting.getContents());

        contentTable.setItems(contents);

        contentTable.setRowFactory(ct -> {
            TableRow<Content> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Content content = row.getItem();
                    currentContentTextArea.setText(content.getContent());
                }
            });
            return row;
        });
    }

    private void navigateToHistoryPage(Button button) {
        if (CalendlyApplication.user == null) return;
        Controller.navigateToOtherStage(button, "teacher-history.fxml", "History");
    }

    private String specifyStatus(List<User> students) {
        if (!students.isEmpty()) return "Done";
        else return "Canceled";
    }
}
