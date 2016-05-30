package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 
 * this class is server where players connect
 * 
 */

public class Server extends Thread {

    /** port to connect */
    private final int PORT;
    /** socket listing for new players */
    private final ServerSocket server;
    /** landspace shared for everyone */
    private final String TERRAIN;
    /** players connected */
    private final Collection<ClientManager> players;
    /** check if server is online */
    private Boolean start;

    /** builder */
    public Server(final String path, int port) throws UnknownHostException, IOException {
	this.PORT = port;
	this.server = new ServerSocket(PORT);
	this.TERRAIN = path;
	this.players = new ConcurrentLinkedQueue<>();
	this.start = true;
    }

    /** code's fragmet that thead runnig */
    @Override
    public void run() {
	while (this.start) {
	    try {
		final Socket client = server.accept();
		final ClientManager clientManager = new ClientManager(this, client);
		this.newPlayer();
		clientManager.start();
	    } catch (IOException e) {
	    }
	}
    }

    /** this method notify everyone that a new player is arrived */
    public void newPlayer() {
	for (ClientManager managers : this.players) {
	    managers.setNewPlayer(true);
	}
    }

    /** this method close server */
    public void stopServer() {
	new Thread() {
	    @Override
	    public void run() {
		try {
		    Server.this.start = false;
		    Server.this.server.close();
		} catch (Exception e) {
		}
	    }
	}.run();
    }

    /** this method add new player */
    public synchronized void addPlayer(ClientManager clientManager) {
	boolean exist = false;
	for (ClientManager manager : this.players) {
	    if (manager.getAddress().equals(clientManager.getAddress()))
		exist = true;
	}
	if (!exist)
	    this.players.add(clientManager);

    }

    /** this method remove a player */
    public synchronized void removePlayer(ClientManager clientManager) {
	this.players.remove(clientManager);
    }

    /** this method return players */
    public synchronized Collection<ClientManager> getPlayers() {
	return this.players;
    }

    /** this method get terrain */
    public String getTERRAIN() {
	return TERRAIN;
    }

    /** this method get start */
    public Boolean isStart() {
	return this.start;
    }

    /** this method set start */
    public void setStart(Boolean start) {
	this.start = start;
    }

}
