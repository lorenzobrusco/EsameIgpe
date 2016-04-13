package control;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;

import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ControlBuilder;
import de.lessvoid.nifty.builder.ControlDefinitionBuilder;
import de.lessvoid.nifty.builder.ImageBuilder;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import editor.LoadTerrain;
import logic.World;
import multiPlayer.Client;
import multiPlayer.NotifyStateModel;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;
import singlePlayer.travel.PanelGame;

/*
 * 	Questa classe, oltre ad essere un singleton
 * 	viene utilizzata per gestire, memorizzare, 
 * 	controllare tutto ci� che avviene in fasi di
 * 	gioco
 * 
 */
public class GameManager {

	private static GameManager manager;
	private Collection<NodeModel> spatial;
	private Collection<NodeModel> nodeRender;
	private Collection<NodeCharacter> enemies;
	private Collection<PointLight> lights;
	private Collection<NotifyStateModel> notifyStateModels;
	private AbstractMap<String, NodeCharacter> players;
	private AbstractMap<Integer, Element> enemiesLifeBar;
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
	private Nifty nifty;
	private String modelGame;
	private java.awt.Point point;

	private GameManager() {

		this.spatial = new Stack<>();
		this.nodeRender = new ArrayList<>();
		this.enemies = new ArrayList<>();
		this.lights = new ArrayList<>();
		this.players = new HashMap<>();
		this.notifyStateModels = new ConcurrentLinkedQueue<>();
		this.enemiesLifeBar = new HashMap<>();
		this.editor = false;

	}

	/*
	 * Questo metodo imposta il SimpleApplication in modo da poter caricare i
	 * modelli, le texture, e quant'altro da qualsiasi parte del codice
	 */
	public void setParams(SimpleApplication application) {
		this.loadTerrain = new LoadTerrain();
		this.application = application;
	}

	/*
	 * Questo metodo imposta il nodo Terreno in modo da poter attaccare e
	 * staccare un modello da qualsiasi parte del gioco
	 */
	public void setTerrain(Node terrain) {
		this.terrain = terrain;
		this.worldXExtent = ((BoundingBox) this.terrain.getWorldBound()).getXExtent();
		this.worldZExtent = ((BoundingBox) this.terrain.getWorldBound()).getZExtent();
		this.secondLayer = new boolean[(((int) (worldXExtent * 2)) + 1)][(((int) (worldZExtent * 2)) + 1)];
	}

	public void setBullet(BulletAppState appState) {
		this.bulletAppState = appState;
	}

	/*
	 * Metodo necessario per richiamare il gameManager da qualsiasi parte del
	 * codice
	 */
	public static GameManager getIstance() {
		if (manager == null)
			manager = new GameManager();
		return manager;
	}

	/*
	 * Metodo deprecato
	 */
	public void setPanelGame(PanelGame game) {
		control = new GameControl(new World(1200, 650, 0));
		this.game = game;
	}

	/*
	 * Cre un pointLight in una data posizione e lo aggiunge ad una collezioni
	 * di pointLight
	 */
	public void addPointShadow(Vector3f localTranslation) {

		PointLight light = new PointLight();
		light.setColor(new ColorRGBA(0.8f, 0.7f, 0.5f, 0.2f));
		light.setRadius(40f);
		light.setPosition(new Vector3f(localTranslation.x, localTranslation.y + 1f, localTranslation.z));
		this.lights.add(light);

	}

	/*
	 * Vengo attaccati i pointLight al terreno
	 */
	public void addPointLightToScene() {
		for (PointLight light : this.lights) {
			terrain.addLight(light);
		}
	}

	/*
	 * Viene aggiunta la fisica al gioco
	 */
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

	/*
	 * Lancia l'intelligenza artificiale del nemico nel singlePlayer
	 */
	public void startEnemiesIntelligence() {
		for (NodeCharacter enemy : this.enemies) {
			((NodeEnemy) enemy).runIntelligence();
			enemy.endAttack();
		}
	}

	/*
	 * Vengo aggiunti i modeli da renderizzare nel collezione
	 * 
	 */
	public boolean addModelRender(NodeModel model) {
		if (this.nodeRender.contains(model))
			return false;
		this.nodeRender.add(model);
		return true;
	}

	/*
	 * Controlla se il modello esiste, e se esiste lo toglie logicamente dal
	 * gioco
	 */
	public void removeModel(String name) {
		for (NodeModel model : this.spatial) {
			if (model.getName().equals(name)) {
				this.spatial.remove(model);
				return;
			}
		}
	}

