package fr.ostix.worldCreator.toolBox;

public enum FileType {
    OBJ(".obj"),
    COLLADA(".dae"),

    OGG(".ogg");

    private String extension;

    FileType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
