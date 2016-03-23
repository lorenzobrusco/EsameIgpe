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

import multiPlayer.protocols.CommunicationProtocol;
import server.formatIP.Format;

public class ClientManager extends Thread implements CommunicationProtocol {

    private final Server server;
    private Socket socket;
    private String address;
    private String nameClient;
    private String nameModel;
    private Vector3f startPosition;
    private final BufferedReader INPUT;
    private final DataOutputStream OUTPUT;
    private boolean establishedConnection;
    public boolean newPlayer;
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
    private final static String ENDSENDSTATE = "end send your state";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String YESIHAVE = "yes, I have";
    private final static String DELETE = "delete this player ";
    private final static String PATH = "assets/MultiPlayer/";

    public ClientManager(Server server, Socket socket) throws IOException {
	this.server = server;
	this.establishedConnection = false;
	this.newPlayer = false;
	this.address = new String();
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
	    this.address = this.INPUT.readLine();
	    this.nameClient = this.INPUT.readLine();
	    this.nameModel = this.INPUT.readLine();
	    final float x = Float.parseFloat(this.INPUT.readLine());
	    final float y = Float.parseFloat(this.INPUT.readLine());
	    final float z = Float.parseFloat(this.INPUT.readLine());
	    this.startPosition = new Vector3f(x, y, z);
	    if (new Format(this.address).itIsCorrectFormat()) {
		this.OUTPUT.writeBytes(YOUAREWELCOME + "\n");
		this.establishedConnection = true;
		if (this.INPUT.readLine().equals(WHOISTHERE)) {
		    this.OUTPUT.writeBytes(this.server.getPlayers().size() + "\n");
		    for (ClientManager manager : this.server.getPlayers()) {
			manager.communicationNewPlayer(this.address, this.nameModel,
				String.valueOf(this.startPosition.x), String.valueOf(this.startPosition.y),
				String.valueOf(this.startPosition.z));
			this.communicationNewPlayer(manager.address, manager.nameModel,
				String.valueOf(manager.startPosition.x), String.valueOf(manager.startPosition.y),
				String.valueOf(manager.startPosition.z));
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
	    this.establishedConnection = false;

	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void communicationState() {
	try {

	    this.OUTPUT.writeBytes(SENDSTATE + "\n");

	    String address = this.INPUT.readLine();
	    address += this.INPUT.readLine();

	    final Vector3f walkdirection = new Vector3f(Float.parseFloat(INPUT.readLine()),
		    Float.parseFloat(INPUT.readLine()), Float.parseFloat(INPUT.readLine()));

	    final Vector3f viewdirection = new Vector3f(Float.parseFloat(INPUT.readLine()),
		    Float.parseFloat(INPUT.readLine()), Float.parseFloat(INPUT.readLine()));

	    final int life = Integer.parseInt(INPUT.readLine());

	    final boolean attack = Boolean.parseBoolean(INPUT.readLine());
	    // System.out.println("CMS: " + address + " --- " + walkdirection +
	    // " ------ "+ viewdirection);
	    if (INPUT.readLine().equals(ENDSENDSTATE)) {
		for (ClientManager manager : this.server.getPlayers()) {
		    manager.statePlayer(address, walkdirection, viewdirection, life, attack);
		}
	    }
	    ;// TODO metodo che comunica a tutti lo spostamento
	} catch (IOException e) {
	    System.out.println("client : connection");
	} catch (NumberFormatException ex) {
	    System.out.println("client : cast float");
	}
    }

    public void communicateExitPlayer(String player) {
	try {
	    this.OUTPUT.writeBytes(DELETE + "\n");
	    for (ClientManager manager : this.server.getPlayers()) {
		//TODO finire
	    }
	} catch (IOException e) {
	    // TODO da gestire
	}
    }

    public void statePlayer(String address, Vector3f walk, Vector3f view, int life, boolean attack) {
	try {
	    this.OUTPUT.writeBytes(PLAYER + "\n");
	    this.OUTPUT.writeBytes(address + "\n");
	    this.OUTPUT.writeBytes(walk.x + "\n");
	    this.OUTPUT.writeBytes(walk.y + "\n");
	    this.OUTPUT.writeBytes(walk.z + "\n");
	    this.OUTPUT.writeBytes(view.x + "\n");
	    this.OUTPUT.writeBytes(view.y + "\n");
	    this.OUTPUT.writeBytes(view.z + "\n");
	    this.OUTPUT.writeBytes(life + "\n");
	    this.OUTPUT.writeBytes(attack + "\n");
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

    public void communicationNewPlayer(String name, String model, String x, String y, String z) {
	try {
	    this.OUTPUT.writeBytes(NEWPLAYER + "\n");
	    this.OUTPUT.writeBytes(name + "\n");
	    this.OUTPUT.writeBytes(model + "\n");
	    this.OUTPUT.writeBytes(x + "\n");
	    this.OUTPUT.writeBytes(y + "\n");
	    this.OUTPUT.writeBytes(z + "\n");
	    this.newPlayer = false;
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void run() {
	try {
	    this.startConnection();
	    while (this.establishedConnection) {
		String message = this.INPUT.readLine();
		if (message.equals(SENDSTATE))
		    this.communicationState();
		if (message.equals(CLOSE))
		    this.endConnection();
	    }
	    this.socket.close();
	    this.INPUT.close();
	    this.OUTPUT.close();
	    this.server.remuvePlayer(this);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public void notifyAllNewPlayer() {
	for (ClientManager manager : this.server.getPlayers()) {
	    manager.setNewPlayer(true);
	    manager.communicationNewPlayer(this.address, this.nameModel, String.valueOf(this.startPosition.x),
		    String.valueOf(this.startPosition.y), String.valueOf(this.startPosition.z));

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

}
