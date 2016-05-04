package main;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.system.AppSettings;
import control.GameManager;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.EditorTerrain;
import multiPlayer.MultiPlayer;
import singlePlayer.SinglePlayer;
import singlePlayer.Sound;

public class Main extends SimpleApplication implements ActionListener, ScreenController {

	private EditorTerrain editorTerrain;
	private SinglePlayer player;
	private BulletAppState bulletAppState;
	private MultiPlayer multiPlayer;
	private boolean singleplayer;
	private boolean multiplayer;
	private boolean editor;
	private boolean debug;
	private NiftyJmeDisplay niftyDisplay;
	private Nifty nifty;
	private Sound menuSound;
	
	private static final String pathSinglePlayer = "singlePlayer.SinglePlayer";
	private static final String pathEditor = "editor.EditorTerrain";
	private static final String pathMultiPlayer = "multiPlayer.MultiPlayer";
	private int indexCharacter;

	private ArrayList<String> characters;
	private String ipAddress;
	private String namePlayer;

	public Main() {

	}

	public static void main(String[] args) {
		AppSettings gameSettings = new AppSettings(false);
//		 gameSettings.setResolution(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,
//		 java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);

		gameSettings.setResolution(800, 600);

		gameSettings.setFullscreen(false);
		gameSettings.setVSync(true);
		gameSettings.setTitle("Thief");
		gameSettings.setUseInput(true);
		gameSettings.setFrameRate(500);
		gameSettings.setSamples(0);
		gameSettings.setRenderer("LWJGL-OpenGL2");
		Main app = new Main();
		app.setSettings(gameSettings);
		app.setShowSettings(false);

		// disable statistics
		app.setDisplayFps(false);
		app.setDisplayStatView(false);
		app.start();

	}

	public void simpleInitApp() {
		// ------------------------------Parametri-------------------------------//
		this.bulletAppState = new BulletAppState();
		this.stateManager.attach(bulletAppState);
		this.assetManager.registerLocator("assets/", FileLocator.class);
		this.debug = false;
		this.singleplayer = false;
		this.multiplayer = false;
		this.editor = false;
		this.indexCharacter = 0;
		this.ipAddress = "192.168.1.48";
		this.namePlayer = "Antonio";

		GameManager.getIstance().setParams(this);
		GameManager.getIstance().setBullet(bulletAppState);
		this.setupAudio();
		this.flyCam.setMoveSpeed(100f);
		this.flyCam.setEnabled(false);
		mouseInput.setCursorVisible(true);

		this.niftyDisplay = new NiftyJmeDisplay(GameManager.getIstance().getApplication().getAssetManager(),
				GameManager.getIstance().getApplication().getInputManager(),
				GameManager.getIstance().getApplication().getAudioRenderer(),
				GameManager.getIstance().getApplication().getGuiViewPort());
		this.nifty = niftyDisplay.getNifty();
		this.nifty.fromXml("Interface/screenMenu.xml", "start", this);
		GameManager.getIstance().getApplication().getGuiViewPort().addProcessor(niftyDisplay);
		GameManager.getIstance().setNifty(nifty);
	
		//this.menuSound.playSound();
	}

	@Override
	public void simpleUpdate(float tpf) {
		if (singleplayer)
			player.simpleUpdate(tpf);
		else if (editor)
			editorTerrain.simpleUpdate(tpf);
		else if (multiplayer)
			multiPlayer.simpleUpdate(tpf);
	}

	public void singlePlayer() {

		singleplayer = true;
		multiplayer = false;
		editor = false;
		this.cam.clearViewportChanged();
		GameManager.getIstance().setEditor(false);
		this.flyCam.setEnabled(false);
		GameManager.getIstance().setModelGame(pathSinglePlayer);
		this.player = new SinglePlayer(viewPort, rootNode, cam, "test2", true, true, true);
		this.initKeys();
		this.menuSound.stopSound();

	}

