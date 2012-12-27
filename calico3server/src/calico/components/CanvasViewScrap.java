package calico.components;

import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;

public class CanvasViewScrap extends CGroup {

	private long targetCanvasUUID;

	public CanvasViewScrap(long uuid, long cuid, long targetCanvas)
	{
		super(uuid, cuid);
		
		this.targetCanvasUUID = targetCanvas;


		networkLoadCommand = NetworkCommand.CANVASVIEW_SCRAP_LOAD;
	}
	
	public CalicoPacket[] getUpdatePackets(long uuid, long cuid, long puid, int dx, int dy, boolean captureChildren) {
		CalicoPacket[] packet = super.getUpdatePackets(uuid, cuid, puid, dx, dy, captureChildren);
		
		packet[0].putLong(targetCanvasUUID);
		
		return packet;
	}
	
}
