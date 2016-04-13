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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.LoadTerrain;

public class SinglePlayer implements ScreenController {

	private final String attack1 = "Attack1";
	private final String attack2 = "Attack2";
	private final String run = "Run";
	private final String rotateClockwise = "rotateClockwise";
	private final String rotateCounterClockwise = "rotateCounterClockwise";
	private final String bonfire = "BonFire";
	private final ViewPort viewPort;
	private final Node rootNode;
	private Node nodeScene;
	private CollisionShape collisionShape;
	private RigidBodyControl rigidBodyControl;
	private final LoadTerrain loadTerrain;
	private GameRender render;
	private Sound ambient;

	
	private Element progressBarElement;
	
	
	public SinglePlayer(ViewPort viewPort, Node rootNode, Camera cam, String level, boolean shadows, boolean fog, boolean water) {
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
		//if (GameManager.getIstance().getNodeThief().isControlRender()) {
			this.render.rayRendering();
		//}
		if (!GameManager.getIstance().getNodeThief().isRun())
			GameManager.getIstance().getNodeThief().stop();
		GameManager.getIstance().startEnemiesIntelligence();
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
		GameManager.getIstance().getApplication().getInputManager().addMapping(bonfire, new KeyTrigger(KeyInput.KEY_F));
		GameManager.getIstance().getApplication().getInputManager().addMapping("mouse",
				new KeyTrigger(KeyInput.KEY_LCONTROL));
		GameManager.getIstance().getApplication().getInputManager().addMapping("damage",
				new KeyTrigger(KeyInput.KEY_P));
		GameManager.getIstance().getApplication().getInputManager().addListener(
				GameManager.getIstance().getNodeThief().actionListener, run, attack1, attack2, bonfire, "toggleRotate","damage");
		GameManager.getIstance().getApplication().getInputManager().addListener(
				GameManager.getIstance().getNodeThief().analogListener, run, rotateClockwise, rotateCounterClockwise);
	}
	
	private void loadNifty()
	{
		
		GameManager.getIstance().getNifty().fromXml("Interface/singlePlayer.xml", "lifeBarScreen", this);
		this.progressBarElement = GameManager.getIstance().getNifty().getScreen("lifeBarScreen").findElementByName("progressbarThief");
		
	   
	    GameManager.getIstance().getNodeThief().setLifeBar(progressBarElement);
	
	}

	private void setupAmbientSound() {
		this.ambient = new Sound(GameManager.getIstance().getTerrain(), "Gameplay", false, false, true, 0.8f, false);
		this.ambient.playSound();
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {
		
		
	}

	@Override
	public void onEndScreen() {
		
		
	}

	@Override
	public void onStartScreen() {
		
		
	}
}
