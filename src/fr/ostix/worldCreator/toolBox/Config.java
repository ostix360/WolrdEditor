package fr.ostix.worldCreator.toolBox;

import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;

import javax.swing.filechooser.*;
import java.io.*;

public class Config {

    public static TerrainTexturePack TERRAIN_DEFAULT_PACK;
    public static TerrainTexture BLEND_MAP;
    public static File OUTPUT_FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\entityExporter");
    public static File REPOSITORY_FOLDER = new File(FileSystemView.getFileSystemView().getDefaultDirectory().getAbsolutePath() + "\\entityExporter");
    public static File optionFile = new File(System.getProperty("user.home", ".")+ "\\AppData\\Roaming\\WorldCreator.config");
    public static Model CUBE;
}
