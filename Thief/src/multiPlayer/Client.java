package multiPlayer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import control.GameManager;
import multiPlayer.format.FormatStringChat;
import multiPlayer.format.StringBuilder;
import multiPlayer.notify.NotifyStateModel;
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
    private final int PORT;
    /** protocols communications with the Server */
    private final static String KNOCK = "knock knock";
    private final static String WHOAREYOU = "who are you?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String CLOSE = "close connection";
    private final static String NEWPLAYER = "it's arrive a new player";
    private final static String WHOISTHERE = "tell me, who is there ?";
    private final static String SENDSTATE = "send your state";
    private final static String PLAYER = "the player: ";
    private final static String DELETE = "delete this player ";
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
    private final static int LIFENUMBER = 50;
    /** damage inflicted on the enemy */
    private final static int DAMAGE = 10;
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
    /** input manager */
    private final InputManager inputManager;
    /** Socket of communication with Server */
    private final Socket socket;
    /** Reader from Server */
    private final BufferedReader INPUT;
    /** Writer for Server */
    private final DataOutputStream OUTPUT;
    /** connection stabilished with server */
    private boolean establishedConnection;

    /** new state */
    private String lineToSend;

    /** constructor */
    public Client(final String namePlayer, final String nameModel, final String address,
	    final InputManager inputManager, final Camera cam, int port) throws UnknownHostException, IOException {
	this.PORT = port;
	this.socket = new Socket(address, PORT);
	this.establishedConnection = true;
	this.lineToSend = "";
	this.namePlayer = namePlayer;
	this.cam = cam;
	this.inputManager = inputManager;
	this.nameModel = PATHMODEL + nameModel + "/" + nameModel + ".mesh.j3o";
	this.INPUT = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	this.OUTPUT = new DataOutputStream(this.socket.getOutputStream());
	this.IAM = this.ipAddress();
    }

    /** Client connect with Server */
    @Override
    public void startConnection() {
	try {
	    this.nameTerrain = this.INPUT.readLine();
	    GameManager.getIstance().getMultiplayer().setStart(true);
	    /** time to loading */
	    while (!GameManager.getIstance().getMultiplayer().isCreated())
		super.sleep(500);
	    this.OUTPUT.writeBytes(KNOCK + "\n");
	    String line = this.INPUT.readLine();
	    if (line.equals(WHOAREYOU)) {
		final String out = new StringBuilder().builderString(new Vector3f(), new Vector3f(),
			GameManager.getIstance().getNodeThief().getLocalTranslation(),
			GameManager.getIstance().getNodeThief().getLife(), false, this.IAM, this.nameModel,
			this.namePlayer, 0);
		this.OUTPUT.writeBytes(out + "\n");
		line = this.INPUT.readLine();
		if (line.equals(YOUAREWELCOME)) {
		    this.OUTPUT.writeBytes(WHOISTHERE + "\n");
		    int size = Integer.parseInt(this.INPUT.readLine());
		    for (int i = 0; i < size; i++) {
			if (this.INPUT.readLine().equals(NEWPLAYER)) {
			    String message = this.INPUT.readLine();
			    this.addNewPlayers(new StringBuilder().builderKeyPlayer(message),
				    new StringBuilder().builderModel(message), new StringBuilder().builderName(message),
				    new StringBuilder().builderPosition(message));
			}
		    }
		    this.establishedConnection = true;
		}
	    }
	} catch (IOException e) {// TODO catch
	    System.out.println("eccezioni nello start");
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    /** The player closed connection with server */
    @Override
    public void endConnection() {
	try {
	    this.OUTPUT.writeBytes(CLOSE + "\n");
	    this.OUTPUT.writeBytes(IAM + nameModel + "\n");
	    this.establishedConnection = false;
	} catch (IOException e) {// TODO catch
	    System.out.println("eccezioni nel end");
	}
    }

    /** the player communicates his position */
    @Override
    public void communicationState() {
	try {
	    this.OUTPUT.writeBytes(this.lineToSend + "\n");
	} catch (IOException e) {// TODO catch
	    System.out.println("eccezioni nel communicationState");
	}
    }

    /** This Method communicates an Player Updates */
    public void notifyUpdate(Vector3f walk, Vector3f view, int life, boolean attack, Vector3f location, int score) {
	try {
	    this.lineToSend = new StringBuilder().builderString(walk, view, location, life, attack, this.IAM,
		    this.nameModel, this.namePlayer, score);
	    this.OUTPUT.writeBytes(SENDSTATE + "\n");

	} catch (IOException e) {// TODO catch
	    System.out.println("eccezioni nel notifyUpdate");
	}
    }

    /** This Method communicates any player has left the multyplayer */
    public void communicateExitPlayer() {
	try {
	    String player = INPUT.readLine();
	    this.removeModel(player);
	} catch (IOException e) {// TODO cathc
	    System.out.println("eccezioni nel communicateExitPlayer");
	    // TODO da gestire
	}
    }

    /** This Method Updates state of a Player */
    public void statePlayer() {
	try {

	    String line = this.INPUT.readLine();
	    if (!new StringBuilder().checkString(line))
		return;
	    String key = new StringBuilder().builderKeyPlayer(line);

	    if (GameManager.getIstance().getPlayers().get(key) != null) {
		((NodeEnemyPlayers) GameManager.getIstance().getPlayers().get(key)).setState(line);
	    }

	} catch (IOException e) {// TODO catch
	    e.printStackTrace();
	    System.out.println("eccezioni nel statePlayer");
	}
    }

    /** This Method return Player IP address */
    @Override
    public String ipAddress() {
	try {
	    return InetAddress.getLocalHost().getHostAddress();
	} catch (UnknownHostException e) {
	    return "127.0.0.1";
	}
    }

    /** This Method communicates that there is a new player */
    public void communicationNewPlayer() {
	try {
	    String line = this.INPUT.readLine();
	    this.addNewPlayers(new StringBuilder().builderKeyPlayer(line), new StringBuilder().builderModel(line),
		    new StringBuilder().builderName(line), new StringBuilder().builderPosition(line));
	} catch (IOException e) {// TODO catch
	    System.out.println("eccezioni nel communicationNewPlayer");
	}
    }

    /**
     * This Method set the Start Position for a Player where there aren't enemy
     * or obstacles
     */
    public void bornPosition(Node scene) {
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(this.nameModel);
	float x = (float) ((Math.random() * GameManager.getIstance().getWorldXExtent()) * 0.25);
	float z = (float) ((Math.random() * GameManager.getIstance().getWorldZExtent()) * 0.25);

	while (!GameManager.getIstance().isWalkable(x, z)) {
	    x = (float) ((Math.random() * GameManager.getIstance().getWorldXExtent()) * 0.25);
	    z = (float) ((Math.random() * GameManager.getIstance().getWorldZExtent()) * 0.25);

	}
	Vector3f position = new Vector3f(x, GameManager.getIstance().getTerrainQuad().getHeight(new Vector2f(x, z)), z);
	spatial.setLocalTranslation(position);
	GameManager.getIstance().setNodeThief(new NodeThief(spatial, position, true));
	GameManager.getIstance().addModel(GameManager.getIstance().getNodeThief());
	GameManager.getIstance().addScorePlayer(GameManager.getIstance().getNodeThief());
	GameManager.getIstance().getNodeThief().setSinglePlayer(false);
	GameManager.getIstance().getNodeThief().setCam(this.cam, this.inputManager);
	GameManager.getIstance().getMultiplayer().loadNifty();
	GameManager.getIstance().getNodeThief().setName(namePlayer);
	GameManager.getIstance().sortScorePlyer();
	scene.attachChild(GameManager.getIstance().getNodeThief());
	this.setKey();
    }

    /** This Method add a Player and his Model in the Game's Terrain */
    public void addNewPlayers(String name, String model, String player, Vector3f location) {

	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(model);
	spatial.setLocalTranslation(location);
	NodeCharacter players = new NodeEnemyPlayers(spatial, new Vector3f(1.5f, 4.4f, 80f), location, LIFENUMBER,
		DAMAGE, name);
	players.addCharacterControl();
	GameManager.getIstance().addModelEnemy(players);
	GameManager.getIstance().addModel(players);
	players.addPhysicsSpace();
	players.setName(player);
	GameManager.getIstance().addScorePlayer(players);
	GameManager.getIstance().getBullet().getPhysicsSpace().add(players);
	GameManager.getIstance().addPlayes(name, players);
	GameManager.getIstance().addNotifyStateModel(new NotifyStateModel(true, players));
	GameManager.getIstance().sortScorePlyer();
    }

    /** This Method remove a Model in the Game's Terrain */
    public void removeModel(String key) {

	GameManager.getIstance()
		.addNotifyStateModel(new NotifyStateModel(false, GameManager.getIstance().getPlayers().get(key)));
	GameManager.getIstance().removePlayers(key);
	GameManager.getIstance().removeModel(key);

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
	GameManager.getIstance().getApplication().getInputManager().addMapping(chatBox,
		new KeyTrigger(KeyInput.KEY_RETURN));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, run, attack1, attack2, toggleRotate, chatBox,
		"sendMessage");
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, run, rotateClockwise, rotateCounterClockwise);
    }

    /** Thread's Method */
    @Override
    public void run() {

	try {
	    this.startConnection();
	    while (this.establishedConnection) {
		final String message = this.INPUT.readLine();
		System.out.println("client: " + message);
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
		else if (message.equals(SENDMESSAGE))
		    this.riceivedMessage();
	    }
	    this.socket.close();
	    this.INPUT.close();
	    this.OUTPUT.close();
	} catch (IOException e) {// TODO catch
	    System.out.println("eccezioni nel run");
	}
    }

    /** this method get player's name */
    public String getNamePlayer() {
	return namePlayer;
    }

    /** this method get model's name */
    public String getNameModel() {
	return nameModel;
    }

    /** this method get terrain's name */
    public String getNameTerrain() {
	return nameTerrain;
    }

    /** this method set terrain's name */
    public void setNameTerrain(String nameTerrain) {
	this.nameTerrain = nameTerrain;
    }

}
