package ytpplusui;

import java.awt.Desktop;
import java.io.File;
import java.io.FilenameFilter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.swing.DefaultListModel;
import ytpplusui.EffectsController;
import ytpplusui.SettingsController;
import zone.arctic.ytpplus.YTPGenerator;
import zone.arctic.ytpplus.Utilities;

public class FXMLController {
    @FXML
    private TextField tfClipCount, tfMaxStream, tfMinStream;

    @FXML
    private ProgressBar barProgress;

    @FXML
    private Button btnBrowseFFMPEG, btnBrowseFFPROBE,btnBrowseMAGICK,
        btnBrowseTEMP, btnBrowseSOUNDS, btnBrowseMUSIC, btnSaveAs,
        btnBrowseRESOURCES, tfSOURCES, btnBrowseSOURCES, btnCreate;

    @FXML
    private ListView<String> listviewSourcesList;

    //javafx sucks. It's got a lot under the hood but it sucks.
    //This is incredibly messy. And I can't fix it because javafx sucks.
    //Moral of this story is don't use javafx. Swing is your friend.

    private ObservableList<String> sourceList = FXCollections.observableArrayList();

    private File LAST_BROWSED;

    @FXML
    void addSource(ActionEvent event) {
        FileChooser.ExtensionFilter fileFilter = new FileChooser.ExtensionFilter("All video files supported by ffmpeg", "*");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileFilter);
        fileChooser.setTitle("Choose Source");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        List<File> selected = fileChooser.showOpenMultipleDialog(null);
        if (selected==null) return;
        for (File file : selected) {
            sourceList.add(file.getAbsolutePath().replace('\\', '/'));
        }
        listviewSourcesList.setItems(sourceList);
        LAST_BROWSED = selected.get(0).getParentFile();
    }

    private String TEMP;
    private Thread vidThread;
    @FXML
    void goNow(ActionEvent event) throws Exception {
        if (sourceList.isEmpty()) {
            System.out.println("You need some sources...");
            return;
        }
        vidThread = new Thread() {
            public void run() {
                try {

                btnCreate.setDisable(true);
                barProgress.setProgress(0);

                SettingsController cfg = SettingsController.getController();
                EffectsController effects = EffectsController.getController();

                TEMP = cfg.getTemp();
                YTPGenerator generator = new YTPGenerator(TEMP + "tempoutput.mp4");

                generator.setFFmpeg(cfg.getFFmpeg());
                generator.setFFprobe(cfg.getFFprobe());
                generator.setMagick(cfg.getMagick());

                String jobDir = TEMP + "job_" + System.currentTimeMillis() + "/";
                generator.setTemp(jobDir);
                new File(jobDir).mkdir();
                new File(generator.getTemp()).mkdir();
                generator.setSounds(cfg.getSounds());
                generator.setMusic(cfg.getMusic());
                generator.setResources(cfg.getResources());
                generator.setSources(cfg.getSources());

                generator.setEffectChance(effects.getEffectChance());
                generator.setTransitionClipChance(effects.getTransitionChance());
                generator.setEffect("RandomSound", effects.getRandomSound());
                generator.setEffect("RandomSoundMute", effects.getRandomSoundMute());
                generator.setEffect("Reverse", effects.getReverse());
                generator.setEffect("SpeedUp", effects.getSpeedUp());
                generator.setEffect("SlowDown", effects.getSlowDown());
                generator.setEffect("Chorus", effects.getChorus());
                generator.setEffect("Vibrato", effects.getVibrato());
                generator.setEffect("HighPitch", effects.getHighPitch());
                generator.setEffect("LowPitch", effects.getLowPitch());
                generator.setEffect("Dance", effects.getDance());
                generator.setEffect("Squidward", effects.getSquidward());
                generator.setEffect("Mirror", effects.getMirror());

                for (final String source : sourceList)
                    generator.addSource(source);

                int maxclips = effects.getClipCount();
                generator.setMaxClips(maxclips);
                generator.setMaxDuration(effects.getMaxStream());
                generator.setMinDuration(effects.getMinStream());

                generator.setLazySwitch(effects.getLazySwitch());
                generator.setLazySwitchChance(effects.getLazySwitchChance());
                generator.setLazySwitchInterrupt(effects.getLazySwitchInterrupt());
                generator.setLazySwitchMaxClips(effects.getLazySwitchMaxClips());
                if (effects.getLazySwitch() && cfg.getLazySource().length() > 0)
                    generator.setLazySwitchStartingSource(cfg.getLazySource());

                generator.setProgressCallback(generator.new ProgressCallback() {
                    private double pv = 0.0;
                    @Override
                    public synchronized void progress(double v) {
                        if (v > pv)
                            barProgress.setProgress((pv = v));
                    }
                });
                generator.go();
                barProgress.setProgress(1);
                btnCreate.setDisable(false);

                } catch (Exception ex) {
                    btnCreate.setDisable(false);
                    ex.printStackTrace();
                }
                vidThread = null;
            }
        };
        vidThread.start();
    }

    @FXML
    void emergStop() {
        if (vidThread == null)
            return;

        vidThread.stop();
        vidThread = null;
        btnCreate.setDisable(false);
    }

    private ObservableList<String> getSelectedSources() {
        return listviewSourcesList.getSelectionModel().getSelectedItems();
    }

    @FXML
    void removeSource(ActionEvent event) {
        ObservableList<String> selection = getSelectedSources();
        if (selection.size() > 0)
            sourceList.removeAll(selection);
    }

    @FXML
    void removeAllSource(ActionEvent event) {
        sourceList.clear();
    }

    @FXML
    void saveAsVideo(ActionEvent event) {
        SettingsController cfg = SettingsController.getController();
        if (!new File(cfg.getTemp() + "tempoutput.mp4").exists())
            return;

        FileChooser.ExtensionFilter fileFilter = new FileChooser.ExtensionFilter("Video files (*.mp4)", "*.mp4");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileFilter);
        fileChooser.setTitle("Choose Source");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showSaveDialog(null);
        if (selected == null)
            return;

        Path temp = Paths.get(cfg.getTemp() + "tempoutput.mp4");
        try {
            Files.copy(temp, selected.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            System.out.println("Had a problem copying the file.");
        }
    }

    @FXML
    void openEffectsWindow(ActionEvent event) {
        EffectsController.open();
    }

    @FXML
    void openSettingsWindow(ActionEvent event) {
        SettingsController.open();
    }

    @FXML
    void makeLazy(ActionEvent event) {
        ObservableList<String> list = getSelectedSources();
        if (list.size() > 0)
            SettingsController.getController().setLazySource(list.get(0));
    }

    public static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
