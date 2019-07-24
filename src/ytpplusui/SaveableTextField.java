package ytpplusui;

import java.util.prefs.Preferences;
import javafx.scene.control.TextField;

public class SaveableTextField extends TextField {
    private boolean loaded = false;
    {
        textProperty().addListener((ob, oldV, newV) -> {
            if (!loaded) {
                setText(loadValue(getId()));
                loaded = true;
            } else
                saveValue(getId(), newV);
        });
    }

    protected Preferences getPreferences() {
        return Preferences.userNodeForPackage(MainApp.class);
    }

    protected String loadValue(String id) {
        return getPreferences().get(id, getText());
    }

    protected void saveValue(String id, String value) {
        getPreferences().put(id, value);
    }
}
