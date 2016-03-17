package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.HashMap;

public class Server extends Thread {

    private final static int PORT = 8080;
    private final ServerSocket server;
    private final String TERRAIN;
    private final AbstractMap<String, ClientManager> players;

    public Server(final String path) throws UnknownHostException, IOException {
	this.server = new ServerSocket(PORT);
	this.TERRAIN = path;
	this.players = new HashMap<>();
    }

    @Override
    public void run() {

	while (true) {

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

    public void addPlayer(ClientManager clientManager) {
	if (!this.players.containsKey(clientManager.getAddress()))
	    this.players.put(clientManager.getAddress(), clientManager);

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
