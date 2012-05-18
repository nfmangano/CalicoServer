package calico.plugins.iip;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import calico.components.CCanvas;
import calico.networking.netstuff.CalicoPacket;

public class CIntentionCell
{
	public static final String DEFAULT_TITLE = "<default>";

	long uuid;
	long canvas_uuid;
	final Point location;
	String title;
	final List<Long> intentionTypeIds = new ArrayList<Long>();

	public CIntentionCell(long uuid, long canvasId, boolean inUse)
	{
		this.uuid = uuid;
		this.canvas_uuid = canvasId;
		this.location = new Point();
		this.title = DEFAULT_TITLE;
	}

	public long getId()
	{
		return uuid;
	}

	public long getCanvasId()
	{
		return canvas_uuid;
	}
	
	public Point getLocation()
	{
		return location;
	}

	/**
	 * If different than the current location, set the location of the CIC and return true.
	 */
	public boolean setLocation(int x, int y)
	{
		if ((location.x == x) && (location.y == y))
		{
			return false;
		}

		location.x = x;
		location.y = y;

		return true;
	}

	public String getTitle()
	{
		return title;
	}

	public boolean hasUserTitle()
	{
		return !title.equals(DEFAULT_TITLE);
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void addIntentionType(long typeId)
	{
		intentionTypeIds.add(typeId);
	}

	public boolean hasIntentionType(long typeId)
	{
		return intentionTypeIds.contains(typeId);
	}

	public void removeIntentionType(long typeId)
	{
		intentionTypeIds.remove(typeId);
	}

	public CalicoPacket getCreatePacket()
	{
		return CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_CREATE, uuid, canvas_uuid, location.x, location.y, title);
	}

	public void populateState(IntentionalInterfaceState state)
	{
		state.addCellPacket(getCreatePacket());

		for (long typeId : intentionTypeIds)
		{
			state.addCellPacket(CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_TAG, uuid, typeId));
		}
	}
}
