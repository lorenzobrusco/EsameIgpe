package editor;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Sphere;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainQuad;
import control.GameManager;
import de.lessvoid.nifty.controls.Label;
import de.lessvoid.nifty.controls.ListBox;
import de.lessvoid.nifty.controls.Slider;
import de.lessvoid.nifty.controls.SliderChangedEvent;
import de.lessvoid.nifty.controls.TextField;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.elements.render.ImageRenderer;
import de.lessvoid.nifty.render.NiftyImage;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import singlePlayer.Sound;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeModel;
import singlePlayer.model.NodeThief;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.NiftyEventSubscriber;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.vecmath.GMatrix;

public class EditorTerrain implements ScreenController {

	private Node rootNode;
	private boolean lowerTerrain = false;
	private boolean mouse = false;
	private boolean tree = false;
	private boolean thief = false;
	private boolean portal = false;
	private boolean builder = false;
	private boolean chapel = false;
	private boolean castle = false;
	private boolean bonFire = false;
	private boolean enemy = false;
	private TerrainQuad terrain;
	private Geometry marker;
	private BitmapText hintText;
	private boolean raiseTerrain = false;	
	private LoadTerrain loadTerrain;
	private NodeThief thiefModel;
	private NodeModel bonFireModel;
	private Node currentSpatial;
	private Stack<Node> spatials;
	private final Camera cam;
	private final BitmapFont guiFont;
	private final Node guiNode;
	private final ViewPort viewPort;
	private final AppSettings settings;
	private Sound editorSound;

	public EditorTerrain(Node rootNode, Camera cam, BitmapFont guiFont, Node guiNode, ViewPort port,
			AppSettings settings, String path) {

		this.rootNode = rootNode;		
		this.cam = cam;
		this.guiFont = guiFont;
		this.guiNode = guiNode;
		this.viewPort = port;
		this.settings = settings;
		this.spatials = new Stack<>();
		this.currentSpatial = new Node();
		this.loadTerrain = new LoadTerrain();
		this.makeScene(path + ".j3o");
		this.cam.setLocation(new Vector3f(0, 128, 0));
		this.cam.lookAtDirection(new Vector3f(0, -1f, 0).normalizeLocal(), Vector3f.UNIT_X);
		this.loadHintText();
		this.initCrossHairs();
		this.createMarker();
		this.setKey();
		this.rootNode.addLight(loadTerrain.makeAmbientLight());
		this.makeNiftyEditor();
		this.setupAudio();
		this.editorSound.playSound();

		// TODO elimina thief e bon fire gia presenti

//		 this.terrain.detachChild(this.thiefModel.getModel());
//		 this.terrain.detachChild(this.bonFireModel.getModel());
	}

	public void simpleUpdate(float tpf) {
		Vector3f intersection = getWorldIntersection();
		if (this.raiseTerrain && !this.mouse) {
			if (intersection != null) {
				this.adjustHeight(intersection, 20, tpf * 60);
			}
		} else if (this.lowerTerrain && !this.mouse) {
			if (intersection != null) {
				this.adjustHeight(intersection, 20, -tpf * 60);
			}
		} else if (this.tree && !this.mouse) {
			if (intersection != null) {
				this.makeTree(intersection);
				this.tree = false;
			}
		} else if (this.thief && !this.mouse) {
			if (intersection != null) {
				this.makeThief(intersection);
				this.thief = false;
			}
		} else if (this.portal && !this.mouse) {
			if (intersection != null) {
				this.makePortal(intersection);
				this.portal = false;
			}
		} else if (this.builder && !this.mouse) {
			if (intersection != null) {
				this.makeBuilder(intersection);
				this.builder = false;
			}
		} else if (this.chapel && !this.mouse) {
			if (intersection != null) {
				this.makeChapel(intersection);
				this.chapel = false;
			}
		} else if (this.bonFire && !this.mouse) {
			if (intersection != null) {
				this.makeBonFire(intersection);
				this.bonFire = false;
			}
		} else if (this.castle && !this.mouse) {
			if (intersection != null) {
				this.makeCastle(intersection);
				this.castle = false;
			}
		} else if (this.enemy && !this.mouse) {
			if (intersection != null) {
				this.makeEnemy(intersection);
				this.enemy = false;
			}
		}
		if (this.terrain != null && intersection != null) {
			float h = this.terrain.getHeight(new Vector2f(intersection.x, intersection.z));
			Vector3f tl = this.terrain.getWorldTranslation();
			this.marker.setLocalTranslation(tl.add(new Vector3f(intersection.x, h, intersection.z)));
		}
	}

