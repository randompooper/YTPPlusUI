package ytpplusui;

import java.util.List;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.scene.control.CheckBox;

public class SaveableCheckBox extends CheckBox {
    private static List<SaveableCheckBox> myselfs = new ArrayList<SaveableCheckBox>();
    {
        myselfs.add(this);
    }

    public static void loadAll() {
        if (myselfs == null)
            return;

        for (SaveableCheckBox s : myselfs)
            s.loadAndSetup();

        myselfs.clear();
        myselfs = null;
    }

    public void loadAndSetup() {
        setSelected(loadValue(getId(), isSelected()));
        selectedProperty().addListener((ob, oldV, newV) -> {
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
