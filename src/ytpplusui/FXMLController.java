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

    private String jobDir;

    private YTPGenerator ytp = new YTPGenerator();

    @FXML
    void addSource(ActionEvent event) {
        FileChooser.ExtensionFilter fileFilter = new FileChooser.ExtensionFilter("All video files supported by ffmpeg", "*");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileFilter);
        fileChooser.setTitle("Choose Source");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        List<File> selected = fileChooser.showOpenMultipleDialog(null);
        if (selected == null)
            return;

        ytp.setFFprobe(SettingsController.getController().getFFprobe());
        for (File file : selected) {
            String path = file.getAbsolutePath().replace('\\', '/');
            if (ytp.addSource(path))
                sourceList.add(path);
        }
        listviewSourcesList.setItems(sourceList);
        LAST_BROWSED = selected.get(0).getParentFile();
    }

    private Thread vidThread;
    @FXML
    void goNow(ActionEvent event) throws Exception {
        if (sourceList.isEmpty()) {
            System.out.println("You need some sources...");
            return;
        }

        SettingsController cfg = SettingsController.getController();
        EffectsController effects = EffectsController.getController();

        String TEMP = cfg.getTemp();

        ytp.setFFmpeg(cfg.getFFmpeg());
        ytp.setFFprobe(cfg.getFFprobe());
        ytp.setMagick(cfg.getMagick());

        jobDir = TEMP + "job_" + System.currentTimeMillis() + "/";
        ytp.setOutputFile(TEMP + "tempoutput.mp4");
        ytp.setTemp(jobDir);
        new File(TEMP).mkdir();
        new File(ytp.getTemp()).mkdir();
        ytp.setSounds(cfg.getSounds());
        ytp.setMusic(cfg.getMusic());
        ytp.setResources(cfg.getResources());
        ytp.setSources(cfg.getSources());

        ytp.setTransitionClipChance(effects.getTransitionChance());
        ytp.setEffectChance(effects.getEffectChance());
        ytp.setEffect("RandomSound", effects.getRandomSound());
        ytp.setEffect("RandomSoundMute", effects.getRandomSoundMute());
        ytp.setEffect("Reverse", effects.getReverse());
        ytp.setEffect("SpeedUp", effects.getSpeedUp());
        ytp.setEffect("SlowDown", effects.getSlowDown());
        ytp.setEffect("Chorus", effects.getChorus());
        ytp.setEffect("Vibrato", effects.getVibrato());
        ytp.setEffect("HighPitch", effects.getHighPitch());
        ytp.setEffect("LowPitch", effects.getLowPitch());
        ytp.setEffect("Dance", effects.getDance());
        ytp.setEffect("Squidward", effects.getSquidward());
        ytp.setEffect("Mirror", effects.getMirror());

        ytp.setMaxClips(effects.getClipCount());
        ytp.setMaxDuration(effects.getMaxStream());
        ytp.setMinDuration(effects.getMinStream());

        ytp.setLazySwitch(effects.getLazySwitch());
        ytp.setLazySwitchChance(effects.getLazySwitchChance());
        ytp.setLazySwitchInterrupt(effects.getLazySwitchInterrupt());
        ytp.setLazySwitchMaxClips(effects.getLazySwitchMaxClips());

        if (effects.getLazySwitch() && cfg.getLazySource().length() > 0)
            ytp.setLazySwitchStartingSource(cfg.getLazySource());

        ytp.setLazySeek(effects.getLazySeek());
        ytp.setLazySeekChance(effects.getLazySeekChance());
        ytp.setLazySeekFromStart(effects.getLazySeekFromStart());
        ytp.setLazySeekInterrupt(effects.getLazySeekInterrupt());
        ytp.setLazySeekMaxClips(effects.getLazySeekMaxClips());
        ytp.setLazySeekNearby(effects.getLazySeekNearby());
        ytp.setLazySeekNearbyMin(effects.getLazySeekNearbyMin());
        ytp.setLazySeekNearbyMax(effects.getLazySeekNearbyMax());
        ytp.setLazySeekSameChance(effects.getLazySeekSameChance());

        ytp.setReconvertEffected(cfg.getReconvertClips());

        YTPGenerator.ConcatMethod method;
        switch (cfg.getConcatMethod()) {
            case 0:
                method = YTPGenerator.ConcatMethod.DEMUXER;
                break;
            case 1:
                method = YTPGenerator.ConcatMethod.CONCAT_PROTO;
                break;
            case 2:
            default:
                method = YTPGenerator.ConcatMethod.CONCAT_FILTER;
                System.err.println("Concat filter disables clip reconvertion");
                ytp.setReconvertEffected(false);
                break;
        }
        ytp.setConcatMethod(method);

        ytp.setProgressCallback(ytp.new ProgressCallback() {
            private double pv = 0.0;
            @Override
            public synchronized void progress(double v) {
                if (v > pv)
                    barProgress.setProgress((pv = v));
            }
        });
        vidThread = new Thread() {
            @Override
            public void run() {
                try {
                    btnCreate.setDisable(true);
                    barProgress.setProgress(0);
                    ytp.go();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                btnCreate.setDisable(false);
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
        Utilities.rmDir(new File(jobDir));
    }

    private ObservableList<String> getSelectedSources() {
        return listviewSourcesList.getSelectionModel().getSelectedItems();
    }

    @FXML
    void removeSource(ActionEvent event) {
        ObservableList<String> selection = getSelectedSources();
        if (selection.size() > 0) {
            for (final String s : selection)
                ytp.removeSource(s);

            sourceList.removeAll(selection);
        }
    }

    @FXML
    void removeAllSource(ActionEvent event) {
        sourceList.clear();
        ytp.clearSources();
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
