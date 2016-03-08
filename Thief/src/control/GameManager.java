package control;

import java.util.ArrayList;
import java.util.Stack;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioRenderer;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import editor.LoadTerrain;
import logic.World;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;
import singlePlayer.travel.PanelGame;

public class GameManager {

	private static GameManager manager;

	private Stack<NodeModel> spatial;

	private ArrayList<NodeModel> nodeRender;

	private ArrayList<NodeEnemy> enemies;

	private ArrayList<PointLight> lights;

	private GameControl control;

	private PanelGame game;

	private SimpleApplication application;

	private BulletAppState bulletAppState;

	private LoadTerrain loadTerrain;

	private NodeThief thief;

	private NodeModel bonfire;

	private Node terrain;

	private AudioRenderer audioRenderer;

	private GameManager() {

		this.spatial = new Stack<>();
		this.nodeRender = new ArrayList<>();
		this.enemies = new ArrayList<>();
		this.lights = new ArrayList<>();
	}

	public void setParams(SimpleApplication application) {
		this.loadTerrain = new LoadTerrain();
		this.application = application;
	}

	public void setTerrain(Node terrain) {
		this.terrain = terrain;
	}

	public void setBullet(BulletAppState appState) {
		this.bulletAppState = appState;
	}

	public static GameManager getIstance() {
		if (manager == null)
			manager = new GameManager();
		return manager;
	}

	public void setPanelGame(PanelGame game) {
		control = new GameControl(new World(1200, 650, 0));
		this.game = game;
	}

	public void addPointShadow(Vector3f localTranslation) {

		PointLight light = new PointLight();
		light.setColor(new ColorRGBA(0.8f, 0.7f, 0.5f, 0.2f));
		light.setRadius(40f);
		light.setPosition(new Vector3f(localTranslation.x, localTranslation.y + 1f, localTranslation.z));
		this.lights.add(light);

	}

	public void addPointLightToScene() {
		for (PointLight light : this.lights) {
			terrain.addLight(light);
		}
	}

	public void addPhysics() {
		for (NodeModel model : spatial) {

			if (model.getName().contains("Chapel") || model.getName().contains("Tree")
					|| model.getName().contains("House") || model.getName().contains("HouseMedium")
					|| model.getName().contains("HouseTwo") || model.getName().contains("WindMill")
					|| model.getName().contains("Portal") || model.getName().contains("Castle")
					|| model.getName().contains("Bonfire")) {
				CollisionShape collisionShape = CollisionShapeFactory.createMeshShape(model);
				RigidBodyControl body = new RigidBodyControl(collisionShape, 0);
				model.addControl(body);
			} else {
				model.addCharacterControll();
			}
			bulletAppState.getPhysicsSpace().add(model);
		}
	}

	public void addModelEnemy(NodeEnemy enemy) {
		this.enemies.add(enemy);
	}

	public ArrayList<NodeEnemy> getModelEnemys() {
		return this.enemies;
	}

	public boolean addModelRender(NodeModel model) {
		if (this.nodeRender.contains(model))
			return false;
		this.nodeRender.add(model);
		return true;
	}

	public void detachModelRender(NodeModel model) {
		this.nodeRender.remove(model);
	}

	public ArrayList<NodeModel> getNodeModel() {
		return this.nodeRender;
	}

	public void repaint() {
		game.repaint();
	}

	public ArrayList<NodeEnemy> getEnemys() {
		return this.enemies;
	}

	public Node getTerrain() {
		return this.terrain;
	}

	public GameControl getControl() {
		return control;
	}

	public Application getApplication() {
		return application;
	}

	public BulletAppState getBullet() {
		return bulletAppState;
	}

	public void addModel(NodeModel model) {
		spatial.add(model);
	}

	public Stack<NodeModel> getModels() {
		return spatial;
	}

	public LoadTerrain getLoadTerrain() {
		return loadTerrain;
	}

	public NodeThief getNodeThief() {
		return thief;
	}

	public void setNodeThief(NodeThief thief) {
		this.thief = thief;
	}

	public NodeModel getBonfire() {
		return bonfire;
	}

	public void setBonfire(NodeModel bonfire) {
		this.bonfire = bonfire;
	}

	public ArrayList<PointLight> getLights() {
		return this.lights;
	}

	public Node getRoootNode() {
		return this.application.getRootNode();
	}

	public void setAudioRender(AudioRenderer audioRenderer) {
		this.audioRenderer = audioRenderer;
	}

	public AudioRenderer getAudioRender() {
		return this.audioRenderer;
	}
}