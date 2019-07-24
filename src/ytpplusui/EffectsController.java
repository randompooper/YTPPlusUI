package ytpplusui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.control.CheckBox;
import ytpplusui.SaveableCheckBox;
import ytpplusui.SaveableTextField;

public class EffectsController {
    private static EffectsController me;
    private static Stage myStage;

    protected final static String resourceName = "EffectsPane.fxml";
    protected final static String title = "Effects";

    public static void init() throws Exception {
        FXMLLoader loader = new FXMLLoader();
        System.out.println("loading " + resourceName);
        loader.setLocation(MainApp.class.getResource(resourceName));

        Scene scene = new Scene(loader.load());
        myStage = new Stage();
        myStage.setScene(scene);
        myStage.setTitle(title);
        myStage.setResizable(false);

        me = loader.getController();
    }

    public static void open() {
        if (!myStage.isShowing())
            myStage.show();
    }

    public static EffectsController getController() {
        return me;
    }

    @FXML
    private SaveableTextField randomSound, randomSoundMute, reverse, speedUp,
        slowDown, chorusAudio, vibratoAudio, highPitch, lowPitch,
        mirror, dance, squidward, effectChance, transitionClipChance,
        tfClipCount, tfMaxStream, tfMinStream, tfLazySwitchChance,
        tfLazySwitchInterrupt, tfLazySwitchMaxClips;

    @FXML
    private SaveableCheckBox cbLazySwitch;

    private int getInt(TextField tf) {
        return Integer.parseInt(tf.getText());
    }

    private double getDouble(TextField tf) {
        return Double.parseDouble(tf.getText());
    }

    public int getEffectChance() {
        return getInt(effectChance);
    }

    public int getTransitionChance() {
        return getInt(transitionClipChance);
    }

    public int getClipCount() {
        return getInt(tfClipCount);
    }

    public double getMinStream() {
        return getDouble(tfMinStream);
    }

    public double getMaxStream() {
        return getDouble(tfMaxStream);
    }

    public int getRandomSound() {
        return getInt(randomSound);
    }

    public int getRandomSoundMute() {
        return getInt(randomSoundMute);
    }

    public int getReverse() {
        return getInt(reverse);
    }

    public int getSpeedUp() {
        return getInt(speedUp);
    }

    public int getSlowDown() {
        return getInt(slowDown);
    }

    public int getChorus() {
        return getInt(chorusAudio);
    }

    public int getVibrato() {
        return getInt(vibratoAudio);
    }

    public int getHighPitch() {
        return getInt(highPitch);
    }

    public int getLowPitch() {
        return getInt(lowPitch);
    }

    public int getMirror() {
        return getInt(mirror);
    }

    public int getDance() {
        return getInt(dance);
    }

    public int getSquidward() {
        return getInt(squidward);
    }

    public boolean getLazySwitch() {
        return cbLazySwitch.isSelected();
    }

    public int getLazySwitchChance() {
        return getInt(tfLazySwitchChance);
    }

    public int getLazySwitchInterrupt() {
        return getInt(tfLazySwitchInterrupt);
    }

    public int getLazySwitchMaxClips() {
        return getInt(tfLazySwitchMaxClips);
    }
}
