package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.AbstractMap;
import java.util.HashMap;
import com.jme3.scene.Node;

public class Server extends Thread {

    private final static int PORT = 8080;
    private final ServerSocket server;
    private final Node TERRAIN;
    private final AbstractMap<String, ClientManager> players;

    public Server(final String namePlayer, final Node TERRAIN) throws UnknownHostException, IOException {
	this.server = new ServerSocket(PORT);
	this.TERRAIN = TERRAIN;
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

    public Node getTERRAIN() {
	return TERRAIN;
    }

}
