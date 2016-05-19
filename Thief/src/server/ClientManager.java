package server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import com.jme3.math.Vector3f;
import multiPlayer.format.FormatIP;
import multiPlayer.format.StringBuilder;
import multiPlayer.protocols.CommunicationProtocol;

public class ClientManager extends Thread implements CommunicationProtocol {

    private final Server server;
    private Socket socket;
    private String address;
    private String nameClient;
    private String nameModel;
    private String player;
    private Vector3f startPosition;
    private Vector3f currentPosition;
    private final BufferedReader INPUT;
    private final DataOutputStream OUTPUT;
    private boolean establishedConnection;
    private boolean newPlayer;
    private int currentTime;
    private String currentState;
    private final static String STARTSENDMETERRAIN = "start send me terrain";
    private final static String ENDSENDMETERRAIN = "end send me terrain";
    private final static String KNOCK = "knock knock";
    private final static String WHOAREYOU = "who are you?";
    private final static String WHOISTHERE = "tell me, who is there ?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String TRYAGAIN = "try again";
    private final static String CLOSE = "close connection";
    private final static String NEWPLAYER = "it's arrive a new player";
    private final static String SENDSTATE = "send your state";
    private final static String PLAYER = "the player: ";
    private final static String SYNCPLAYERS = "send my position";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String YESIHAVE = "yes, I have";
    private final static String DELETE = "delete this player ";
    private final static String PATH = "assets/MultiPlayer/";
    private final static String SENDMESSAGE = "Can I send a message?";

    public ClientManager(Server server, Socket socket) throws IOException {
	this.server = server;
	this.establishedConnection = false;
	this.newPlayer = false;
	this.address = new String();
	this.currentState = "";
	this.socket = socket;
	this.INPUT = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	this.OUTPUT = new DataOutputStream(this.socket.getOutputStream());
    }

