package calico.plugins.events.clients;

import calico.clients.Client;
import calico.plugins.events.CalicoEvent;

public class ClientConnect extends CalicoEvent
{
	private Client client = null;
	
	public ClientConnect(Client client)
	{
		this.client = client;
	}
	
	public Client getClient()
	{
		return this.client;
	}
}
