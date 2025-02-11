package game;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.controls.ActionListener;
import com.jme3.math.Quaternion;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import control.GameManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.EditorTerrain;
import multiPlayer.MultiPlayer;
import singlePlayer.SinglePlayer;

public class StartGame extends SimpleApplication implements ActionListener, ScreenController {

    /** Path for panel 2d */
    private static final String pathSinglePlayer = "singlePlayer.SinglePlayer";
    /** Path for panel 2d */
    private static final String pathEditor = "editor.EditorTerrain";
    /** Path for panel 2d */
    private static final String pathMultiPlayer = "multiPlayer.MultiPlayer";
    /** editor */
    private EditorTerrain editorTerrain;
    /** single player */
    private SinglePlayer player;
    /** multi player */
    private MultiPlayer multiPlayer;
    /** check if player running single player */
    private boolean singleplayer;
    /** check if player running multiplayer */
    private boolean multiplayer;
    /** check if player running editor */
    private boolean editor;
    /** jmonkey's object to add physic */
    private BulletAppState bulletAppState;
    /** nifty's manager */
    private NiftyJmeDisplay niftyDisplay;
    /** panel 2d */
    private Nifty nifty;
    /** index */
    private int indexCharacter;
    /** index */
    private int indexHelp;
    /** index for landscape'array */
    private int indexLandscape;
    /** characters list */
    private Collection<String> characters;
    /** help's images */
    private Collection<String> help;
    /** Landscape's Image */
    private Collection<String> landscape;
    /** player's address */
    private String ipAddress;
    /** player's name */
    private String namePlayer;
    /** id popup */
    private String idPopUp;

    /** builder */
    public StartGame() {
	this.setup();
    }

    /** set parameters */
    public void simpleInitApp() {
	this.bulletAppState = new BulletAppState();
	this.stateManager.attach(bulletAppState);
	this.assetManager.registerLocator("assets/", FileLocator.class);
	GameManager.getIstance().setParams(this);
	GameManager.getIstance().setBullet(bulletAppState);
	GameManager.getIstance().setAudioRendere(this.audioRenderer);
	this.flyCam.setMoveSpeed(100f);
	this.flyCam.setEnabled(false);
	this.mouseInput.setCursorVisible(true);
	this.niftyDisplay = new NiftyJmeDisplay(GameManager.getIstance().getApplication().getAssetManager(),
		GameManager.getIstance().getApplication().getInputManager(),
		GameManager.getIstance().getApplication().getAudioRenderer(),
		GameManager.getIstance().getApplication().getGuiViewPort());
	this.nifty = niftyDisplay.getNifty();
	this.nifty.fromXml("Interface/Xml/screenMenu.xml", "start", this);
	GameManager.getIstance().getApplication().getGuiViewPort().addProcessor(niftyDisplay);
	GameManager.getIstance().setNifty(nifty);
	GameManager.getIstance().getApplication().getInputManager().clearMappings();
	GameManager.getIstance().setupAudio();
	GameManager.getIstance().playMenuSound();
	this.initKeys();
    }

    /** choosed according to the game type */
    @Override
    public void simpleUpdate(float tpf) {
	if (this.singleplayer)
	    this.player.simpleUpdate(tpf);
	else if (this.editor)
	    this.editorTerrain.simpleUpdate(tpf);
	else if (this.multiplayer)
	    this.multiPlayer.simpleUpdate(tpf);
    }

    /** check which button is pressed */
    public ActionListener actionListener = new ActionListener() {
	public void onAction(String name, boolean pressed, float value) {
	    if (name.equals("mouse") && !singleplayer) {
		StartGame.this.mouse();
	    }
	}
    };

