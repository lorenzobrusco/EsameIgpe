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
    private final static String WHOIS = "who is?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String TRYAGAIN = "try again";
    private final static String CLOSE = "close connection";
    private final static String NEWPLAYER = "it's arrive a new player";
    private final static String SENDSTATE = "send your state";
    private final static String ACNOWLEDGEDCLOSECONNECTION = "ok, closing connection";
    private final static String ACNOWLEDGEDPOSITION = "ok, acnwoledged position";
    private final static String ACNOWLEDGEDLIFE = "ok, acnwoledged life";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String YESIHAVE = "yes, I have";
    private final static String PATH = "assets" + File.separator + "MultiPlayer" + File.separator;

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
	    System.out.println("ciao");
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
		System.out.println(address);
		this.OUTPUT.writeBytes(YOUAREWELCOME + "\n");
		this.establishedConnection = true;
	    } else
		this.OUTPUT.writeBytes(TRYAGAIN + "\n");
	    System.out.println(establishedConnection);
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void endConnection() {
	try {
	    if (this.INPUT.readLine().equals(CLOSE)) {
		this.INPUT.readLine().equals(ACNOWLEDGEDCLOSECONNECTION);
		this.socket.close();
		this.INPUT.close();
		this.OUTPUT.close();
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void communicationState() {
	try {
	    this.OUTPUT.writeBytes(SENDSTATE + "\n");
	    this.OUTPUT.writeBytes(ACNOWLEDGEDPOSITION + "\n");
	    this.OUTPUT.writeBytes(ACNOWLEDGEDLIFE + "\n");
	    System.out.println(this.INPUT.readLine());
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

    @Override
    public void communicationNewPlayer(String name, String model, String x, String y, String z) {
	try {
	    this.OUTPUT.writeBytes(NEWPLAYER + "\n");
	    if (INPUT.readLine().equals(WHOIS)) {
		this.OUTPUT.writeBytes(name + "\n");
		this.OUTPUT.writeBytes(model + "\n");
		this.OUTPUT.writeBytes(x + "\n");
		this.OUTPUT.writeBytes(y + "\n");
		this.OUTPUT.writeBytes(z + "\n");
		this.OUTPUT.writeBytes(name + "\n");
		this.newPlayer = false;
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

    }

    @Override
    public void run() {

	this.startConnection();
	while (this.establishedConnection) {
	    if (this.newPlayer) {
		this.notifyAllNewPlayer();
	    }
	    this.communicationState();
	}
    }

    public void notifyAllNewPlayer() {
	System.out.println(nameClient + "si � collegato");
	for (ClientManager manager : this.server.getPlayers()) {
	    manager.setNewPlayer(true);
	    manager.communicationNewPlayer(this.address, this.nameClient, String.valueOf(this.startPosition.x),
		    String.valueOf(this.startPosition.y), String.valueOf(this.startPosition.z));
	}
    }

    public boolean fileTransfer(Socket client) {

	BufferedInputStream outputTerrain = null;
	OutputStream outputStream = null;
	try {
	    File file = new File(PATH + this.server.getTERRAIN() + ".j3o");
	    System.out.println("mando");

	    byte[] terrain = new byte[(int) file.length()];
	    FileInputStream inputTerrain = new FileInputStream(file);
	    outputTerrain = new BufferedInputStream(inputTerrain);
	    outputTerrain.read(terrain, 0, terrain.length);
	    outputStream = client.getOutputStream();
	    outputStream.write(terrain, 0, terrain.length);
	    outputStream.flush();
	    System.out.println("Done.");
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

    public void notifyNewPlayers() {

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
