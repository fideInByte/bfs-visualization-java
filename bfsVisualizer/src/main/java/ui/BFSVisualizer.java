package ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class BFSVisualizer extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new Scene(new Label("JavaFX works ✅"), 400, 200));
        stage.setTitle("BFS Visualizer");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
