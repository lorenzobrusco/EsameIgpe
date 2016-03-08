package main;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.system.AppSettings;

import control.GameManager;
import editor.EditorTerrain;
import singlePlayer.SinglePlayer;

public class Main extends SimpleApplication implements ActionListener {

	private EditorTerrain editorTerrain;
	private SinglePlayer player;
	private BulletAppState bulletAppState;
	private boolean singleplayer;
	private boolean debug;

	public Main() {

	}

	public static void main(String[] args) {
		Main app = new Main();
		//
		// AppSettings gameSettings = new AppSettings(false);
		// gameSettings.setResolution(java.awt.Toolkit.getDefaultToolkit().getScreenSize().width,
		// java.awt.Toolkit.getDefaultToolkit().getScreenSize().height);
		// gameSettings.setFullscreen(true);
		// gameSettings.setVSync(true);
		// gameSettings.setTitle("Thief");
		// gameSettings.setUseInput(true);
		// gameSettings.setFrameRate(500);
		// gameSettings.setSamples(0);
		// gameSettings.setRenderer("LWJGL-OpenGL2");
		// app.setSettings(gameSettings);
		// app.setShowSettings(false);

		// disable statistics
		app.setDisplayFps(true);
		app.setDisplayStatView(true);
		app.start();
	}

	public void simpleInitApp() {
		// ------------------------------Parametri-------------------------------//
		this.bulletAppState = new BulletAppState();
		this.stateManager.attach(bulletAppState);
		this.assetManager.registerLocator("assets/", FileLocator.class);
		this.debug = false;
		this.singleplayer = true;
		GameManager.getIstance().setParams(this);
		GameManager.getIstance().setBullet(bulletAppState);
		this.flyCam.setMoveSpeed(100f);
		this.audioRenderer.setListener(this.listener);

		editor();
		// singlePlayer();

	}

	@Override
	public void simpleUpdate(float tpf) {
		super.simpleUpdate(tpf);
		if (singleplayer) {
			player.simpleUpdate(tpf);
			this.listener.setLocation(cam.getLocation());
			this.listener.setRotation(cam.getRotation());
			this.audioRenderer.update(tpf);

		} else
			editorTerrain.simpleUpdate(tpf);

	}

	public void singlePlayer() {
		singleplayer = true;
		this.player = new SinglePlayer(viewPort, rootNode, cam, "ia");
		this.initKeys();
	}

	public void editor() {
		singleplayer = false;
		GameManager.getIstance().setEditor(true);
		this.editorTerrain = new EditorTerrain(rootNode, cam, guiFont, guiNode, viewPort, settings, "mountain");
		this.initKeys();
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

	@Override
	public void onAction(String arg0, boolean arg1, float arg2) {

	}
}