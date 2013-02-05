package calico.plugins.iip;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import calico.components.CCanvas;
import calico.networking.netstuff.CalicoPacket;
import calico.plugins.iip.graph.layout.CIntentionLayout;
import edu.umd.cs.piccolo.util.PBounds;

public class CIntentionCell
{
	public static final String DEFAULT_TITLE = "<default>";

	private long uuid;
	private long canvas_uuid;
	private final Point location;
	private String title;
	private Long intentionTypeId = null;

	public CIntentionCell(long uuid, long canvasId)
	{
		this.uuid = uuid;
		this.canvas_uuid = canvasId;
//		this.location = new Point(-(CIntentionLayout.INTENTION_CELL_SIZE.width / 2), -(CIntentionLayout.INTENTION_CELL_SIZE.height / 2));
		this.location = new Point(Integer.MIN_VALUE, Integer.MIN_VALUE);
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
	
	public boolean hasIntentionType()
	{
		return (intentionTypeId != null);
	}
	
	public Long getIntentionTypeId()
	{
		return intentionTypeId;
	}

	public void setIntentionType(long intentionTypeId)
	{
		if (intentionTypeId == -1)
			intentionTypeId = CIntentionType.noTagIntentionType;
		this.intentionTypeId = intentionTypeId;
	}

	public void clearIntentionType()
	{
		intentionTypeId = null;
	}

	public CalicoPacket getCreatePacket()
	{
		return CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_CREATE, uuid, canvas_uuid, location.x, location.y, title);
	}

	public void populateState(IntentionalInterfaceState state)
	{
		state.addCellPacket(getCreatePacket());

		if (intentionTypeId != null)
		{
			state.addCellPacket(CalicoPacket.getPacket(IntentionalInterfacesNetworkCommands.CIC_TAG, uuid, intentionTypeId));
		}
	}
	
	/**
	 * Get the center point of this CIC in Intention View coordinates.
	 */
	public Point2D getCenter()
	{
		Rectangle2D.Double rect = new Rectangle2D.Double(location.x, location.y, 
				CIntentionLayout.INTENTION_CELL_SIZE.width,
				CIntentionLayout.INTENTION_CELL_SIZE.height);
		return new Point2D.Double(rect.getCenterX(), rect.getCenterY());
	}

	public Rectangle2D copyBounds() {
		return 	new Rectangle2D.Double(location.x, location.y, 
				CIntentionLayout.INTENTION_CELL_SIZE.width,
				CIntentionLayout.INTENTION_CELL_SIZE.height);
	}
}
