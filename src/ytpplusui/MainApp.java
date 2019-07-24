package ytpplusui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainApp extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent object = FXMLLoader.load(MainApp.class.getResource("FXML.fxml"));

        Scene scene = new Scene(object);
        stage.setScene(scene);
        stage.show();
        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Platform.exit();
        });
        stage.setTitle("YTP+ V1.2 Beta");

        SettingsController.init();
        EffectsController.init();

        SaveableCheckBox.loadAll();
        SaveableTextField.loadAll();
    }
}
