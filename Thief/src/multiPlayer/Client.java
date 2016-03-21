package multiPlayer;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import control.GameManager;
import multiPlayer.protocols.CommunicationProtocol;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeThief;

public class Client extends Thread implements CommunicationProtocol {

    private final static int PORT = 8080;
    private final static String KNOCK = "knock knock";
    private final static String WHOAREYOU = "who are you?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String TRYAGAIN = "try again";
    private final static String CLOSE = "close connection";
    private final static String NEWPLAYER = "it's arrive a new player";
    private final static String WHOISTHERE = "tell me, who is there ?";
    private final static String SENDSTATE = "send your state";
    private final static String PLAYER = "the player: ";
    private final static String ENDSENDSTATE = "end send your state";
    private final static String ACNOWLEDGEDPOSITION = "ok, acnwoledged position";
    private final static String ACNOWLEDGEDLIFE = "ok, acnwoledged life";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String STARTSENDMETERRAIN = "start send me terrain";
    private final static String ENDSENDMETERRAIN = "end send me terrain";
    private final static String YESIHAVE = "yes, I have";
    private final static String NOIHAVENT = "no, I haven't";
    private final static String PATH = "assets/MultiPlayer/";
    private final static String PATHMODEL = "Models/Characters/";
    private final static String attack1 = "Attack1";
    private final static String attack2 = "Attack2";
    private final static String run = "Run";
    private final static String rotateClockwise = "rotateClockwise";
    private final static String rotateCounterClockwise = "rotateCounterClockwise";
    public final static int FILE_SIZE = 7134962;
    private final static int LIFENUMBER = 100;
    private final static int DAMAGE = 5;
    private final String IAM;
    private final Socket socket;
    private final BufferedReader INPUT;
    private final DataOutputStream OUTPUT;
    private boolean establishedConnection;
    private final String namePlayer;
    private final String nameModel;
    private String nameTerrain;
    private final Node rootNode;
    private final Camera cam;

    public Client(final String namePlayer, final String nameModel, final String address, final Node rootNode,
	    final Camera cam) throws UnknownHostException, IOException {
	this.socket = new Socket(address, PORT);
	this.establishedConnection = true;
	this.namePlayer = namePlayer;
	this.rootNode = rootNode;
	this.cam = cam;
	this.nameModel = PATHMODEL + nameModel + "/" + nameModel + ".mesh.j3o";
	this.INPUT = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	this.OUTPUT = new DataOutputStream(this.socket.getOutputStream());
	this.IAM = this.ipAddress();
    }

