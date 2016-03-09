package editor;

import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.Light;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FogFilter;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.DirectionalLightShadowFilter;
import com.jme3.shadow.PointLightShadowFilter;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;
import control.GameManager;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;

public class LoadTerrain {
	public LoadTerrain() {
	}

	private void createNodeModel(TerrainQuad terrain, Spatial spatial, Vector3f dimension, boolean editor) {

		NodeModel nodeModel = new NodeModel(spatial, dimension, spatial.getLocalTranslation());
		GameManager.getIstance().addModel(nodeModel);
		if (editor) {
			nodeModel.moveModel(spatial.getWorldTranslation());
			terrain.detachChild(spatial);
			terrain.attachChild(nodeModel.getModel());
		} else {
			terrain.detachChild(spatial);
		}
	}

	private void createNodeCharacter(TerrainQuad terrain, Spatial spatial, Vector3f dimension, boolean editor) {

		NodeEnemy nodeModel = new NodeEnemy(spatial, dimension, spatial.getLocalTranslation());
		GameManager.getIstance().addModel(nodeModel);
		if (editor) {
			nodeModel.moveModel(spatial.getWorldTranslation());
			terrain.detachChild(spatial);
			terrain.attachChild(nodeModel.getModel());

		} else {
			terrain.detachChild(spatial);
		}
	}

	private void createNodeModelBonFire(TerrainQuad terrain, Spatial spatial, Vector3f dimension, boolean editor) {

		NodeModel nodeModel = new NodeModel(spatial, dimension, spatial.getLocalTranslation());
		GameManager.getIstance().setBonfire(nodeModel);
		GameManager.getIstance().addModel(nodeModel);
		if (editor) {
			nodeModel.moveModel(spatial.getWorldTranslation());
			terrain.detachChild(spatial);
			terrain.attachChild(nodeModel.getModel());
		} else {
			terrain.detachChild(spatial);
		}
	}

	private void createNodeThief(TerrainQuad terrain, Spatial spatial, boolean editor) {
		NodeThief nodeModel = new NodeThief(spatial);
		GameManager.getIstance().setNodeThief(nodeModel);
		GameManager.getIstance().addModel(nodeModel);
		if (editor) {
			nodeModel.moveModel(spatial.getWorldTranslation());
			terrain.detachChild(spatial);
			terrain.attachChild(nodeModel.getModel());

		} else {
			terrain.detachChild(spatial);
		}
	}

