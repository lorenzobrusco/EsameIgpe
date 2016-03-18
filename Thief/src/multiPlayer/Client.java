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

import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

import control.GameManager;
import multiPlayer.protocols.CommunicationProtocol;
import singlePlayer.model.NodeCharacter;
import singlePlayer.model.NodeEnemy;
import singlePlayer.model.NodeThief;

public class Client extends Thread implements CommunicationProtocol {

    private final static int PORT = 8080;
    private final static String KNOCK = "knock knock";
    private final static String WHOAREYOU = "who are you?";
    private final static String WHOIS = "who is?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String TRYAGAIN = "try again";
    private final static String CLOSE = "close connection";
    private final static String NEWPLAYER = "it's arrive a new player";
    private final static String SENDSTATE = "send your state";
    private final static String ENDSENDSTATE = "end send your state";
    private final static String ACNOWLEDGEDCLOSECONNECTION = "ok, closing connection";
    private final static String POSITION = "my position is: ";
    private final static String ACNOWLEDGEDPOSITION = "ok, acnwoledged position";
    private final static String LIFE = "my life is: ";
    private final static String ACNOWLEDGEDLIFE = "ok, acnwoledged life";
    private final static String HAVEYOUTHISTERRAIN = "have you this terrain?";
    private final static String STARTSENDMETERRAIN = "start send me terrain";
    private final static String ENDSENDMETERRAIN = "end send me terrain";
    private final static String YESIHAVE = "yes, I have";
    private final static String NOIHAVENT = "no, I haven't";
    private final static String PATH = "assets" + File.separator + "MultiPlayer" + File.separator;
    private final static String PATHMODEL = "Models" + File.separator + "Characters" + File.separator;
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

    public Client(final String namePlayer, final String nameModel, final String address)
	    throws UnknownHostException, IOException {
	this.socket = new Socket(address, PORT);
	this.establishedConnection = false;
	this.namePlayer = namePlayer;
	this.nameModel = PATHMODEL + nameModel + File.separator + nameModel + ".mesh.j3o";
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
		this.bornPosition();
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLocalTranslation().x + "\n");
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLocalTranslation().y + "\n");
		this.OUTPUT.writeBytes(GameManager.getIstance().getNodeThief().getLocalTranslation().z + "\n");
	    }
	    if (this.INPUT.readLine().equals(YOUAREWELCOME))
		this.establishedConnection = true;
	    else if (this.INPUT.readLine().equals(TRYAGAIN))
		this.startConnection();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void endConnection() {
	try {
	    this.OUTPUT.writeBytes(CLOSE + "\n");
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDCLOSECONNECTION)) {
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
	    if (this.INPUT.readLine().equals(SENDSTATE))
		this.OUTPUT.writeBytes(POSITION + 100 + "\n"); // TODO
							       // GameManager.getIstance().getNodeThief().getLocalTranslation()
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDPOSITION))
		this.OUTPUT.writeBytes(LIFE + 50 + "\n"); // TODO
							  // GameManager.getIstance().getNodeThief().getLIFE()
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDLIFE))
		this.OUTPUT.writeBytes(ENDSENDSTATE + "\n");
	    System.out.println("ho mandato le info");
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
	    if(INPUT.readLine().equals(NEWPLAYER))
		OUTPUT.writeBytes(WHOIS + "\n");
	    	this.addNewPlayers(INPUT.readLine(), INPUT.readLine(), INPUT.readLine(), INPUT.readLine(), INPUT.readLine());
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    @Override
    public void run() {

	this.startConnection();
	while (this.establishedConnection) {
	    this.communicationState();
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
		System.out.println("byte " + bytesRead);
		System.out.println("current " + current);
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

    public void bornPosition() {
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(this.nameModel);
	spatial.setLocalTranslation(new Vector3f(50, 0, 50));
	GameManager.getIstance().setNodeThief(new NodeThief(spatial));
	GameManager.getIstance().addModel(GameManager.getIstance().getNodeThief());
	GameManager.getIstance().getTerrain().attachChild(GameManager.getIstance().getNodeThief());
	GameManager.getIstance().getNodeThief().addCharacterControll();
	GameManager.getIstance().getBullet().getPhysicsSpace().add(GameManager.getIstance().getNodeThief());
    }
    
    public void addNewPlayers(String name, String model, String x, String y, String z) {
	Spatial spatial = GameManager.getIstance().getApplication().getAssetManager().loadModel(model);
	spatial.setLocalTranslation(new Vector3f(Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(z)));
	NodeCharacter players = new NodeEnemyPlayers(spatial, new Vector3f(1.5f, 4.4f, 80f), LIFENUMBER, DAMAGE);
	GameManager.getIstance().addModelEnemy((NodeEnemy) players);
	GameManager.getIstance().getTerrain().attachChild(players);
	GameManager.getIstance().addPlayes(name, players);
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

    public static void main(String[] args) {
	try {
	    new Client("lorenzo", "Jarvan", "160.97.123.113").start();
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    
}
