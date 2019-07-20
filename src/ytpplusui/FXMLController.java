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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.SelectionMode;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javax.swing.DefaultListModel;
import zone.arctic.ytpplus.YTPGenerator;

public class FXMLController {
// <editor-fold defaultstate="collapsed" desc="idk how to neaten this so heres a fold to make it neater">

    @FXML
    private Button btnCreate;

    @FXML
    private TextField tfClipCount;

    @FXML
    private TextField tfMaxStream;

    @FXML
    private TextField tfMinStream;

    @FXML
    private ProgressBar barProgress;

    @FXML
    private TextField tfFFMPEG;

    @FXML
    private TextField tfFFPROBE;

    @FXML
    private TextField tfMAGICK;

    @FXML
    private TextField tfTEMP;

    @FXML
    private TextField tfSOUNDS;

    @FXML
    private TextField tfMUSIC;

    @FXML
    private TextField tfRESOURCES;

    @FXML
    private Button btnBrowseFFMPEG;

    @FXML
    private Button btnBrowseFFPROBE;

    @FXML
    private Button btnBrowseMAGICK;

    @FXML
    private Button btnBrowseTEMP;

    @FXML
    private Button btnBrowseSOUNDS;

    @FXML
    private Button btnBrowseMUSIC;

    @FXML
    private Button btnBrowseRESOURCES;

    @FXML
    private TextField tfSOURCES;

    @FXML
    private Button btnHelpMeConfig;

    @FXML
    private ListView<String> listviewSourcesList;

    @FXML
    private Button btnBrowseSOURCES;

    @FXML
    private Button btnSaveAs;

    @FXML
    private TextField randomSound, randomSoundMute, reverse, speedUp,
        slowDown, chorusAudio, vibratoAudio, highPitch, lowPitch,
        mirror, dance, squidward;

    @FXML
    private TextField effectChance, transitionClipChance;

// </editor-fold>
    
    //javafx sucks. It's got a lot under the hood but it sucks.
    //This is incredibly messy. And I can't fix it because javafx sucks.
    //Moral of this story is don't use javafx. Swing is your friend.

    ObservableList<String> sourceList = FXCollections.observableArrayList();
    
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

