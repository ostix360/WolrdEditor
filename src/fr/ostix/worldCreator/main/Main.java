package fr.ostix.worldCreator.main;

import fr.ostix.worldCreator.audio.*;
import fr.ostix.worldCreator.core.Timer;
import fr.ostix.worldCreator.core.resourcesLoader.*;
import fr.ostix.worldCreator.core.resourcesProcessor.ModelLoaderRequest;
import fr.ostix.worldCreator.core.loader.OBJFileLoader;
import fr.ostix.worldCreator.core.resourcesProcessor.TextureLoaderRequest;
import fr.ostix.worldCreator.core.resourcesProcessor.GLRequestProcessor;
import fr.ostix.worldCreator.creator.Workspace;
import fr.ostix.worldCreator.entity.Entity;
import fr.ostix.worldCreator.entity.component.light.Light;
import fr.ostix.worldCreator.entity.Transform;
import fr.ostix.worldCreator.entity.camera.Camera;
import fr.ostix.worldCreator.frame.*;
import fr.ostix.worldCreator.graphics.MasterRenderer;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.terrain.*;
import fr.ostix.worldCreator.terrain.texture.*;
import fr.ostix.worldCreator.toolBox.*;
import fr.ostix.worldCreator.water.*;
import fr.ostix.worldCreator.world.*;
import fr.ostix.worldCreator.world.chunk.*;
import org.joml.*;
import org.lwjgl.openal.*;

import java.io.*;
import java.lang.*;
import java.lang.Runtime;
import java.net.URISyntaxException;
import java.util.*;

public class Main {

    public static final int MAX_LIGHTS = 5;
    private static final List<Light> lights = new ArrayList<>();
    private static final List<WaterTile> waterTiles = new ArrayList<>();
    private static final Map<Vector2f, Chunk> terrains = Collections.synchronizedMap(new HashMap<>());

    public static final Light light = new Light(new Vector3f(100,100000,100), Color.SUN,null);

    public static void main(String[] args) throws InterruptedException, URISyntaxException {
        readConfig();
        AudioManager.init(AL11.AL_EXPONENT_DISTANCE);
        MasterRenderer renderer = new MasterRenderer();
        Transform playerTransform = new Transform(new Vector3f(2000,0,2000),new Vector3f(0,0,0),1);
        Camera cam = new Camera(playerTransform);
        Workspace workspace = new Workspace();
        renderer.initToRender(waterTiles,terrains,lights);
        World world = new World(renderer,terrains,cam);
        ExitMenu exitMenu = new ExitMenu(world);
        Thread.currentThread().setUncaughtExceptionHandler((t, e) -> {
            System.err.println("Exception in " + t.getName());
            e.printStackTrace();
            exitMenu.show(true);
            System.exit(301);
        });
        MainFrame frame = new MainFrame(renderer,workspace,cam,world);
        Thread renderThread = new Thread(frame.getRenderRunnable(),"Render Thread");
        renderThread.setUncaughtExceptionHandler((t, e) -> {
            System.err.println("Exception in " + t.getName());
            e.printStackTrace();
            exitMenu.show(true);
            System.exit(301);
        });
        renderThread.start();
        lights.add(light);


        ResourcePackLoader rpl = new ResourcePackLoader();
        try {
            rpl.loadAllResource();
            MainFrame.loadEntities(rpl);
        } catch (Exception e) {
            System.err.println("Error during the resources pack loading");
            e.printStackTrace();
        }

        TextureLoaderRequest textureRequest = new TextureLoaderRequest(Main.class.getResourceAsStream("/white.png"));
        GLRequestProcessor.sendRequest(textureRequest);
        ModelData data = OBJFileLoader.loadModel(Main.class.getResourceAsStream("/model/cube.obj"));
        ModelLoaderRequest cube2 = new ModelLoaderRequest(data);
        GLRequestProcessor.sendRequest(cube2);




        TextureLoaderRequest backgroundTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/grassy2.png"));
        TextureLoaderRequest rTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/mud.png"));
        TextureLoaderRequest gTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/grassFlowers.png"));
        TextureLoaderRequest bTexture = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/path.png"));
        TextureLoaderRequest blendRequest = new TextureLoaderRequest(Main.class.getResourceAsStream("/res/terrain/blendMap.png"));
        GLRequestProcessor.sendRequest(backgroundTexture,rTexture,gTexture,bTexture,blendRequest);

        Timer.waitForRequest(blendRequest);
        Timer.waitForRequest(bTexture);
        Timer.waitForRequest(gTexture);
        Timer.waitForRequest(rTexture);
        Timer.waitForRequest(backgroundTexture);

        TerrainTexture backt = new TerrainTexture(backgroundTexture.getTexture().getId());
        TerrainTexture rt = new TerrainTexture(rTexture.getTexture().getId());
        TerrainTexture gt = new TerrainTexture(gTexture.getTexture().getId());
        TerrainTexture bt = new TerrainTexture(bTexture.getTexture().getId());
        TerrainTexture blendt = new TerrainTexture(blendRequest.getTexture().getId());

        TerrainTexturePack tp = new TerrainTexturePack(backt,rt,gt,bt);
        Config.BLEND_MAP = blendt;
        Config.TERRAIN_DEFAULT_PACK = tp;
        Config.CUBE = new Model(cube2.getModel(),new Texture(textureRequest.getTexture(),
                new TextureProperties(false,true,0,0,0,
                        0,1,false,true,false)));
//        Terrain t = new Terrain(0,0,tp,blendt);
        Runtime.getRuntime().addShutdownHook(new Thread(()-> {
            System.out.println("Fermeture");
        }));
    }

    private static void readConfig() {
        if(!Config.optionFile.exists()|| !Config.optionFile.canRead()){
            new PopUp("Bonjour et bienvenue sur ce logiciel.\n",
                    "Avant toutes choses vous devez configurer vos options.\n",
                    "Allez dans Autre puis dans option et séléctionnez l'emplacement de chaque dossier.\n" ,
                    "Générez le monde vos rêves. :-)");
            System.err.println("File not found");
        }else{
            try (FileReader fr = new FileReader(Config.optionFile);
            BufferedReader reader = new BufferedReader(fr)){
                String[] config = reader.readLine().split(";");
                if (config.length != 2){
                    System.err.println("Error in this file");
                    Config.optionFile.delete();
                    return;
                }
                Config.OUTPUT_FOLDER = new File(config[0]);
                Config.REPOSITORY_FOLDER = new File(config[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void addLight(Light light) {
        Main.lights.add(light);
    }

    public static void notifyNullModel(Entity entity) {
        Logger.err("The model of  " + entity.toString() + " is null");
        ResourcePackLoader rpl = new ResourcePackLoader();
        try {
            rpl.loadAllResource();
            MainFrame.loadEntities(rpl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
