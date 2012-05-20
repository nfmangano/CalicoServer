package calico.plugins.iip;

import java.awt.Point;

public class CCanvasLinkAnchor
{
	public enum Type
	{
		FLOATING,
		INTENTION_CELL;
	}

	private long uuid;
	private long link_uuid;
	private long canvas_uuid;
	private Type type;
	private Point point;

	private long group_uuid;

	public CCanvasLinkAnchor(long uuid, long link_uuid, long canvas_uuid)
	{
		this.uuid = uuid;
		this.link_uuid = link_uuid;
		this.canvas_uuid = canvas_uuid;
		type = Type.FLOATING;
		point = new Point();

		this.group_uuid = 0L;
	}

	public CCanvasLinkAnchor(long uuid, long link_uuid, long canvas_uuid, Type type, int x, int y)
	{
		this(uuid, link_uuid, canvas_uuid);

		this.type = type;
		point.x = x;
		point.y = y;
	}

	public CCanvasLinkAnchor(long uuid, long link_uuid, long canvas_uuid, Type type, int x, int y, long group_uuid)
	{
		this(uuid, link_uuid, canvas_uuid, type, x, y);

		this.group_uuid = group_uuid;
	}

	public long getId()
	{
		return uuid;
	}
	
	public long getLinkId()
	{
		return link_uuid;
	}

	public long getCanvasId()
	{
		return canvas_uuid;
	}

	public long getGroupId()
	{
		return group_uuid;
	}

	public Type getType()
	{
		return type;
	}

	public Point getPoint()
	{
		return point;
	}
	
	public void move(long canvas_uuid, Type type, int x, int y)
	{
		this.canvas_uuid = canvas_uuid;
		this.type = type;
		point.x = x;
		point.y = y;
	}
}
