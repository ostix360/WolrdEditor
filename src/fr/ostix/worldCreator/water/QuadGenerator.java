package fr.ostix.worldCreator.water;


import fr.ostix.worldCreator.core.loader.*;
import fr.ostix.worldCreator.core.resourcesProcessor.*;
import fr.ostix.worldCreator.graphics.model.*;

public class QuadGenerator  {

	public static final int VERTEX_COUNT = 4;
	private static final float[] VERTICES = {0, 0, 1, 0, 1, 1, 0, 1};
	private static final int[] INDICES = {0, 3, 1, 1, 3, 2};


	public static MeshModel getQuad() {
		return Loader.INSTANCE.loadToVAO(VERTICES, INDICES);
	}
}
