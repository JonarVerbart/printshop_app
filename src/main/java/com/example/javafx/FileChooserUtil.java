package com.example.javafx;

import java.io.File;

import javafx.stage.FileChooser;
import javafx.stage.Window;

public class FileChooserUtil {
    
    public File loadFile(Window ownerWindow, String fileDescription, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select a saved cart .json");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileDescription, extension));

        return fileChooser.showOpenDialog(ownerWindow);
    }

    public File saveFile(Window ownerWindow, String fileDescription, String extension) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save your cart");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileDescription, extension));

        return fileChooser.showSaveDialog(ownerWindow);
    }

}
