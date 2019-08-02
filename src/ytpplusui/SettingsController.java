package ytpplusui;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.net.URI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import java.util.prefs.Preferences;
import ytpplusui.SaveableTextField;
import ytpplusui.SaveableCheckBox;

public class SettingsController {
    private static SettingsController me;
    private static Stage myStage;

    protected final static String resourceName = "SettingsPane.fxml";
    protected final static String title = "Settings";

    public static void init() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        System.out.println("loading " + resourceName);
        loader.setLocation(MainApp.class.getResource(resourceName));

        Scene scene = new Scene(loader.load());
        myStage = new Stage();
        myStage.setScene(scene);
        myStage.setResizable(false);

        me = loader.getController();
        me.initUI();
    }

    public static void open() {
        if (!myStage.isShowing()) {
            /* Fix title eventually disappearing */
            myStage.setTitle(title);
            myStage.show();
        }
    }

    public static SettingsController getController() {
        return me;
    }

    @FXML
    private SaveableTextField tfFFMPEG, tfFFPROBE, tfMAGICK, tfTEMP,
        tfSOUNDS, tfMUSIC, tfRESOURCES, tfSOURCES;

    @FXML
    private SaveableCheckBox cbReconvertClips;

    @FXML
    private TextField tfLazySource;

    @FXML
    private Button btnBrowseFFMPEG, btnBrowseFFPROBE, btnBrowseMAGICK,
        btnBrowseTEMP, btnBrowseSOUNDS, btnBrowseMUSIC,
        btnBrowseRESOURCES, btnBrowseSOURCES, btnBrowseLazySource;

    @FXML
    private ComboBox<String> comboConcatMethod;

    public void initUI() {
        SingleSelectionModel<String> model = comboConcatMethod.getSelectionModel();
        Preferences pref = Preferences.userNodeForPackage(MainApp.class);
        model.selectedIndexProperty().addListener((ob, oldV, newV) -> {
            int idx = (int)newV;
            pref.putInt(comboConcatMethod.getId(), idx);
            cbReconvertClips.setDisable(idx == 2);
        });
        model.select(pref.getInt(comboConcatMethod.getId(), 2));
    }

    public String getFFmpeg() {
        return tfFFMPEG.getText();
    }

    public String getFFprobe() {
        return tfFFPROBE.getText();
    }

    public String getMagick() {
        return tfMAGICK.getText();
    }

    public String getTemp() {
        return tfTEMP.getText() + "/";
    }

    public String getSounds() {
        return tfSOUNDS.getText() + "/";
    }

    public String getMusic() {
        return tfMUSIC.getText() + "/";
    }

    public String getResources() {
        return tfRESOURCES.getText() + "/";
    }

    public String getSources() {
        return tfSOURCES.getText() + "/";
    }

    public String getLazySource() {
        return tfLazySource.getText();
    }

    public void setLazySource(String path) {
        tfLazySource.setText(path);
    }

    public boolean getReconvertClips() {
        return cbReconvertClips.isSelected();
    }

    public int getConcatMethod() {
        return comboConcatMethod.getSelectionModel().getSelectedIndex();
    }

    @FXML
    void openBrowser(ActionEvent event) {
        switch (((Control)event.getSource()).getId()) {
            case "btnBrowseFFMPEG":
                actuallyOpenBrowser(tfFFMPEG);
                break;
            case "btnBrowseFFPROBE":
                actuallyOpenBrowser(tfFFPROBE);
                break;
            case "btnBrowseMAGICK":
                actuallyOpenBrowser(tfMAGICK);
                break;
            case "btnBrowseTEMP":
                actuallyOpenDirBrowser(tfTEMP);
                break;
            case "btnBrowseSOUNDS":
                actuallyOpenDirBrowser(tfSOUNDS);
                break;
            case "btnBrowseMUSIC":
                actuallyOpenDirBrowser(tfMUSIC);
                break;
            case "btnBrowseRESOURCES":
                actuallyOpenDirBrowser(tfRESOURCES);
                break;
            case "btnBrowseSOURCES":
                actuallyOpenDirBrowser(tfSOURCES);
                break;
            case "btnBrowseLazySource":
                actuallyOpenBrowser(tfLazySource);
        }
    }

    private File LAST_BROWSED;
    private void actuallyOpenBrowser(TextField toChange) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showOpenDialog(null);
        if (selected == null)
            return;

        toChange.setText(selected.getAbsolutePath().replace('\\', '/'));
        LAST_BROWSED = selected.getParentFile();
    }

    private void actuallyOpenDirBrowser(TextField toChange) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showDialog(null);
        if (selected == null)
            return;

        toChange.setText(selected.getAbsolutePath().replace('\\', '/') + "/");
        LAST_BROWSED = selected.getParentFile();
    }
}