    @Override
    public void startConnection() {
	try {
	    if (this.INPUT.readLine().equals(HAVEYOUTHISTERRAIN)) {
		nameTerrain = this.INPUT.readLine();
		final File file = new File(PATH + nameTerrain + ".j3o");
		if (file.exists()) {
		    this.OUTPUT.writeBytes(YESIHAVE + "\n");
		} else {
		    this.OUTPUT.writeBytes(NOIHAVENT + "\n");
		    if (this.INPUT.readLine().equals(STARTSENDMETERRAIN)) {
			this.fileRecieved(file);
			if (this.INPUT.readLine().equals(ENDSENDMETERRAIN)) {
			    ; // TODO RICEVUTO TUTTO
			}
		    }
		}
	    }
	    this.OUTPUT.writeBytes(KNOCK + "\n");
	    if (this.INPUT.readLine().equals(WHOAREYOU)) {
		this.OUTPUT.writeBytes(IAM + "\n");
		this.OUTPUT.writeBytes(this.namePlayer + "\n");
		this.OUTPUT.writeBytes(this.nameModel + "\n");
		// TODO metodo per trovare un punto dove nascere privo di nemici
		// e di ostacoli;
		this.OUTPUT.writeBytes(50 + "\n");
		this.OUTPUT.writeBytes(0 + "\n");
		this.OUTPUT.writeBytes(50 + "\n");
	    }
	    if (this.INPUT.readLine().equals(YOUAREWELCOME)) {
		this.establishedConnection = true;
		this.OUTPUT.writeBytes(WHOISTHERE + "\n");
		int size = Integer.parseInt(this.INPUT.readLine());
		for (int i = 0; i < size; i++) {
		    if (this.INPUT.readLine().equals(NEWPLAYER))
			this.addNewPlayers(INPUT.readLine(), INPUT.readLine(), INPUT.readLine(), INPUT.readLine(),
				INPUT.readLine());
		}
	    } else if (this.INPUT.readLine().equals(TRYAGAIN))
		this.startConnection();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void endConnection() {
	try {
	    this.OUTPUT.writeBytes(CLOSE + "\n");
	    this.establishedConnection = false;

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public synchronized void communicationState() {

	try {
	    float x, y, z;
	    this.OUTPUT.writeBytes(this.IAM + "\n");
	    this.OUTPUT.writeBytes(this.nameModel + "\n");
	    if (GameManager.getIstance().getNodeThief().getCharacterControl() == null) {
		x = 0;
		y = 0;
		z = 0;
	    } else {
		x = GameManager.getIstance().getNodeThief().getCharacterControl().getWalkDirection().x;
		y = GameManager.getIstance().getNodeThief().getCharacterControl().getWalkDirection().y;
		z = GameManager.getIstance().getNodeThief().getCharacterControl().getWalkDirection().z;
	    }
	    this.OUTPUT.writeBytes(x + "\n");
	    this.OUTPUT.writeBytes(y + "\n");
	    this.OUTPUT.writeBytes(z + "\n");
	    if (GameManager.getIstance().getNodeThief().getCharacterControl() == null) {
		x = 0;
		y = 0;
		z = 0;
	    } else {
		x = GameManager.getIstance().getNodeThief().getCharacterControl().getViewDirection().x;
		y = GameManager.getIstance().getNodeThief().getCharacterControl().getViewDirection().y;
		z = GameManager.getIstance().getNodeThief().getCharacterControl().getViewDirection().z;
	    }
	    this.OUTPUT.writeBytes(x + "\n");
	    this.OUTPUT.writeBytes(y + "\n");
	    this.OUTPUT.writeBytes(z + "\n");
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDPOSITION))
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLIFE() + "\n");
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDLIFE))
		this.OUTPUT.writeBytes(ENDSENDSTATE + "\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    public synchronized void statePlayer() {
	try {
	    String player = this.INPUT.readLine();
	    Vector3f walk = new Vector3f(Float.parseFloat(this.INPUT.readLine()),
		    Float.parseFloat(this.INPUT.readLine()), Float.parseFloat(this.INPUT.readLine()));
	    Vector3f view = new Vector3f(Float.parseFloat(this.INPUT.readLine()),
		    Float.parseFloat(this.INPUT.readLine()), Float.parseFloat(this.INPUT.readLine()));
	    int life = Integer.parseInt(this.INPUT.readLine());
	    if (GameManager.getIstance().getPlayers().get(player) != null) {
		GameManager.getIstance().getPlayers().get(player).getCharacterControl().setViewDirection(view);
		GameManager.getIstance().getPlayers().get(player).getCharacterControl().setWalkDirection(walk);
		GameManager.getIstance().getPlayers().get(player).setLife(life);
		rootNode.updateGeometricState();
	    }
	    System.out.println("client:  " + walk + " ---- " + view);

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public String ipAddress() {
	URL url;
	try {
	    url = new URL("http://checkip.amazonaws.com/");
	    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	    return br.readLine();
	} catch (MalformedURLException e) {
	    e.printStackTrace();
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;

    }

    public void communicationNewPlayer() {
	try {
	    this.addNewPlayers(INPUT.readLine(), INPUT.readLine(), INPUT.readLine(), INPUT.readLine(),
		    INPUT.readLine());

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void run() {

	try {
	    this.startConnection();
	    while (this.establishedConnection) {
		final String message = INPUT.readLine();
		if (message.equals(NEWPLAYER))
		    this.communicationNewPlayer();
		else if (message.equals(SENDSTATE))
		    this.communicationState();
		else if (message.equals(PLAYER))
		    this.statePlayer();
		else if (message.equals(CLOSE))
		    endConnection();
	    }
	    this.socket.close();
	    this.INPUT.close();
	    this.OUTPUT.close();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void fileRecieved(File file) {
	FileOutputStream fileOutputStream = null;
	BufferedOutputStream bufferedOutputStream = null;

	try {
	    byte[] mybytearray = new byte[FILE_SIZE];
	    InputStream is = this.socket.getInputStream();
	    fileOutputStream = new FileOutputStream(file);
	    bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
	    int bytesRead = is.read(mybytearray, 0, mybytearray.length);
	    int current = bytesRead;
	    do {
		bytesRead = is.read(mybytearray, current, (mybytearray.length - current));
		if (bytesRead >= 0)
		    current += bytesRead;
	    } while (bytesRead > -1);

	    bufferedOutputStream.write(mybytearray, 0, current);
	    bufferedOutputStream.flush();
	} catch (IOException e) {
	    e.printStackTrace();
	} finally {
	    if (fileOutputStream != null && bufferedOutputStream != null)
		try {
		    fileOutputStream.close();
		    bufferedOutputStream.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
    }

    public void bornPosition(Node scene) {
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(this.nameModel);
	spatial.setLocalTranslation(new Vector3f(50, 0, 50));
	GameManager.getIstance().setNodeThief(new NodeThief(spatial));
	GameManager.getIstance().addModel(GameManager.getIstance().getNodeThief());
	GameManager.getIstance().getNodeThief().setSinglePlayer(false);
	GameManager.getIstance().getNodeThief().setCam(this.cam);
	scene.attachChild(GameManager.getIstance().getNodeThief());
	this.setKey();
    }

    public void addNewPlayers(String name, String model, String x, String y, String z) {

	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(model);
	Vector3f vector3f = new Vector3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
	spatial.setLocalTranslation(vector3f);
	NodeCharacter players = new NodeEnemyPlayers(spatial, new Vector3f(1.5f, 4.4f, 80f), vector3f, LIFENUMBER,
		DAMAGE);
	players.addCharacterControll();
	GameManager.getIstance().addModelEnemy(players);
	GameManager.getIstance().addModel(players);
	GameManager.getIstance().getTerrain().attachChild(players);
	name += model;
	GameManager.getIstance().addPlayes(name, players);
	rootNode.updateGeometricState();
    }

    public void setKey() {
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
	GameManager.getIstance().getApplication().getInputManager().addMapping("mouse",
		new KeyTrigger(KeyInput.KEY_LCONTROL));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, run, attack1, attack2, "toggleRotate");
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, run, rotateClockwise, rotateCounterClockwise);
    }

    public String getNamePlayer() {
	return namePlayer;
    }

    public String getNameModel() {
	return nameModel;
    }

    public String getNameTerrain() {
	return nameTerrain;
    }

    public void setNameTerrain(String nameTerrain) {
	this.nameTerrain = nameTerrain;
    }

}
