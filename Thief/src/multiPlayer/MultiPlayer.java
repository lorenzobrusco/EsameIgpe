package multiPlayer;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
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
    private Sound ambientSound;
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
    /** input manager */
    private final InputManager inputManager;
    /** camera */
    private final Camera cam;
    /** start */
    private boolean start;
    /** check if game is created */
    private boolean created;

    /**
     * constructor
     * 
     */
    public MultiPlayer(InputManager inputManager, ViewPort viewPort, Node rootNode, Camera cam, String address,
	    String namePlayer, String nameModel, int port) throws UnknownHostException, IOException {
	this.start = false;
	this.created = false;
	this.client = new Client(namePlayer, nameModel, address, inputManager, cam, port);
	this.client.start();
	this.nameModel = nameModel;
	this.viewPort = viewPort;
	this.rootNode = rootNode;
	this.inputManager = inputManager;
	this.cam = cam;
	cam.setFrustumFar(200);
	cam.onFrameChange();
	this.loadTerrain = new LoadTerrain();
	this.nodeScene = new Node("Scene");
	GameManager.getIstance().setMultiplayer(this);
    }

    /** this method is called for each update */
    public void simpleUpdate(Float tpf) {
	if (this.start) {
	    this.loadLevel(this.client.getNameTerrain(), this.rootNode, this.cam, this.inputManager);
	    MultiPlayer.this.start = false;
	}
	if (this.created) {
	    this.render.rayRendering();
	    if (!GameManager.getIstance().getNodeThief().isRun())
		GameManager.getIstance().getNodeThief().stop();
	}
    }

    /** this method load landscape */
    public void loadLevel(String level, Node rootNode, Camera cam, InputManager inputManager) {
	new Thread() {
	    public void run() {
		GameManager.getIstance().getApplication().enqueue(new Callable<Void>() {
		    public Void call() {
			final TerrainQuad terrainQuad = loadTerrain.loadTerrainMultiPlayer(level + ".j3o");
			MultiPlayer.this.nodeScene.attachChild(terrainQuad);
			MultiPlayer.this.nodeScene.addLight(loadTerrain.makeAmbientLight());
			MultiPlayer.this.collisionShape = CollisionShapeFactory.createMeshShape((Node) nodeScene);
			MultiPlayer.this.rigidBodyControl = new RigidBodyControl(collisionShape, 0);
			MultiPlayer.this.nodeScene.addControl(rigidBodyControl);
			GameManager.getIstance().setTerrain(nodeScene);
			GameManager.getIstance().makeSecondLayer();
			MultiPlayer.this.client.bornPosition(nodeScene);
			MultiPlayer.this.rootNode.attachChild(nodeScene);
			GameManager.getIstance().getBullet().getPhysicsSpace().add(rigidBodyControl);
			GameManager.getIstance().addPhysics();
			GameManager.getIstance().addPointLightToScene();
			GameManager.getIstance().setClient(client);
			MultiPlayer.this.render = new GameRender(terrainQuad);
			MultiPlayer.this.viewPort.addProcessor(loadTerrain.makeFilter(true, true, true));
			MultiPlayer.this.setupAmbientSound();
			MultiPlayer.this.created = true;
			return null;
		    }
		});
	    };
	}.start();
    }

    /** this method disconnect player */
    public void exit() {
	this.client.endConnection();
	this.stopAmbientSound();
    }

    /** this method set sound */
    private void setupAmbientSound() {
	this.ambientSound = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.8f,
		false);
	GameManager.getIstance().stopMenuSound();
	this.playAmbientSound();
    }

    private void playAmbientSound() {
	this.ambientSound.getAudioNode().setVolume(0.0f);
	this.ambientSound.playSound();
	for (float i = 0.0f; i < 0.8f; i += 0.1f) {
	    this.ambientSound.getAudioNode().setVolume(this.ambientSound.getAudioNode().getVolume() + 0.1f);
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		this.ambientSound.stopSound();
	    }
	}
    }

    private void stopAmbientSound() {
	for (float i = this.ambientSound.getAudioNode().getVolume(); i > 0.1f; i -= 0.1f) {
	    this.ambientSound.getAudioNode().setVolume(this.ambientSound.getAudioNode().getVolume() - 0.1f);
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e) {
		this.ambientSound.stopSound();
	    }
	}

    }

    /** send message when player press send button */
    public void sendMessage() {

	final TextField text = GameManager.getIstance().getNifty().getCurrentScreen()
		.findNiftyControl("#chat-text-input", TextField.class);
	GameManager.getIstance().getClient().sendMessage(text.getDisplayedText());
	text.setText("");

    }

    /** this method load panel 2d */
    public void loadNifty() {
	GameManager.getIstance().getNifty().fromXml("Interface/Xml/multiPlayer.xml", "lifeBarScreen", this);
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

    /** this method create a popup to exit */
    public void openCloseSureExitButton() {
	Element element = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName("sureExitControl");
	element.setVisible(!element.isVisible());
    }

    /** this method resume game */
    public void resumeGame() {
	GameManager.getIstance().getNifty().gotoScreen("lifeBarScreen");
	GameManager.getIstance().resumeGame();
    }

    /** this method reset every things */
    public void reset() {
	GameManager.getIstance().setPaused(false);
	GameManager.getIstance().getApplication().getInputManager().reset();
	GameManager.getIstance().getApplication().getInputManager().setCursorVisible(true);
	GameManager.getIstance().getNifty().exit();
	GameManager.getIstance().getApplication().getViewPort().clearProcessors();
	GameManager.getIstance().getNifty().fromXml("Interface/Xml/screenMenu.xml", "start", this);
	GameManager.getIstance().getNodeThief().stopBonfireSound();
	GameManager.getIstance().getNodeThief().stopChapelSound();
	GameManager.getIstance().getNodeThief().getCamera().setEnabled(false);
	GameManager.getIstance().quitGame();
    }

    /** this method close game */
    public void quitGame() {
	this.exit();
	this.reset();
    }

    /** this method get start */
    public boolean isStart() {
	return this.start;
    }

    /** this method set start */
    public void setStart(boolean start) {
	this.start = start;
    }

    /** this method get created */
    public boolean isCreated() {
	return this.created;
    }

    /** this method set created */
    public void setCreated(boolean created) {
	this.created = created;
    }

    /** jmonkey's methods */
    @Override
    public void bind(Nifty arg0, Screen arg1) {
	this.borderLifeBarThief = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		.findElementByName("borderLifeBarThief");
	GameManager.getIstance().getNodeThief().setLifeBar(progressLifeBarThief, borderLifeBarThief, nameModel);
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
