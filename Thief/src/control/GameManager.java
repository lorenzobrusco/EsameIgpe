package control;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.audio.AudioRenderer;
import com.jme3.bounding.BoundingBox;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.PointLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import editor.LoadTerrain;
import multiPlayer.Client;
import multiPlayer.ModelState;
import multiPlayer.MultiPlayer;
import multiPlayer.Pair;
import multiPlayer.notify.NotifyBoxAttack;
import multiPlayer.notify.NotifyStateModel;
import server.Server;
import singlePlayer.SinglePlayer;
import singlePlayer.Sound;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;

/**
 * 
 * this class is game's manager
 *
 */

public class GameManager {

	/** singleton */
	private static GameManager manager;
	/** models */
	private Collection<NodeModel> spatial;
	/** models to rendering */
	private Collection<NodeModel> nodeRender;
	/** enemies */
	private Collection<NodeCharacter> enemies;
	/** light */
	private Collection<PointLight> lights;
	/** state models */
	private Collection<NotifyStateModel> notifyStateModels;
	/** box attack */
	private Collection<NotifyBoxAttack> boxsAttack;
	/** player's states */
	private Collection<Pair<NodeCharacter, ModelState>> states;
	/** score multiplayer */
	private List<NodeCharacter> scorePlayers;
	/** players multiplayer */
	private AbstractMap<String, NodeCharacter> players;
	/** enemy's lifebars */
	private AbstractMap<Integer, Element> enemiesLifeBar;
	/** application */
	private SimpleApplication application;
	/** jmonkey's object */
	private BulletAppState bulletAppState;
	/** load terrain */
	private LoadTerrain loadTerrain;
	/** main's character */
	private NodeThief thief;
	/** bonfire */
	private NodeModel bonfire;
	/** portal */
	private NodeModel portal;
	/** terrain */
	private Node terrain;
	/** terrainquand */
	private TerrainQuad terrainQuad;
	/** singleplayer */
	private SinglePlayer singlePlayer;
	/** multiplayer */
	private MultiPlayer multiplayer;
	/** sounds */
	private AudioRenderer audioRenderer;
	/** it's true if choose editor */
	private boolean editor;
	/** client */
	private Client client;
	/** extends x */
	private float worldXExtent;
	/** extends z */
	private float worldZExtent;
	/** matrix */
	private boolean[][] secondLayer;
	/** panel 2d */
	private Nifty nifty;
	/** game type */
	private String modelGame;
	/** it's true if is pause */
	private boolean paused;
	/** server */
	private Server server;
	/** sound */
	private Sound menuSound;

	/** builder */
	private GameManager() {

		this.spatial = new Stack<>();
		this.nodeRender = new ArrayList<>();
		this.enemies = new ArrayList<>();
		this.lights = new ArrayList<>();
		this.scorePlayers = new ArrayList<>();
		this.players = new HashMap<>();
		this.notifyStateModels = new ConcurrentLinkedQueue<>();
		this.boxsAttack = new ConcurrentLinkedQueue<>();
		this.states = new ConcurrentLinkedQueue<>();
		this.enemiesLifeBar = new HashMap<>();
		this.editor = false;
		this.paused = false;

	}

	/** getInstance singleton */
	public static GameManager getIstance() {
		if (manager == null)
			manager = new GameManager();
		return manager;
	}

	/** this method set params */
	public void setParams(SimpleApplication application) {
		this.loadTerrain = new LoadTerrain();
		this.application = application;
	}

	/** this method set terrain */
	public void setTerrain(Node terrain) {
		this.terrain = terrain;
		this.worldXExtent = ((BoundingBox) this.terrain.getWorldBound()).getXExtent();
		this.worldZExtent = ((BoundingBox) this.terrain.getWorldBound()).getZExtent();
		this.secondLayer = new boolean[(((int) (worldXExtent * 2)) + 1)][(((int) (worldZExtent * 2)) + 1)];
	}

	/** this method add bonfire's light */
	public void addPointShadow(Vector3f localTranslation) {

		PointLight light = new PointLight();
		light.setColor(new ColorRGBA(0.8f, 0.7f, 0.5f, 0.2f));
		light.setRadius(40f);
		light.setPosition(new Vector3f(localTranslation.x, localTranslation.y + 1f, localTranslation.z));
		this.lights.add(light);

	}