    /** start single player */
    public void singlePlayer() {
	// this.rootNode.detachAllChildren();
	this.inputManager.setCursorVisible(false);
	StartGame.this.nifty.getCurrentScreen().findElementByName("loadingBackground").setVisible(true);
	GameManager.getIstance().setEditor(false);
	this.cam.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
	GameManager.getIstance().setModelGame(pathSinglePlayer);
	String[] level = GameManager.getIstance().getNifty().getCurrentScreen()
		.findNiftyControl("landscapeListBox", ListBox.class).getFocusItem().toString().split("\\.");
	this.player = new SinglePlayer(inputManager, viewPort, rootNode, cam, level[0], true, true, true);
	this.initKeys();
	this.singleplayer = true;
	this.editor = false;
	this.multiplayer = false;

    }

    /** start multiplayer */
    public void multiPlayer() {
	try {
	    this.namePlayer = GameManager.getIstance().getNifty().getCurrentScreen()
		    .findNiftyControl("textfieldName", TextField.class).getDisplayedText();
	    this.ipAddress = GameManager.getIstance().getNifty().getCurrentScreen()
		    .findNiftyControl("textfieldIP", TextField.class).getDisplayedText();
	    GameManager.getIstance().setEditor(false);
	    this.cam.setRotation(new Quaternion(0.0f, 1.0f, 0.0f, 0.0f));
	    GameManager.getIstance().setModelGame(pathMultiPlayer);
	    this.multiPlayer = new MultiPlayer(inputManager, viewPort, rootNode, cam, ipAddress, namePlayer,
		    ((ArrayList<String>) characters).get(indexCharacter),
		    Integer.parseInt(GameManager.getIstance().getNifty().getCurrentScreen()
			    .findNiftyControl("myTextFieldPortMultiPlayer", TextField.class).getDisplayedText()));
	    this.inputManager.setCursorVisible(false);
	    StartGame.this.nifty.getCurrentScreen().findElementByName("loadingBackgroundMulti").setVisible(true);
	    this.initKeys();
	    this.multiplayer = true;
	    this.singleplayer = false;
	    this.editor = false;
	} catch (UnknownHostException ex) {
	    final Element popup = GameManager.getIstance().getNifty().createPopup("exceptionServer");
	    this.idPopUp = popup.getId();
	    GameManager.getIstance().getNifty().showPopup(GameManager.getIstance().getNifty().getCurrentScreen(),
		    popup.getId(), null);
	} catch (NumberFormatException n) {
	    final Element popup = GameManager.getIstance().getNifty().createPopup("exceptionServer");
	    this.idPopUp = popup.getId();
	    GameManager.getIstance().getNifty().showPopup(GameManager.getIstance().getNifty().getCurrentScreen(),
		    popup.getId(), null);
	} catch (IOException e) {
	    final Element popup = GameManager.getIstance().getNifty().createPopup("exceptionServer");
	    this.idPopUp = popup.getId();
	    GameManager.getIstance().getNifty().showPopup(GameManager.getIstance().getNifty().getCurrentScreen(),
		    popup.getId(), null);
	}
    }

    public void editor() {
	this.rootNode.detachAllChildren();
	GameManager.getIstance().setEditor(true);
	GameManager.getIstance().setModelGame(pathEditor);
	this.editorTerrain = new EditorTerrain(rootNode, cam, guiFont, guiNode, viewPort, settings, "mountain");
	this.mouseInput.setCursorVisible(false);
	this.initKeys();
	this.flyCam.setEnabled(true);
	this.editor = true;
	this.singleplayer = false;
	this.multiplayer = false;
    }

    /** this method load next panel 2d */
    public void loadScreen(String nextScreen) {
	nifty.gotoScreen(nextScreen);

    }

