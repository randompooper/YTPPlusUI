package ytpplusui;

import java.util.prefs.Preferences;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class SaveableCheckBox extends CheckBox {
    private int loadedHAX = 0;
    {
        /* HAAAX: force firing listener in addListener */
        idProperty().addListener((ob, oldV, newV) -> {
            fire();
        });
        selectedProperty().addListener((ob, oldV, newV) -> {
            /* HAAAX: in order to finally set selected state also set same value twice */
            if (loadedHAX++ < 2)
                setSelected(loadValue(getId(), oldV));
            else
                saveValue(getId(), newV);
        });
    }

    protected Preferences getPreferences() {
        return Preferences.userNodeForPackage(MainApp.class);
    }

    protected boolean loadValue(String id, boolean prev) {
        return getPreferences().getBoolean(id, prev);
    }

    protected void saveValue(String id, boolean value) {
        getPreferences().putBoolean(id, value);
    }
}
