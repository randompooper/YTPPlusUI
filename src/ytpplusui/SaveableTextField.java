package ytpplusui;

import java.util.List;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import javafx.scene.control.TextField;

public class SaveableTextField extends TextField {
    private static List<SaveableTextField> myselfs = new ArrayList<SaveableTextField>();
    {
        myselfs.add(this);
    }

    public static void loadAll() {
        if (myselfs == null)
            return;

        for (SaveableTextField s : myselfs)
            s.loadAndSetup();

        myselfs.clear();
        myselfs = null;
    }

    public void loadAndSetup() {
        setText(loadValue(getId(), getText()));
        textProperty().addListener((ob, oldV, newV) -> {
            saveValue(getId(), newV);
        });
    }

    protected Preferences getPreferences() {
        return Preferences.userNodeForPackage(MainApp.class);
    }

    protected String loadValue(String id, String prev) {
        return getPreferences().get(id, prev);
    }

    protected void saveValue(String id, String value) {
        getPreferences().put(id, value);
    }
}
