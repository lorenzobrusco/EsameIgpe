package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Server extends Thread {

    private final static int PORT = 8080;
    private final ServerSocket server;
    private final String TERRAIN;
    private final Collection<ClientManager> players;
    private Boolean start;

    public Server(final String path) throws UnknownHostException, IOException {
	this.server = new ServerSocket(PORT);
	this.TERRAIN = path;
	this.players = new ConcurrentLinkedQueue<>();
	this.start = true;
    }

    @Override
    public void run() {

	while (this.start) {

	    try {
	    	
		Socket client = server.accept();
		ClientManager clientManager = new ClientManager(this, client);
		this.newPlayer();
		clientManager.start();
	    } 
	    catch(SocketTimeoutException timeoutException){
	    	System.out.println("time out");
	    }
	    catch (IOException e) {

	    }
	}
	
    }

    public void newPlayer() {
	for (ClientManager managers : this.players) {
	    managers.setNewPlayer(true);
	}
    }

    public void stopServer() {
    	new Thread(){
    		@Override
    		public void run() {
    			try{
    				Server.this.start = false;
    				Server.this.server.close();
    			}
    			catch (Exception e) {// TODO: handle exception
				}
    		}
    	}.run();
	}

    public synchronized void addPlayer(ClientManager clientManager) {
	boolean exist = false;
	for (ClientManager manager : this.players) {
	    if (manager.getAddress().equals(clientManager.getAddress()))
		exist = true;
	}
	if (!exist)
	    this.players.add(clientManager);
    }

    public synchronized void removePlayer(ClientManager clientManager) {
	this.players.remove(clientManager);
    }

    public synchronized Collection<ClientManager> getPlayers() {
	return this.players;
    }

    public String getTERRAIN() {
	return TERRAIN;
    }

	public Boolean isStart() {	
		return this.start;
	}

	public void setStart(Boolean start) {
		this.start = start;
	}
    
    



}
