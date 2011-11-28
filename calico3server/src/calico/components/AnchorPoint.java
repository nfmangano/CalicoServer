package calico.components;

import java.awt.*;

/**
 * Used to denote the anchor points in {@link CArrow}
 * @author mdempsey
 *
 */
public class AnchorPoint implements Cloneable
{
	
	private int type = CArrow.TYPE_CANVAS;
	private Point point = new Point(0,0);
	private long uuid = 0L;
	
	
	public AnchorPoint(int type, Point point, long uuid)
	{
		this.type = type;
		this.point = point;
		this.uuid = uuid;
	}
	public AnchorPoint(Point point, long uuid)
	{
		this(CArrow.TYPE_CANVAS, point, uuid);
	}
	public AnchorPoint(int type, long uuid, Point point)
	{
		this(type, point, uuid);
	}
	
	public AnchorPoint(int type, long uuid, int x, int y)
	{
		this(type, new Point(x,y), uuid);
	}
	
	public int getType()
	{
		return this.type;
	}
	public Point getPoint()
	{
		return this.point;
	}
	public long getUUID()
	{
		return this.uuid;
	}
	public int getX()
	{
		return this.point.x;
	}
	public int getY()
	{
		return this.point.y;
	}
	
	
	public void translate(int x, int y)
	{
		this.point.translate(x, y);
	}
	
	public void setType(int type)
	{
		this.type = type;
	}
	public void setUUID(long uuid)
	{
		this.uuid = uuid;
	}
	
	
	// For cloning dolly
	public AnchorPoint clone()
	{
		return new AnchorPoint(this.type, new Point(this.point.x, this.point.y), this.uuid);
	}
}