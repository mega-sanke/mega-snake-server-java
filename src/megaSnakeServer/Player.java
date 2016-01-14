package megaSnakeServer;

import event.MessageEvent;
import event.MessageListener;
import tcp.message.Message;
import tcp.message.Message.Target;
import tcp.message.Message.Type;
import tcp.messagesConection.ChatClient;

import java.util.Arrays;

public class Player implements MessageListener {
	
	private Server server;
	private ChatClient socket;
	private int id;
	private int wCount, hCount;
	private boolean[] winds = { false, false, false, false };

	
	
	public static final String FOOD = "food", DEAD = "dead", OK = "ok", GATE = "gate", START = "start", ID = "id",
			FIRST_CONNECT = "first", WIDTH = "first/width", HEIGHT = "first/height", NEIGHBOR_ADD = "neighbor/add",
			NEIGHBOR_ADD_WIND = "neighbor/add/wind", NEIGHBOR_ADD_COUNT = "neighbor/add/count",
			NEIGHBOR_ADD_SIZE = "neighbor/add/size", GATE_ID = "gate/id", GATE_PLYER_ID = "gate/plyer/id",
			GATE_PREV_MOVE = "gate/prev/move";

	public Player(Server server, ChatClient c, int id) {
		this.server = server;
		this.socket = c;
		this.id = id;
		socket.addMessageListener(this);
		Message m = Message.create(Target.BROADCAST, Type.DATA);
		m.putData(ID, id + "");
		send(m);
		if(server.getPlayers().isEmpty()){
			m = Message.create(Target.BROADCAST, Type.DATA);
			m.putData(START, " ");
			send(m);
		}
		
	}

	public int getFreeWind() {
		if(!haveFreeWalls())
			return -1;
		int windNum = (int) (Math.random() * 4);
		while (winds[windNum]) {
			windNum = (int) (Math.random() * 4);
		}
		return windNum;
	}
	
	public String concerWall(){
		int w = getFreeWind();
		if(w != -1){
			winds[w]  = true;
			switch (w) {
			case 0:
				return "NORTH";
			case 1:
				return "SOUTH";
			case 2:
				return "EAST";
			case 3:
				return "WEST";
			default:
				return null;
			}
		
		}
		return null;
	}

	public boolean haveFreeWalls() {
		return !Arrays.equals(winds, new boolean[] { true, true, true, true });
	}
	
	public void send(Message m){
		socket.send(m);
	}

	public int getID() {
		// TODO Auto-generated method stub
		return id;
	}

	@Override
	public void onReceive(MessageEvent e) {
		
		Message m = e.getMessage();
		if(m.getData(FIRST_CONNECT) != null && !server.getPlayers().isEmpty()){
			wCount = Integer.parseInt(m.getData(WIDTH));
			hCount = Integer.parseInt(m.getData(HEIGHT));
			Player p = server.getPlayers().get((int) (Math.random() * server.getPlayers().size()));
			while (!p.haveFreeWalls()) {
				p = server.getPlayers().get((int) (Math.random() * server.getPlayers().size()));
			}
			System.out.println(server.getPlayers());
			
			int wind_num = p.getFreeWind();
			
			p.winds[wind_num] = true;
			winds[wind_num + (int)Math.round(Math.pow(-1, wind_num % 2))] = true;
			
			String wind = "";
			
			switch (wind_num) {
			case 0:
				wind =  "NORTH";
				break;
			case 1:
				wind =  "SOUTH";
				break;
			case 2:
				wind =  "EAST";
				break;
			case 3:
				wind =  "WEST";
				break;
			default:
				wind =  null;
			}
					
			
			
			
			int wallSize = 0;
			
			if (wind.equals("NORTH") || wind.equals("SOUTH")) {
				wallSize = Math.min(p.wCount, wCount);
			} else {
				wallSize = Math.min(p.hCount, hCount);
			}
			Message m1 = Message.create(Target.BROADCAST, Type.DATA);
			m1.putData(NEIGHBOR_ADD, " ");
			m1.putData(NEIGHBOR_ADD_COUNT, wallSize + "");
			m1.putData(NEIGHBOR_ADD_SIZE, (server.getLastGateID() + 1) + "");
			m1.putData(NEIGHBOR_ADD_WIND, wind);
			send(m1);
			if(wind.equals("NORTH")){
				wind = "SOUTH";
			} else if(wind.equals("SOUTH")) {
				wind = "NORTH";
			} else if(wind.equals("WEST")){
				wind = "EAST";
			} else if(wind.equals("EAST")){
				wind  = "WEST";
			}
			
			Message m2 = Message.create(Target.BROADCAST, Type.DATA);
			m2.putData(NEIGHBOR_ADD, "");
			m2.putData(NEIGHBOR_ADD_COUNT, wallSize + "");
			m2.putData(NEIGHBOR_ADD_SIZE, (server.getLastGateID() + 1) + "");
			m2.putData(NEIGHBOR_ADD_WIND, wind);
			p.send(m2);
			server.increasLastGateID(wallSize);
			
			server.addPlayer(this);
		} else if(m.getData(FIRST_CONNECT) != null){
			wCount = Integer.parseInt(m.getData(WIDTH));
			hCount = Integer.parseInt(m.getData(HEIGHT));
			server.addPlayer(this);
		}
		
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return " " + id + " " + Arrays.toString(winds);
	}
}
