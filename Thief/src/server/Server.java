package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.HashMap;

import singlePlayer.model.NodeCharacter;

public class Server extends Thread {

    private final static int PORT = 8080;
    private final ServerSocket server;
    private final String TERRAIN;
    private final AbstractMap<String, NodeCharacter> players;
    private boolean start;

    public Server(final String path) throws UnknownHostException, IOException {
	this.server = new ServerSocket(PORT);
	this.TERRAIN = path;
	this.players = new HashMap<>();
	this.start = true;
    }

    @Override
    public void run() {

	while (this.start) {

	    try {
		Socket client = server.accept();
		ClientManager clientManager = new ClientManager(this, client);
		clientManager.start();
		this.addPlayer(clientManager);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

    }

    public void stopServer() {
	this.start = false;
    }

    public void addPlayer(ClientManager clientManager) {
	if (!this.players.containsKey(clientManager.getAddress()))
	    this.players.put(clientManager.getAddress(), clientManager.makePlayer());

    }

    public void remuvePlayer(ClientManager clientManager) {
	if (this.players.containsKey(clientManager.getAddress()))
	    this.players.remove(clientManager.getAddress());
    }

    public String getTERRAIN() {
	return TERRAIN;
    }

    public static void main(String[] args) {
	try {
	    new Server("mountain").start();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
