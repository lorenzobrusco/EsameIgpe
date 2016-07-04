package multiPlayer;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.Callable;
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
    private final String MOUSE = "mouse";
    private final String CHATBOX = "chatBox";
    private final String TOGGLEROTATE = "toggleRotate";
    private final String ATTACK1 = "Attack1";
    private final String ATTACK2 = "Attack2";
    private final String RUN = "Run";
    private final String ROTATECLOCKWISE = "rotateClockwise";
    private final String ROTATECOUNTERCLOCKWISE = "rotateCounterClockwise";
    private final String PAUSE = "Pause";
    private final String SENDMESSAGECHAT = "sendMessage";
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
    /** connection established with server */
    private boolean establishedConnection;
    /** new state */
    private String lineToSend;

    /** constructor */
    public Client(final String namePlayer, final String nameModel, final String address,
	    final InputManager inputManager, final Camera cam, int port) throws UnknownHostException, IOException {
	this.PORT = port;
	this.socket = new Socket(address, PORT);
	this.establishedConnection = false;
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
	    /** time to loading models */
	    while (!GameManager.getIstance().getMultiplayer().isCreated())
		super.sleep(500);
	    /***/
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
		    this.establishedConnection = true;
		    this.OUTPUT.writeBytes(WHOISTHERE + "\n");
		    int size = Integer.parseInt(this.INPUT.readLine());
		    for (int i = 0; i < size; i++) {
			if (this.INPUT.readLine().equals(NEWPLAYER)) {
			    String message = this.INPUT.readLine();
			    this.addNewPlayers(new StringBuilder().builderKeyPlayer(message),
				    new StringBuilder().builderModel(message), new StringBuilder().builderName(message),
				    new StringBuilder().builderPosition(message),
				    new StringBuilder().builderLife(message));
			}
		    }
		}
	    }
	} catch (IOException e) {
	    this.exception();
	} catch (InterruptedException e) {
	    this.exception();
	}
    }

    /** The player closed connection with server */
    @Override
    public void endConnection() {
	try {
	    if (this.socket.isConnected()) {
		this.OUTPUT.writeBytes(CLOSE + "\n");
		String line = IAM + nameModel;
		this.OUTPUT.writeBytes(line + "\n");
		this.establishedConnection = false;
	    }
	} catch (IOException e) {
	    GameManager.getIstance().setPaused(false);
	    GameManager.getIstance().getNifty().fromXml("Interface/Xml/screenMenu.xml", "start",
		    GameManager.getIstance().getMultiplayer());
	    GameManager.getIstance().getApplication().getInputManager().reset();
	    GameManager.getIstance().getApplication().getInputManager().setCursorVisible(true);
	    GameManager.getIstance().getApplication().getViewPort().clearProcessors();
	    GameManager.getIstance().getNodeThief().getCamera().setEnabled(false);
	}
    }

    /** the player communicates his position */
    @Override
    public void communicationState() {
	try {
	    this.OUTPUT.writeBytes(this.lineToSend + "\n");
	} catch (IOException e) {
	    this.exception();
	}
    }

    /** This Method communicates an Player Updates */
    public synchronized void notifyUpdate(Vector3f walk, Vector3f view, int life, boolean attack, Vector3f location,
	    int score) {
	try {

	    final String line = new StringBuilder().builderString(walk, view, location, life, attack, this.IAM,
		    this.nameModel, this.namePlayer, score);
	    if (!this.lineToSend.equals(line)) {
		this.lineToSend = line;
		this.OUTPUT.writeBytes(SENDSTATE + "\n");
	    }
	} catch (IOException e) {
	    this.exception();
	} catch (NullPointerException e) {
	    this.exception();
	}
    }

    /** This Method communicates any player has left the multyplayer */
    public void communicateExitPlayer() {
	try {
	    String player = INPUT.readLine();
	    this.removeModel(player);
	} catch (IOException e) {
	    this.exception();
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
	} catch (IOException e) {
	    this.exception();
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
		    new StringBuilder().builderName(line), new StringBuilder().builderPosition(line),
		    new StringBuilder().builderLife(line));
	} catch (IOException e) {
	    this.exception();
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
	GameManager.getIstance().getApplication().enqueue(new Callable<Void>() {
	    public Void call() {
		scene.attachChild(GameManager.getIstance().getNodeThief());
		return null;
	    }
	});
	this.setKey();
    }

    /** This Method add a Player and his Model in the Game's Terrain */
    public void addNewPlayers(String name, String model, String player, Vector3f location, int life) {
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(model);
	spatial.setLocalTranslation(location);
	NodeCharacter players = new NodeEnemyPlayers(spatial, new Vector3f(1.5f, 4.4f, 80f), location, life, DAMAGE,
		name);
	players.addCharacterControl();
	GameManager.getIstance().addModelEnemy(players);
	GameManager.getIstance().addModel(players);
	players.addPhysicsSpace();
	players.setName(player);
	GameManager.getIstance().addScorePlayer(players);
	GameManager.getIstance().getBullet().getPhysicsSpace().add(players);
	GameManager.getIstance().addPlayes(name, players);
	GameManager.getIstance().getApplication().enqueue(new Callable<Void>() {
	    public Void call() {
		GameManager.getIstance().getTerrain().attachChild(players);
		return null;
	    }
	});
	GameManager.getIstance().sortScorePlyer();
    }

    /** This Method remove a Model in the Game's Terrain */
    public void removeModel(String key) {
	final NodeCharacter player = GameManager.getIstance().getPlayers().get(key);
	GameManager.getIstance().getApplication().enqueue(new Callable<Node>() {
	    public Node call() {

		if (GameManager.getIstance().getTerrain().getChildren().contains(player))
		    GameManager.getIstance().getTerrain().detachChild(player);
		else if (((Node) GameManager.getIstance().getTerrain().getChild(0)).getChildren().contains(player))
		    ((Node) GameManager.getIstance().getTerrain().getChild(0)).detachChild(player);
		return GameManager.getIstance().getTerrain();
	    }
	});
	GameManager.getIstance().removePlayers(key);
	GameManager.getIstance().removeModel(key);
	GameManager.getIstance().removeScorePlayer(player);
    }

    /** This method Sends a Message for Player's ChatBox */
    public void sendMessage(String displayedText) {
	try {
	    this.OUTPUT.writeBytes(SENDMESSAGE + "\n");
	    this.OUTPUT.writeBytes(this.namePlayer + "\n");
	    this.OUTPUT.writeBytes(displayedText + "\n");
	} catch (IOException e) {
	    this.exception();
	}

    }

    /** This method Riceived a Message from Player's ChatBox and Print */
    public void riceivedMessage() {
	try {
	    final String namePlayer = INPUT.readLine();
	    final String messageChatBox = INPUT.readLine();
	    new FormatStringChat(namePlayer).printMessageChatBox(messageChatBox);
	} catch (IOException e) {
	    this.exception();
	}

    }

    /** This method set Keyboard Command for MultiPlayer */
    public void setKey() {
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.RUN,
		new KeyTrigger(KeyInput.KEY_W));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.ATTACK1,
		new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.ATTACK2,
		new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.ROTATECLOCKWISE,
		new KeyTrigger(KeyInput.KEY_A));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.ROTATECOUNTERCLOCKWISE,
		new KeyTrigger(KeyInput.KEY_D));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.MOUSE,
		new KeyTrigger(KeyInput.KEY_LCONTROL));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.CHATBOX,
		new KeyTrigger(KeyInput.KEY_TAB));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.SENDMESSAGECHAT,
		new KeyTrigger(KeyInput.KEY_RETURN));
	GameManager.getIstance().getApplication().getInputManager().addMapping(this.PAUSE,
		new KeyTrigger(KeyInput.KEY_ESCAPE));
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().actionListener, this.RUN, this.ATTACK1, this.ATTACK2,
		this.TOGGLEROTATE, this.CHATBOX, this.PAUSE, this.SENDMESSAGECHAT);
	GameManager.getIstance().getApplication().getInputManager().addListener(
		GameManager.getIstance().getNodeThief().analogListener, this.RUN, this.ROTATECLOCKWISE,
		this.ROTATECOUNTERCLOCKWISE);
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
		else if (message.equals(SENDMESSAGE))
		    this.riceivedMessage();
	    }
	    this.socket.close();
	    this.INPUT.close();
	    this.OUTPUT.close();
	    this.quitGame();
	} catch (IOException e) {
	    this.exception();
	}catch (NullPointerException e) {
	    this.exception();
	}
	
    }

    /** this method clear mapping */
    public void quitGame() {
	GameManager.getIstance().setPaused(false);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.RUN);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ATTACK1);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ATTACK2);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ROTATECLOCKWISE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.ROTATECOUNTERCLOCKWISE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.MOUSE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.CHATBOX);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.PAUSE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.TOGGLEROTATE);
	GameManager.getIstance().getApplication().getInputManager().deleteMapping(this.SENDMESSAGECHAT);
    }

    /** this method is called when server and client aren't synchronized */
    private void exception() {
	GameManager.getIstance().getApplication().enqueue(new Callable<Void>() {
	    @Override
	    public Void call() {
		Client.this.establishedConnection = false;
		GameManager.getIstance().getMultiplayer().reset();
		return null;
	    }
	});
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

    /** this method check if it's connected and return it */
    public boolean isConnected() {
	return this.establishedConnection;
    }

}
