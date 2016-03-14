package main;

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
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import editor.EditorTerrain;
import singlePlayer.SinglePlayer;
import singlePlayer.Sound;

public class Main extends SimpleApplication implements ActionListener, ScreenController {

	private EditorTerrain editorTerrain;
	private SinglePlayer player;
	private BulletAppState bulletAppState;
	private boolean singleplayer;
	private boolean editor;
	private boolean debug;
	private NiftyJmeDisplay niftyDisplay;
	private Nifty nifty;
	private Sound menuSound;

	public Main() {

	}

	public static void main(String[] args) {
		Main app = new Main();
		AppSettings gameSettings = new AppSettings(false);
		gameSettings.setResolution(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,
				java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
		gameSettings.setFullscreen(true);
		gameSettings.setVSync(true);
		gameSettings.setTitle("Thief");
		gameSettings.setUseInput(true);
		gameSettings.setFrameRate(500);
		gameSettings.setSamples(0);
		gameSettings.setRenderer("LWJGL-OpenGL2");
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
		this.editor = false;

		GameManager.getIstance().setParams(this);
		GameManager.getIstance().setBullet(bulletAppState);
		this.setupAudio();
		this.flyCam.setMoveSpeed(100f);

		mouseInput.setCursorVisible(true);
		this.flyCam.setEnabled(false);
		this.niftyDisplay = new NiftyJmeDisplay(GameManager.getIstance().getApplication().getAssetManager(),
				GameManager.getIstance().getApplication().getInputManager(),
				GameManager.getIstance().getApplication().getAudioRenderer(),
				GameManager.getIstance().getApplication().getGuiViewPort());
		this.nifty = niftyDisplay.getNifty();
		this.nifty.fromXml("Interface/screenMenu.xml", "start", this);

		GameManager.getIstance().getApplication().getGuiViewPort().addProcessor(niftyDisplay);

		this.menuSound.playSound();
	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		if (singleplayer)
			player.simpleUpdate(tpf);
		else if (editor)
			editorTerrain.simpleUpdate(tpf);
	}

	public void singlePlayer() {

		singleplayer = true;
		editor = false;
		GameManager.getIstance().setEditor(false);
		this.player = new SinglePlayer(viewPort, rootNode, cam, "ia");
		this.initKeys();
		this.menuSound.stopSound();
	}

	public void editor() {

		editor = true;
		singleplayer = false;
		GameManager.getIstance().setEditor(true);
		this.editorTerrain = new EditorTerrain(rootNode, cam, guiFont, guiNode, viewPort, settings, "mountain", nifty);
		mouseInput.setCursorVisible(false);
		flyCam.setEnabled(true);
		this.initKeys();
		this.menuSound.stopSound();

	}

	private void initKeys() {
		GameManager.getIstance().getApplication().getInputManager().addMapping("debug",
				new KeyTrigger(KeyInput.KEY_TAB));
		inputManager.addListener(actionListener, "debug", "mouse");
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
			} else if (name.equals("mouse") && !singleplayer) {
				Main.this.mouse();
			}
		}
	};

	public void setupAudio() {
		this.menuSound = new Sound(this.rootNode, "Menu", false, false, true, 1.0f, false);
	}

	@Override
	public void onAction(String arg0, boolean arg1, float arg2) {
		// TODO Auto-generated method stub

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

	public void loadScreen(String nextScreen) {
		nifty.gotoScreen(nextScreen);

	}

	public void closeGame() {

		System.exit(0);

	}

	public void openEditor() {
		editor();

	}

	public void openSinglePlayer() {

		nifty.exit();
		singlePlayer();

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

}