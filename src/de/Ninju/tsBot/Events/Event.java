package de.Ninju.tsBot.Events;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import com.github.theholywaffle.teamspeak3.api.VirtualServerProperty;
import com.github.theholywaffle.teamspeak3.api.event.ChannelCreateEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDeletedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelDescriptionEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ChannelPasswordChangedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.PrivilegeKeyUsedEvent;
import com.github.theholywaffle.teamspeak3.api.event.ServerEditedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3Listener;
import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.Ninju.tsBot.Main.ChannelHistory;
import de.Ninju.tsBot.Main.Load;


public class Event {
	
	static HashMap<Integer, Long> icons = new HashMap<>();

	public static void loadEvents() {
		Utils.loadChannels();
		Load.api.registerAllEvents();
		Load.api.addTS3Listeners(new TS3Listener() {
			
			
			@Override
			public void onTextMessage(TextMessageEvent e) {
				Client c = Load.api.getClientInfo(e.getInvokerId());
				if(e.getMessage().equalsIgnoreCase("!nopoke")) {
					if(c.isInServerGroup(10)) {
						Load.api.removeClientFromServerGroup(10, c.getDatabaseId());
					}else {
						Load.api.addClientToServerGroup(10, c.getDatabaseId());
					}
				}
				if(e.getMessage().equalsIgnoreCase("!nomsg")) {
					if(c.isInServerGroup(11)) {
						Load.api.removeClientFromServerGroup(11, c.getDatabaseId());
					}else {
						Load.api.addClientToServerGroup(11, c.getDatabaseId());
					}
				}
				
			}
			
			@Override
			public void onServerEdit(ServerEditedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPrivilegeKeyUsed(PrivilegeKeyUsedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onClientMoved(ClientMovedEvent e) {
				Client c = Load.api.getClientInfo(e.getClientId());
				Utils.deleteChannel();
				ChannelHistory history = Load.clientChannelHistory.get(c.getId());
				history.addChannel();
				if(history.isChannelhopping() == 2) {
					Load.api.pokeClient(c.getId(), "Es scheint, als würdest du Channelhoppen, bitte unterlasse das!");
				}else if(history.isChannelhopping() == 1) {
					Load.api.banClient(c.getId(), 30*60, "Autoban: Channelhopping");
				}
				if(e.getTargetChannelId() == 5) {
					Utils.support(c);
				}
				if(e.getTargetChannelId() == 9 || e.getTargetChannelId() == 10) {
					Utils.createChannel(c, Load.api.getChannelInfo(c.getChannelId()).getName());
				}
				
			}
			
			@Override
			public void onClientLeave(ClientLeaveEvent e) {
				Load.afkMover();
				int id = e.getClientId();
				if(Load.onlineSups.contains(id)) {
					Load.updateSupport();
				}
				Map<VirtualServerProperty, String> property = new HashMap<VirtualServerProperty, String>();
				property.put(VirtualServerProperty.VIRTUALSERVER_NAME, "Ninju Test Server ("+Load.api.getClients().size()+"/"
				        +Load.api.getServerInfo().getMaxClients()+")");
				Load.api.editServer(property);
				Load.api.deleteIcon(icons.get(id));
				icons.remove(id);
				Utils.deleteChannel();
			}
			
			@Override
			public void onClientJoin(ClientJoinEvent e) {
				Load.afkMover();
				Client c = Load.api.getClientInfo(e.getClientId());
				Load.api.sendPrivateMessage(c.getId(), "Willkommen auf dem TS³Server [B]"+c.getNickname()+"[/B]!");
				if(c.isInServerGroup(9)) {
					Load.updateSupport();
				}
				Load.clientChannelHistory.put(c.getId(), new ChannelHistory());
				Map<VirtualServerProperty, String> property = new HashMap<VirtualServerProperty, String>();
				property.put(VirtualServerProperty.VIRTUALSERVER_NAME, "Ninju Test Server ("+Load.api.getClients().size()+"/"
				        +Load.api.getServerInfo().getMaxClients()+")");
				Load.api.editServer(property);
				if(!c.isServerQueryClient()) {
					try {
						URL url = new URL("https://minotar.net/avatar/"+c.getNickname()+"/16.png");
						InputStream in = new BufferedInputStream(url.openStream());
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						byte[] buf = new byte[1024];
						int i = 0;
						while(-1 != (i = in.read(buf))) {
							out.write(buf, 0 , i);
						}
						in.close();
						out.close();
						byte[] response = out.toByteArray();
						long iconID = Load.api.uploadIconDirect(response);
						Load.api.addClientPermission(c.getDatabaseId(), "i_icon_id", (int) iconID, false);
						icons.put(c.getId(), iconID);
					}catch(IOException e1) {}
				}
			}
			
			@Override
			public void onChannelPasswordChanged(ChannelPasswordChangedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChannelMoved(ChannelMovedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChannelEdit(ChannelEditedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChannelDescriptionChanged(ChannelDescriptionEditedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChannelDeleted(ChannelDeletedEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onChannelCreate(ChannelCreateEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}
}
