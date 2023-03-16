package com.android.scrcpy.scrcpyfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        stage.getIcons().add(new Image("/android.png"));
        stage.setResizable(false);//不准拖动改变大小
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("ScrcpyFX v1.0");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}