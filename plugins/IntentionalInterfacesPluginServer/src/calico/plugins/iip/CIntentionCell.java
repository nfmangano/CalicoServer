package calico.plugins.iip;

import java.awt.Point;

import calico.networking.netstuff.CalicoPacket;

public class CIntentionCell
{
	long uuid;
	long canvas_uuid;
	boolean inUse;
	Point location;

	public CIntentionCell(long uuid, long canvas_uuid, boolean inUse)
	{
		this.uuid = uuid;
		this.canvas_uuid = canvas_uuid;
		this.inUse = inUse;
		this.location = null;
	}

	public long getId()
	{
		return uuid;
	}

	public long getCanvasId()
	{
		return canvas_uuid;
	}

	public boolean isInUse()
	{
		return inUse;
	}

	public void setInUse(boolean inUse)
	{
		this.inUse = inUse;
	}

	public void setLocation(int x, int y)
	{
		if (location == null)
		{
			location = new Point();
		}
		
		location.x = x;
		location.y = y;
	}

	public CalicoPacket getState()
	{
		if (location == null)
		{
			return CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_CREATE, uuid, canvas_uuid, inUse, false, 0, 0);
		}
		else
		{
			return CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_CREATE, uuid, canvas_uuid, inUse, true, location.x, location.y);
		}
	}
}
