/* Copyright 2019 randompooper
 * This file is part of YTPPlusUI.
 *
 * YTPPlusUI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * YTPPlusUI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with YTPPlusUI.  If not, see <https://www.gnu.org/licenses/>.
 */
package ytpplusui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.util.prefs.Preferences;

public class MainApp extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    public static Preferences prefs() {
        return Preferences.userNodeForPackage(MainApp.class);
    }

    public static Boolean load(String id, Boolean v) {
        return prefs().getBoolean(id, v);
    }

    public static void save(String id, Boolean v) {
        prefs().putBoolean(id, v);
    }

    public static Integer load(String id, Integer v) {
        return prefs().getInt(id, v);
    }

    public static void save(String id, Integer v) {
        prefs().putInt(id, v);
    }

    public static Double load(String id, Double v) {
        return prefs().getDouble(id, v);
    }

    public static void save(String id, Double v) {
        prefs().putDouble(id, v);
    }

    public static String load(String id, String v) {
        return prefs().get(id, v);
    }

    public static void save(String id, String v) {
        prefs().put(id, v);
    }

    /* TO DO: Sanely handle v == null */
    public static <T> T load(String id, T v) {
        if (v instanceof Boolean)
            return (T)load(id, (Boolean)v);
        else if (v instanceof Integer)
            return (T)load(id, (Integer)v);
        else if (v instanceof Double)
            return (T)load(id, (Double)v);
        else if (v instanceof String)
            return (T)load(id, (String)v);
        else
            System.err.println("Unknown type, returning null: " + v);

        return null;
    }

    public static <T> void save(String id, T v) {
        if (v instanceof Boolean)
            prefs().putBoolean(id, (Boolean)v);
        else if (v instanceof Integer)
            prefs().putInt(id, (Integer)v);
        else if (v instanceof Double)
            prefs().putDouble(id, (Double)v);
        else if (v instanceof String)
            prefs().put(id, (String)v);
        else
            System.err.println("Unknown type, saving nothing: " + v);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent object = FXMLLoader.load(MainApp.class.getResource("FXML.fxml"));

        Scene scene = new Scene(object);
        stage.setScene(scene);
        stage.show();
        /* Exit from application when main window closes */
        scene.getWindow().addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
            Platform.exit();
        });
        stage.setTitle("YTP+ 1.3");
    }
}