	// TODO Daviede

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

	public void printSecondLayer() {
		for (int x = 0; x < this.secondLayer.length; x++) {
			for (int z = 0; z < this.secondLayer[x].length; z++) {
				if (this.secondLayer[x][z])
					;//System.out.println("obstacle on " + (x - this.worldXExtent) + " " + (z - this.worldZExtent));
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

	public void setClient(final Client client) {
		this.client = client;
	}

	public Client getClient() {
		return this.client;
	}

	public Collection<NodeModel> getModels() {
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

	public AbstractMap<String, NodeCharacter> getPlayers() {
		return this.players;
	}

	public void addPlayes(String address, NodeCharacter player) {
		this.players.put(address, player);
	}

	public void removePlayers(String address) {
		this.players.remove(address);
	}

	public void removeEnemyLifeBar(int key) {
		this.enemiesLifeBar.remove(key);

	}

	public AbstractMap<Integer, Element> getEnemiesLifeBar() {
		return this.enemiesLifeBar;
	}

	public NodeModel getBonfire() {
		return bonfire;
	}

	public void setBonfire(NodeModel bonfire) {
		this.bonfire = bonfire;
	}

	public Collection<PointLight> getLights() {
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

	public void addModelEnemy(NodeCharacter enemy) {
		this.enemies.add(enemy);

	}

	public Collection<NodeCharacter> getModelEnemys() {
		return this.enemies;
	}

	public void detachModelRender(NodeModel model) {
		this.nodeRender.remove(model);
	}

	public Collection<NodeModel> getNodeModel() {
		return this.nodeRender;
	}

	public void repaint() {
		game.repaint();
	}

	public Collection<NodeCharacter> getEnemys() {
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

	public synchronized void addNotifyStateModel(NotifyStateModel notifyStateModel) {
		this.notifyStateModels.add(notifyStateModel);
	}

	public synchronized NotifyStateModel getNotifyStateModel() {
		return ((ConcurrentLinkedQueue<NotifyStateModel>) this.notifyStateModels).poll();
	}

	public Collection<NotifyStateModel> getNotyStateModels() {
		return this.notifyStateModels;
	}

	public boolean[][] getSecondLayer() {
		return this.secondLayer;
	}

	public Nifty getNifty() {
		return nifty;
	}

	public void setNifty(Nifty nifty) {
		this.nifty = nifty;
	}

	public void createLifeBarEnemy(NodeCharacter character) {

		final Screen screen = this.nifty.getCurrentScreen();

		final ControlDefinitionBuilder builderInstanceLifeBar = new ControlDefinitionBuilder("lifeBarEnemy") {
			{

				controller(modelGame);
				image(new ImageBuilder() {
					{
						filename("Interface/border.png");
						childLayoutAbsolute();
						imageMode("resize:15,2,15,15,15,2,15,2,15,2,15,15");

						image(new ImageBuilder() {
							{
								id("progressBar" + character.getKeyCharacter());
								filename("Interface/inner.png");
								x("0");
								y("0");
								width("100%");
								height("100%");
								imageMode("resize:15,2,15,15,15,2,15,2,15,2,15,15");
							}
						});
					}
				});

			}
		};

		builderInstanceLifeBar.registerControlDefintion(nifty);

		final ControlBuilder builderLifeBar = new ControlBuilder("lifeBar" + character.getKeyCharacter(),
				"lifeBarEnemy");
		builderLifeBar.height(4 + "%");
		builderLifeBar.width(10 + "%");
		builderLifeBar.x((int) character.getLocalTranslation().x + "px");
		builderLifeBar.y((int) character.getLocalTranslation().z + "px");
		final Element lifeBarEnemy = builderLifeBar.build(nifty, nifty.getCurrentScreen(),
				screen.findElementByName("layerLifeBarEnemy"));

		//System.out.println(point.x + "  " + point.y);

		// e.setConstraintWidth(new SizeValue(100+"%"));
		// e.getParent().layoutElements();

		this.enemiesLifeBar.put(character.getKeyCharacter(), lifeBarEnemy);

	}

	public String getModelGame() {
		return modelGame;
	}

	public void setModelGame(String modelGame) {
		this.modelGame = modelGame;
	}

	public java.awt.Point getPoint() {
		return point;
	}

	public void setPoint(java.awt.Point point) {
		this.point = point;
	}

}