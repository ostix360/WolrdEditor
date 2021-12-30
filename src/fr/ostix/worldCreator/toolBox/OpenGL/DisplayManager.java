package fr.ostix.worldCreator.toolBox.OpenGL;


public class DisplayManager {

    private static int width = 1080;
    private static int height = 720;



    public static void setWidth(int width) {
        DisplayManager.width = width;
    }

    public static void setHeight(int height) {
        DisplayManager.height = height;
    }

    public static int getWidth() {
        return width;
    }

    public static int getHeight() {
        return height;
    }

}
