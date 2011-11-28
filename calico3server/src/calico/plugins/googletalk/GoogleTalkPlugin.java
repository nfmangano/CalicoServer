package calico.plugins.googletalk;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;

import calico.ProcessQueue;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import calico.plugins.*;
import calico.plugins.events.*;
import calico.plugins.events.clients.ClientConnect;
import calico.plugins.events.scraps.*;

public class GoogleTalkPlugin extends AbstractCalicoPlugin implements CalicoPlugin
{

	
	private XMPPConnection connection = null;
	private Presence presence = null;
	
	public GoogleTalkPlugin()
	{
		super();

		PluginInfo.name = "GoogleTalkPlugin";
		PluginInfo.author = "mdempsey";
		PluginInfo.info = "Google Talk Interface";
		PluginInfo.url = "http://google.com/";
		
	}//
	
	
	void sendMessage(String username, String message)
	{
		Message msg = new Message(username, Message.Type.chat);
		msg.setBody(message);
		connection.sendPacket(msg);
		
		// Make the packet
//		CalicoPacket packet = new CalicoPacket(null);
		// HANDLE THE PACKET
//		ProcessQueue.receive("command", null, packet);

		
		debug("Message sent");
	}
	
	
	public void onPluginStart()
	{
		RegisterAdminCommand("gtalk_send","receiveChatCommand");
		try
		{
			
			// connect to gtalk server
			ConnectionConfiguration connConfig = new ConnectionConfiguration(GetConfigString("plugin.googletalk.server"), GetConfigInt("plugin.googletalk.port"), GetConfigString("plugin.googletalk.service"));
			connection = new XMPPConnection(connConfig);
			connection.connect();
			
			// login with username and password
			connection.login(GetConfigString("plugin.googletalk.username"), GetConfigString("plugin.googletalk.password"));
			
			// set presence status info
			presence = new Presence(Presence.Type.available);
			connection.sendPacket(presence);
			
			FirePluginEvent(new CalicoEvent());
			
			// Send a message
			//sendMessage("mrdempsey@gmail.com", "Calico GTalk Plugin Active");
			
			GTalkPacketListener packetListener = new GTalkPacketListener(this);
			connection.addPacketListener(packetListener, null);
		}
		catch(Exception e)
		{
			System.out.println("GTALK EXCEPTION");
			e.printStackTrace();
		}
	}
	
	public void onPluginEnd()
	{
		System.out.println("GTALK SHUTDOWN");
		// set presence status to unavailable
		presence = new Presence(Presence.Type.unavailable);
		connection.sendPacket(presence);
	}
	
	public void onException(Exception e)
	{
		
	}
	
	
	
	public void receiveChatCommand(PluginCommandParameters params, StringBuilder output)
	{
		//FireEvent(new ScrapReload(1L));
		sendMessage("mrdempsey@gmail.com", params.getString(0));
	}
	
	
	
	public void onClientConnect(ClientConnect event)
	{
		//sendMessage("mrdempsey@gmail.com", "Client has joined");
	}
	
	public void onScrapCreate(ScrapCreate event)
	{
		System.out.println("RECEIVED: "+event.getClass().getCanonicalName());
	}


	@Override
	public Class<?> getNetworkCommandsClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
