package singlePlayer;

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
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.builder.ImageBuilder;
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

    private final String ATTACK1 = "Attack1";
    private final String ATTACK2 = "Attack2";
    private final String RUN = "Run";
    private final String ROTATECLOCKWISE = "rotateClockwise";
    private final String ROTATECOUNTERCLOCKWISE = "rotateCounterClockwise";
    private final String BONFIRE = "BonFire";
    private final String PAUSE = "Pause";
    private final ViewPort viewPort;
    private final Node rootNode;
    private Node nodeScene;
    private CollisionShape collisionShape;
    private RigidBodyControl rigidBodyControl;
    private final LoadTerrain loadTerrain;
    private GameRender render;
    private Sound ambient;

    private Element progressLifeBarThief;
    private Element borderLifeBarThief;

    public SinglePlayer(ViewPort viewPort, Node rootNode, Camera cam, String level, boolean shadows, boolean fog,
	    boolean water) {
	this.viewPort = viewPort;
	this.rootNode = rootNode;
	cam.setFrustumFar(200);
	cam.onFrameChange();
	this.loadTerrain = new LoadTerrain();
	this.nodeScene = new Node("Scene");
	this.loadLevel(level, shadows, fog, water);
	GameManager.getIstance().getNodeThief().setSinglePlayer(true);
	GameManager.getIstance().getNodeThief().setCam(cam);
	this.setKey();
	this.loadNifty();
	GameManager.getIstance().makeSecondLayer();
	GameManager.getIstance().printSecondLayer();
	this.setupAmbientSound();

    }

    public void loadLevel(String level, boolean shadows, boolean fog, boolean water) {
	TerrainQuad terrainQuad = loadTerrain.loadTerrain(level + ".j3o", false);
	this.nodeScene.attachChild(terrainQuad);
	this.nodeScene.addLight(loadTerrain.makeAmbientLight());
	this.collisionShape = CollisionShapeFactory.createMeshShape((Node) nodeScene);
	this.rigidBodyControl = new RigidBodyControl(collisionShape, 0);
	this.nodeScene.addControl(rigidBodyControl);
	this.rootNode.attachChild(nodeScene);
	GameManager.getIstance().setTerrain(nodeScene);
	GameManager.getIstance().getBullet().getPhysicsSpace().add(rigidBodyControl);
	GameManager.getIstance().addPhysics();
	GameManager.getIstance().addPointLightToScene();
	this.render = new GameRender(terrainQuad);
	this.viewPort.addProcessor(loadTerrain.makeFilter(shadows, fog, water));
    }

    public void simpleUpdate(Float tpf) {
	this.render.rayRendering();
	if (!GameManager.getIstance().getNodeThief().isRun())
	    GameManager.getIstance().getNodeThief().stop();
	GameManager.getIstance().startEnemiesIntelligence();

    }

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
	GameManager.getIstance().getApplication().getInputManager().addMapping("debug",
		new KeyTrigger(KeyInput.KEY_TAB));
	GameManager.getIstance().getApplication().getInputManager().addMapping(BONFIRE, new KeyTrigger(KeyInput.KEY_F));
	GameManager.getIstance().getApplication().getInputManager().addMapping("mouse",
		new KeyTrigger(KeyInput.KEY_LCONTROL));
	GameManager.getIstance().getApplication().getInputManager().addMapping(PAUSE, new KeyTrigger(KeyInput.KEY_P));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, RUN, ATTACK1, ATTACK2, BONFIRE, "toggleRotate",
		PAUSE);
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, RUN, ROTATECLOCKWISE, ROTATECOUNTERCLOCKWISE);
    }

    private void loadNifty() {

	GameManager.getIstance().getNifty().fromXml("Interface/singlePlayer.xml", "lifeBarScreen", this);
	this.borderLifeBarThief = GameManager.getIstance().getNifty().getScreen("lifeBarScreen")
		.findElementByName("borderLifeBarThief");
	GameManager.getIstance().getNodeThief().setLifeBar(progressLifeBarThief, borderLifeBarThief, "Yasuo");

    }

    private void setupAmbientSound() {
	this.ambient = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.8f, false);
	this.ambient.playSound();
    }

    public void startGrow(String nameButton) {

	NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/" + nameButton + "OnHover.png", false);
	Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    public void endGrow(String nameButton) {

	NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null,
		"Interface/" + nameButton + ".png", false);
	Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    public void openCloseSureExitButton() {
	Element element = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName("sureExitControl");
	element.setVisible(!element.isVisible());

    }

    public void resumeGame() {
	GameManager.getIstance().getNifty().gotoScreen("lifeBarScreen");
	GameManager.getIstance().resumeGame();

    }

    public void quitGame() {
	openCloseSureExitButton();
	GameManager.getIstance().setPaused(false);
	GameManager.getIstance().getApplication().getInputManager().clearMappings();
	GameManager.getIstance().getNifty().exit();
	this.rootNode.detachAllChildren();
	this.viewPort.clearProcessors();
	GameManager.getIstance().getNifty().fromXml("Interface/screenMenu.xml", "start", this);
	GameManager.getIstance().getApplication().getInputManager().setCursorVisible(true);

    }

    /**jmonkey's method*/
    @Override
    public void bind(Nifty d, Screen arg1) {
    }

    /**jmonkey's method*/
    @Override
    public void onEndScreen() {
    }
    
    /**jmonkey's method*/
    @Override
    public void onStartScreen() {

    }
}
