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

import java.io.File;
import java.nio.file.StandardCopyOption;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.beans.property.StringProperty;

public class PathElement extends HBox {
    private TextField toChange;
    private Button button;
    private File LAST_BROWSED;

    public PathElement(boolean isDirectory) {
        super();
        setIsDirectory(isDirectory);
        button = new Button("...");
        button.setOnAction(event -> {
            selectFile();
        });
        toChange = new TextField();
        toChange.setMaxWidth(Double.MAX_VALUE);
        setHgrow(toChange, Priority.ALWAYS);
        getChildren().addAll(toChange, button);
        styleProperty().addListener((ob, oldV, newV) -> {
            toChange.setStyle(newV);
        });
    }

    public void setIsDirectory(boolean state) {
        isDirectory = state;
    }

    public boolean getIsDirectory() {
        return isDirectory;
    }

    public void setPath(String path) {
        toChange.setText(path);
    }

    public String getPath() {
        return toChange.getText();
    }

    public StringProperty pathProperty() {
        return toChange.textProperty();
    }

    private File getSelectedFile() {
        if (getIsDirectory()) {
            DirectoryChooser c = new DirectoryChooser();
            c.setTitle("Choose directory");
            c.setInitialDirectory(LAST_BROWSED);
            return c.showDialog(null);
        } else {
            FileChooser c = new FileChooser();
            c.setTitle("Choose file");
            c.setInitialDirectory(LAST_BROWSED);
            return c.showOpenDialog(null);
        }
    }

    private void selectFile() {
        File selected = getSelectedFile();
        if (selected == null)
            return;

        toChange.setText(selected.getAbsolutePath().replace('\\', '/'));
        LAST_BROWSED = selected.getParentFile();
    }

    private boolean isDirectory;
}
