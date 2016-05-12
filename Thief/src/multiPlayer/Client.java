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
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import control.GameManager;
import multiPlayer.format.FormatFloat;
import multiPlayer.format.FormatStringChat;
import multiPlayer.format.StringBuilder;
import multiPlayer.protocols.CommunicationProtocol;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeThief;

/**
 * 
 * This class represents the Player and communicates with the Server
 *
 */
public class Client extends Thread implements CommunicationProtocol {
    /** port communication */
    private final static int PORT = 8080;

    /** protocols communications with the Server */
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
    private final static String SYNCPLAYERS = "send my position";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String STARTSENDMETERRAIN = "start send me terrain";
    private final static String ENDSENDMETERRAIN = "end send me terrain";
    private final static String YESIHAVE = "yes, I have";
    private final static String NOIHAVENT = "no, I haven't";
    private final static String DELETE = "delete this player ";
    private final static String PATH = "assets/MultiPlayer/";
    private final static String PATHMODEL = "Models/Characters/";
    private final static String SENDMESSAGE = "Can I send a message?";

    /** Keyboard Commands */
    private final static String debug = "debug";
    private final static String mouse = "mouse";
    private final static String chatBox = "chatBox";
    private final static String toggleRotate = "toggleRotate";
    private final static String attack1 = "Attack1";
    private final static String attack2 = "Attack2";
    private final static String run = "Run";
    private final static String rotateClockwise = "rotateClockwise";
    private final static String rotateCounterClockwise = "rotateCounterClockwise";

    /** Max file Size */
    public final static int FILE_SIZE = 7134962;
    /** Life Number Player */
    private final static int LIFENUMBER = 100;
    /** damage inflicted on the enemy */
    private final static int DAMAGE = 5;
    /** Player IP Address */
    private final String IAM;
    /** Player Name */
    private final String namePlayer;
    /** name of the player character */
    private final String nameModel;
    /** name of the Terrain */
    private String nameTerrain;
    /** Camera Player */
    private final Camera cam;
    /** Socket of communication with Server */
    private final Socket socket;
    /** Reader from Server */
    private final BufferedReader INPUT;
    /** Writer for Server */
    private final DataOutputStream OUTPUT;
    /** connection stabilished with server */
    private boolean establishedConnection;
    // TODO NEXT
    private boolean next;
    private Queue<ModelState> states;

    public Client(final String namePlayer, final String nameModel, final String address, final Camera cam)
	    throws UnknownHostException, IOException {
	this.socket = new Socket(address, PORT);
	this.establishedConnection = true;
	this.next = true;
	this.namePlayer = namePlayer;
	this.cam = cam;
	this.states = new LinkedBlockingQueue<>();
	this.nameModel = PATHMODEL + nameModel + "/" + nameModel + ".mesh.j3o";
	this.INPUT = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	this.OUTPUT = new DataOutputStream(this.socket.getOutputStream());
	this.IAM = this.ipAddress();
    }

