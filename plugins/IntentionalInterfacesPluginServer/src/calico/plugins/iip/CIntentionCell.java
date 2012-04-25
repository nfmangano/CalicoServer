package calico.plugins.iip;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import calico.components.CCanvas;
import calico.networking.netstuff.CalicoPacket;

public class CIntentionCell
{
	long uuid;
	long canvas_uuid;
	boolean inUse;
	Point location;
	String title;
	final List<Long> intentionTypeIds = new ArrayList<Long>();

	public CIntentionCell(long uuid, CCanvas canvas, boolean inUse)
	{
		this.uuid = uuid;
		this.canvas_uuid = canvas.getUUID();
		this.inUse = inUse;
		this.location = null;

		// patch the grid Y position to match the UI display
		String zeroBasedCoordinateText = canvas.getCoordText();
		String coordinates = zeroBasedCoordinateText.substring(0, 1) + ((char) (((int) zeroBasedCoordinateText.charAt(1)) + 1));
		this.title = "Canvas " + coordinates;
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

	public String getTitle()
	{
		return title;
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

	public void populateState(IntentionalInterfaceState state)
	{
		if (location == null)
		{
			state.addCellPacket(CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_CREATE, uuid, canvas_uuid, inUse, false, 0, 0, title));
		}
		else
		{
			state.addCellPacket(CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_CREATE, uuid, canvas_uuid, inUse, true, location.x, location.y,
					title));
		}

		for (long typeId : intentionTypeIds)
		{
			state.addCellPacket(CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_TAG, uuid, typeId));
		}
	}
}
