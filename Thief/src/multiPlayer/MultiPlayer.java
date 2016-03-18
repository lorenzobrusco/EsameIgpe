package multiPlayer;

import java.io.IOException;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import control.GameManager;
import control.GameRender;
import editor.LoadTerrain;
import singlePlayer.Sound;

public class MultiPlayer {

    private final String attack1 = "Attack1";
    private final String attack2 = "Attack2";
    private final String run = "Run";
    private final String rotateClockwise = "rotateClockwise";
    private final String rotateCounterClockwise = "rotateCounterClockwise";
    private final ViewPort viewPort;
    private final Node rootNode;
    private Node nodeScene;
    private CollisionShape collisionShape;
    private RigidBodyControl rigidBodyControl;
    private final LoadTerrain loadTerrain;
    private GameRender render;
    private Sound ambient;
    private Client client = null;

    public MultiPlayer(ViewPort viewPort, Node rootNode, Camera cam, String address, String namePlayer,
	    String nameModel) {

	this.viewPort = viewPort;
	this.rootNode = rootNode;
	cam.setFrustumFar(200);
	cam.onFrameChange();
	this.loadTerrain = new LoadTerrain();
	this.nodeScene = new Node("Scene");
	this.loadLevel("mountain", address, namePlayer, nameModel, rootNode);
	GameManager.getIstance().getNodeThief().setSinglePlayer(false);
	GameManager.getIstance().getNodeThief().setCam(cam);
	this.setKey();
	this.setupAmbientSound();

    }

    public void loadLevel(String level, String address, String namePlayer, String nameModel, Node rootNode) {
	final TerrainQuad terrainQuad = loadTerrain.loadTerrainMultiPlayer(level + ".j3o");
	this.nodeScene.attachChild(terrainQuad);
	this.nodeScene.addLight(loadTerrain.makeAmbientLight());
	this.collisionShape = CollisionShapeFactory.createMeshShape((Node) nodeScene);
	this.rigidBodyControl = new RigidBodyControl(collisionShape, 0);
	this.nodeScene.addControl(rigidBodyControl);
	try {
	    this.client = new Client(namePlayer, nameModel, address, rootNode);
	    this.client.start();
	} catch (IOException e) {
	    e.printStackTrace();
	}
	this.client.bornPosition(nodeScene);
	this.rootNode.attachChild(nodeScene);
	GameManager.getIstance().setTerrain(nodeScene);
	GameManager.getIstance().getBullet().getPhysicsSpace().add(rigidBodyControl);
	GameManager.getIstance().addPhysics();
	GameManager.getIstance().addPointLightToScene();
	this.render = new GameRender(terrainQuad);
	this.viewPort.addProcessor(loadTerrain.makeFilter(true, true, true));
    }

    public void simpleUpdate(Float tpf) {
	this.render.rayRendering();
	if (!GameManager.getIstance().getNodeThief().isRun())
	    GameManager.getIstance().getNodeThief().stop();
    }

    private void setKey() {
	GameManager.getIstance().getApplication().getInputManager().addMapping(run, new KeyTrigger(KeyInput.KEY_W));
	GameManager.getIstance().getApplication().getInputManager().addMapping(attack1,
		new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	GameManager.getIstance().getApplication().getInputManager().addMapping(attack2,
		new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
	GameManager.getIstance().getApplication().getInputManager().addMapping(rotateClockwise,
		new KeyTrigger(KeyInput.KEY_A));
	GameManager.getIstance().getApplication().getInputManager().addMapping(rotateCounterClockwise,
		new KeyTrigger(KeyInput.KEY_D));
	GameManager.getIstance().getApplication().getInputManager().addMapping("debug",
		new KeyTrigger(KeyInput.KEY_TAB));
	GameManager.getIstance().getApplication().getInputManager().addMapping("mouse",
		new KeyTrigger(KeyInput.KEY_LCONTROL));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, run, attack1, attack2, "toggleRotate");
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, run, rotateClockwise, rotateCounterClockwise);
    }

    private void setupAmbientSound() {
	this.ambient = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.8f, false);
	this.ambient.playSound();
    }
}
