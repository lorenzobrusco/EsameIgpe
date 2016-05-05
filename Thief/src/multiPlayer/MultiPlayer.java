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
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.Chat;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.LoadTerrain;
import singlePlayer.Sound;

public class MultiPlayer implements ScreenController {

    private final ViewPort viewPort;
    private final Node rootNode;
    private Node nodeScene;
    private CollisionShape collisionShape;
    private RigidBodyControl rigidBodyControl;
    private final LoadTerrain loadTerrain;
    private GameRender render;
    private Sound ambient;
    private Client client = null;
    
    private String nameModel;
	private Element progressLifeBarThief;
	private Element borderLifeBarThief;
	private final int MAX_CHARACTER_IN_LINE = 80;
	private String namePlayer;
	

    public MultiPlayer(ViewPort viewPort, Node rootNode, Camera cam, String address, String namePlayer,
	    String nameModel) {
    this.nameModel = nameModel;
    this.namePlayer = namePlayer;
	this.viewPort = viewPort;
	this.rootNode = rootNode;
	cam.setFrustumFar(200);
	cam.onFrameChange();
	this.nameModel = nameModel;
	this.loadTerrain = new LoadTerrain();
	this.nodeScene = new Node("Scene");
	this.loadLevel("mountain", address, namePlayer, nameModel, rootNode, cam);

	loadNifty();
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
	    this.client = new Client(namePlayer, nameModel, address, cam);
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

	@Override
	public void bind(Nifty arg0, Screen arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onEndScreen() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartScreen() {
		// TODO Auto-generated method stub
		
	}
	
	private void loadNifty()
	{
		
		GameManager.getIstance().getNifty().fromXml("Interface/multiPlayer.xml", "lifeBarScreen", this);		
		this.borderLifeBarThief = GameManager.getIstance().getNifty().getScreen("lifeBarScreen").findElementByName("borderLifeBarThief");	
		System.out.println(nameModel);
		GameManager.getIstance().getNodeThief().setLifeBar(progressLifeBarThief, borderLifeBarThief,nameModel);
	
	}
	
	public void sendMessage()
	{

		TextField text = GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("#chat-text-input", TextField.class);	
		GameManager.getIstance().getClient().sendMessage(text.getDisplayedText());
		text.setText("");
		
	}
	
	public void startGrow(String nameButton) {

		NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null, "Interface/" + nameButton + "OnHover.png", false);
		Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	}

	public void endGrow(String nameButton) {

		NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null, "Interface/" + nameButton + ".png", false);
		Element niftyElement =GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	}
	
}
