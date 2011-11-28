package calico.networking;


import calico.networking.netstuff.*;
import calico.components.*;
import calico.admin.*;
import calico.clients.*;
import calico.uuid.*;
import calico.*;
import java.net.*;


public class Packet
{
	private Client client;
	private CalicoPacket packet;
	
	public Packet(Client c, CalicoPacket p)
	{
		client = c;
		packet = p;
	}

	public Client getClient()
	{
		return client;
	}

	public CalicoPacket getData()
	{
		return packet;
	}
	
}

