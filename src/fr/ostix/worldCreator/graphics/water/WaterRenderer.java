package fr.ostix.worldCreator.graphics.water;


import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.entity.camera.*;
import fr.ostix.worldCreator.graphics.model.*;
import fr.ostix.worldCreator.graphics.textures.*;
import fr.ostix.worldCreator.toolBox.OpenGL.*;
import fr.ostix.worldCreator.water.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.util.List;

public class WaterRenderer {

	// private static final float WAVE_SPEED = 0.03f;

	private static final String DUDV_MAP = "water/waterDUDV";
	private static final String NORMAL_MAP = "water/normalMap";

	private final MeshModel quad;
	private final WaterShader shader;
	private final WaterFrameBuffers fbos;
	private final TextureLoader dudvMap;
	private final TextureLoader normalMap;
	private float moveFactor = 0;

	public WaterRenderer(WaterFrameBuffers fbos) {
		this.shader = new WaterShader();
		this.fbos = fbos;
		this.quad = QuadGenerator.getQuad();
		this.dudvMap = Loader.INSTANCE.loadTexture(DUDV_MAP);
		this.normalMap = Loader.INSTANCE.loadTexture(NORMAL_MAP);
	}

	public void render(List<WaterTile> water, ICamera camera, Vector3f lightDir) {
		prepareRender(camera, lightDir);
		for (WaterTile tile : water) {
			Matrix4f modelMatrix = createModelMatrix(tile.getX(), tile.getHeight(), tile.getZ());
			shader.modelMatrix.loadMatrixToUniform(modelMatrix);
			GL11.glDrawElements(GL11.GL_TRIANGLES, quad.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
		}
		finish();
	}

	public void cleanUp() {
		fbos.cleanUp();
		shader.cleanUp();
	}

	private void prepareRender(ICamera camera, Vector3f lightDir) {
		shader.bind();
		shader.projectionMatrix.loadMatrixToUniform(camera.getProjectionMatrix());
		shader.viewMatrix.loadMatrixToUniform(camera.getViewMatrix());
		shader.cameraPosition.loadVector3fToUniform(camera.getPosition());

		moveFactor += 0.0005f;
		moveFactor %= 1;
		shader.moveFactor.loadFloatToUniform(moveFactor);

		shader.lightDirection.loadVector3fToUniform(lightDir);

		quad.getVAO().bind(0);

		bindTextures();

		doRenderSettings();
	}

	private void bindTextures() {
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getReflectionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE1);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionTexture());
		GL13.glActiveTexture(GL13.GL_TEXTURE2);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvMap.getId());
		GL13.glActiveTexture(GL13.GL_TEXTURE3);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMap.getId());
		GL13.glActiveTexture(GL13.GL_TEXTURE4);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, fbos.getRefractionDepthTexture());
	}

	private void doRenderSettings() {
		OpenGlUtils.enableDepthTesting(true);
		OpenGlUtils.antialias(false);
		OpenGlUtils.cullBackFaces(true);
		OpenGlUtils.enableAlphaBlending();
	}

	private void finish() {
		VAO.unbind(0);
		shader.unBind();
		OpenGlUtils.antialias(true);
		OpenGlUtils.disableBlending();
		OpenGlUtils.enableDepthTesting(true);
	}

	private Matrix4f createModelMatrix(float x, float y, float z) {
		Matrix4f modelMatrix = new Matrix4f();
		modelMatrix.translate(new Vector3f(x, y, z));
		modelMatrix.scale(new Vector3f(WaterTile.TILE_SIZE, WaterTile.TILE_SIZE, WaterTile.TILE_SIZE));
		return modelMatrix;
	}

}
