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

import javafx.beans.property.Property;
import javafx.stage.Stage;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.paint.Color;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.beans.value.ObservableValue;
import java.util.prefs.Preferences;

import ytpplusui.PathElement;
import ytpplusui.SettingsOption;
import ytpplusui.SettingsOption.OptionType;
import ytpplusui.SettingsOption.Value;

public class SettingsWindow {
    public SettingsWindow(SettingsOption[] options) {
        this(options, 1);
    }

    public SettingsWindow(SettingsOption[] options, int cols) {
        myStage = new Stage();
        myStage.setScene(makeScene(options, cols));
    }

    private void nodeError(Node elem, boolean set) {
        elem.setStyle(set ? "-fx-background-color: red;" : "");
    }

    private void applyComboBox(SettingsOption opt, ComboBox<String> elem) {
        final Value bind = opt.getBind();
        elem.setId(opt.getId());
        elem.getItems().addAll((String [])opt.getExtra());
        elem.setMaxWidth(Double.MAX_VALUE);
        SingleSelectionModel model = elem.getSelectionModel();

        if (opt.saveable()) {
            bind.set((int)MainApp.load(elem.getId(), (Integer)bind.get()));
            model.selectedIndexProperty().addListener((ob, oldV, newV) -> {
                MainApp.save(elem.getId(), (Integer)newV);
            });
        }
        model.select((int)bind.get());

        model.selectedIndexProperty().addListener((ob, oldV, newV) -> {
            Boolean ok = bind.set((Integer)newV);
            if (ok != null)
                nodeError(elem, !ok);
        });
    }

    private <T> void applyCommon(SettingsOption opt, Node elem, Property<T> v) {
        final Value bind = opt.getBind();
        elem.setId(opt.getId());

        if (opt.saveable()) {
            bind.set(MainApp.load(elem.getId(), (T)bind.get()));
            v.addListener((ob, oldV, newV) -> {
                MainApp.save(elem.getId(), newV);
            });
        }
        v.setValue((T)bind.get());

        v.addListener((ob, oldV, newV) -> {
            Boolean ok = bind.set(newV);
            if (ok != null)
                nodeError(elem, !ok);
        });
    }

    private <T> void applyTextFieldNumber(SettingsOption opt, TextField elem, StringToNumber<T> cvt) {
        final Value bind = opt.getBind();
        final Property<String> v = elem.textProperty();
        elem.setId(opt.getId());
        elem.setMaxWidth(70);

        if (opt.saveable()) {
            bind.set(MainApp.load(elem.getId(), (T)bind.get()));
            v.addListener((ob, oldV, newV) -> {
                try {
                    MainApp.save(elem.getId(), cvt.fromString(newV));
                } catch (NumberFormatException ex) {/* Parsed below */}
            });
        }
        v.setValue(cvt.toString((T)bind.get()));

        v.addListener((ob, oldV, newV) -> {
            try {
                Boolean ok = bind.set(cvt.fromString(newV));
                nodeError(elem, ok == null ? false : true);
            } catch (NumberFormatException ex) {
                nodeError(elem, true);
            }
        });
    }

    private Scene makeScene(SettingsOption[] options, int maxCols) {
        GridPane pane = new GridPane();
        pane.setHgap(2.0);
        pane.setVgap(2.0);

        int i = 0, j = 0;
        for (SettingsOption opt : options) {
            Node node = null;
            Label lbl = new Label(opt.getLabel());

            switch (opt.getType()) {

            case DoubleNumberField:
            case IntegerNumberField:
                final TextField nf = new TextField();
                if (opt.getType() == OptionType.IntegerNumberField)
                    applyTextFieldNumber(opt, nf, new StringToInteger());
                else
                    applyTextFieldNumber(opt, nf, new StringToDouble());

                node = nf;
                break;

            case TextField:
                final TextField tf = new TextField();
                applyCommon(opt, tf, tf.textProperty());
                tf.setMaxWidth(Double.MAX_VALUE);
                node = tf;
                break;

            case PathFile:
            case PathDir:
                final PathElement path = new PathElement(opt.getType() == OptionType.PathDir);
                applyCommon(opt, path, path.pathProperty());
                node = path;
                break;

            case CheckBox:
                final CheckBox cb = new CheckBox();
                applyCommon(opt, cb, cb.selectedProperty());
                node = cb;
                break;

            case ComboBox:
                final ComboBox<String> box = new ComboBox<>();
                applyComboBox(opt, box);
                node = box;
                break;

            }
            /* Compiler still thinks we haven't checked all enum types */
            assert(node != null);
            pane.add(lbl, j * 2, i);
            pane.add(node, j * 2 + 1, i);
            if (++j == maxCols) {
                ++i;
                j = 0;
            }
        }
        return new Scene(pane);
    }

    public void show() {
        if (!myStage.isShowing())
            myStage.show();
    }

    private interface StringToNumber<T> {
        public String toString(T value);
        public T fromString(String value) throws NumberFormatException;
    }

    private class StringToDouble implements StringToNumber<Double> {
        @Override
        public String toString(Double value) {
            return value.toString();
        }
        @Override
        public Double fromString(String value) throws NumberFormatException {
            return Double.parseDouble(value);
        }
    }

    private class StringToInteger implements StringToNumber<Integer> {
        @Override
        public String toString(Integer value) {
            return value.toString();
        }
        @Override
        public Integer fromString(String value) throws NumberFormatException {
            return Integer.parseInt(value);
        }
    }

    public Node lookup(String id) {
        return myStage.getScene().lookup(id);
    }

    private Stage myStage;
}
