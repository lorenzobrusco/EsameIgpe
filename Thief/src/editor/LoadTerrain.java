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

/**
 * 
 * this class load landscape and call corresponding class for each model linked
 * in it
 *
 */
public class LoadTerrain {

    /**
     * this methods, if it's called from editor add spatial while if it isn't
     * called from editor add corresponding static model
     */
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

    /**
     * this methods, if it's called from editor add spatial while if it isn't
     * called from editor add corresponding character
     */
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

    /**
     * this methods, if it's called from editor add spatial while if it isn't
     * called from editor add spawn point
     */
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

    /**
     * this methods, if it's called from editor add spatial while if it isn't
     * called from editor add main character
     */
    private void createNodeThief(TerrainQuad terrain, Spatial spatial, boolean editor) {
	NodeThief nodeModel = new NodeThief(spatial, spatial.getLocalTranslation(), false);
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

    /**
     * this method load landscape in editor or single player and for each model
     * linked in it call corresponding class
     */
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
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.5f, 4.4f, 90f), editor);
	    } else if (spatial.getName().contains("Rengar")) {
		spatial.setName("Rengar");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.5f, 4.4f, 80f), editor);
	    } else if (spatial.getName().contains("Talon")) {
		spatial.setName("Talon");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.5f, 4.4f, 50f), editor);
	    } else if (spatial.getName().contains("Wukong")) {
		spatial.setName("Wukong");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.4f, 2.6f, 40f), editor);
	    } else if (spatial.getName().contains("Azir")) {
		spatial.setName("Azir");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.5f, 4.4f, 60f), editor);
	    } else if (spatial.getName().contains("XiinZhao")) {
		spatial.setName("XiinZhao");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.5f, 4.4f, 50f), editor);
	    } else if (spatial.getName().contains("Katarina")) {
		spatial.setName("Katarina");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.2f, 3.6f, 45f), editor);
	    } else if (spatial.getName().contains("Jarvan")) {
		spatial.setName("Jarvan");
		this.createNodeCharacter(terrain, spatial, new Vector3f(2.0f, 5.5f, 45f), editor);
	    } else if (spatial.getName().contains("Fiora")) {
		spatial.setName("Fiora");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.2f, 4f, 50f), editor);
	    } else if (spatial.getName().contains("Sejuani")) {
		spatial.setName("Sejuani");
		this.createNodeCharacter(terrain, spatial, new Vector3f(2.5f, 6f, 280f), editor);
	    } else if (spatial.getName().contains("Volibear")) {
		spatial.setName("Volibear");
		this.createNodeCharacter(terrain, spatial, new Vector3f(3f, 7f, 200f), editor);
	    } else if (spatial.getName().contains("WarWick")) {
		spatial.setName("WarWick");
		this.createNodeCharacter(terrain, spatial, new Vector3f(1.5f, 4.4f, 150f), editor);
	    } else if (spatial.getName().contains("Yasuo")) {
		spatial.setName("Yasuo");
		this.createNodeThief(terrain, spatial, editor);
	    }
	}
	GameManager.getIstance().setTerrainQuad(terrain);
	return terrain;
    }

    /**
     * this method load landscape in multiplayer and for each model linked in it
     * call corresponding class
     */
    public TerrainQuad loadTerrainMultiPlayer(String path) {
	TerrainQuad terrain;
	Node terrainGeo = (Node) GameManager.getIstance().getApplication().getAssetManager()
		.loadModel("MultiPlayer/" + path);
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
		this.createNodeModel(terrain, spatial, new Vector3f(1.57f, 10f, 1000f), false);
	    } else if (spatial.getName().contains("Portal")) {
		spatial.setName("Portal");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("Bonfire")) {
		spatial.setName("Bonfire");
		GameManager.getIstance().addPointShadow(spatial.getLocalTranslation());
		this.createNodeModelBonFire(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("Chapel")) {
		spatial.setName("Chapel");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("Castle")) {
		spatial.setName("Castle");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("House")) {
		spatial.setName("House");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("HouseMedium")) {
		spatial.setName("HouseMedium");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("HouseTwo")) {
		spatial.setName("HouseTwo");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    } else if (spatial.getName().contains("WindMill")) {
		spatial.setName("WindMill");
		this.createNodeModel(terrain, spatial, new Vector3f(7.3f, 15f, 1000f), false);
	    }
	}
	GameManager.getIstance().setTerrainQuad(terrain);
	return terrain;
    }

    /*** this method make ambient light */
    public Light makeAmbientLight() {
	AmbientLight ambLight = new AmbientLight();
	ambLight.setColor(new ColorRGBA(1f, 1f, 1f, 0.2f));
	return ambLight;
    }

    /** this method make direction light */
    public Light makeDirectionLight() {
	DirectionalLight sun = new DirectionalLight();
	sun.setDirection(new Vector3f(-5f, -5f, -5f).negate());
	sun.setColor(new ColorRGBA(1f, 1f, 1f, 0.2f));
	return sun;
    }

    /** this ethod make and add light to bonfire */
    public PointLightShadowFilter makePointShadow(PointLight light) {

	PointLightShadowFilter pssmRendere = new PointLightShadowFilter(
		GameManager.getIstance().getApplication().getAssetManager(), 1024);
	pssmRendere.setLight(light);
	pssmRendere.setFlushQueues(false);
	return pssmRendere;
    }

    /** this method add water, shadow and fog */
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

    /** this method make shadow */
    private DirectionalLightShadowFilter getDirectionalLightShadowFilter() {
	DirectionalLightShadowFilter directionalLightShadowFilter = new DirectionalLightShadowFilter(
		GameManager.getIstance().getApplication().getAssetManager(), 1024, 4);
	DirectionalLight directionalLight = new DirectionalLight();
	directionalLight.setDirection(new Vector3f(5f, 5f, 5f).negate());
	directionalLightShadowFilter.setLight(directionalLight);
	directionalLightShadowFilter.setShadowIntensity(0.5f);
	return directionalLightShadowFilter;
    }

    /** this method make water */
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

    /** this method make fog */
    private FogFilter getFogFilter() {
	FogFilter fog = new FogFilter();
	fog.setFogColor(new ColorRGBA(0.70f, 0.70f, 0.70f, 0.5f));
	fog.setFogDistance(800f);
	fog.setFogDensity(3.0f);
	return fog;
    }

}
