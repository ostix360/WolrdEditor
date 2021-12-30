package fr.ostix.worldCreator.frame.editor;

import java.io.File;

public class FileInList {

    private File file;
    private String name;

    public FileInList(File file) {
        this.file = file;
        this.name = file.getName().split("\\.")[0];
    }

    public File getFile() {
        return file;
    }

    public String toString() {
        return name;
    }
}
