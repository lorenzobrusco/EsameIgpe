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
import control.GameManager;
import multiPlayer.protocols.CommunicationProtocol;

public class Client extends Thread implements CommunicationProtocol {

    private final static String ADDRESS = "lorenzobrusco92.ddns.net"; // TODO
								      // passare
								      // ip a
								      // mano
    private final static int PORT = 8080;
    private final static String KNOCK = "knock knock";
    private final static String HOWAREYOU = "who are you?";
    private final static String YOUAREWELCOME = "ok, you're welcome";
    private final static String TRYAGAIN = "try again";
    private final static String CLOSE = "close connection";
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
    public final static int FILE_SIZE = 6022386;
    private final static String PATH = "assets" + File.separator + "MultiPlayer" + File.separator;
    private final String IAM;
    private final Socket socket;
    private final BufferedReader INPUT;
    private final DataOutputStream OUTPUT;
    private boolean establishedConnection;
    private final String namePlayer;

    public Client(final String namePlayer) throws UnknownHostException, IOException {
	this.socket = new Socket(ADDRESS, PORT);
	this.establishedConnection = false;
	this.namePlayer = namePlayer;
	this.INPUT = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
	this.OUTPUT = new DataOutputStream(this.socket.getOutputStream());
	this.IAM = this.ipAddress();
    }

    @Override
    public void startConnection() {
	try {
	    if(this.INPUT.readLine().equals(HAVEYOUTHISTERRAIN)){
		final String nameTerrain = this.INPUT.readLine();
		final File file = new File(PATH + nameTerrain);
		if(file.exists())
		    this.OUTPUT.writeBytes(YESIHAVE + "\n");
		else{
		    this.OUTPUT.writeBytes(NOIHAVENT + "\n");
		    if(this.INPUT.readLine().equals(STARTSENDMETERRAIN)){
			this.fileRecieved();
			if(this.INPUT.readLine().equals(ENDSENDMETERRAIN)){
;			    //TODO RICEVUTO TUTTO
			}
		    }
		}
	    }
	    this.OUTPUT.writeBytes(KNOCK + "\n");
	    if (this.INPUT.readLine().equals(HOWAREYOU))
		this.OUTPUT.writeBytes(IAM + "\n");
	    if (this.INPUT.readLine().equals(YOUAREWELCOME))
		this.establishedConnection = true;
	    else if (this.INPUT.readLine().equals(TRYAGAIN))
		this.startConnection(); // try again
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
		this.OUTPUT.writeBytes(POSITION + GameManager.getIstance().getNodeThief().getLocalTranslation() + "\n");
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDPOSITION))
		this.OUTPUT.writeBytes(LIFE + GameManager.getIstance().getNodeThief().getLIFE() + "\n");
	    if (this.INPUT.readLine().equals(ACNOWLEDGEDLIFE))
		this.OUTPUT.writeBytes(ENDSENDSTATE + "\n");
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

	while (this.establishedConnection) {
	    // TODO scambio di informazioni
	    this.communicationState();
	}
    }

    public void fileRecieved() {
	
	FileOutputStream fileOutputStream = null;
	BufferedOutputStream bufferedOutputStream = null;
	try {
	    byte[] mybytearray = new byte[FILE_SIZE];
	    InputStream is = this.socket.getInputStream();
	    fileOutputStream = new FileOutputStream("");
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

    public String getNamePlayer() {
	return namePlayer;
    }

}
