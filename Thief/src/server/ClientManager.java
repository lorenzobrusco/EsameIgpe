package server;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import com.jme3.math.Vector3f;
import multiPlayer.format.FormatIP;
import multiPlayer.format.StringBuilder;
import multiPlayer.protocols.CommunicationProtocol;

/**
 * 
 * this class handles connection
 *
 */

public class ClientManager extends Thread implements CommunicationProtocol {

    /** server */
    private final Server server;
    /** Socket of communication with client */
    private Socket socket;
    /** Player IP Address */
    private String address;
    /** Player Name */
    private String nameClient;
    /** name of the player character */
    private String nameModel;
    /** it's used to make key */
    private String player;
    /** start position when player has born */
    private Vector3f startPosition;
    /** current player's position */
    private Vector3f currentPosition;
    /** Writer for client */
    private final BufferedReader INPUT;
    /** Reader for client */
    private final DataOutputStream OUTPUT;
    /** connection established with client */
    private boolean establishedConnection;
    /** notify new player */
    private boolean newPlayer;
    /** protocols communications with the client */
    private final static String KNOCK = "knock knock";
    private final static String WHOAREYOU = "who are you?";
    private final static String WHOISTHERE = "tell me, who is there ?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String CLOSE = "close connection";
    private final static String NEWPLAYER = "it's arrive a new player";
    private final static String SENDSTATE = "send your state";
    private final static String PLAYER = "the player: ";
    private final static String DELETE = "delete this player ";
    private final static String SENDMESSAGE = "Can I send a message?";

    /** constructor */
    public ClientManager(Server server, Socket socket) throws IOException {
	this.server = server;
	this.establishedConnection = false;
	this.newPlayer = false;
	this.address = new String();
	this.socket = socket;
	this.INPUT = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	this.OUTPUT = new DataOutputStream(this.socket.getOutputStream());
    }

