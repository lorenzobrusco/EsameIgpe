package singlePlayer;

import java.util.concurrent.Callable;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
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
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.LoadTerrain;

/**
 * 
 * this class handles singleplayer
 *
 */

public class SinglePlayer implements ScreenController {
    /** key event */
    private final String ATTACK1 = "Attack1";
    private final String ATTACK2 = "Attack2";
    private final String RUN = "Run";
    private final String ROTATECLOCKWISE = "rotateClockwise";
    private final String ROTATECOUNTERCLOCKWISE = "rotateCounterClockwise";
    private final String BONFIRE = "BonFire";
    private final String PAUSE = "Pause";
    /***/
    /** jmonkey's object */
    private final ViewPort viewPort;
    private final Node rootNode;
    private Node nodeScene;
    private CollisionShape collisionShape;
    private RigidBodyControl rigidBodyControl;
    private final LoadTerrain loadTerrain;
    /***/
    private GameRender render;
    private Sound ambientSound;
    private Element progressLifeBarThief;
    private Element borderLifeBarThief;

    /** constructor */
    public SinglePlayer(InputManager inputManager, ViewPort viewPort, Node rootNode, Camera cam, String level,
	    boolean shadows, boolean fog, boolean water) {
	this.viewPort = viewPort;
	this.rootNode = rootNode;
	cam.setFrustumFar(200);
	cam.onFrameChange();
	this.loadTerrain = new LoadTerrain();
	new Thread() {
	    public void run() {
		GameManager.getIstance().getApplication().enqueue(new Callable<Void>() {
		    public Void call() {
			SinglePlayer.this.loadLevel(level, shadows, fog, water);
			GameManager.getIstance().getNodeThief().setSinglePlayer(true);
			GameManager.getIstance().getNodeThief().setCam(cam, inputManager);
			GameManager.getIstance().getNifty().fromXml("Interface/Xml/singlePlayer.xml", "lifeBarScreen",
				SinglePlayer.this);
			SinglePlayer.this.setKey();
			SinglePlayer.this.setupAmbientSoundSound();

			return null;
		    }
		});
	    };
	}.start();
    }

    /** this method handles to load models */
    public void loadLevel(String level, boolean shadows, boolean fog, boolean water) {
	TerrainQuad terrainQuad = loadTerrain.loadTerrain(level + ".j3o", false);
	this.nodeScene = new Node("Scene");
	this.nodeScene.attachChild(terrainQuad);
	this.rootNode.addLight(loadTerrain.makeDirectionLight());
	this.collisionShape = CollisionShapeFactory.createMeshShape((Node) nodeScene);
	this.rigidBodyControl = new RigidBodyControl(collisionShape, 0);
	this.nodeScene.addControl(rigidBodyControl);
	this.rootNode.attachChild(nodeScene);
	GameManager.getIstance().setTerrain(nodeScene);
	GameManager.getIstance().getBullet().getPhysicsSpace().add(rigidBodyControl);
	GameManager.getIstance().addPhysics();
	GameManager.getIstance().addPointLightToScene();
	GameManager.getIstance().setSinglePlayer(this);
	this.render = new GameRender(terrainQuad);
	this.viewPort.addProcessor(loadTerrain.makeFilter(shadows, fog, water));
    }

    /** this method update root node */
    public void simpleUpdate(Float tpf) {
	if (this.nodeScene != null) {
	    this.render.rayRendering();
	    if (!GameManager.getIstance().getNodeThief().isRun())
		GameManager.getIstance().getNodeThief().stop();
	    GameManager.getIstance().getNodeThief().saySomething();
	    GameManager.getIstance().startEnemiesIntelligence();
	}
    }

