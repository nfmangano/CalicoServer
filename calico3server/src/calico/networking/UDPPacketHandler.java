package calico.networking;

import java.net.*;

import org.apache.log4j.Logger;

import calico.*;
import calico.clients.*;
import calico.networking.netstuff.*;

public class UDPPacketHandler
{
	private static Logger logger = Logger.getLogger(UDPPacketHandler.class.getName());
	
	public static void setup()
	{
	
	}


	public static void receive(CalicoPacket packet, InetSocketAddress sender)
	{
		int command = ByteUtils.readInt(packet.getBuffer(), 0);
		receive(command, packet, sender);
	}

	public static void receive(int command, CalicoPacket pdata, InetSocketAddress sender)
	{
		// set it to AFTER the command
		pdata.setPosition(ByteUtils.SIZE_OF_INT);
		
		switch(command)
		{
			case NetworkCommand.UDP_CHALLENGE:UDP_CHALLENGE(pdata, sender);break;
		}
		
		
	}
	
	public static void send(InetSocketAddress recip, CalicoPacket packet)
	{
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	
	private static void UDP_CHALLENGE(CalicoPacket packet, InetSocketAddress sender)
	{
		long challenge = packet.getLong();
		
		int clientid = ClientManager.getClientFromChallenge(challenge);
		
		logger.debug("CLIENT "+clientid+" CHALLENGE PASS");
		
	}
	
	
	
	
}//END