    @Override
    public void startConnection() {
	try {

	    this.OUTPUT.writeBytes(HAVEYOUTHISTERRAIN + "\n");
	    this.OUTPUT.writeBytes(this.server.getTERRAIN() + "\n");
	    if (!this.INPUT.readLine().equals(YESIHAVE)) {
		this.OUTPUT.writeBytes(STARTSENDMETERRAIN + "\n");
		this.fileTransfer(socket);
		this.OUTPUT.writeBytes(ENDSENDMETERRAIN + "\n");
	    }
	    if (this.INPUT.readLine().equals(KNOCK))
		this.OUTPUT.writeBytes(WHOAREYOU + "\n");
	    String line = this.INPUT.readLine();
	    if (!new StringBuilder().checkString(line)) {
		this.OUTPUT.writeBytes(TRYAGAIN + "\n");
		return;
	    }
	    this.address = new StringBuilder().builderAddress(line);
	    this.player = new StringBuilder().builderKeyPlayer(line);
	    this.nameModel = new StringBuilder().builderModel(line);
	    this.startPosition = new StringBuilder().builderPosition(line);
	    this.currentPosition = this.startPosition;
	    if (new FormatIP(this.address).itIsCorrectFormat()) {
		this.OUTPUT.writeBytes(YOUAREWELCOME + "\n");
		this.establishedConnection = true;
		if (this.INPUT.readLine().equals(WHOISTHERE)) {
		    this.OUTPUT.writeBytes(this.server.getPlayers().size() + "\n");
		    for (ClientManager manager : this.server.getPlayers()) {

			this.communicationNewPlayer(manager.address, manager.nameModel, manager.nameClient,
				manager.startPosition);
			manager.communicationNewPlayer(this.address, this.nameModel, this.nameClient,
				this.startPosition);
		    }

		}
		this.server.addPlayer(this);
	    } else
		this.OUTPUT.writeBytes(TRYAGAIN + "\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void endConnection() {
	try {
	    this.OUTPUT.writeBytes(CLOSE + "\n");
	    String client = this.INPUT.readLine();
	    System.out.println("player to exit: " + client);
	    this.communicateExitPlayer(client);
	    this.establishedConnection = false;
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void communicationState() {
	try {

	    this.OUTPUT.writeBytes(SENDSTATE + "\n");

	    String line = this.INPUT.readLine();
	    
	    if (!new StringBuilder().checkString(line))
		return;

	    this.currentState = line;
	    
	    final String key = new StringBuilder().builderKeyPlayer(line);

	    final Vector3f walkdirection = new StringBuilder().builderWalk(line);

	    final Vector3f viewdirection = new StringBuilder().builderView(line);

	    this.currentPosition = new StringBuilder().builderPosition(line);

	    final int life = new StringBuilder().builderLife(line);

	    final boolean attack = new StringBuilder().builderAttack(line);

	    final int score = new StringBuilder().builderScore(line);

	    // TODO
	    // System.out.println("stato: " + address + " --- " + walkdirection
	    // + " ------ " + viewdirection);

	    for (ClientManager manager : this.server.getPlayers()) {
		if (manager != this)
		    manager.statePlayer(key, walkdirection, viewdirection, life, attack, score);
	    }

	    ;// TODO metodo che comunica a tutti lo spostamento
	} catch (IOException e) {
	    System.out.println("client : connection");
	} catch (NumberFormatException ex) {
	    System.out.println("client : cast float");
	}
    }

    public void communicateExitPlayer(String player) {

	for (ClientManager manager : this.server.getPlayers()) {
	    if (manager != this)
		manager.sendPlayerToDelete(player);
	}

    }

    public void sendPlayerToDelete(String player) {
	try {
	    this.OUTPUT.writeBytes(DELETE + "\n");
	    this.OUTPUT.writeBytes(player + "\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void statePlayer(String address, Vector3f walk, Vector3f view, int life, boolean attack, int score) {
	try {
	    this.OUTPUT.writeBytes(PLAYER + "\n");
	    String line = new StringBuilder().builderString(walk, view, new Vector3f(), life, attack, address, "",
		    this.nameClient, score);

	    this.OUTPUT.writeBytes(line + "\n");
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    // TODO inizio sincronizzazione col server

    public void syncWithServer() {
	for (ClientManager manager : this.server.getPlayers()) {
	    if (manager != this)
		manager.syncPlayers(player, currentPosition);
	}
	this.currentTime = (int) System.currentTimeMillis();

    }

    public void syncPlayers(String player, Vector3f local) {

	try {

	    this.OUTPUT.writeBytes(SYNCPLAYERS + "\n");
	    String line = new StringBuilder().builderString(new Vector3f(), new Vector3f(), local, 0, false, player, "",
		    this.nameClient, 0);
	    this.OUTPUT.writeBytes(line + "\n");

	} catch (IOException e) {
	    // TODO
	}
    }

    // TODO fine sincronizzazione col server

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

    // TODO
    public void communicationNewPlayer(String name, String model, String nameClient, Vector3f location) {
	try {
	    this.OUTPUT.writeBytes(NEWPLAYER + "\n");
	    String line = new StringBuilder().builderString(new Vector3f(), new Vector3f(), location, 0, false, name,
		    model, nameClient, 0);
	    this.OUTPUT.writeBytes(line + "\n");
	    this.newPlayer = false;
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    public void riceivedMessage() {
	try {
	    String player = this.INPUT.readLine();
	    String message = this.INPUT.readLine();
	    for (ClientManager manager : server.getPlayers()) {
		manager.sendMessage(player, message);
	    }

	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

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

    public void notifyAllNewPlayer() {
	for (ClientManager manager : this.server.getPlayers()) {
	    manager.setNewPlayer(true);
	    manager.communicationNewPlayer(this.player, this.nameModel, this.nameClient, this.startPosition);
	}
    }

    public boolean fileTransfer(Socket client) {

	BufferedInputStream outputTerrain = null;
	OutputStream outputStream = null;
	try {
	    File file = new File(PATH + this.server.getTERRAIN() + ".j3o");
	    byte[] terrain = new byte[(int) file.length()];
	    FileInputStream inputTerrain = new FileInputStream(file);
	    outputTerrain = new BufferedInputStream(inputTerrain);
	    outputTerrain.read(terrain, 0, terrain.length);
	    outputStream = client.getOutputStream();
	    outputStream.write(terrain, 0, terrain.length);
	    outputStream.flush();

	    return true;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	} finally {
	    try {
		outputStream.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
    }

    // TODO run
    @Override
    public void run() {
	try {
	    this.startConnection();
	    this.currentTime = (int) System.currentTimeMillis();
	    while (this.establishedConnection) {
		if ((int) System.currentTimeMillis() - this.currentTime >= 5000) {
		    this.syncWithServer();
		}
		final String message = this.INPUT.readLine();
		if (message.equals(SENDSTATE))
		    this.communicationState();
		if (message.equals(CLOSE))
		    this.endConnection();
		if (message.equals(SENDMESSAGE))
		    this.riceivedMessage();
	    }
	    this.socket.close();
	    this.INPUT.close();
	    this.OUTPUT.close();
	    this.server.removePlayer(this);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public Server getServer() {
	return server;
    }

    public String getAddress() {
	return this.address;
    }

    public void setAddress(String nameClient) {
	this.address = nameClient;
    }

    public String getNameClient() {
	return nameClient;
    }

    public void setNameClient(String nameClient) {
	this.nameClient = nameClient;
    }

    public String getNameModel() {
	return nameModel;
    }

    public void setNameModel(String nameModel) {
	this.nameModel = nameModel;
    }

    public Vector3f getStartPosition() {
	return startPosition;
    }

    public void setStartPosition(Vector3f startPosition) {
	this.startPosition = startPosition;
    }

    public boolean isNewPlayer() {
	return newPlayer;
    }

    public void setNewPlayer(boolean newPlayer) {
	this.newPlayer = newPlayer;
    }

    public Vector3f getCurrentPosition() {
	return currentPosition;
    }

    public void setCurrentPosition(Vector3f currentPosition) {
	this.currentPosition = currentPosition;
    }

}
