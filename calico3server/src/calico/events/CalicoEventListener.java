package calico.events;

import calico.clients.Client;
import calico.networking.netstuff.CalicoPacket;

public interface CalicoEventListener {
	
	public void handleCalicoEvent(int event, CalicoPacket p, Client client);

}