	public void multiPlayer() {

		multiplayer = true;
		singleplayer = false;
		editor = false;
		this.flyCam.setEnabled(false);
//		namePlayer = GameManager.getIstance().getNifty().getCurrentScreen()
//				.findNiftyControl("textfieldName", TextField.class).getDisplayedText();
//		ipAddress = GameManager.getIstance().getNifty().getCurrentScreen()
//				.findNiftyControl("textfieldIP", TextField.class).getDisplayedText();
		GameManager.getIstance().setEditor(false);
		GameManager.getIstance().setModelGame(pathMultiPlayer);
		System.out.println("ho scelto il personaggio: "+characters.get(indexCharacter));
		this.multiPlayer = new MultiPlayer(viewPort, rootNode, cam, ipAddress, namePlayer,
				characters.get(indexCharacter));
		// TODO inserire ip server
		
		this.initKeys();
		this.menuSound.stopSound();
	}

	public void editor() {

		editor = true;
		singleplayer = false;
		multiplayer = false;
		this.flyCam.setEnabled(true);
		GameManager.getIstance().setEditor(true);
		GameManager.getIstance().setModelGame(pathEditor);
		this.editorTerrain = new EditorTerrain(rootNode, cam, guiFont, guiNode, viewPort, settings, "mountain");
		mouseInput.setCursorVisible(false);
		this.initKeys();
		this.menuSound.stopSound();

	}

	private void initKeys() {
		GameManager.getIstance().getApplication().getInputManager().addMapping("debug",
				new KeyTrigger(KeyInput.KEY_TAB));
		GameManager.getIstance().getApplication().getInputManager().addMapping("exit",
				new KeyTrigger(KeyInput.KEY_ESCAPE));
		GameManager.getIstance().getApplication().getInputManager().addMapping("chatBox",
				new KeyTrigger(KeyInput.KEY_ESCAPE));
		inputManager.addListener(actionListener, "debug", "exit", "mouse" ,"chatBox");
	}

	private void mouse() {
		this.inputManager.setCursorVisible(!this.inputManager.isCursorVisible());
		this.flyCam.setEnabled(!this.flyCam.isEnabled());

	}

