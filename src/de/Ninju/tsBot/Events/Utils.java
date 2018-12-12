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
	private static ArrayList<String> sChannels = new ArrayList<>();

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
		
		String cName = null;
		for(String name : sChannels) {
			Channel ch = Load.api.getChannelByNameExact(name, true);
			if(ch.getTotalClients() == 0) {
				cName = name;
				Load.api.deleteChannel(ch.getId());
			}
		}
		if(cName != null) {
			sChannels.remove(cName);
		}
	}
	
	public static void support(Client c) {
		Load.updateSupport();
		if(Load.onlineSups.size() >= 1) {
			Load.api.sendPrivateMessage(c.getId(), "Es wurden "+ Load.onlineSups.size() + " Supporter benachrichtigt.");
			boolean var = false;
			Map<ChannelProperty, String> property = new HashMap<ChannelProperty, String>();
			property.put(ChannelProperty.CHANNEL_MAXCLIENTS, "0");
			property.put(ChannelProperty.CHANNEL_FLAG_MAXCLIENTS_UNLIMITED, "0");
			property.put(ChannelProperty.CHANNEL_FLAG_SEMI_PERMANENT, "1");
			property.put(ChannelProperty.CHANNEL_DESCRIPTION, c.getUniqueIdentifier());
			property.put(ChannelProperty.CHANNEL_ORDER, "5");
			Channel ch = Load.api.getChannelByNameExact("» Support • "+c.getNickname(), true);
			if(ch != null) {
				ChannelInfo ci = Load.api.getChannelInfo(ch.getId());
				Map<ChannelProperty, String> p = new HashMap<ChannelProperty, String>();
				p.put(ChannelProperty.CHANNEL_NAME, "» Support • " + Load.api.getClientByUId(ci.getDescription()).getNickname());
				Load.api.editChannel(ci.getId(), p);
				sChannels.add(ci.getName());
				var = true;
			}
			ChannelInfo ci = Load.api.getChannelInfo(Load.api.createChannel("» Support • "+c.getNickname(), property));
			Load.api.addChannelPermission(c.getId(), "i_channel_needed_join_power", 35);
			Load.api.addChannelPermission(c.getId(), "i_channel_needed_delete_power", 75);
			Load.api.moveClient(c.getId(), ci.getId());
			if(!var) {
				sChannels.add(ci.getName());
			}
			for(int id : Load.onlineSups) {
				String name = c.getNickname().replaceAll("|", "%7C").replaceAll(" ", "%20");
				Load.api.sendPrivateMessage(id, "[URL=client://"+c.getId()+"/"+c.getUniqueIdentifier()+"~"+ name +"]"+c.getNickname()+"[/URL] braucht support.");
			}
		}else {
			Load.api.kickClientFromChannel(c.getId());
			Load.api.sendPrivateMessage(c.getId(), "Es ist aktuell kein Supporter online.");
		}
	}

}
