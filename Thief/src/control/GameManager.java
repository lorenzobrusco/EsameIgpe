package control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioRenderer;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.PointLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import editor.LoadTerrain;
import logic.World;
import multiPlayer.Client;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;
import singlePlayer.travel.PanelGame;

public class GameManager {

    private static GameManager manager;
    private Stack<NodeModel> spatial;
    private ArrayList<NodeModel> nodeRender;
    private ArrayList<NodeCharacter> enemies;
    private HashMap<String, NodeCharacter> players;
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
    private boolean editor;
    private Client client;
    private float worldXExtent;
    private float worldZExtent;
    private boolean[][] secondLayer;

    private GameManager() {

	this.spatial = new Stack<>();
	this.nodeRender = new ArrayList<>();
	this.enemies = new ArrayList<>();
	this.lights = new ArrayList<>();
	this.players = new HashMap<>();
	this.editor = false;
    }

    public void setParams(SimpleApplication application) {
	this.loadTerrain = new LoadTerrain();
	this.application = application;
    }

    public void setTerrain(Node terrain) {
	this.terrain = terrain;
	this.worldXExtent = ((BoundingBox) this.terrain.getWorldBound()).getXExtent();
	this.worldZExtent = ((BoundingBox) this.terrain.getWorldBound()).getZExtent();
	this.secondLayer = new boolean[(((int) (worldXExtent * 2)) + 1)][(((int) (worldZExtent * 2)) + 1)];
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

    public void setClient(final Client client) {
	this.client = client;
    }
    
    public Client getClient(){
	return this.client;
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

    public synchronized void addPhysics() {
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

    public void startEnemiesIntelligence() {
	for (NodeCharacter enemy : this.enemies) {
	    ((NodeEnemy) enemy).runIntelligence();
	    enemy.endAttack();
	}
    }

    public void addModelEnemy(NodeCharacter enemy) {
	this.enemies.add(enemy);
    }

    public ArrayList<NodeCharacter> getModelEnemys() {
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

    public ArrayList<NodeCharacter> getEnemys() {
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

    public void remuveModel(String name){
	for(NodeModel model : this.spatial){
	    if(model.getName().equals(name)){
		this.spatial.remove(model);
		return;
	    }
	}
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

    public HashMap<String, NodeCharacter> getPlayers() {
	return this.players;
    }

    public void addPlayes(String address, NodeCharacter player) {
	this.players.put(address, player);
    }

    public void removePlayers(String address) {
	this.removePlayers(address);
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

    public void setAudioRender(AudioRenderer audioRenderer) {
	this.audioRenderer = audioRenderer;
    }

    public AudioRenderer getAudioRender() {
	return this.audioRenderer;
    }

    public void setEditor(boolean editor) {
	this.editor = editor;
    }

    public boolean isEditor() {
	return this.editor;
    }

    public void makeSecondLayer() {
	for (Spatial model : this.getModels()) {
	    if (!model.getName().equals("Bonfire") && !(model instanceof NodeCharacter)) {
		this.makeModelPerimeter(model);
		System.out
			.println(model.getName() + " " + "xExtent " + ((BoundingBox) model.getWorldBound()).getXExtent()
				+ ", " + "zExtent " + ((BoundingBox) model.getWorldBound()).getZExtent());
		this.secondLayer[(((int) model.getWorldBound().getCenter().getX())
			+ (int) this.worldXExtent)][(((int) model.getWorldBound().getCenter().getZ())
				+ (int) this.worldZExtent)] = true;
	    }
	}
    }

    public boolean[][] getSecondLayer() {
	return this.secondLayer;
    }

    public void printSecondLayer() {
	for (int x = 0; x < this.secondLayer.length; x++) {
	    for (int z = 0; z < this.secondLayer[x].length; z++) {
		if (this.secondLayer[x][z])
		    System.out.println("obstacle on " + (x - this.worldXExtent) + " " + (z - this.worldZExtent));
	    }
	}
    }

    private void makeModelPerimeter(Spatial model) {

	BoundingBox boundingBox = new BoundingBox();
	boundingBox.setXExtent((((BoundingBox) model.getWorldBound()).getXExtent()) + 2);
	boundingBox.setZExtent((((BoundingBox) model.getWorldBound()).getZExtent()) + 2);

	final WireBox wireBox2 = new WireBox();
	wireBox2.fromBoundingBox(boundingBox);
	wireBox2.setLineWidth(0f);
	final Geometry boxAttach = new Geometry("boxAttach", wireBox2);

	final Material material = new Material(GameManager.getIstance().getApplication().getAssetManager(),
		"Common/MatDefs/Misc/Unshaded.j3md");
	boxAttach.setMaterial(material);

	material.setColor("Color", ColorRGBA.Red);
	((Node) model).attachChild(boxAttach);
	boundingBox.setCenter(boxAttach.getLocalTranslation());

    }
}