    /** this method load characters list */
    public void loadCharacter() {
	try {
	    Files.walk(Paths.get("assets/Interface/MultiPlayer/PlayerImage")).forEach(filePath -> {
		if (Files.isRegularFile(filePath)) {
		    String[] split = filePath.getFileName().toString().split("\\.");
		    characters.add(split[0]);
		}
	    });
	    NiftyImage image = nifty.getRenderEngine().createImage(null,
		    "Interface/MultiPlayer/PlayerImage/" + ((ArrayList<String>) characters).get(0) + ".png", false);
	    Element niftyElement = nifty.getScreen("serverScreen").findElementByName("serverState");
	    niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	} catch (IOException e) {
	    this.showPopUp("exceptionStartGame");
	}
    }

    public void loadLandScape() {
	try {
	    Files.walk(Paths.get("assets/Interface/Image/Landscape")).forEach(filePath -> {
		if (Files.isRegularFile(filePath)) {
		    String[] split = filePath.getFileName().toString().split("\\.");
		    this.landscape.add(split[0]);
		}
	    });
	    NiftyImage image = nifty.getRenderEngine().createImage(null,
		    "Interface/Image/Landscape/" + ((ArrayList<String>) landscape).get(0) + ".png", false);
	    Element niftyElement = nifty.getScreen("serverScreen").findElementByName("imageLandScape");
	    niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	} catch (IOException e) {
	    this.showPopUp("exceptionStartGame");
	}

    }

    public void loadHelpScreen() {
	loadHelp();
	nifty.gotoScreen("helpScreen");
    }

    public void loadHelp() {
	try {
	    Files.walk(Paths.get("assets/Interface/Image/Graphics/Help")).forEach(filePath -> {
		if (Files.isRegularFile(filePath)) {
		    String[] split = filePath.getFileName().toString().split("\\.");
		    help.add(split[0]);
		}
	    });
	    NiftyImage image = nifty.getRenderEngine().createImage(null,
		    "Interface/Image/Graphics/Help/" + ((ArrayList<String>) help).get(0) + ".png", false);
	    Element niftyElement = nifty.getScreen("helpScreen").findElementByName("backgroundHelpImage");
	    niftyElement.getRenderer(ImageRenderer.class).setImage(image);

	} catch (IOException e) {
	    this.showPopUp("exceptionStartGame");
	}

    }

    /** this method change panel 2d and open server's panel */
    public void openServerScreen() {

	if (GameManager.getIstance().getServer() == null || !GameManager.getIstance().getServer().isStart()) {
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("arrowLeft")
		    .setVisible(true);
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("arrowRight")
		    .setVisible(true);
	    final NiftyImage image = nifty.getRenderEngine().createImage(null,
		    "Interface/Image/Graphics/serverIsClose.png", false);
	    final Element niftyElement = nifty.getScreen("serverScreen").findElementByName("serverState");
	    niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("closeServerButton")
		    .setVisible(false);
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("startServerButton")
		    .setVisible(true);
	} else if (GameManager.getIstance().getServer().isStart()) {
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("arrowLeft")
		    .setVisible(false);
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("arrowRight")
		    .setVisible(false);
	    final NiftyImage image = nifty.getRenderEngine().createImage(null,
		    "Interface/Image/Graphics/serverIsOpen.png", false);
	    final Element niftyElement = nifty.getScreen("serverScreen").findElementByName("serverState");
	    niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("closeServerButton")
		    .setVisible(true);
	    GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("startServerButton")
		    .setVisible(false);
	}
	if (landscape.isEmpty())
	    loadLandScape();

	this.loadScreen("serverScreen");

    }

    /** this method is invoked when user press on textfield */
    public void resetParamsTextfield(String nameTextField) {

	GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl(nameTextField, TextField.class)
		.setText("");
    }