	public ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean pressed, float value) {
			if (name.equals("debug")) {
				debug = !debug;
				bulletAppState.setDebugEnabled(debug);
			} else if (name.equals("exit")) {
				Main.this.closeGame();
			}

			else if (name.equals("mouse") && !singleplayer) {
				Main.this.mouse();
			}
		
		}
	};

	public void setupAudio() {
		this.menuSound = new Sound(this.rootNode, "Menu", false, false, true, 1.0f, false);
	}

	

	public void loadScreen(String nextScreen) {
		nifty.gotoScreen(nextScreen);

	}

	public void loadCharacter() {

		characters = new ArrayList<String>();
		try {
			Files.walk(Paths.get("assets/Interface/MultiPlayer/PlayerImage")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					String[] split = filePath.getFileName().toString().split("\\.");
					characters.add(split[0]);

				}
			});
		} catch (IOException e) {
			e.printStackTrace();

		}

		NiftyImage image = nifty.getRenderEngine().createImage(null,
				"Interface/MultiPlayer/PlayerImage/" + characters.get(0) + ".png", false);
		Element niftyElement = nifty.getScreen("serverScreen").findElementByName("serverState");
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);

	}

	public void openServerScreen() {

		if (GameManager.getIstance().getServer() == null || !GameManager.getIstance().getServer().isStart()) {
			NiftyImage image = nifty.getRenderEngine().createImage(null, "Interface/serverIsClose.png", false);
			Element niftyElement = nifty.getScreen("serverScreen").findElementByName("serverState");
			niftyElement.getRenderer(ImageRenderer.class).setImage(image);

			GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("closeServerButton")
					.setVisible(false);
			GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("startServerButton")
					.setVisible(true);

		}

		else if (GameManager.getIstance().getServer().isStart()) {

			NiftyImage image = nifty.getRenderEngine().createImage(null, "Interface/serverIsOpen.png", false);
			Element niftyElement = nifty.getScreen("serverScreen").findElementByName("serverState");
			niftyElement.getRenderer(ImageRenderer.class).setImage(image);
			GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("closeServerButton")
					.setVisible(true);
			GameManager.getIstance().getNifty().getScreen("serverScreen").findElementByName("startServerButton")
					.setVisible(false);
		}

		loadScreen("serverScreen");

	}

	public void resetParamsTextfield(String nameTextField) {

		if (nameTextField.equals("myTextFieldName"))
			if (GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl(nameTextField, TextField.class)
					.getDisplayedText().equals("Your Name"))
				GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl(nameTextField, TextField.class)
						.setText("");

		if (nameTextField.equals("myTextFieldIP"))
			if (GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl(nameTextField, TextField.class)
					.getDisplayedText().equals("IP-address")
					|| (GameManager.getIstance().getNifty().getCurrentScreen()
							.findNiftyControl(nameTextField, TextField.class).getDisplayedText().equals(getIPAddress())
							&& GameManager.getIstance().getNifty().getScreen("multiPlayerScreen")
									.findElementByName("myTextFieldIP").isFocusable()))
				GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl(nameTextField, TextField.class)
						.setText("");

	}

	public void nextCharacter() {
		if (indexCharacter == characters.size() - 1)
			indexCharacter = 0;
		else
			indexCharacter++;
		NiftyImage image = nifty.getRenderEngine().createImage(null,
				"Interface/MultiPlayer/PlayerImage/" + characters.get(indexCharacter) + ".png", false);
		Element niftyElement = nifty.getCurrentScreen().findElementByName("imagePlayer");
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
		
		System.out.println(characters.get(indexCharacter));
	}

	public void redoCharacter() {
		if (indexCharacter == 0)
			indexCharacter = characters.size() - 1;
		else
			indexCharacter--;
		NiftyImage image = nifty.getRenderEngine().createImage(null,
				"Interface/MultiPlayer/PlayerImage/" + characters.get(indexCharacter) + ".png", false);
		Element niftyElement = nifty.getCurrentScreen().findElementByName("imagePlayer");
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
		
		System.out.println(characters.get(indexCharacter));
	}

	public void closeGame() {

		if (this.multiplayer)
			this.multiPlayer.exit();
		System.exit(0);

	}

	public void openEditor() {
		editor();

	}

	public void openSinglePlayer() {
		GameManager.getIstance().getNifty().exit();
		singlePlayer();

	}

	public void openMultiPlayerGame() {
		nifty.exit();
		multiPlayer();
	}
	
	



	public void openMultiPlayerScreen() {

		if (characters == null) 
		{

			loadCharacter();
		}

		if (GameManager.getIstance().getServer() != null && GameManager.getIstance().getServer().isStart()) {
			System.out.println("server attivo");
			GameManager.getIstance().getNifty().getScreen("multiPlayerScreen").findElementByName("imServerImage")
					.setVisible(true);
			GameManager.getIstance().getNifty().getScreen("multiPlayerScreen")
					.findNiftyControl("myTextFieldIP", TextField.class).setText(getIPAddress());
			GameManager.getIstance().getNifty().getScreen("multiPlayerScreen").findElementByName("myTextFieldIP")
					.setFocusable(false);
		} else {
			GameManager.getIstance().getNifty().getScreen("multiPlayerScreen").findElementByName("imServerImage")
					.setVisible(false);
			GameManager.getIstance().getNifty().getScreen("multiPlayerScreen").findElementByName("myTextFieldIP")
					.setFocusable(true);

		}

		loadScreen("multiPlayerScreen");

	}

	public void startGrow(String nameButton) {

		NiftyImage image = nifty.getRenderEngine().createImage(null, "Interface/" + nameButton + "OnHover.png", false);
		Element niftyElement = nifty.getCurrentScreen().findElementByName(nameButton);
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	}

	public void endGrow(String nameButton) {

		NiftyImage image = nifty.getRenderEngine().createImage(null, "Interface/" + nameButton + ".png", false);
		Element niftyElement = nifty.getCurrentScreen().findElementByName(nameButton);
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	}

	public String getIPAddress() {
		return GameManager.getIstance().ipAddress();

	}

	public void startServer() {

		GameManager.getIstance().startServer("");
		openServerScreen();

	}

	public void closeServer() {

		GameManager.getIstance().getServer().stopServer();
		openServerScreen();
	}

	
	@Override
	public void onAction(String arg0, boolean arg1, float arg2) {

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