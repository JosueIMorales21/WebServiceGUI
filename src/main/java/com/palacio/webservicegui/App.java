package com.palacio.webservicegui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import static com.palacio.webservicegui.LogConfig.LogConfig.loadConfig;
import static com.palacio.webservicegui.Properties.ConfigProperties.loadProperties;

public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        try {
            loadProperties();
            loadConfig();
        } catch (Exception e) {
            System.out.println("ERROR AL CARGAR DATOS..." + e);
        }
        FXMLLoader loader = new FXMLLoader(App.class.getResource("primary.fxml"));
        Parent root = loader.load();

        Controller controller = loader.getController();

        controller.radioButtonsConfig();

        Scene scene = new Scene(root, 700, 400);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

}
