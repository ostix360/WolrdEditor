package fr.ostix.worldCreator.graphics.font.rendering;



import fr.ostix.worldCreator.graphics.font.meshCreator.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import org.lwjgl.opengl.GL13;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;

public class FontRenderer {

    private final FontShader shader;

    public FontRenderer() {
        shader = new FontShader();
    }

    public void render(Map<FontType, List<GUIText>> guisTexts) {
        prepare();
        for (FontType font : guisTexts.keySet()) {
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, font.getTextureAtlas());
            for (GUIText text : guisTexts.get(font)) {
                renderText(text);
            }
        }
        unBind();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void prepare() {
        OpenGlUtils.enableAlphaBlending();
        OpenGlUtils.enableDepthTesting(false);
        shader.bind();
    }

    private void renderText(GUIText text) {
        text.getVao().getVAO().bind(0,1);
        shader.loadColor(text.getColour());
        shader.loadTranslation(text.getPosition());
        glDrawArrays(GL_TRIANGLES, 0, text.getVertexCount());
        VAO.unbind(0,1);
    }

    private void unBind() {
        shader.unBind();
        OpenGlUtils.disableBlending();
        OpenGlUtils.enableDepthTesting(true);
    }

}