	/** this method add all lights */
	public void addPointLightToScene() {
		for (PointLight light : this.lights) {
			terrain.addLight(light);
		}
	}

	/** this method add physic */
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
				model.addCharacterControl();
			}
			this.bulletAppState.getPhysicsSpace().add(model);
		}
	}

	/** this method start enemy's ai */
	public void startEnemiesIntelligence() {
		for (NodeCharacter enemy : this.enemies) {
			((NodeEnemy) enemy).runIntelligence();
		}
	}

	/** this method add model to rendering */
	public boolean addModelRender(NodeModel model) {
		if (this.nodeRender.contains(model))
			return false;
		this.nodeRender.add(model);
		return true;
	}

	/** this method remove model */
	public void removeModel(String name) {
		for (NodeModel model : this.spatial) {
			if (model.getName().equals(name)) {
				this.spatial.remove(model);
				return;
			}
		}
	}

	/** this method check if point x,z is free */
	public boolean isWalkable(float x, float z) {
		if (this.secondLayer[(int) (x + this.worldXExtent)][(int) (z + this.worldZExtent)]
				|| this.getTerrainQuad().getHeight(new Vector2f(x, z)) < -2
						&& this.getTerrainQuad().getHeight(new Vector2f(x, z)) > 10) {
			return false;
		}
		return true;
	}

	/** build a boolean matrix to know if point(x,z) is walkable */
	public void makeSecondLayer() {
		for (Spatial model : this.getModels()) {
			if (!model.getName().equals("Bonfire") && !(model instanceof NodeCharacter)) {
				this.makeModelArea(model);
				this.secondLayer[(((int) model.getWorldBound().getCenter().getX())
						+ (int) this.worldXExtent)][(((int) model.getWorldBound().getCenter().getZ())
								+ (int) this.worldZExtent)] = true;
			}
		}
	}

	/**
	 * set to true or false secondLayer matrix cells, if it is true that point
	 * is walkable else not
	 */
	private void makeModelArea(Spatial model) {

		int xModelStart = (int) (model.getLocalTranslation().getX()
				- (((BoundingBox) model.getWorldBound()).getXExtent() / 2) - 10);
		int zModelStart = (int) (model.getLocalTranslation().getZ()
				- (((BoundingBox) model.getWorldBound()).getZExtent() / 2) - 10);

		int xModelEnd = (int) (model.getLocalTranslation().getX()
				+ (((BoundingBox) model.getWorldBound()).getXExtent() / 2) + 10);
		int zModelEnd = (int) (model.getLocalTranslation().getZ()
				+ (((BoundingBox) model.getWorldBound()).getZExtent() / 2) + 10);

		for (int x = xModelStart; x < xModelEnd; x++) {
			for (int z = zModelStart; z < zModelEnd; z++) {
				this.secondLayer[(int) (x + this.worldXExtent)][(int) (z + this.worldZExtent)] = true;
			}
		}
	}

	/** this method set pause */
	public void pauseGame() {
		this.paused = true;
		this.application.getInputManager().setCursorVisible(true);
		this.thief.getCamera().setEnabled(false);
		this.thief.stop();

		for (NodeModel model : this.getModels()) {

			for (Sound sound : model.getAllSound()) {
				sound.stopSound();
			}

			if (model instanceof NodeEnemy)
				((NodeEnemy) model).pauseIntelligence();
		}
	}

	/** this method is called when user come back to menu */
	public void quitGame() {

		for (NodeModel model : this.getModels()) {

			for (Sound sound : model.getAllSound()) {
				sound.stopSound();
			}

			if (model instanceof NodeEnemy)
				((NodeEnemy) model).pauseIntelligence();
		}

		this.menuSound = new Sound(this.terrain, "Menu", false, false, true, 1.0f, false);
		this.menuSound.playSound();
		this.spatial.clear();
		this.nodeRender.clear();
		this.enemies.clear();
		this.lights.clear();
		this.notifyStateModels.clear();
		this.players.clear();
		this.enemiesLifeBar.clear();
		this.terrain.detachAllChildren();
	}

	/** this method is called to resume game */
	public void resumeGame() {
		this.paused = false;
		this.thief.getCamera().setEnabled(true);
		this.thief.getCamera().setDragToRotate(false);
		this.application.getInputManager().setCursorVisible(false);

		for (NodeCharacter enemy : enemies)
			((NodeEnemy) enemy).resumeIntelligence();
	}

	/** this method start server */
	public void startServer(String path, int port) {
		try {
	
			this.server = new Server(path, port);
			this.server.start();
		} catch (UnknownHostException e) {

			e.printStackTrace();
		} catch (IOException e) {
			// TODO catch
			e.printStackTrace();
		}
	}

	/** this method sort score lists */
	public void sortScorePlyer() {// TODO score
		this.scorePlayers.sort(new Comparator<NodeCharacter>() {
			@Override
			public int compare(NodeCharacter arg0, NodeCharacter arg1) {
				if (arg0.getScore() > arg1.getScore())
					return 1;
				else if (arg0.getScore() < arg1.getScore())
					return 2;
				else
					return 0;
			}
		});
		for (int i = 0; i < this.scorePlayers.size(); i++) {
			this.multiplayer.setPlayerInScoreLists(((ArrayList<NodeCharacter>) this.scorePlayers).get(i).getName()
					+ ": " + ((ArrayList<NodeCharacter>) this.scorePlayers).get(i).getScore() + "", i);
		}
	}

	/** this method set bullet */
	public void setBullet(BulletAppState appState) {
		this.bulletAppState = appState;
	}

	/** this method set client */
	public void setClient(final Client client) {
		this.client = client;
	}

	/** this method get client */
	public Client getClient() {
		return this.client;
	}

	/** this method get models */
	public Collection<NodeModel> getModels() {
		return spatial;
	}

	/** this method get loadterrain */
	public LoadTerrain getLoadTerrain() {
		return loadTerrain;
	}

	/** this method get main's character */
	public NodeThief getNodeThief() {
		return thief;
	}

	/** this method set main's character */
	public void setNodeThief(NodeThief thief) {
		this.thief = thief;
	}

	/** this method get players */
	public AbstractMap<String, NodeCharacter> getPlayers() {
		return this.players;
	}

	/** this method add players */
	public void addPlayes(String address, NodeCharacter player) {
		this.players.put(address, player);
	}

	/** this method remove players */
	public void removePlayers(String address) {
		this.players.remove(address);
	}

	/** this method remove enemy's lifebar */
	public void removeEnemyLifeBar(int key) {
		this.enemiesLifeBar.remove(key);
	}

	/** this method get enemy's lifebar */
	public AbstractMap<Integer, Element> getEnemiesLifeBar() {
		return this.enemiesLifeBar;
	}

	/** this method get bonfire */
	public NodeModel getBonfire() {
		return bonfire;
	}

	/** this method set bonfire */
	public void setBonfire(NodeModel bonfire) {
		this.bonfire = bonfire;
	}

	/** this method get lights */
	public Collection<PointLight> getLights() {
		return this.lights;
	}

	/** this method set sounds */
	public void setAudioRender(AudioRenderer audioRenderer) {
		this.audioRenderer = audioRenderer;
	}

	/** this method get sounds */
	public AudioRenderer getAudioRender() {
		return this.audioRenderer;
	}

	/** this method set editor */
	public void setEditor(boolean editor) {
		this.editor = editor;
	}

	/** this method get editor */
	public boolean isEditor() {
		return this.editor;
	}

	/** this method add emenies */
	public void addModelEnemy(NodeCharacter enemy) {
		this.enemies.add(enemy);

	}

	/** this method get enemies */
	public Collection<NodeCharacter> getModelEnemys() {
		return this.enemies;
	}

	/** this method get application */
	public Application getApplication() {
		return application;
	}

	/** this method remove model to rendering */
	public void detachModelRender(NodeModel model) {
		this.nodeRender.remove(model);
	}

	/** this method get model to rendering */
	public Collection<NodeModel> getNodeModel() {
		return this.nodeRender;
	}

	/** this method get enemies */
	public Collection<NodeCharacter> getEnemies() {
		return this.enemies;
	}

	/** this method get terrain */
	public Node getTerrain() {
		return this.terrain;
	}

	/** this method get bullet */
	public BulletAppState getBullet() {
		return this.bulletAppState;
	}

	/** this method add model */
	public void addModel(NodeModel model) {
		this.spatial.add(model);
	}

	/** this method add model's state */
	public synchronized void addNotifyStateModel(NotifyStateModel notifyStateModel) {
		this.notifyStateModels.add(notifyStateModel);
	}

	/** this method get model's state */
	public synchronized NotifyStateModel getNotifyStateModel() {
		return ((ConcurrentLinkedQueue<NotifyStateModel>) this.notifyStateModels).poll();
	}

	/** this method check if collection is empty */
	public synchronized boolean getNotyStateModelsIsEmpty() {
		return this.notifyStateModels.isEmpty();
	}

	/** this method add box attack */
	public synchronized void addBoxAttack(NotifyBoxAttack boxAttack) {
		this.boxsAttack.add(boxAttack);
	}

	/** this method add state */
	public synchronized void addState(NodeCharacter character, ModelState modelState) {
		this.states.add(new Pair<NodeCharacter, ModelState>(character, modelState));
	}

	/** this method return true if states is empty */
	public synchronized boolean stateIsEmpty() {
		return ((ConcurrentLinkedQueue<Pair<NodeCharacter, ModelState>>) this.states).isEmpty();
	}

	/** this method get box attack */
	public synchronized NotifyBoxAttack getBoxAttack() {
		return ((ConcurrentLinkedQueue<NotifyBoxAttack>) this.boxsAttack).poll();
	}

	/** this method check if collection is empty */
	public synchronized boolean getBoxsAttackIsEmpty() {
		return this.boxsAttack.isEmpty();
	}

	/** this method add player and score */
	public void addScorePlayer(NodeCharacter character) {
		this.scorePlayers.add(character);
		// this.sortScorePlyer();
	}

	/** this method remove score */
	public void removeScorePlayer(NodeCharacter character) {
		this.scorePlayers.remove(character);
	}

	/** this method get score */
	public ArrayList<NodeCharacter> getScorePlayer() {
		return (ArrayList<NodeCharacter>) this.scorePlayers;
	}

	/** this method matrix */
	public boolean[][] getSecondLayer() {
		return this.secondLayer;
	}

	/** this method get nifty */
	public Nifty getNifty() {
		return nifty;
	}

	/** this method set nifty */
	public void setNifty(Nifty nifty) {
		this.nifty = nifty;
	}

	/** this method get game type */
	public String getModelGame() {
		return modelGame;
	}

	/** this method set game type */
	public void setModelGame(String modelGame) {
		this.modelGame = modelGame;
	}

	/** this method get pause */
	public boolean isPaused() {
		return this.paused;
	}

	/** this method set pause */
	public void setPaused(boolean value) {
		this.paused = value;
	}

	/** this method get server */
	public Server getServer() {
		return server;
	}

	/** this method get terrainquand */
	public TerrainQuad getTerrainQuad() {
		return this.terrainQuad;
	}

	/** this method set terrainquad */
	public void setTerrainQuad(final TerrainQuad quad) {
		this.terrainQuad = quad;
	}

	/** this method get singleplayer */
	public SinglePlayer getSinglePlayer() {
		return singlePlayer;
	}

	/** this method set singleplayer */
	public void setSinglePlayer(SinglePlayer singlePlayer) {
		this.singlePlayer = singlePlayer;
	}

	/** this method get multiplayer */
	public MultiPlayer getMultiplayer() {
		return multiplayer;
	}

	/** this method set multiplayer */
	public void setMultiplayer(MultiPlayer multiplayer) {
		this.multiplayer = multiplayer;
	}

	/** this method get portal */
	public NodeModel getPortal() {
		return portal;
	}

	public Collection<Pair<NodeCharacter, ModelState>> getStates() {
		return states;
	}

	/** this method set portal */
	public void setPortal(NodeModel portal) {
		this.portal = portal;
	}

	/** this method get x */
	public float getWorldXExtent() {
		return worldXExtent;
	}

	/** this method z */
	public float getWorldZExtent() {
		return worldZExtent;
	}

	public void stopMenuSound() {
		if (!(this.menuSound == null))
			this.menuSound.stopSound();
	}
}
