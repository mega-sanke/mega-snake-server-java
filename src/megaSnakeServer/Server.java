package megaSnakeServer;

import event.ClientEvent;
import event.ClientListener;
import tcp.message.Message;
import tcp.messagesConection.ChatClient;
import tcp.messagesConection.ChatServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Server extends ChatServer implements ClientListener {
	private List<Player> players;
	private int currentID;
	private int lastGateID = 0;

	public Server() throws IOException {
		super();
		players = new ArrayList<>();
		addCLientListener(this);
		currentID = -1;
	}

	public List<Player> getPlayers() {
		return players;
	}

	@Override
	public void onClientConnected(ClientEvent e) {  
		ChatClient client = e.getClient();
		Player p = new Player(this, client, ++currentID);

	}

	public int getLastGateID() {
		return lastGateID;
	}

	public void increasLastGateID(int d) {
		this.lastGateID += d;
	}

	public void broadCast(Message m, Player p) {
		for (Player player : players) {
			if(player != p)
				player.send(m);
		}
	}
	
	public void addPlayer(Player p){
		players.add(p);
	}

	public static void main(String[] args) throws IOException {
		new Server().start();
	}
	
}
