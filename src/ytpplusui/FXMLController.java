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
import java.lang.reflect.Method;
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
import javafx.stage.FileChooser;
import javax.swing.DefaultListModel;
import ytpplusui.SettingsWindow;
import ytpplusui.SettingsOption;
import ytpplusui.SettingsOption.Value;
import ytpplusui.SettingsOption.OptionType;
import ytpplus.YTPGenerator;
import ytpplus.Utilities;

public class FXMLController {
    @FXML
    void makeLazy(ActionEvent event) {
        PathElement p = (PathElement)cfg.lookup("#LazySwitchStartingSource");
        assert(p != null);

        ObservableList<String> selection = getSelectedSources();
        if (selection.size() < 1)
            return;

        p.setPath(selection.get(0));
    }

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

        /* TO DO: Open dialog and report progress on how each video
         * processes
         */
        for (File file : selected) {
            String path = file.getAbsolutePath().replace('\\', '/');
            if (ytp.addSource(path))
                sourceList.add(path);
        }
        listviewSourcesList.setItems(sourceList);
        LAST_BROWSED = selected.get(0).getParentFile();
    }

    @FXML
    void goNow(ActionEvent event) throws Exception {
        if (sourceList.isEmpty()) {
            System.out.println("You need some sources...");
            return;
        }
        jobDir = TEMP + "/job_" + System.currentTimeMillis() + "/";
        ytp.setOutputFile(TEMP + "/tempoutput.mp4");
        ytp.setTemp(jobDir);
        new File(TEMP).mkdir();
        new File(ytp.getTemp()).mkdir();

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
        if (!new File(TEMP + "/tempoutput.mp4").exists())
            return;

        FileChooser.ExtensionFilter fileFilter = new FileChooser.ExtensionFilter("Video files (*.mp4)", "*.mp4");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileFilter);
        fileChooser.setTitle("Choose Source");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showSaveDialog(null);
        if (selected == null)
            return;

        Path temp = Paths.get(TEMP + "/tempoutput.mp4");
        try {
            Files.copy(temp, selected.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            System.out.println("Had a problem copying the file.");
        }
    }

    @FXML
    void openSettingsWindow(ActionEvent event) {
        cfg.show();
    }

    @FXML
    void openEffectsWindow(ActionEvent event) {
        effects.show();
    }

    private SettingsOption ytpOpt(String name, String description, OptionType type) {
        return ytpOpt(name, description, type, true);
    }

    private SettingsOption ytpOpt(String name, String description, OptionType type, boolean save) {
        return ytpOpt(name, description, type, save, null);
    }

    private SettingsOption ytpOpt(String name, String description, OptionType type, boolean save, Object[] extra) {
        Value val = null;
        try {
            switch (type) {

            case TextField:
            case PathDir:
            case PathFile:
                val = new YTPValue<String>(name, String.class);
                break;

            case DoubleNumberField:
                val = new YTPValue<Double>(name, double.class);
                break;

            /* Provide int wrapper in case your getter/setter is working with enum */
            case ComboBox:
            case IntegerNumberField:
                val = new YTPValue<Integer>(name, int.class);
                break;

            case CheckBox:
                val = new YTPValue<Boolean>(name, boolean.class);
                break;

            }
        } catch (Exception ex) {
            System.err.println("AVAST! " + ex);
        }
        return new SettingsOption(name, description, type, val, save, extra);
    }

    private SettingsOption ytpEffectOpt(String name, String description) {
        return new SettingsOption(name, description, OptionType.DoubleNumberField, new YTPEffectValue(name));
    }

    /* Wrap YTPGenerator generic getter/setter */
    private class YTPValue<T> implements Value {
        public YTPValue(String name, Class<?> cls) throws Exception {
            getter = YTPGenerator.class.getMethod("get" + name);
            setter = YTPGenerator.class.getMethod("set" + name, cls);
        }
        @Override
        public Object get() {
            try {
                return getter.invoke(ytp);
            } catch (Exception ex) {
                System.err.println("AVAST! " + ex);
            }
            return null;
        }
        @Override
        public Boolean set(Object value) {
            try {
                return (Boolean)setter.invoke(ytp, (T)value);
            } catch (Exception ex) {
                System.err.println("AVAST! " + ex);
            }
            return false;
        }

        private final Method getter, setter;
    }

    private class YTPEffectValue implements Value {
        public YTPEffectValue(String name) {
            effectName = name;
        }

        @Override
        public Object get() {
            return ytp.getEffect(effectName);
        }
        @Override
        public Boolean set(Object value) {
            ytp.setEffect(effectName, (double)value);
            return null;
        }

        final private String effectName;
    }

    private YTPGenerator ytp = new YTPGenerator();

    private File LAST_BROWSED;
    private String TEMP = "temp", jobDir;

    private SettingsWindow cfg, effects;
    {
        SettingsOption[] opts = new SettingsOption[] {
            ytpOpt("FFmpeg", "ffmpeg", OptionType.PathFile),
            ytpOpt("FFprobe", "ffprobe", OptionType.PathFile),
            ytpOpt("Magick", "magick", OptionType.PathFile),
            new SettingsOption("Temp", "temp", OptionType.PathDir, new Value() {
                @Override
                public Object get() {
                    return TEMP;
                }
                @Override
                public Boolean set(Object value) {
                    TEMP = (String)value;
                    return null;
                }
            }),
            ytpOpt("Sounds", "sounds", OptionType.PathDir),
            ytpOpt("Music", "music", OptionType.PathDir),
            ytpOpt("Resources", "resources", OptionType.PathDir),
            ytpOpt("Sources", "sources", OptionType.PathDir),
            ytpOpt("LazySwitchStartingSource", "Lazy source", OptionType.PathFile, false),
            ytpOpt("IntroVideo", "Intro clip", OptionType.PathFile),
            ytpOpt("ConcatMethod", "Concatenation method",
                OptionType.ComboBox, true, new String[] {
                    "Demuxer",
                    "Concat protocol",
                    "Concat filter"
                }
            ),
            ytpOpt("ReconvertEffected", "Reconvert clips", OptionType.CheckBox)
        };
        cfg = new SettingsWindow(opts);
    }
    {
        SettingsOption[] opts = new SettingsOption[] {
            ytpEffectOpt("RandomSound", "Random sound"),
            ytpEffectOpt("RandomSoundMute", "Random sound (w/mute)"),
            ytpEffectOpt("Reverse", "Reverse clip"),
            ytpEffectOpt("SpeedUp", "Speed up clip (no pitch)"),
            ytpEffectOpt("SlowDown", "Slow down clip (no pitch)"),
            ytpEffectOpt("Chorus", "Chorus audio"),
            ytpEffectOpt("Vibrato", "Vibrato audio"),
            ytpEffectOpt("HighPitch", "Speed up clip (w/pitch)"),
            ytpEffectOpt("LowPitch", "Slow down clip (w/pitch)"),
            ytpEffectOpt("Mirror", "Mirror"),
            ytpEffectOpt("Dance", "Dance"),
            ytpEffectOpt("Squidward", "Squidward"),
            ytpOpt("EffectChance", "Effect (%)", OptionType.DoubleNumberField),
            ytpOpt("TransitionClipChance", "Transition clip (%)", OptionType.DoubleNumberField),
            ytpOpt("MinDuration", "Min clip duration", OptionType.DoubleNumberField),
            ytpOpt("MaxDuration", "Max clip duration", OptionType.DoubleNumberField),
            ytpOpt("MaxClips", "Clip count", OptionType.IntegerNumberField),
            ytpOpt("LazySwitch", "Lazy switching", OptionType.CheckBox),
            ytpOpt("LazySwitchChance", "Lazy switch (%)", OptionType.DoubleNumberField),
            ytpOpt("LazySwitchInterrupt", "Lazy interrupt (%)", OptionType.DoubleNumberField),
            ytpOpt("LazySwitchMaxClips", "Lazy switch max clips", OptionType.IntegerNumberField),
            ytpOpt("LazySeek", "Lazy seeking", OptionType.CheckBox),
            ytpOpt("LazySeekChance", "Lazy seek (%)", OptionType.DoubleNumberField),
            ytpOpt("LazySeekFromStart", "Lazy seek from start", OptionType.CheckBox),
            ytpOpt("LazySeekInterrupt", "Lazy seek interrupt (%)", OptionType.DoubleNumberField),
            ytpOpt("LazySeekMaxClips", "Lazy seek max clips", OptionType.IntegerNumberField),
            ytpOpt("LazySeekNearby", "Lazy seek nearby", OptionType.CheckBox),
            ytpOpt("LazySeekNearbyMin", "Lazy seek nearby min", OptionType.DoubleNumberField),
            ytpOpt("LazySeekNearbyMax", "Lazy seek nearby max", OptionType.DoubleNumberField),
            ytpOpt("LazySeekSameChance", "Lazy seek same (%)", OptionType.DoubleNumberField)
        };
        effects = new SettingsWindow(opts, 2);
    }

    private Thread vidThread;

    @FXML
    private Button btnCreate;

    @FXML
    private TextField tfClipCount, tfMaxStream, tfMinStream;

    @FXML
    private ProgressBar barProgress;

    @FXML
    private ListView<String> listviewSourcesList;

    private ObservableList<String> sourceList = FXCollections.observableArrayList();
}
