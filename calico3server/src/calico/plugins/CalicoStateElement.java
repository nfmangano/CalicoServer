package calico.plugins;

import calico.networking.netstuff.CalicoPacket;

public interface CalicoStateElement {

	public CalicoPacket[] getCalicoStateElementUpdatePackets();
	
}
