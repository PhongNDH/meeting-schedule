package com.calendlygui.utils;

import com.calendlygui.CalendlyApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Objects;

import static java.time.ZoneId.systemDefault;

public class Controller {
    public static void navigateToOtherStage(Button button , String fxmlFile, String title) {
        //Allow to execute a Runnable object in the JavaFX Application Thread asynchronously
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) button.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(CalendlyApplication.class.getResource(fxmlFile));
                Scene scene = new Scene(loader.load());
                stage.setTitle(title);
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ;
        });
    }

    public static void navigateToOtherStage(Label label , String fxmlFile, String title) {
        //Allow to execute a Runnable object in the JavaFX Application Thread asynchronously
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) label.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(CalendlyApplication.class.getResource(fxmlFile));
                Scene scene = new Scene(loader.load());
                stage.setTitle(title);
                stage.setScene(scene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ;
        });
    }

    public static void closeApplication(Button button){
        Platform.runLater(() -> {
            Stage stage = (Stage) button.getScene().getWindow();
            stage.close();
        });
    }

    public static void setTextToEmpty(Text... texts) {
        for (Text txt : texts) {
            txt.setText("");
        }
    }

    public static void setTextFieldToEmpty(TextField... texts) {
        for (TextField txt : texts) {
            txt.setText("");
        }
    }

    public static void initialiseProfile(TextField usernameTextField, TextField emailTextField, TextField roleTextField, TextField genderTextField, TextField registerDatetimeTextfield, ImageView avatarImage){
        usernameTextField.setText(CalendlyApplication.user.getUsername());
        emailTextField.setText(CalendlyApplication.user.getEmail());
        String role = CalendlyApplication.user.isTeacher() ? "Teacher" : "Student";
        roleTextField.setText(role);
        String gender = CalendlyApplication.user.getGender() ? "Male" : "Female";
        genderTextField.setText(gender);
        Timestamp registerDatetime = CalendlyApplication.user.getRegisterDatetime();
        Date registerDate = new Date(registerDatetime.getTime());
        LocalDate localDate = registerDate.toInstant().atZone(systemDefault()).toLocalDate();
        registerDatetimeTextfield.setText(Format.getStringFormatFromTimestamp(registerDatetime, "dd/MM/yyyy"));
        if (gender.equals("Male")) {
            if (role.equals("Student")) {
                avatarImage.setImage(new Image(Objects.requireNonNull(Controller.class.getResource("/assets/avatar/male2.png")).toExternalForm()));
            } else {
                avatarImage.setImage(new Image(Objects.requireNonNull(Controller.class.getResource("/assets/avatar/male.png")).toExternalForm()));
            }
        }else{
            if (role.equals("Student")) {
                avatarImage.setImage(new Image(Objects.requireNonNull(Controller.class.getResource("/assets/avatar/female2.png")).toExternalForm()));
            } else {
                avatarImage.setImage(new Image(Objects.requireNonNull(Controller.class.getResource("/assets/avatar/female.png")).toExternalForm()));
            }
        }
    }

    public static void changeFormatForDatepicker(DatePicker datePicker){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // Set the StringConverter for the DateTimePicker to format dates
        datePicker.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate object) {
                if (object != null) {
                    return formatter.format(object);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                } else {
                    return null;
                }
            }
        });
    }

}
