package calico.components;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.util.Map;
import org.apache.log4j.Logger;

import calico.components.composable.Composable;
import calico.components.composable.ComposableElement;
import calico.components.composable.ComposableElementController;
import calico.controllers.CCanvasController;
import calico.controllers.CConnectorController;
import calico.controllers.CGroupController;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolox.nodes.PComposite;

public class CConnector extends PComposite implements Composable{
	
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
	
	//The components that are drawn by Piccolo
	private PPath connectorLine = null;
	
	//The data model for the connector
	private Point pointHead = null;
	private Point pointTail = null;
	private Polygon rawPolygon = null;
	
	//Save the current anchor location and parent before moving
	private Point savedHeadPoint = null;
	private Point savedTailPoint = null;
	private long savedAnchorHeadUUID = 0l;
	private long savedAnchorTailUUID = 0l;
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

		resetBounds();
	}
	
	public long getCanvasUUID()
	{
		return this.canvasUID;
	}
	
	public Polygon getPolygon()
	{
		return calico.utils.Geometry.getPolyFromPath(connectorLine.getPathReference().getPathIterator(null));
	}
	
	public GeneralPath getPathReference()
	{
		return connectorLine.getPathReference();
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
		//Remove all elements
		ComposableElementController.no_notify_removeAllElements(this.uuid);
		
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
	
	public void savePosition(int anchorType)
	{
		switch(anchorType)
		{
		case TYPE_HEAD: savedHeadPoint = (Point) pointHead.clone();
						savedAnchorHeadUUID = anchorHeadUUID;
			break;
		case TYPE_TAIL: savedTailPoint = (Point) pointTail.clone();
						savedAnchorTailUUID = anchorTailUUID;
			break;
		}
	}
	
	public void loadPosition(int anchorType)
	{
		switch(anchorType)
		{
		case TYPE_HEAD: 
			if (CGroupController.groups.get(savedAnchorHeadUUID).containsPoint(savedHeadPoint.x, savedHeadPoint.y))
			{
				setAnchorUUID(savedAnchorHeadUUID, anchorType);
				CGroupController.no_notify_add_child_connector(savedAnchorHeadUUID, this.uuid);
				setAnchorPoint(anchorType, savedHeadPoint);
			}		
			else
			{
				CConnectorController.no_notify_delete(this.uuid);
			}
			
			savedHeadPoint = null;
			savedAnchorHeadUUID = 0l;
			break;
		case TYPE_TAIL: 
			if (CGroupController.groups.get(savedAnchorTailUUID).containsPoint(savedTailPoint.x, savedTailPoint.y))
			{
				setAnchorUUID(savedAnchorTailUUID, anchorType);
				CGroupController.no_notify_add_child_connector(savedAnchorTailUUID, this.uuid);
				setAnchorPoint(anchorType, savedTailPoint);
			}
			else
			{
				CConnectorController.no_notify_delete(this.uuid);
			}
			
			savedTailPoint = null;
			savedAnchorTailUUID = 0l;
			break;
		}
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
	
	public void setAnchorPoint(int anchorType, Point point)
	{
		switch(anchorType)
		{
		case TYPE_HEAD: pointHead = (Point) point.clone();
			break;

		case TYPE_TAIL: pointTail = (Point) point.clone();
			break;

		}
	}
	
	public Point getAnchorPoint(int anchorType)
	{
		switch(anchorType)
		{
		case TYPE_HEAD: return pointHead;

		case TYPE_TAIL: return pointTail;

		}
		return null;
	}
	
	public long getUUID()
	{
		return this.uuid;
	}
	
	public void setColor(Color color)
	{
		this.color = color;
	}
	
	public Color getColor()
	{
		return color;
	}
	
	public void setStroke(Stroke stroke)
	{
		this.stroke = stroke;
	}
	
	public Stroke getStroke()
	{
		return stroke;
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
	
	public void moveAnchor(int type, int deltaX, int deltaY)
	{
		if (type == TYPE_HEAD)
		{
			pointHead.setLocation(pointHead.x + deltaX, pointHead.y + deltaY);
		}
		else if (type == TYPE_TAIL)
		{
			pointTail.setLocation(pointTail.x + deltaX, pointTail.y + deltaY);
		}
	}

	
	
	protected void applyAffineTransform(Polygon points)
	{
		PAffineTransform piccoloTextTransform = getPTransform(points);
		GeneralPath p = (GeneralPath) getBezieredPoly(points).createTransformedShape(piccoloTextTransform);
		connectorLine.setPathTo(p);

	}
	
	public PAffineTransform getPTransform(Polygon points) {
		PAffineTransform piccoloTextTransform = new PAffineTransform();
		return piccoloTextTransform;
	}
	
	public GeneralPath getBezieredPoly(Polygon pts)
	{
		GeneralPath p = new GeneralPath();
		if (pts.npoints > 0)
		{
			p.moveTo(pts.xpoints[0], pts.ypoints[0]);
			if (pts.npoints >= 4)
			{
				int counter = 1;
				for (int i = 1; i+2 < pts.npoints; i += 3)
				{
					p.curveTo(pts.xpoints[i], pts.ypoints[i], 
							pts.xpoints[i+1], pts.ypoints[i+1], 
							pts.xpoints[i+2], pts.ypoints[i+2]);
					counter += 3;
				}
				while (counter < pts.npoints)
				{
					p.lineTo(pts.xpoints[counter], pts.ypoints[counter]);
					counter++;
				}
			}
			else
			{
				for (int i = 1; i < pts.npoints; i++)
				{
					p.lineTo(pts.xpoints[i], pts.ypoints[i]);
				}
			}
		}
		return p;
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


	@Override
	public CalicoPacket[] getComposableElements() {
		if (!ComposableElementController.elementList.containsKey(uuid))
		{
			return new CalicoPacket[0];
		}
		
		CalicoPacket[] packets = new CalicoPacket[ComposableElementController.elementList.get(uuid).size()];
		int count = 0;
		for (Map.Entry<Long, ComposableElement> entry : ComposableElementController.elementList.get(uuid).entrySet())
		{
			packets[count] = entry.getValue().getPacket();
			count++;
		}
		
		return packets;
	}
	
	@Override
	public int getComposableType()
	{
		return Composable.TYPE_CONNECTOR;
	}

	@Override
	public void resetToDefaultElements() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeAllElements() {
		// TODO Auto-generated method stub
		
	}
}