	public void loadHintText() {
		this.hintText = new BitmapText(this.guiFont, false);
		this.hintText.setLocalTranslation(0, this.cam.getHeight(), 0);
		this.guiNode.attachChild(this.hintText);
	}

	public void save() {
		if (GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("textfieldSaveTerrain", TextField.class).getDisplayedText()
				.equals("")) {
			new SaveTerrain(this.rootNode).saveModel("default");
		} else {
			new SaveTerrain(this.rootNode).saveModel(GameManager.getIstance().getNifty().getCurrentScreen()
					.findNiftyControl("textfieldSaveTerrain", TextField.class).getDisplayedText());
			GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("textfieldSaveTerrain", TextField.class).setText("");
		}
		this.readScenes();
	}

	public void delete() {
		if (!this.spatials.isEmpty()) {
			this.currentSpatial = this.spatials.pop();
			if (!this.currentSpatial.getName().contains("Yasuo"))
				this.terrain.detachChild(this.currentSpatial);
			this.setName();
		}
	}

	private void loadScene(String name) {
		GameManager.getIstance().getModels().clear();
		this.rootNode.detachChild(this.terrain);
		this.viewPort.clearProcessors();
		this.makeScene(name);
	}

	public void reset() {
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	public void load() {
		this.loadScene(
				GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("listBox", ListBox.class).getFocusItem().toString());
	}

	public void deleteScene() {
		File file = new File("assets" + File.separator + "Scenes" + File.separator
				+ GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("listBox", ListBox.class).getFocusItem().toString());
		System.out.println(file.toString());
		if (file.exists() && !(file.toString().contains("mountain")))
			file.delete();
		this.readScenes();
	}

	private void makeScene(String path) {
		this.terrain = this.loadTerrain.loadTerrain(path, true);
		this.rootNode.attachChild(this.terrain);
		this.viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		this.viewPort.addProcessor(loadTerrain.makeFilter(true, false, true));
		this.bonFireModel = GameManager.getIstance().getBonfire();
		this.thiefModel = GameManager.getIstance().getNodeThief();
	}

	private void initCrossHairs() {
		BitmapText ch = new BitmapText(this.guiFont, false);
		ch.setSize(this.guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+");
		ch.setLocalTranslation(this.settings.getWidth() / 2 - this.guiFont.getCharSet().getRenderedSize() / 3 * 2,
				this.settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		this.guiNode.attachChild(ch);
	}

	public ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean pressed, float tpf) {
			if (name.equals("Raise")) {
				EditorTerrain.this.raiseTerrain = pressed;
			} else if (name.equals("Lower")) {
				EditorTerrain.this.lowerTerrain = pressed;
			} else if (name.equals("tree")) {
				EditorTerrain.this.tree = pressed;
			} else if (name.equals("thief")) {
				EditorTerrain.this.thief = pressed;
			} else if (name.equals("enemy")) {
				EditorTerrain.this.enemy = pressed;
			} else if (name.equals("portal")) {
				EditorTerrain.this.portal = pressed;
			} else if (name.equals("builder")) {
				EditorTerrain.this.builder = pressed;
			} else if (name.equals("chapel")) {
				EditorTerrain.this.chapel = pressed;
			} else if (name.equals("castle")) {
				EditorTerrain.this.castle = pressed;
			} else if (name.equals("bonFire")) {
				EditorTerrain.this.bonFire = pressed;
			} else if (name.equals("mouse")) {
				EditorTerrain.this.mouse = !EditorTerrain.this.mouse;
			}

		}
	};

	private void setKey() {

		GameManager.getIstance().getApplication().getInputManager().addMapping("mouse",
				new KeyTrigger(KeyInput.KEY_LCONTROL));
		GameManager.getIstance().getApplication().getInputManager().addMapping("tree", new KeyTrigger(KeyInput.KEY_1));
		GameManager.getIstance().getApplication().getInputManager().addMapping("thief", new KeyTrigger(KeyInput.KEY_2));
		GameManager.getIstance().getApplication().getInputManager().addMapping("portal",
				new KeyTrigger(KeyInput.KEY_4));
		GameManager.getIstance().getApplication().getInputManager().addMapping("enemy", new KeyTrigger(KeyInput.KEY_3));
		GameManager.getIstance().getApplication().getInputManager().addMapping("builder",
				new KeyTrigger(KeyInput.KEY_5));
		GameManager.getIstance().getApplication().getInputManager().addMapping("chapel",
				new KeyTrigger(KeyInput.KEY_6));
		GameManager.getIstance().getApplication().getInputManager().addMapping("windMill",
				new KeyTrigger(KeyInput.KEY_7));
		GameManager.getIstance().getApplication().getInputManager().addMapping("bonFire",
				new KeyTrigger(KeyInput.KEY_8));
		GameManager.getIstance().getApplication().getInputManager().addMapping("castle",
				new KeyTrigger(KeyInput.KEY_9));
		GameManager.getIstance().getApplication().getInputManager().addMapping("Raise",
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		GameManager.getIstance().getApplication().getInputManager().addMapping("Lower",
				new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		GameManager.getIstance().getApplication().getInputManager().addListener(this.actionListener, "exit", "Lower",
				"Raise", "enemy", "portal", "builder", "chapel", "windMill", "bonFire", "castle", "thief", "tree",
				"mouse", "save");
	}

	private void adjustHeight(Vector3f loc, float radius, float height) {
		int radiusStepsX = (int) (radius / this.terrain.getLocalScale().x);
		int radiusStepsZ = (int) (radius / this.terrain.getLocalScale().z);
		float xStepAmount = this.terrain.getLocalScale().x;
		float zStepAmount = this.terrain.getLocalScale().z;
		List<Vector2f> locs = new ArrayList<Vector2f>();
		List<Float> heights = new ArrayList<Float>();
		for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
			for (int x = -radiusStepsX; x < radiusStepsX; x++) {
				float locX = loc.x + (x * xStepAmount);
				float locZ = loc.z + (z * zStepAmount);
				if (isInRadius(locX - loc.x, locZ - loc.z, radius)) {
					float h = calculateHeight(radius, height, locX - loc.x, locZ - loc.z);
					locs.add(new Vector2f(locX, locZ));
					heights.add(h);
				}
			}
		}
		this.terrain.adjustHeight(locs, heights);
		this.terrain.updateModelBound();
	}

	private boolean isInRadius(float x, float y, float radius) {
		Vector2f point = new Vector2f(x, y);
		return point.length() <= radius;
	}

	private float calculateHeight(float radius, float heightFactor, float x, float z) {
		Vector2f point = new Vector2f(x, z);
		float val = point.length() / radius;
		val = 1 - val;
		if (val <= 0) {
			val = 0;
		}
		return heightFactor * val;
	}

	private Vector3f getWorldIntersection() {
		Vector3f origin = this.cam
				.getWorldCoordinates(new Vector2f(this.settings.getWidth() / 2, this.settings.getHeight() / 2), 0.0f);
		Vector3f direction = this.cam
				.getWorldCoordinates(new Vector2f(this.settings.getWidth() / 2, this.settings.getHeight() / 2), 0.3f);
		direction.subtractLocal(origin).normalizeLocal();
		Ray ray = new Ray(origin, direction);
		CollisionResults results = new CollisionResults();
		int numCollisions = this.terrain.collideWith(ray, results);
		if (numCollisions > 0) {
			CollisionResult hit = results.getClosestCollision();
			return hit.getContactPoint();
		}
		return null;
	}

	private void makeNiftyEditor() {

		GameManager.getIstance().getNifty().fromXml("Interface/editor.xml", "start", this);	
		}

	private void createMarker() {
		Sphere sphere = new Sphere(8, 8, 10.5f);
		this.marker = new Geometry("Marker");
		this.marker.setMesh(sphere);
		Material mat = new Material(GameManager.getIstance().getApplication().getAssetManager(),
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", ColorRGBA.Green);
		this.marker.setMaterial(mat);
		this.rootNode.attachChild(this.marker);
	}

	private void makeTree(Vector3f intersect) {
		NodeModel tree = new NodeModel("Tree/Tree.mesh.j3o", new Vector3f(1.57f, 10f, 1000f));
		tree.getModel().scale(5f);
		tree.getModel().setLocalTranslation(intersect);
		this.terrain.attachChild(tree.getModel());
		tree.moveModel(tree.getModel().getLocalTranslation());
		this.spatials.add((Node) tree.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makeEnemy(Vector3f intersect) {
		NodeCharacter enemy = null;
		int rand = (int) (Math.random() * 12);
		switch (rand) {
		case 0:
			enemy = new NodeCharacter("Characters/Jayce/Jayce.mesh.j3o", new Vector3f(3.0f, 6f, 100f), 50, 10);
			break;
		case 1:
			enemy = new NodeCharacter("Characters/Rengar/Rengar.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 2:
			enemy = new NodeCharacter("Characters/Talon/Talon.mesh.j3o", new Vector3f(3.0f, 4.5f, 10f), 50, 10);
			break;
		case 3:
			enemy = new NodeCharacter("Characters/Wukong/Wukong.mesh.j3o", new Vector3f(3.0f, 4.5f, 10f), 50, 10);
			break;
		case 4:
			enemy = new NodeCharacter("Characters/Azir/Azir.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 5:
			enemy = new NodeCharacter("Characters/XiinZhao/XiinZhao.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 6:
			enemy = new NodeCharacter("Characters/Katarina/Katarina.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 7:
			enemy = new NodeCharacter("Characters/Jarvan/Jarvan.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 8:
			enemy = new NodeCharacter("Characters/Fiora/Fiora.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 9:
			enemy = new NodeCharacter("Characters/Sejuani/Sejuani.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 10:
			enemy = new NodeCharacter("Characters/Volibear/Volibear.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		case 11:
			enemy = new NodeCharacter("Characters/WarWick/WarWick.mesh.j3o", new Vector3f(3.0f, 7f, 100f), 50, 10);
			break;
		default:
			break;
		}
		enemy.getModel().setLocalTranslation(intersect);
		this.terrain.attachChild(enemy.getModel());
		enemy.moveModel(enemy.getModel().getLocalTranslation());

		this.spatials.add((Node) enemy.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makeBuilder(Vector3f intersect) {
		NodeModel builder = null;
		int rand = (int) (Math.random() * 4);
		switch (rand) {
		case 0:
			builder = new NodeModel("Buildings/House/House.mesh.j3o", new Vector3f(3.0f, 6f, 100f));
			break;
		case 1:
			builder = new NodeModel("Buildings/HouseTwo/HouseTwo.mesh.j3o", new Vector3f(3.0f, 7f, 100f));
			break;
		case 2:
			builder = new NodeModel("Buildings/HouseMedium/HouseMedium.mesh.j3o", new Vector3f(3.0f, 4.5f, 10f));
			builder.setName("HouseMedium");
			break;
		case 3:
			builder = new NodeModel("Buildings/WindMill/WindMill.mesh.j3o", new Vector3f(3.0f, 7f, 100f));
			builder.setName("WindMill");
			break;
		default:
			break;
		}
		builder.getModel().setLocalTranslation(intersect);
		this.terrain.attachChild(builder.getModel());
		builder.moveModel(builder.getModel().getLocalTranslation());
		this.spatials.add((Node) builder.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makeChapel(Vector3f intersect) {

		NodeModel builder = new NodeModel("Buildings/Chapel/Chapel.mesh.j3o", new Vector3f(3.0f, 7f, 100f));
		builder.getModel().setLocalTranslation(intersect);
		this.terrain.attachChild(builder.getModel());
		builder.moveModel(builder.getModel().getLocalTranslation());
		this.spatials.add((Node) builder.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makeCastle(Vector3f intersect) {

		NodeModel castle = new NodeModel("Buildings/Castle/Castle.j3o", new Vector3f(3.0f, 7f, 100f));
		castle.setName("Castle");
		castle.getModel().setLocalTranslation(intersect.x, intersect.y, intersect.z);
		this.terrain.attachChild(castle.getModel());
		castle.moveModel(castle.getModel().getLocalTranslation());
		this.spatials.add((Node) castle.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makeBonFire(Vector3f intersect) {// TODO togliete commenti per
		// creare un nuovo bonFire
		// Node bonfire = new Node("Bonfire");
		//
		// Spatial wood =
		// GameManager.getIstance().getApplication().getAssetManager()
		// .loadModel("Models/Specials/Bonfire/Bonfire.mesh.j3o");
		// wood.setLocalTranslation(0, 0.4f, 0);
		//
		// ParticleEmitter fire = new ParticleEmitter("Emitter",
		// com.jme3.effect.ParticleMesh.Type.Triangle, 3000);
		// fire.setLocalTranslation(bonfire.getLocalTranslation().x - 0.1f,
		// bonfire.getLocalTranslation().y + 0.5f,
		// bonfire.getLocalTranslation().x + 0.2f);
		// Material mat_red = new
		// Material(GameManager.getIstance().getApplication().getAssetManager(),
		// "Common/MatDefs/Misc/Particle.j3md");
		// mat_red.setTexture("Texture",
		// GameManager.getIstance().getApplication().getAssetManager().loadTexture("Effects/Explosion/flame.png"));
		// fire.setMaterial(mat_red);
		// fire.setImagesX(2);
		// fire.setImagesY(2);
		// fire.setEndColor(new ColorRGBA(1f, 0f, 0f, 1f));
		// fire.setStartColor(new ColorRGBA(1f, 1f, 0f, 0.5f));
		// fire.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 2,
		// 0));
		// fire.setStartSize(1.0f);
		// fire.setEndSize(0.1f);
		// fire.setGravity(0, -1f, 0);
		// fire.setLowLife(1f);
		// fire.setHighLife(2f);
		// fire.getParticleInfluencer().setVelocityVariation(0.3f);
		//
		// bonfire.attachChild(wood);
		// bonfire.attachChild(fire);
		//
		// NodeModel nodeBonFire = new NodeModel(bonfire, new Vector3f(7.3f,
		// 15f, 1000f));
		//
		// this.bonFireModel = nodeBonFire;
		// this.bonFireModel.setName(bonfire.getName());
		// this.bonFireModel.getModel().setLocalTranslation(intersect);
		// this.terrain.attachChild(nodeBonFire.getModel());
		// this.bonFireModel.moveModel(nodeBonFire.getModel().getLocalTranslation());
		// this.spatials.add((Node) nodeBonFire.getModel());
		// this.setName();
		// this.nifty.getCurrentScreen().findNiftyControl("sliderRotate",
		// Slider.class)
		// .setValue(nifty.getCurrentScreen().findNiftyControl("sliderRotate",
		// Slider.class).getMin());

		this.bonFireModel.setName(this.bonFireModel.getName());
		this.bonFireModel.getModel().setLocalTranslation(intersect);
		this.bonFireModel.moveModel(this.bonFireModel.getModel().getLocalTranslation());
		this.spatials.add((Node) this.bonFireModel.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makePortal(Vector3f intersect) {
		NodeModel portal = new NodeModel("Specials/Portal/Portal.mesh.j3o", new Vector3f(7.3f, 15f, 1000f));
		portal.getModel().setLocalTranslation(intersect);
		this.terrain.attachChild(portal.getModel());
		portal.moveModel(portal.getModel().getLocalTranslation());
		this.spatials.add((Node) portal.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void makeThief(Vector3f intersect) {// TODO togliete commenti per
		// creare un nuovo yasuo
		// this.thiefModel = new
		// NodeThief(GameManager.getIstance().getApplication().getAssetManager()
		// .loadModel("Models/Characters/Yasuo/Yasuo.mesh.j3o"));
		this.thiefModel.getModel().setLocalTranslation(intersect);
		this.thiefModel.moveModel(intersect);
		this.terrain.attachChild(thiefModel.getModel());
		this.spatials.add((Node) this.thiefModel.getModel());
		this.setName();
		GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class)
				.setValue(GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("sliderRotate", Slider.class).getMin());
	}

	private void readScenes() {

		@SuppressWarnings("unchecked")
		ListBox<String> listBox = GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("listBox", ListBox.class);
		listBox.clear();
		try {
			Files.walk(Paths.get("assets/Scenes")).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					listBox.addItem(filePath.getFileName().toString());
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		listBox.selectItemByIndex(0);
	}

	private void setName() {
		if (this.spatials.isEmpty()) {
			GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("NameModel", Label.class).setText("Model:");
			return;
		}
		if (this.spatials.peek().getName().contains("wukong")) {
			GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("NameModel", Label.class).setText("Model: Yasuo");
		} else {
			String[] split = this.spatials.peek().getName().split("-");
			GameManager.getIstance().getNifty().getCurrentScreen().findNiftyControl("NameModel", Label.class).setText("Model:" + split[0]);
		}
	}

	@NiftyEventSubscriber(pattern = "sliderRotate")
	public void onSliderChangedEvent(final String id, final SliderChangedEvent event) {
		if (!this.spatials.isEmpty()) {
			this.currentSpatial = this.spatials.peek();
			if (this.currentSpatial.getName().contains("HouseMedium"))
				this.currentSpatial.rotate(0.0f, 0.0f, event.getValue());
			else if (this.currentSpatial.getName().contains("WindMill"))
				this.currentSpatial.rotate(0.0f, event.getValue(), 0.0f);
			else if (this.currentSpatial.getName().contains("Chapel")
					|| this.currentSpatial.getName().contains("HouseTwo")
					|| this.currentSpatial.getName().contains("House")
					|| this.currentSpatial.getName().contains("Castle"))
				this.currentSpatial.rotate(0.0f, event.getValue(), 0.0f);
			else if (!this.currentSpatial.getName().contains("Tree"))
				this.currentSpatial.rotate(0.0f, 0.0f, event.getValue());
		}
	}

	public void startGrow(String nameButton) {

		NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null, "Interface/" + nameButton + "OnHover.png", false);
		Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	}

	public void endGrow(String nameButton) {

		NiftyImage image = GameManager.getIstance().getNifty().getRenderEngine().createImage(null, "Interface/" + nameButton + ".png", false);
		Element niftyElement = GameManager.getIstance().getNifty().getCurrentScreen().findElementByName(nameButton);
		niftyElement.getRenderer(ImageRenderer.class).setImage(image);
	}

	private void setupAudio() {
		this.editorSound = new Sound(this.terrain, "Editor", false, false, true, 1.0f, false);
	}

	public void closeEditor() 
	{
    
		GameManager.getIstance().getApplication().getInputManager().clearMappings();
		GameManager.getIstance().getNifty().exit();
		this.rootNode.detachAllChildren();
		this.viewPort.clearProcessors();
		this.guiNode.detachAllChildren();
		GameManager.getIstance().getNifty().fromXml("Interface/screenMenu.xml", "start", this);
		GameManager.getIstance().getApplication().getInputManager().setCursorVisible(true);	
	}

	@Override
	public void bind(Nifty arg0, Screen arg1) {

	}

	@Override
	public void onEndScreen() {

	}

	@Override
	public void onStartScreen() {
		this.readScenes();
	}
}