    /** Client connect with Server */
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
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLocalTranslation().x + "\n");
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLocalTranslation().y + "\n");
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLocalTranslation().z + "\n");

		System.out.println(nameModel + "---" + GameManager.getIstance().getNodeThief().getLocalTranslation());
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
	    System.out.println("eccezzioni nello start");
	}
    }

    /** The player closed connection with server */
    @Override
    public void endConnection() {
	try {

	    this.OUTPUT.writeBytes(CLOSE + "\n");
	    this.OUTPUT.writeBytes(IAM + nameModel + "\n");
	    this.establishedConnection = false;

	} catch (IOException e) {
	    System.out.println("eccezzioni nel end");
	}
    }

    /** the player communicates his position */
    @Override
    public void communicationState() {

	try {

	    //TODO 
	    ModelState stateModel = this.states.poll();
	    String line = new StringBuilder().builderString(stateModel.getWalk(), stateModel.getView(), stateModel.getLocation(),
		    stateModel.getLife(), stateModel.isAttack(), this.IAM, this.nameModel);
	
	    this.OUTPUT.writeBytes(line + "\n");
//	    this.OUTPUT.writeBytes(this.nameModel + "\n");
//
//	    this.OUTPUT.writeBytes(stateModel.getWalk().x + "\n");
//	    this.OUTPUT.writeBytes(stateModel.getWalk().y + "\n");
//	    this.OUTPUT.writeBytes(stateModel.getWalk().z + "\n");
//
//	    this.OUTPUT.writeBytes(stateModel.getView().x + "\n");
//	    this.OUTPUT.writeBytes(stateModel.getView().y + "\n");
//	    this.OUTPUT.writeBytes(stateModel.getView().z + "\n");
//
//	    this.OUTPUT.writeBytes(stateModel.getLocation().x + "\n");
//	    this.OUTPUT.writeBytes(stateModel.getLocation().y + "\n");
//	    this.OUTPUT.writeBytes(stateModel.getLocation().z + "\n");
//
//	    this.OUTPUT.writeBytes(stateModel.getLife() + "\n");
//
//	    this.OUTPUT.writeBytes(stateModel.isAttack() + "\n");

//	    this.OUTPUT.writeBytes(ENDSENDSTATE + "\n");

	    this.next = true;

	} catch (IOException e) {
	    System.out.println("eccezzioni nel communicationState");
	}

    }

    /** This Method communicates an Player Updates */
    public void notifyUpdate(Vector3f walk, Vector3f view, int life, boolean attack, Vector3f location) {
	try {
	    if (next) {
		this.next = false;
		this.states.add(new ModelState(walk, view, life, attack, location));
		this.OUTPUT.writeBytes(SENDSTATE + "\n");
	    }
	} catch (IOException e) {
	    System.out.println("eccezzioni nel notifyUpdate");
	}
    }

    /** This Method communicates ana player has left the multyplayer */
    public void communicateExitPlayer() {
	try {
	    String player = INPUT.readLine();
	    this.removeModel(player);
	} catch (IOException e) {
	    System.out.println("eccezzioni nel communicateExitPlayer");
	    // TODO da gestire
	}
    }

    /** This Method Updates state of a Player */
    public void statePlayer() {
	String player = null;
	try {
	    player = this.INPUT.readLine();

	    final Vector3f walk = new FormatFloat().formatVector(this.INPUT.readLine(), this.INPUT.readLine(),
		    this.INPUT.readLine());

	    final Vector3f view = new FormatFloat().formatVector(this.INPUT.readLine(), this.INPUT.readLine(),
		    this.INPUT.readLine());

	    final int life = Integer.parseInt(this.INPUT.readLine());

	    final boolean attack = Boolean.parseBoolean(INPUT.readLine());

	    if (GameManager.getIstance().getPlayers().get(player) != null) {
		((NodeEnemyPlayers) GameManager.getIstance().getPlayers().get(player)).setViewDirection(view);
		((NodeEnemyPlayers) GameManager.getIstance().getPlayers().get(player)).setWalkDirection(walk);
		GameManager.getIstance().getPlayers().get(player).setLife(life);
		if (attack)
		    ((NodeEnemyPlayers) GameManager.getIstance().getPlayers().get(player)).startAttack();
	    }
	    // TODO controllare ogni n secondi che la posizione dei nemici
	    // corrisponda con quella che il server consosce

	} catch (IOException e) {
	    System.out.println("eccezzioni nel statePlayer");
	} catch (NumberFormatException ex) {
	    System.out.println("eccezzioni formato float statePlayer");
	}
    }

    public void syncPlayers() {
	try {
	    final String player = this.INPUT.readLine();
	    final Vector3f localPlayer = new FormatFloat().formatVector(this.INPUT.readLine(), this.INPUT.readLine(),
		    this.INPUT.readLine());
	    GameManager.getIstance().getPlayers().get(player).getCharacterControl().warp(localPlayer);

	} catch (IOException e) {
	    // TODO
	    System.out.println("eccezzioni nel syncPlayers");
	}
    }

    // TODO fine sincronizzazione col server
    /** This Method return Player IP address */
    @Override
    public String ipAddress() {
	URL url;
	try {
	    url = new URL("http://checkip.amazonaws.com/");
	    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
	    return br.readLine();
	} catch (MalformedURLException e) {
	    System.out.println("eccezzioni nel ipAddress");
	} catch (IOException e) {
	    System.out.println("eccezzioni nel ipAddress");
	}

	return null;

    }

    /** This Method communicates that there is a new player */
    public void communicationNewPlayer() {
	try {
	    this.addNewPlayers(INPUT.readLine(), INPUT.readLine(), INPUT.readLine(), INPUT.readLine(),
		    INPUT.readLine());
	} catch (IOException e) {
	    System.out.println("eccezzioni nel communicationNewPlayer");
	}
    }

    // TODO FileRecieved
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

    /**
     * This Method set the Start Position for a Player where there aren't enemy
     * or obstacles
     */
    public void bornPosition(Node scene) {
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(this.nameModel);
	spatial.setLocalTranslation(new Vector3f(30, 0, 30));
	GameManager.getIstance().setNodeThief(new NodeThief(spatial, true));
	GameManager.getIstance().addModel(GameManager.getIstance().getNodeThief());
	GameManager.getIstance().getNodeThief().setSinglePlayer(false);
	GameManager.getIstance().getNodeThief().setCam(this.cam);
	scene.attachChild(GameManager.getIstance().getNodeThief());
	this.setKey();
    }

    /** This Method add a Player and his Model in the Game's Terrain */
    public void addNewPlayers(String name, String model, String x, String y, String z) {

	name += model;
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(model);
	Vector3f vector3f = new Vector3f(Float.parseFloat(x), Float.parseFloat(y), Float.parseFloat(z));
	System.out.println(vector3f);
	spatial.setLocalTranslation(vector3f);

	NodeCharacter players = new NodeEnemyPlayers(spatial, new Vector3f(1.5f, 4.4f, 80f), vector3f, LIFENUMBER,
		DAMAGE, name);
	players.addCharacterControl();
	GameManager.getIstance().addModelEnemy(players);
	GameManager.getIstance().addModel(players);
	players.addPhysicsSpace();
	players.setName(name);

	GameManager.getIstance().getBullet().getPhysicsSpace().add(players);
	GameManager.getIstance().addPlayes(name, players);
	GameManager.getIstance().addNotifyStateModel(new NotifyStateModel(true, players));
    }

    /** This Method remove a Model in the Game's Terrain */
    public void removeModel(String key) {

	GameManager.getIstance()
		.addNotifyStateModel(new NotifyStateModel(false, GameManager.getIstance().getPlayers().get(key)));
	GameManager.getIstance().removePlayers(key);
	GameManager.getIstance().removeModel(key);

    }

    /** This method set Keyboard Command for MultiPlayer */
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
	GameManager.getIstance().getApplication().getInputManager().addMapping(debug, new KeyTrigger(KeyInput.KEY_TAB));
	GameManager.getIstance().getApplication().getInputManager().addMapping(mouse,
		new KeyTrigger(KeyInput.KEY_LCONTROL));
	GameManager.getIstance().getApplication().getInputManager().addMapping("sendMessage",
			new KeyTrigger(KeyInput.KEY_RETURN));
	GameManager.getIstance().getApplication().getInputManager().addMapping(chatBox, new KeyTrigger(KeyInput.KEY_9));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, run, attack1, attack2, toggleRotate, chatBox,"sendMessage");
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, run, rotateClockwise, rotateCounterClockwise);
    }

    /** This method Sends a Message for Player's ChatBox */
    public void sendMessage(String displayedText) {
	try {
	    this.OUTPUT.writeBytes(SENDMESSAGE + "\n");
	    this.OUTPUT.writeBytes(this.namePlayer + "\n");
	    this.OUTPUT.writeBytes(displayedText + "\n");

	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    /** This method Riceived a Message from Player's ChatBox and Print */
    public void riceivedMessage() {

	try {
	    final String namePlayer = INPUT.readLine();
	    final String messageChatBox = INPUT.readLine();
	    new FormatStringChat(namePlayer).printMessageChatBox(messageChatBox);

	} catch (IOException e) {
	    // TODO catch
	    e.printStackTrace();
	}

    }

    /** Thread's Method */
    @Override
    public void run() {

	try {
	    this.startConnection();
	    while (this.establishedConnection) {
		final String message = this.INPUT.readLine();
		if (message.equals(NEWPLAYER))
		    this.communicationNewPlayer();
		else if (message.equals(SENDSTATE))
		    this.communicationState();
		else if (message.equals(PLAYER))
		    this.statePlayer();
		else if (message.equals(DELETE))
		    this.communicateExitPlayer();
		else if (message.equals(CLOSE))
		    this.endConnection();
		else if (message.equals(SYNCPLAYERS))
		    this.syncPlayers();
		else if (message.equals(SENDMESSAGE))
		    this.riceivedMessage();

	    }
	    this.socket.close();
	    this.INPUT.close();
	    this.OUTPUT.close();
	} catch (IOException e) {
	    System.out.println("eccezzioni nel run");
	}
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