    /** Server connect with client */
    @Override
    public void startConnection() {
	try {
	    this.OUTPUT.writeBytes(this.server.getTERRAIN() + "\n");
	    if (this.INPUT.readLine().equals(KNOCK))
		this.OUTPUT.writeBytes(WHOAREYOU + "\n");
	    String line = this.INPUT.readLine();
	    this.address = new StringBuilder().builderAddress(line);
	    this.player = new StringBuilder().builderKeyPlayer(line);
	    this.nameModel = new StringBuilder().builderModel(line);
	    this.startPosition = new StringBuilder().builderPosition(line);
	    this.nameClient = new StringBuilder().builderName(line);
	    this.currentPosition = this.startPosition;
	    if (new FormatIP(this.address).itIsCorrectFormat()) {
		this.OUTPUT.writeBytes(YOUAREWELCOME + "\n");
		if (this.INPUT.readLine().equals(WHOISTHERE)) {
		    this.establishedConnection = true;
		    this.OUTPUT.writeBytes(this.server.getPlayers().size() + "\n");
		    for (ClientManager manager : this.server.getPlayers()) {
			this.communicationNewPlayer(manager.address, manager.nameModel, manager.nameClient,
				manager.startPosition);
			manager.communicationNewPlayer(this.address, this.nameModel, this.nameClient,
				this.startPosition);
		    }
		}
		this.server.addPlayer(this);
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    /** server closed connection with client */
    @Override
    public void endConnection() {
	try {
	    this.OUTPUT.writeBytes(CLOSE + "\n");
	    String client = this.INPUT.readLine();
	    this.communicateExitPlayer(client);
	    this.establishedConnection = false;
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    /** server received client's state */
    @Override
    public void communicationState() {
	try {

	    this.OUTPUT.writeBytes(SENDSTATE + "\n");

	    String line = this.INPUT.readLine();
	    
	    if (!new StringBuilder().checkString(line))
		return;

	    final String key = new StringBuilder().builderKeyPlayer(line);
	    
	    final Vector3f walkdirection = new StringBuilder().builderWalk(line);

	    final Vector3f viewdirection = new StringBuilder().builderView(line);

	    final Vector3f position = new StringBuilder().builderPosition(line);

	    final int life = new StringBuilder().builderLife(line);

	    final boolean attack = new StringBuilder().builderAttack(line);

	    final int score = new StringBuilder().builderScore(line);

	    for (ClientManager manager : this.server.getPlayers()) {
		if (manager != this)// TODO non mandare messaggio a me stesso
		    manager.statePlayer(key, walkdirection, viewdirection, position, life, attack, score);
	    }

	} catch (IOException e) {
	    System.out.println("client : connection");
	}
    }

    /** This Method communicates any player has left the multyplayer */
    public void communicateExitPlayer(String player) {

	for (ClientManager manager : this.server.getPlayers()) {
	    if (manager != this)
		manager.sendPlayerToDelete(player);
	}

    }

    /** this method send player's name to delete */
    public void sendPlayerToDelete(String player) {
	try {
	    this.OUTPUT.writeBytes(DELETE + "\n");
	    this.OUTPUT.writeBytes(player + "\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /** This Method Updates state of a Player */
    public void statePlayer(String address, Vector3f walk, Vector3f view, Vector3f position, int life, boolean attack,
	    int score) {
	try {
	    this.OUTPUT.writeBytes(PLAYER + "\n");
	    String line = new StringBuilder().builderString(walk, view, position, life, attack, address, "",
		    this.nameClient, score);
	    this.OUTPUT.writeBytes(line + "\n");
	} catch (IOException e) {
	    e.printStackTrace();
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
    public void communicationNewPlayer(String name, String model, String nameClient, Vector3f location) {
	try {
	    this.OUTPUT.writeBytes(NEWPLAYER + "\n");
	    String line = new StringBuilder().builderString(new Vector3f(), new Vector3f(), location, 0, false, name,
		    model, nameClient, 0);
	    System.out.println(line);
	    this.OUTPUT.writeBytes(line + "\n");
	    this.newPlayer = false;
	} catch (IOException e) {
	    // TODO catch
	    e.printStackTrace();
	}

    }

    /** This method Riceived a Message from Player's to send any players */
    public void riceivedMessage() {
	try {
	    String player = this.INPUT.readLine();
	    String message = this.INPUT.readLine();
	    for (ClientManager manager : server.getPlayers()) {
		manager.sendMessage(player, message);
	    }

	} catch (IOException e) {
	    // TODO catch
	    e.printStackTrace();
	}

    }

    /** This method Sends a Message for Player's ChatBox */
    public void sendMessage(String player, String message) {
	try {
	    this.OUTPUT.writeBytes(SENDMESSAGE + "\n");
	    this.OUTPUT.writeBytes(player + "\n");
	    this.OUTPUT.writeBytes(message + "\n");
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    /** this method notify everyone that new players is arrive */
    public void notifyAllNewPlayer() {
	for (ClientManager manager : this.server.getPlayers()) {
	    manager.setNewPlayer(true);
	    manager.communicationNewPlayer(this.player, this.nameModel, this.nameClient, this.startPosition);
	}
    }

    /** thread's run */
    @Override
    public void run() {
	try {
	    this.startConnection();
	    while (this.establishedConnection) {
		final String message = this.INPUT.readLine();
		if (message.equals(SENDSTATE))
		    this.communicationState();
		if (message.equals(CLOSE))
		    this.endConnection();
		if (message.equals(SENDMESSAGE))
		    this.riceivedMessage();
	    }
	    this.INPUT.close();
	    this.OUTPUT.close();
	    this.socket.close();
	    this.server.removePlayer(this);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    /** this method get server */
    public Server getServer() {
	return server;
    }

    /** this method get address */
    public String getAddress() {
	return this.address;
    }

    /** this method set address */
    public void setAddress(String nameClient) {
	this.address = nameClient;
    }

    /** this method get client's name */
    public String getNameClient() {
	return nameClient;
    }

    /** this method set client's name */
    public void setNameClient(String nameClient) {
	this.nameClient = nameClient;
    }

    /** this method get model's name */
    public String getNameModel() {
	return nameModel;
    }

    /** this method set model's name */
    public void setNameModel(String nameModel) {
	this.nameModel = nameModel;
    }

    /** this method get start position */
    public Vector3f getStartPosition() {
	return startPosition;
    }

    /** this method set start position */
    public void setStartPosition(Vector3f startPosition) {
	this.startPosition = startPosition;
    }

    /** this method check if there is a new player */
    public boolean isNewPlayer() {
	return newPlayer;
    }

    /** this method set new player */
    public void setNewPlayer(boolean newPlayer) {
	this.newPlayer = newPlayer;
    }

    /** this method get current position */
    public Vector3f getCurrentPosition() {
	return currentPosition;
    }

    /** this method set current position */
    public void setCurrentPosition(Vector3f currentPosition) {
	this.currentPosition = currentPosition;
    }
}