    /** this method set key triggers */
    private void setKey() {
	GameManager.getIstance().getApplication().getInputManager().addMapping(RUN, new KeyTrigger(KeyInput.KEY_W));
	GameManager.getIstance().getApplication().getInputManager().addMapping(ATTACK1,
		new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	GameManager.getIstance().getApplication().getInputManager().addMapping(ATTACK2,
		new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
	GameManager.getIstance().getApplication().getInputManager().addMapping(ROTATECLOCKWISE,
		new KeyTrigger(KeyInput.KEY_A));
	GameManager.getIstance().getApplication().getInputManager().addMapping(ROTATECOUNTERCLOCKWISE,
		new KeyTrigger(KeyInput.KEY_D));
	GameManager.getIstance().getApplication().getInputManager().addMapping("win",
		new KeyTrigger(KeyInput.KEY_SPACE));
	GameManager.getIstance().getApplication().getInputManager().addMapping(BONFIRE, new KeyTrigger(KeyInput.KEY_F));
	GameManager.getIstance().getApplication().getInputManager().addMapping(PAUSE,
		new KeyTrigger(KeyInput.KEY_ESCAPE));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, RUN, ATTACK1, ATTACK2, BONFIRE, "toggleRotate",
		PAUSE, "win");
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, RUN, ROTATECLOCKWISE, ROTATECOUNTERCLOCKWISE);
    }

    /** this meethod add ambient sounds */
    private void setupAmbientSoundSound() {
	this.ambientSound = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.0f,
		false);
	GameManager.getIstance().stopMenuSound();
	this.playAmbientSoundSound();
    }

    /** this method is called when cursor move up a button */
    public void startGrow(String nameButton) {
	NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/Image/Button/" + nameButton + "OnHover.png", false);
	Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    /** this method is called when cursor outside a button */
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

    /** this method is called when thief found portal */
    public void win() {
	GameManager.getIstance().getNifty().getCurrentScreen().findElementByName("layerWinner").setVisible(true);
    }

    /** this method resume game */
    public void resumeGame() {
	GameManager.getIstance().getNifty().gotoScreen("lifeBarScreen");
	GameManager.getIstance().resumeGame();
    }

    /** this method close game */
    public void quitGame() {
	GameManager.getIstance().setPaused(false);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.RUN);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ATTACK1);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ATTACK2);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ROTATECLOCKWISE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ROTATECOUNTERCLOCKWISE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping("win");
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.BONFIRE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.PAUSE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping("toggleRotate");
	GameManager.getIstance().getApplication().getInputManager().reset();
	GameManager.getIstance().getApplication().getInputManager().setCursorVisible(true);
	GameManager.getIstance().getNifty().exit();
	GameManager.getIstance().getApplication().getViewPort().clearProcessors();
	GameManager.getIstance().getNifty().fromXml("Interface/Xml/screenMenu.xml", "start", this);
	GameManager.getIstance().getNodeThief().stopBonfireSound();
	GameManager.getIstance().getNodeThief().stopChapelSound();
	GameManager.getIstance().getNodeThief().getCamera().setEnabled(false);
	GameManager.getIstance().quitGame();
	this.stopAmbientSound();
    }

    /** stop playing ambient sound */
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

    /** start playing ambient sound */
    private void playAmbientSoundSound() {
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

    /** this method show message when thief is near something */
    public void showMessageForPlayer(String id) {
	if (GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(id) != null)
	    GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(id).setVisible(true);
    }

    /** this method hide message when thief is near something */
    public void hideMessageForPlayer(String id) {
	if (GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(id) != null)
	    GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(id).setVisible(false);
    }

    /** jmonkey's method */
    @Override
    public void bind(Nifty d, Screen arg1) {

	this.borderLifeBarThief = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		.findElementByName("borderLifeBarThief");
	GameManager.getIstance().getNodeThief().setLifeBar(progressLifeBarThief, borderLifeBarThief, "Yasuo");
    }

    /** jmonkey's method */
    @Override
    public void onEndScreen() {
    }

    /** jmonkey's method */
    @Override
    public void onStartScreen() {
    }
}