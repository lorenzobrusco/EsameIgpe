package multiPlayer;

import java.io.IOException;

import javax.vecmath.GMatrix;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import control.GameManager;
import control.GameRender;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.elements.render.TextRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.LoadTerrain;
import multiPlayer.notify.NotifyBoxAttack;
import multiPlayer.notify.NotifyStateModel;
import singlePlayer.Sound;

/**
 * 
 * This class is manager to multiplayer
 *
 */

public class MultiPlayer implements ScreenController {

    /** node with landscape */
    private Node nodeScene;
    /** object to load landscape */
    private final LoadTerrain loadTerrain;
    /** manager to render */
    private GameRender render;
    /** sounds */
    private Sound ambient;
    /** manager to player */
    private Client client = null;
    /** player's name */
    private String nameModel;
    /** player's lifebar */
    private Element progressLifeBarThief;
    /** character's image */
    private Element borderLifeBarThief;
    /** jmonkey's object */
    private RigidBodyControl rigidBodyControl;
    /** jmonkey's object */
    private CollisionShape collisionShape;
    /** jmonkey's object */
    private final Node rootNode;
    /** jmonkey's object */
    private final ViewPort viewPort;

    public MultiPlayer(ViewPort viewPort, Node rootNode, Camera cam, String address, String namePlayer,
	    String nameModel) {
	this.nameModel = nameModel;
	this.viewPort = viewPort;
	this.rootNode = rootNode;
	cam.setFrustumFar(200);
	cam.onFrameChange();
	this.nameModel = nameModel;
	this.loadTerrain = new LoadTerrain();
	this.nodeScene = new Node("Scene");
	this.loadLevel("mountain", address, namePlayer, nameModel, rootNode, cam);
	this.loadNifty();
	this.setupAmbientSound();
	GameManager.getIstance().setMultiplayer(this);
    }

    /** this method is called for each update */
    public void simpleUpdate(Float tpf) {
	this.render.rayRendering();
	if (!GameManager.getIstance().getNodeThief().isRun())
	    GameManager.getIstance().getNodeThief().stop();
	if (!GameManager.getIstance().getNotyStateModelsIsEmpty()) {
	    NotifyStateModel stateModel = GameManager.getIstance().getNotifyStateModel();
	    if (stateModel.isAttach()) {
		GameManager.getIstance().getTerrain().attachChild(stateModel.getModel());
	    } else {
		GameManager.getIstance().getTerrain().detachChild(stateModel.getModel());
	    }
	}
	if (!GameManager.getIstance().getBoxsAttackIsEmpty()) {
	    NotifyBoxAttack box = GameManager.getIstance().getBoxAttack();
	    if (box.isAttach())
		GameManager.getIstance().getTerrain().attachChild(box.getModel());
	    else
		GameManager.getIstance().getTerrain().detachChild(box.getModel());
	}
    }

    /** this method load landscape */
    public void loadLevel(String level, String address, String namePlayer, String nameModel, Node rootNode,
	    Camera cam) {
	try {
	    final TerrainQuad terrainQuad = loadTerrain.loadTerrainMultiPlayer(level + ".j3o");
	    this.nodeScene.attachChild(terrainQuad);
	    this.nodeScene.addLight(loadTerrain.makeAmbientLight());
	    this.collisionShape = CollisionShapeFactory.createMeshShape((Node) nodeScene);
	    this.rigidBodyControl = new RigidBodyControl(collisionShape, 0);
	    this.nodeScene.addControl(rigidBodyControl);
	    this.client = new Client(namePlayer, nameModel, address, cam);
	    GameManager.getIstance().setTerrain(nodeScene);
	    GameManager.getIstance().makeSecondLayer();
	    this.client.bornPosition(nodeScene);
	    this.rootNode.attachChild(nodeScene);
	    GameManager.getIstance().getBullet().getPhysicsSpace().add(rigidBodyControl);
	    GameManager.getIstance().addPhysics();
	    GameManager.getIstance().addPointLightToScene();
	    this.client.start();
	    GameManager.getIstance().setClient(client);
	    this.render = new GameRender(terrainQuad);
	    this.viewPort.addProcessor(loadTerrain.makeFilter(true, true, true));
	} catch (IOException e) {
	    // TODO catch
	    e.printStackTrace();
	}
    }

    /** this method disconnect player */
    public void exit() {
	this.client.endConnection();
    }

    /** this method set sound */
    private void setupAmbientSound() {
	this.ambient = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.8f, false);
	this.ambient.playSound();
    }

    /** send message when player press send button */
    public void sendMessage() {

	TextField text = GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("#chat-text-input",
		TextField.class);
	GameManager.getIstance().getClient().sendMessage(text.getDisplayedText());
	text.setText("");

    }

    /** this method load panel 2d */
    private void loadNifty() {

	GameManager.getIstance().getNifty().fromXml("Interface/Xml/multiPlayer.xml", "lifeBarScreen", this);
	this.borderLifeBarThief = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		.findElementByName("borderLifeBarThief");
	System.out.println(nameModel);
	GameManager.getIstance().getNodeThief().setLifeBar(progressLifeBarThief, borderLifeBarThief, nameModel);

    }

    /** this method sort player */
    public void setPlayerInScoreLists(String name, int number) {

	Element element;
	switch (number) {
	case 0:
	    element = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		    .findElementByName("firstPlayerText");
	    element.getRenderer(TextRenderer.class).setText(name);
	    break;
	case 1:
	    element = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		    .findElementByName("secondPlayerText");
	    element.getRenderer(TextRenderer.class).setText(name);
	    break;
	case 2:
	    element = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		    .findElementByName("thirdPlayerText");
	    element.getRenderer(TextRenderer.class).setText(name);
	    break;
	default:
	    break;
	}
    }

    /** this method is called when cursor move up a button */
    public void startGrow(String nameButton) {

	NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/Image/Button/" + nameButton + "OnHover.png", false);
	Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    /** this method is called when editor is close */
    public void endGrow(String nameButton) {

	NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/Image/Button/" + nameButton + ".png", false);
	Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    public void UpdateScore() {

    }

    /** jmonkey's methods */
    @Override
    public void bind(Nifty arg0, Screen arg1) {
    }

    /** jmonkey's methods */
    @Override
    public void onEndScreen() {
    }

    /** jmonkey's methods */
    @Override
    public void onStartScreen() {
    }

}
