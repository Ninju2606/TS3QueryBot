package de.Ninju.tsBot.Main;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.TS3Query.FloodRate;
import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.Ninju.tsBot.Events.Event;

public class Load {

	public static final TS3Config config = new TS3Config();
	public static TS3Query query;
	public static TS3Api api;

	
	public static ArrayList<Integer> onlineSups = new ArrayList<>();
	public static HashMap<Integer, ChannelHistory> clientChannelHistory = new HashMap<>();

	public static void main(String[] args) {
		config.setHost("YOUR IP");
		config.setFloodRate(FloodRate.UNLIMITED);
		config.setDebugLevel(Level.ALL);
		query = new TS3Query(config);
		query.connect();
		api = new TS3Api(query);
		ApiLogin.apiLogin();
		api.selectVirtualServerByPort(9987);
		api.setNickname("TS3 Querybot - Video");
		Event.loadEvents();
		updateSupport();
		updateChannelHistory();
		afkMover();
		System.out.println("Der Bot ist gestartet!");
	}

	public static void updateSupport() {
		onlineSups.clear();
		for(Client c : api.getClients()) {
			if(c.isInServerGroup(9)) {
				onlineSups.add(c.getId());
			}

		}
		Map<ChannelProperty, String> property = new HashMap<ChannelProperty, String>();
		if(onlineSups.size() == 0) {
			if(!api.getChannelInfo(5).getName().contains("Warte auf Support [CLOSED]")) {
				property.put(ChannelProperty.CHANNEL_NAME, "Warte auf Support [CLOSED]");
				property.put(ChannelProperty.CHANNEL_MAXCLIENTS, "0");
				property.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
				Load.api.editChannel(5, property);
				property.clear();
			}
		}else {
			if(!api.getChannelInfo(5).getName().contains("Warte auf Support ["+onlineSups.size()+"]")) {
				property.put(ChannelProperty.CHANNEL_NAME, "Warte auf Support ["+onlineSups.size()+"]");
				property.put(ChannelProperty.CHANNEL_MAXCLIENTS, "1");
				property.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "1");
				Load.api.editChannel(5, property);
				property.clear();
			}
		}
	}
	public static void afkMover() {
		for(Client c : api.getClients()) {
			if(c.getIdleTime() >= 15*60*1000) {
				api.moveClient(c.getId(), 7);
			}
		}
	}
	
	public static void updateChannelHistory() {
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				for(@SuppressWarnings("rawtypes") Map.Entry e : clientChannelHistory.entrySet()) {
					((ChannelHistory) e.getValue()).remChannel();
				}
				System.out.println("ChannelHistories updated");
			}
		}, 60*1000, 60*1000);
	}

}