	public TerrainQuad loadTerrain(String path, boolean editor) {
		TerrainQuad terrain;
		Node terrainGeo = (Node) GameManager.getIstance().getApplication().getAssetManager()
				.loadModel("Scenes/" + path);
		String something = "";
		for (Spatial spatial : ((Node) terrainGeo).getChildren())
			if (spatial.getName().contains("terrain"))
				something = spatial.getName();
		terrain = (TerrainQuad) ((Node) terrainGeo).getChild(something);
		TerrainLodControl control = new TerrainLodControl(terrain,
				GameManager.getIstance().getApplication().getCamera());
		control.setLodCalculator(new DistanceLodCalculator(65, 2.7f));
		terrain.addControl(control);
		terrain.setShadowMode(ShadowMode.CastAndReceive);
		for (Spatial spatial : terrain.getChildren()) {
			if (spatial.getName().contains("Tree")) {
				spatial.setName("Tree");
				this.createNodeModel(terrain, spatial, new Vector3f(1.57f, 10f, 1000f), editor);
			} else if (spatial.getName().contains("Portal")) {
				spatial.setName("Portal");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("Bonfire")) {
				spatial.setName("Bonfire");
				GameManager.getIstance().addPointShadow(spatial.getLocalTranslation());
				this.createNodeModelBonFire(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("Chapel")) {
				spatial.setName("Chapel");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("Castle")) {
				spatial.setName("Castle");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("House")) {
				spatial.setName("House");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("HouseMedium")) {
				spatial.setName("HouseMedium");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("HouseTwo")) {
				spatial.setName("HouseTwo");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("WindMill")) {
				spatial.setName("WindMill");
				this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), editor);
			} else if (spatial.getName().contains("Jayce")) {
				spatial.setName("Jayce");
				this.createNodeCharacter(terrain, spatial, new Vector3f(2.0f, 4.5f, 10f), editor);
			} else if (spatial.getName().contains("Rengar")) {
				spatial.setName("Rengar");
				this.createNodeCharacter(terrain, spatial, new Vector3f(3.0f, 6f, 100f), editor);
			} else if (spatial.getName().contains("Talon")) {
				spatial.setName("Talon");
				this.createNodeCharacter(terrain, spatial, new Vector3f(3.0f, 7f, 100f), editor);
			} else if (spatial.getName().contains("Wukong")) {
				spatial.setName("Wukong");
				this.createNodeCharacter(terrain, spatial, new Vector3f(3.0f, 7f, 100f), editor);
			} else if (spatial.getName().contains("Azir")) {
				spatial.setName("Azir");
				this.createNodeCharacter(terrain, spatial, new Vector3f(2.0f, 4.5f, 10f), editor);
			} else if (spatial.getName().contains("XiinZhao")) {
				spatial.setName("XiinZhao");
				this.createNodeCharacter(terrain, spatial, new Vector3f(2.0f, 4.5f, 10f), editor);
			} else if (spatial.getName().contains("Yasuo")) {
				spatial.setName("Yasuo");
				this.createNodeThief(terrain, spatial, editor);
			}
		}

		return terrain;
	}

	public Light makeAmbientLight() {
		AmbientLight ambLight = new AmbientLight();
		ambLight.setColor(new ColorRGBA(1f, 1f, 1f, 0.2f));
		return ambLight;
	}

	public PointLightShadowFilter makePointShadow(PointLight light) {

		PointLightShadowFilter pssmRendere = new PointLightShadowFilter(
				GameManager.getIstance().getApplication().getAssetManager(), 1024);
		pssmRendere.setLight(light);
		pssmRendere.setFlushQueues(false);
		return pssmRendere;
	}

	public FilterPostProcessor makeFilter(boolean shadow, boolean fog, boolean water) {

		FilterPostProcessor fpp = new FilterPostProcessor(GameManager.getIstance().getApplication().getAssetManager());
		if (shadow)
			fpp.addFilter(this.getDirectionalLightShadowFilter());
		if (water)
			fpp.addFilter(this.getWaterFilter());
		if (fog)
			fpp.addFilter(this.getFogFilter());

		return fpp;

	}

	private DirectionalLightShadowFilter getDirectionalLightShadowFilter() {
		DirectionalLightShadowFilter directionalLightShadowFilter = new DirectionalLightShadowFilter(
				GameManager.getIstance().getApplication().getAssetManager(), 1024, 4);
		DirectionalLight directionalLight = new DirectionalLight();
		directionalLight.setDirection(new Vector3f(5f, 5f, 5f).negate());
		directionalLightShadowFilter.setLight(directionalLight);
		directionalLightShadowFilter.setShadowIntensity(0.5f);
		return directionalLightShadowFilter;
	}

	private WaterFilter getWaterFilter() {
		WaterFilter water = new WaterFilter();
		water.setWaveScale(0.003f);
		water.setMaxAmplitude(2f);
		water.setFoamExistence(new Vector3f(1f, 2, 0.5f));
		water.setFoamTexture((Texture2D) GameManager.getIstance().getApplication().getAssetManager()
				.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
		water.setRefractionStrength(0.2f);
		water.setWaterHeight(-6f);
		return water;
	}

	private FogFilter getFogFilter() {
		FogFilter fog = new FogFilter();
		fog.setFogColor(new ColorRGBA(0.70f, 0.70f, 0.70f, 0.5f));
		fog.setFogDistance(800f);
		fog.setFogDensity(3.0f);
		return fog;
	}
}
