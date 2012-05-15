package calico.components;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import org.apache.log4j.Logger;

import calico.controllers.CCanvasController;
import calico.controllers.CGroupController;
import calico.controllers.CStrokeController;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.nodes.PComposite;

public class CConnector extends PComposite{
	
	private static Logger logger = Logger.getLogger(CConnector.class.getName());
	
	private static final long serialVersionUID = 1L;
	
	private long uuid = 0L;
	private long canvasUID = 0L;
	
	private Color color = null;
	
	Stroke stroke;
	Color strokePaint;
	float thickness;
	
	final public static int TYPE_HEAD = 1;
	final public static int TYPE_TAIL = 2;
	private long anchorHeadUUID = 0l;
	private long anchorTailUUID = 0l;
	
	//The data model for the connector
	private Point pointHead = null;
	private Point pointTail = null;
	//Orthogonal distance from the direct head to tail line
	private double[] orthogonalDistance;
	//Percent along the direct head to tail line (Percentage in decimal format; Can be negative)
	private double[] travelDistance;
	
	
	public CConnector(long uuid, long cuid, Color color, float thickness, Point head, Point tail, double[] orthogonalDistance, double[] travelDistance,
			 long anchorHead, long anchorTail)
	{
		this.uuid = uuid;
		this.canvasUID = cuid;
		this.color = color;
		this.thickness = thickness;
		this.anchorHeadUUID = anchorHead;
		this.anchorTailUUID = anchorTail;
		
		pointHead = head;
		pointTail = tail;
		
		this.orthogonalDistance = orthogonalDistance;
		this.travelDistance = travelDistance;
		
		stroke = new BasicStroke( thickness );
		strokePaint = this.color;
	}
	
	public long getCanvasUUID()
	{
		return this.canvasUID;
	}
	
	
	public double[] getOrthogonalDistance()
	{
		return orthogonalDistance;
	}
	
	public double[] getTravelDistance()
	{
		return travelDistance;
	}
	
	public Point getHead()
	{
		return pointHead;
	}
	
	public Point getTail()
	{
		return pointTail;
	}
	
	public void delete()
	{		
		// remove from canvas
		CCanvasController.no_notify_remove_child_connector(this.canvasUID, this.uuid);
		
		//Remove from groups
		CGroupController.no_notify_remove_child_connector(this.getAnchorUUID(TYPE_HEAD), uuid);
		CGroupController.no_notify_remove_child_connector(this.getAnchorUUID(TYPE_TAIL), uuid);
	}
	
	public void linearize()
	{
		orthogonalDistance = new double[]{0.0, 0.0};
		travelDistance = new double[]{0.0, 1.0};
	}
	
	public void setAnchorUUID(long uuid, int anchorType)
	{
		switch(anchorType)
		{
		case TYPE_HEAD: anchorHeadUUID = uuid;
			break;
		case TYPE_TAIL: anchorTailUUID = uuid;
			break;
		}
	}
	
	public long getAnchorUUID(int anchorType)
	{
		switch(anchorType)
		{
		case TYPE_HEAD: return anchorHeadUUID;

		case TYPE_TAIL: return anchorTailUUID;

		default: return 0l;
		}
	}
	
	public Point getAnchorPoint(long guuid)
	{
		if (anchorHeadUUID == guuid)
		{
			return pointHead;
		}
		else if (anchorTailUUID == guuid)
		{
			return pointTail;
		}
		return null;
	}
	
	public long getUUID()
	{
		return this.uuid;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public float getThickness()
	{
		return thickness;
	}
	
	
	public void moveAnchor(long guuid, int deltaX, int deltaY)
	{
		if (anchorHeadUUID == guuid)
		{
			pointHead.setLocation(pointHead.x + deltaX, pointHead.y + deltaY);
		}
		if (anchorTailUUID == guuid)
		{
			pointTail.setLocation(pointTail.x + deltaX, pointTail.y + deltaY);
		}
	}
	
	
	public CalicoPacket[] getUpdatePackets(long uuid, long cuid)
	{			
		int packetSize = ByteUtils.SIZE_OF_INT + (2 * ByteUtils.SIZE_OF_LONG) + ByteUtils.SIZE_OF_INT + ByteUtils.SIZE_OF_INT
				+ (ByteUtils.SIZE_OF_INT * 4) + ByteUtils.SIZE_OF_INT + (2 * this.orthogonalDistance.length * ByteUtils.SIZE_OF_LONG) + (2 * ByteUtils.SIZE_OF_LONG);
		
		CalicoPacket packet = new CalicoPacket(packetSize);

		packet.putInt(NetworkCommand.CONNECTOR_LOAD);
		packet.putLong(uuid);
		packet.putLong(cuid);
		packet.putColor(new Color(this.getColor().getRed(), this.getColor().getGreen(), this.getColor().getBlue()));
		packet.putFloat(this.thickness);
		
		packet.putInt(pointHead.x);
		packet.putInt(pointHead.y);
		packet.putInt(pointTail.x);
		packet.putInt(pointTail.y);
		
		packet.putInt(this.orthogonalDistance.length);
		for(int j=0;j<this.orthogonalDistance.length;j++)
		{
			packet.putDouble(this.orthogonalDistance[j]);
			packet.putDouble(this.travelDistance[j]);
		}
		
		packet.putLong(anchorHeadUUID);
		packet.putLong(anchorTailUUID);
		
		return new CalicoPacket[]{packet};

	}
	
	public CalicoPacket[] getUpdatePackets()
	{
		return getUpdatePackets(this.uuid, this.canvasUID);
	}
	
	public int get_signature() {

		int sig = (int) (this.orthogonalDistance.length + pointHead.x + pointHead.y + anchorTailUUID);

//		System.out.println("Debug sig for group " + uuid + ": " + sig + ", 1) " + this.points.npoints + ", 2) " + isPermanent() + ", 3) " + this.points.xpoints[0] + ", 4) " + this.points.xpoints[0] + ", 5) " + this.points.ypoints[0] + ", 6) " + (int)(this.rotation*10) + ", 7) " + (int)(this.scaleX*10) + ", 8) " + (int)(this.scaleY*10));
		return sig;
	}
}
