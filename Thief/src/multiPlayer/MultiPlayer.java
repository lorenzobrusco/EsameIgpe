package multiPlayer;

import java.io.IOException;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import control.GameManager;
import control.GameRender;
import editor.LoadTerrain;
import singlePlayer.Sound;

public class MultiPlayer {

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
	this.loadLevel("mountain", address, namePlayer, nameModel, rootNode, cam);
	this.setupAmbientSound();
    }

    public void loadLevel(String level, String address, String namePlayer, String nameModel, Node rootNode,
	    Camera cam) {
	try {
	    final TerrainQuad terrainQuad = loadTerrain.loadTerrainMultiPlayer(level + ".j3o");
	    this.nodeScene.attachChild(terrainQuad);
	    this.nodeScene.addLight(loadTerrain.makeAmbientLight());
	    this.collisionShape = CollisionShapeFactory.createMeshShape((Node) nodeScene);
	    this.rigidBodyControl = new RigidBodyControl(collisionShape, 0);
	    this.nodeScene.addControl(rigidBodyControl);
	    this.client = new Client(namePlayer, nameModel, address, rootNode, cam);
	    this.client.bornPosition(nodeScene);
	    this.client.start();
	    this.rootNode.attachChild(nodeScene);
	    GameManager.getIstance().setTerrain(nodeScene);
	    GameManager.getIstance().getBullet().getPhysicsSpace().add(rigidBodyControl);
	    GameManager.getIstance().addPhysics();
	    GameManager.getIstance().addPointLightToScene();
	    GameManager.getIstance().setClient(client);
	    this.render = new GameRender(terrainQuad);
	    this.viewPort.addProcessor(loadTerrain.makeFilter(true, true, true));
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void simpleUpdate(Float tpf) {
	if (GameManager.getIstance().getNodeThief().isControlRender())
	    this.render.rayRendering();
	if (!GameManager.getIstance().getNodeThief().isRun())
	    GameManager.getIstance().getNodeThief().stop();
	if (!GameManager.getIstance().getNotyStateModels().isEmpty()) {
	    System.out.println("state");
	    NotifyStateModel stateModel = GameManager.getIstance().getNotifyStateModel();
	    if (stateModel.isAttach()) {
		GameManager.getIstance().getTerrain().attachChild(stateModel.getModel());
	    } else {
		GameManager.getIstance().getTerrain().detachChild(stateModel.getModel());
	    }

	}

    }

    public void exit() {
	this.client.endConnection();
    }

    private void setupAmbientSound() {
	this.ambient = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.8f, false);
	this.ambient.playSound();
    }
}
