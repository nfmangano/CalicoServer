package calico.plugins.events.clients;

import calico.clients.Client;
import calico.plugins.events.CalicoEvent;

public class ClientDisconnect extends CalicoEvent
{
	private Client client = null;
	
	public ClientDisconnect(Client client)
	{
		this.client = client;
	}
	
	public Client getClient()
	{
		return this.client;
	}
}
