package com.github.MaierFlorian.testsmelldetection.gui;

import com.github.MaierFlorian.testsmelldetection.util.ControllerFromOutside;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class MainApplication extends Application {

    private static double xOffset = 0;
    private static double yOffset = 0;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/github/MaierFlorian/testsmelldetection/Home.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        ControllerFromOutside.homeController = loader.<HomeController>getController();
        stage.initStyle(StageStyle.UNDECORATED);

        // Enables moving around the application window:
        root.setOnMousePressed(new EventHandler
                <MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                // drag around
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle(MouseEvent event){
                // drag around
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}