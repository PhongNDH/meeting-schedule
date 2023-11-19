package com.calendlygui.utils;

import com.calendlygui.CalendlyApplication;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

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

    public static void setTextToEmpty(Text... texts) {
        for (Text txt : texts) {
            txt.setText("");
        }
    }

}
