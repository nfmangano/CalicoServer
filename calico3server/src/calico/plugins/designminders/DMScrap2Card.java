package calico.plugins.designminders;

import calico.networking.netstuff.CalicoPacket;
import calico.plugins.events.CalicoEvent;

public class DMScrap2Card extends CalicoEvent
{
	public long uuid = 0L;
	
	
	public DMScrap2Card()
	{
		
	}
	
	
	public void getPacketData(CalicoPacket p)
	{
		this.uuid = p.getLong();
	}
	
}