    /** next character */
    public void nextCharacter() {
	if (indexCharacter == characters.size() - 1)
	    indexCharacter = 0;
	else
	    indexCharacter++;
	NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/MultiPlayer/PlayerImage/" + ((ArrayList<String>) characters).get(indexCharacter) + ".png",
		false);
	Element niftyElement = nifty.getCurrentScreen().findElementByName("imagePlayer");
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);

    }

    /** previous character */
    public void redoCharacter() {
	if (this.indexCharacter == 0)
	    this.indexCharacter = characters.size() - 1;
	else
	    this.indexCharacter--;
	final NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/MultiPlayer/PlayerImage/" + ((ArrayList<String>) characters).get(indexCharacter) + ".png",
		false);
	final Element niftyElement = nifty.getCurrentScreen().findElementByName("imagePlayer");
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);

    }

    /** next landscape */
    public void nextLandscape() {

	if (indexLandscape == landscape.size() - 1)
	    indexLandscape = 0;
	else
	    indexLandscape++;
	NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/Image/Landscape/" + ((ArrayList<String>) landscape).get(indexLandscape) + ".png", false);
	Element niftyElement = nifty.getCurrentScreen().findElementByName("imageLandScape");
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);

    }

    /** Previous landscape */
    public void redoLandscape() {
	if (this.indexLandscape == 0)
	    this.indexLandscape = landscape.size() - 1;
	else
	    this.indexLandscape--;
	final NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/Image/Landscape/" + ((ArrayList<String>) landscape).get(indexLandscape) + ".png", false);
	final Element niftyElement = nifty.getCurrentScreen().findElementByName("imageLandScape");
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);

    }

    /** next page */
    public void nextHelpImage() {
	if (indexHelp == help.size() - 1)
	    indexHelp = 0;
	else
	    indexHelp++;
	NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/Image/Graphics/Help/" + ((ArrayList<String>) help).get(indexHelp) + ".png", false);
	Element niftyElement = nifty.getCurrentScreen().findElementByName("backgroundHelpImage");
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);

    }

    /** previous page */
    public void redoHelpImage() {
	if (this.indexHelp == 0)
	    this.indexHelp = help.size() - 1;
	else
	    this.indexHelp--;
	final NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/Image/Graphics/Help/" + ((ArrayList<String>) help).get(indexHelp) + ".png", false);
	final Element niftyElement = nifty.getCurrentScreen().findElementByName("backgroundHelpImage");
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);

    }

    /** this method set keys */
    private void initKeys() {
	this.inputManager.addListener(actionListener, "mouse");

    }

    /**
     * this method, close connection if user playing in multiplayer and then
     * close game
     */
    public void closeGame() {
	GameManager.getIstance().stopMenuSound();
	GameManager.getIstance().getAudioRender().cleanup();
	this.audioRenderer.cleanup();
	System.exit(0);
    }

    /** this method setup variables */
    public void setup() {
	this.characters = new ArrayList<String>();
	this.help = new ArrayList<String>();
	this.landscape = new ArrayList<String>();
	this.singleplayer = false;
	this.multiplayer = false;
	this.editor = false;
	this.indexCharacter = 0;
	this.indexHelp = 0;
	this.indexLandscape = 0;
	this.ipAddress = "";
	this.namePlayer = "";
	this.idPopUp = "";
    }

    /** this method disable flycam and set visible cursor */
    private void mouse() {
	this.inputManager.setCursorVisible(!this.inputManager.isCursorVisible());
	this.flyCam.setEnabled(!this.flyCam.isEnabled());
    }

    /** this method is called when user choose multiplayer */
    public void openMultiPlayerScreen() {

	if (this.characters.isEmpty()) {
	    this.loadCharacter();
	}
	GameManager.getIstance().getNifty().getScreen("multiPlayerScreen").findElementByName("myTextFieldIP")
		.setFocusable(true);
	this.loadScreen("multiPlayerScreen");

    }

    /** this method lists landscapes */
    public void openSinglePlayerScreen() {

	@SuppressWarnings("unchecked")
	ListBox<String> listBox = GameManager.getIstance().getNifty().getScreen("singlePlayerScreen")
		.findNiftyControl("landscapeListBox", ListBox.class);
	listBox.clear();
	try {
	    Files.walk(Paths.get("assets/Scenes")).forEach(filePath -> {
		if (Files.isRegularFile(filePath)) {
		    if (!filePath.getFileName().toString().equals("mountain.j3o"))
			listBox.addItem(filePath.getFileName().toString());
		}
	    });
	} catch (IOException e) {
	    this.showPopUp("exceptionStartGame");
	}
	listBox.selectItemByIndex(0);
	loadScreen("singlePlayerScreen");
    }

    /** this method is called when cursor move up a button */
    public void startGrow(String nameButton) {
	NiftyImage image = nifty.getRenderEngine().createImage(null,
		"Interface/Image/Button/" + nameButton + "OnHover.png", false);
	Element niftyElement = nifty.getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    /** this method is called when cursor outside a button */
    public void endGrow(String nameButton) {
	NiftyImage image = nifty.getRenderEngine().createImage(null, "Interface/Image/Button/" + nameButton + ".png",
		false);
	Element niftyElement = nifty.getCurrentScreen().findElementByName(nameButton);
	niftyElement.getRenderer(ImageRenderer.class).setImage(image);
    }

    /** this methods start server */
    public void startServer() {
	try {

	    GameManager.getIstance().startServer(((ArrayList<String>) landscape).get(indexLandscape),
		    Integer.parseInt(this.nifty.getCurrentScreen()
			    .findNiftyControl("myTextFieldPortServer", TextField.class).getDisplayedText()));
	    openServerScreen();

	} catch (UnknownHostException e) {
	    this.showPopUp("blindServerPort");
	} catch (IOException e) {
	    this.showPopUp("blindServerPort");
	} catch (NumberFormatException e) {
	    this.showPopUp("blindServerPort");
	}

    }

    /** this method show popup */
    public void showPopUp(final String id) {
	final Element popup = GameManager.getIstance().getNifty().createPopup("blindServerPort");
	this.idPopUp = popup.getId();
	GameManager.getIstance().getNifty().showPopup(GameManager.getIstance().getNifty().getCurrentScreen(),
		popup.getId(), null);
    }

    /** this method open popup when client has exception */
    public void openPopUp() {
	final Element popup = GameManager.getIstance().getNifty().createPopup("exceptionServer");
	this.idPopUp = popup.getId();
	GameManager.getIstance().getNifty().showPopup(GameManager.getIstance().getNifty().getCurrentScreen(),
		popup.getId(), null);
    }

    /** this method close popup */
    public void closePopUp() {
	GameManager.getIstance().getNifty().closePopup(this.idPopUp);
    }

    /** this method close server */
    public void closeServer() {
	GameManager.getIstance().getServer().stopServer();
	openServerScreen();
    }

    /**this method get rootNode*/
    public Node getRoot(){
	return this.rootNode;
    }
    
    /** jmonkey's method */
    @Override
    public void onAction(String arg0, boolean arg1, float arg2) {
    }

    /** jmonkey's method */
    @Override
    public void bind(Nifty nifty, Screen screen) {
    }

    /** jmonkey's method */
    @Override
    public void onEndScreen() {
    }

    /** jmonkey's method */
    @Override
    public void onStartScreen() {

    }

    public static void main(String[] args) {
	StartGame app = new StartGame();
	app.setPauseOnLostFocus(false);
	AppSettings gameSettings = new AppSettings(false);
	// gameSettings.setResolution(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,
	// java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
	//
	// gameSettings.setFullscreen(true);
	// gameSettings.setVSync(true);
	// gameSettings.setTitle("Thief");
	// gameSettings.setUseInput(true);
	// gameSettings.setFrameRate(500);
	// gameSettings.setSamples(0);
	gameSettings.setRenderer("LWJGL-OpenGL2");
	//
	app.setSettings(gameSettings);
	// app.setShowSettings(false);
	app.setDisplayFps(false);
	app.setDisplayStatView(false);
	app.start();

    }

}
