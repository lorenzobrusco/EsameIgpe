package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;

public class Server extends Thread {

    private final static int PORT = 8080;
    private final ServerSocket server;
    private final String TERRAIN;
    private final Collection<ClientManager> players;
    private boolean start;

    public Server(final String path) throws UnknownHostException, IOException {
	this.server = new ServerSocket(PORT);
	this.TERRAIN = path;
	this.players = new ArrayList<>();
	this.start = true;
    }

    @Override
    public void run() {

	while (this.start) {

	    try {
		Socket client = server.accept();
		ClientManager clientManager = new ClientManager(this, client);
		clientManager.start();
		this.newPlayer();
		this.addPlayer(clientManager);
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}

    }

    public void newPlayer(){
	for(ClientManager managers : this.players){
	    managers.setNewPlayer(true);
	}
    }
    
    public void stopServer() {
	this.start = false;
    }

    public synchronized void addPlayer(ClientManager clientManager) {
	this.players.add(clientManager);
    }

    public synchronized void remuvePlayer(ClientManager clientManager) {
	this.players.remove(clientManager);
    }
    
    public synchronized Collection<ClientManager> getPlayers(){
	return this.players;
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
