package fr.ostix.worldCreator.graphics.font.meshCreator;

public class FontType {

    private final int textureLoaderAtlas;
    private final TextMeshCreator loader;


    public FontType(int textureLoaderAtlas, String fontFile) {
        this.textureLoaderAtlas = textureLoaderAtlas;
        this.loader = new TextMeshCreator(fontFile);
    }

    public int getTextureAtlas() {
        return textureLoaderAtlas;
    }

    public TextMeshData loadText(GUIText text) {
        return loader.createTextMesh(text);
    }

}
