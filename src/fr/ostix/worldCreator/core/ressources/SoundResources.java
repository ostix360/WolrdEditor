package fr.ostix.worldCreator.core.ressources;

public class SoundResources {
    private final String name;
    private final String path;
    private final boolean ambient;

    public SoundResources(String name, String path, boolean ambient) {
        this.name = name;
        this.path = path;
        this.ambient = ambient;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public boolean isAmbient() {
        return this.ambient;
    }
}