    @FXML
    void goNow(ActionEvent event) throws Exception {
        if (sourceList.isEmpty()) {
            alert("You need some sources...");
            return;
        }
        
        Thread vidThread = new Thread() {
            public void run() {
                try {
                btnCreate.setDisable(true);
                System.out.println("poop");

                String TEMP = tfTEMP.getText();
                YTPGenerator generator = new YTPGenerator(TEMP + "tempoutput.mp4");
                System.out.println("poop2");
                generator.setFFmpeg(tfFFMPEG.getText());
                generator.setFFprobe(tfFFPROBE.getText());
                generator.setMagick(tfMAGICK.getText());
                System.out.println("poop3");
                String jobDir = TEMP + "job_" + System.currentTimeMillis() + "/";
                generator.setTemp(jobDir);
                new File(jobDir).mkdir();
                new File(generator.getTemp()).mkdir();
                generator.setSounds(tfSOUNDS.getText());
                generator.setMusic(tfMUSIC.getText());
                generator.setResources(tfRESOURCES.getText());
                generator.setSources(tfSOURCES.getText());
                System.out.println("poop4");

                generator.setEffectChance(Integer.parseInt(effectChance.getText()));
                generator.setTransitionClipChance(Integer.parseInt(transitionClipChance.getText()));
                generator.setEffect("RandomSound", Integer.parseInt(randomSound.getText()));
                generator.setEffect("RandomSoundMute", Integer.parseInt(randomSoundMute.getText()));
                generator.setEffect("Reverse", Integer.parseInt(reverse.getText()));
                generator.setEffect("SpeedUp", Integer.parseInt(speedUp.getText()));
                generator.setEffect("SlowDown", Integer.parseInt(slowDown.getText()));
                generator.setEffect("Chorus", Integer.parseInt(chorusAudio.getText()));
                generator.setEffect("Vibrato", Integer.parseInt(vibratoAudio.getText()));
                generator.setEffect("HighPitch", Integer.parseInt(highPitch.getText()));
                generator.setEffect("LowPitch", Integer.parseInt(lowPitch.getText()));
                generator.setEffect("Dance", Integer.parseInt(dance.getText()));
                generator.setEffect("Squidward", Integer.parseInt(squidward.getText()));
                generator.setEffect("Mirror", Integer.parseInt(mirror.getText()));

                System.out.println("poop5");
                for (String source : sourceList) {
                    generator.addSource(source);
                }
                System.out.println("poop6");
                int maxclips = Integer.parseInt(tfClipCount.getText());
                generator.setMaxClips(maxclips);
                generator.setMaxDuration(Double.parseDouble(tfMaxStream.getText()));
                generator.setMinDuration(Double.parseDouble(tfMinStream.getText()));
                System.out.println("poop7");
                
                generator.go();
                System.out.println("poop8");
                while (!generator.done) {
                    barProgress.setProgress(generator.doneCount);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        // Keep going
                    }
                }
                barProgress.setProgress(1);
                //System.out.println("AAAAAAAAAAAAAAA" + media.toURL().toString());
                btnCreate.setDisable(false);
                } catch (Exception ex) {
                   
                   btnCreate.setDisable(false);
                   ex.printStackTrace();
                }
            }
        };
        vidThread.start();
    }

    @FXML
    void helpMeConfig(ActionEvent event) {
        alert("These boxes provide the paths to the executables "
                + "which will be run throughout the batch process.\n\n"
                + "If you're on linux or OSX, \"magick\" should be empty.\n\n"
                + "The only reason it is here is because on Windows, most "
                + "ImageMagick tools are called from commandline by using "
                + "\"magick convert...\", while on other operating systems, "
                + "magick commands can be called simply by saying \"convert...\".\n\n"
                + "If you have separate installations of these tools, feel "
                + "free to change what's in these boxes to suit your fancy.\n\n"
                + "If you have no idea what any of this shit means, leave it be."
                );

    }
    
    @FXML
    void helpMeEffect(ActionEvent event) {
        alert("Currently, these effects are based on a switch statement, and each effect has an equal chance of appearing, which means if you "
                + "turn one of them off, there will be more unedited clips. Additionally, there is a 1/2 chance of there even being an effect on a clip. "
                + "You do the math. There's 11 effects. 1/2 chance of each effect occuring. That means, regardless of being turned on or off, "
                + "each effect has a 1/22 chance of occuring. Pretty nasty, right? I'll add sliders for \"frequency\" in the future...\n\n"
                + "This might be beneficial to know also: There's a 1/15 chance of a \"transition\" clip being used in place of your sources, too. "
                + "So for every 15 clips you tell YTP+ to generate, one of them will be a transition clip from the folder you provide the program. "
                + "It's a big mess of numbers.\n\n"
                + "The reason there aren't frequency sliders now is because someone will probably break them somehow and I don't have the time to debug. "
                + "Give me a few weeks for an update..."
                );

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
        }
    }
    
    public File LAST_BROWSED;
    
    void actuallyOpenBrowser(TextField toChange) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showOpenDialog(null);
        if (selected==null) return;
        toChange.setText(selected.getAbsolutePath().replace('\\', '/'));
        LAST_BROWSED = selected.getParentFile();
    }
    
    void actuallyOpenDirBrowser(TextField toChange) {
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Choose File");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showDialog(null);
        if (selected==null) return;
        toChange.setText(selected.getAbsolutePath().replace('\\', '/') + "/");
        LAST_BROWSED = selected.getParentFile();
    }

    @FXML
    void removeSource(ActionEvent event) {
        ObservableList<String> selection = listviewSourcesList.getSelectionModel().getSelectedItems();
        if (selection.size() > 0)
            sourceList.removeAll(selection);
    }

    @FXML
    void removeAllSource(ActionEvent event) {
        sourceList.clear();
    }

    @FXML
    void saveAsVideo(ActionEvent event) {
        if (!new File(tfTEMP.getText() + "tempoutput.mp4").exists()) return;
        FileChooser.ExtensionFilter fileFilter = new FileChooser.ExtensionFilter("Video files (*.mp4)", "*.mp4");
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(fileFilter);
        fileChooser.setTitle("Choose Source");
        fileChooser.setInitialDirectory(LAST_BROWSED);
        File selected = fileChooser.showSaveDialog(null);
        if (selected==null) return;
        Path temp = Paths.get(tfTEMP.getText() + "tempoutput.mp4");
        try {
        Files.copy(temp, selected.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            alert("Had a problem copying the file.");
        }
    }
    
    @FXML
    void openDiscord(ActionEvent event) {
        try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://discord.gg/mAwQQt7"));
        }
        } catch (Exception ex) {} //how does that even happen
    }

    @FXML
    void openArcticZone() {
        try {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(new URI("https://arctic.zone/"));
        }
        } catch (Exception ex) {} //how does that even happen
    }
    
    void alert(String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        String[] titles = {"Yo", "Mmmmm!", "I'm the invisible man...", "Luigi, look!", "You want it?", "WTF Booooooooooom"};
        alert.setTitle(titles[randomInt(0,titles.length-1)]);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.show();
    }
    
    public static int randomInt(int min, int max) {
        return new Random().nextInt((max - min) + 1) + min;
    }
}
