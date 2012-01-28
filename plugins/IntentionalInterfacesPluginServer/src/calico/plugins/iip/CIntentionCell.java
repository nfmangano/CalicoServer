package calico.plugins.iip;

import java.awt.Point;

import calico.networking.netstuff.CalicoPacket;

public class CIntentionCell
{
	long uuid;
	long canvas_uuid;
	Point location;
	
	public CIntentionCell(long uuid, long canvas_uuid, int x, int y)
	{
		this.uuid = uuid;
		this.canvas_uuid = canvas_uuid;
		this.location = new Point(x, y);
	}
	
	public long getId()
	{
		return uuid;
	}
	
	public long getCanvasId()
	{
		return canvas_uuid;
	}
	
	public void setLocation(int x, int y)
	{
		location.x = x;
		location.y = y;
	}
	
	public CalicoPacket getState()
	{
		return CalicoPacket.getPacket(
				IntentionalInterfacesNetworkCommands.CIC_CREATE,
				uuid,
				canvas_uuid,
				location.x,
				location.y
		);
	}
}
