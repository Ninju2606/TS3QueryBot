package de.Ninju.tsBot.Events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.ChannelProperty;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.Ninju.tsBot.Main.Load;

public class Utils {
	
	private static HashMap<String, ArrayList<ChannelInfo>> channels = new HashMap<>();
	
	public static void loadChannels() {
		channels.put("minecraft", new ArrayList<ChannelInfo>());
		channels.put("fortnite", new ArrayList<ChannelInfo>());
	}
	
	public static void createChannel(Client c, String name) {
		int channelid;
		int channelorder = getChannelOrder(name);
		if(channelorder == 1) {
			channelid = Load.api.getChannelByNameExact(name, true).getId();
		}else {
			channelid = Load.api.getChannelByNameExact(name+ " • №" + (channelorder-1), true).getId();
		}
		Map<ChannelProperty, String> property = new HashMap<ChannelProperty, String>();
		property.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
		property.put(ChannelProperty.CHANNEL_ORDER, String.valueOf(channelid));
		ChannelInfo ci = Load.api.getChannelInfo(Load.api.createChannel(name+ " • №" + channelorder, property));
		Load.api.moveClient(c.getId(), ci.getId());
		//Load.api.setClientChannelGroup(10, c.getId(), c.getDatabaseId());
		channels.get(name.replace("» ", "").toLowerCase()).add(ci);
	}
	
	private static int getChannelOrder(String name) {
		int i = 1;
		ArrayList<Integer> temp = new ArrayList<>();
		for(ChannelInfo info : channels.get(name.replace("» ", "").toLowerCase())) {
			int current = Integer.valueOf(info.getName().split("№")[1]);
			temp.add(current);
			if(i == current) {
				i++;
				while(temp.contains(i)) {
					i++;
				}
			}
		}
		return i;
	}
	
	public static void deleteChannel() {
		
		ChannelInfo ci = null;
		for(String game : channels.keySet()) {
			for(ChannelInfo info : channels.get(game)) {
				Channel ch = Load.api.getChannelByNameExact(info.getName(), true);
				if(ch.getTotalClients() == 0) {
					ci = info;
				}
			}
		}
		if(ci != null) {
			String name = ci.getName().replace("» ", "").split(" • ")[0].toLowerCase();
			channels.get(name).remove(ci);
			Load.api.deleteChannel(ci.getId());
		}
	}

}
