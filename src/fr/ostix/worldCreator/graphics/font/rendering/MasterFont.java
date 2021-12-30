package fr.ostix.worldCreator.graphics.font.rendering;

import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.graphics.font.meshCreator.*;
import fr.ostix.worldCreator.graphics.model.*;

import java.util.*;

public class MasterFont {

    private static final Map<FontType, List<GUIText>> guisTexts = new HashMap<>();
    private static final Map<FontType, List<GUIText>> tempGuisTexts = new HashMap<>();
    private static Loader loader;
    private final FontRenderer renderer;

    public MasterFont(Loader theLoader) {
        renderer = new FontRenderer();
        loader = theLoader;
    }

    public static void add(GUIText text) {
        FontType font = text.getFont();
        TextMeshData data = font.loadText(text);
        MeshModel vao = loader.loadFontToVAO(data.getVertexPositions(), data.getTextureCoords());
        text.setMeshInfo(vao, data.getVertexCount());
        List<GUIText> textBatch = guisTexts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public static void addTempFont(GUIText text) {
        FontType font = text.getFont();
        if (text.getVao() == null || text.getVertexCount() == 0) {
            TextMeshData data = font.loadText(text);
            MeshModel vao = loader.loadFontToVAO(data.getVertexPositions(), data.getTextureCoords());
            text.setMeshInfo(vao, data.getVertexCount());
        }
        List<GUIText> textBatch = tempGuisTexts.computeIfAbsent(font, k -> new ArrayList<>());
        textBatch.add(text);
    }

    public void render() {
        addTempText();
        renderer.render(guisTexts);
        removeTempGuis();
    }

    private void addTempText() {
        for (FontType Tfont : tempGuisTexts.keySet()) {
            List<GUIText> textBatch = tempGuisTexts.get(Tfont);
            if (guisTexts.containsKey(Tfont)) {
                guisTexts.get(Tfont).addAll(textBatch);
            } else {
                guisTexts.put(Tfont, textBatch);
            }
        }
    }

    private void removeTempGuis() {
        for (FontType Tfont : tempGuisTexts.keySet()) {
            List<GUIText> texts = guisTexts.get(Tfont);
            List<GUIText> tTexts = tempGuisTexts.get(Tfont);
            texts.removeAll(tTexts);
            tTexts.clear();
            if (texts.isEmpty()) {
                guisTexts.remove(Tfont);
                tempGuisTexts.remove(Tfont);
            }
        }
    }

    public static void remove(GUIText text) {
        List<GUIText> texts = guisTexts.get(text.getFont());
        texts.remove(text);
        if (texts.isEmpty()) {
            guisTexts.remove(text.getFont());
        }

    }

    public void cleanUp() {
        tempGuisTexts.clear();
        guisTexts.clear();
        renderer.cleanUp();
    }
}
