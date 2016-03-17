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
import multiPlayer.protocols.CommunicationProtocol;
import server.formatIP.Format;

public class ClientManager extends Thread implements CommunicationProtocol {

    private final Server server;
    private Socket socket;
    private String nameClient;
    private final static String STARTSENDMETERRAIN = "start send me terrain";
    private final static String ENDSENDMETERRAIN = "end send me terrain";
    private final static String KNOCK = "knock knock";
    private final static String HOWAREYOU = "who are you?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String TRYAGAIN = "try again";
    private final static String CLOSE = "close connection";
    private final static String SENDSTATE = "send your state";
    private final static String ACNOWLEDGEDCLOSECONNECTION = "ok, closing connection";
    private final static String ACNOWLEDGEDPOSITION = "ok, acnwoledged position";
    private final static String ACNOWLEDGEDLIFE = "ok, acnwoledged life";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String YESIHAVE = "yes, I have";
    private final static String PATH = "assets" + File.separator + "MultiPlayer" + File.separator;
    private final BufferedReader INPUT;
    private final DataOutputStream OUTPUT;
    private boolean establishedConnection;

    public ClientManager(Server server, Socket socket) throws IOException {
	this.server = server;
	this.establishedConnection = false;
	this.nameClient = new String();
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
		this.OUTPUT.writeBytes(HOWAREYOU + "\n");
	    this.nameClient = this.INPUT.readLine();

	    if (new Format(this.nameClient).itIsCorrectFormat()) {
		System.out.println(nameClient);
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
    public void run() {

	this.startConnection();
	while (this.establishedConnection) {
	    this.communicationState();
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
	    System.out.println("Done.");
	    return true;
	} catch (IOException e) {
	    e.printStackTrace();
	    return false;
	} finally {
	    if (outputTerrain != null && outputStream != null)
		try {
		    outputTerrain.close();
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
	return this.nameClient;
    }
}
