package calico.components.composable.connectors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;

import calico.components.CConnector;
import calico.components.composable.ComposableElement;
import calico.controllers.CConnectorController;
import calico.networking.netstuff.ByteUtils;
import calico.networking.netstuff.CalicoPacket;
import calico.networking.netstuff.NetworkCommand;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

/**
 * Element to add custom arrowheads to the ends of connectors
 * Takes in a polygon that will rotate around (0,0) depending on the polar angle in radians 
 * of the points p0 and p1, where p0 is the end point from whichever end the arrowhead is for, 
 * and p1 is pointOffset number of points from that end.
 * 
 * See the default arrow and default circle below for examples
 * @author Wayne
 *
 */
public class ArrowheadElement extends ComposableElement {

	/**
	 * Indicates head or tail. Constant from CConnector
	 */
	private int anchorType;
	private float strokeSize;
	private Color outlineColor;
	private Color fillColor;
	private Polygon polygon;
	
	public ArrowheadElement(long uuid, long cuuid, int anchorType, float strokeSize, Color outlineColor, Color fillColor, Polygon polygon)
	{
		super(uuid, cuuid);
		this.anchorType = anchorType;
		this.strokeSize = strokeSize;
		this.outlineColor = outlineColor;
		this.fillColor = fillColor;
		this.polygon = polygon;
	}

	
	public CalicoPacket getPacket(long uuid, long cuuid)
	{
		int packetSize = ByteUtils.SIZE_OF_INT 				//Command
				+ ByteUtils.SIZE_OF_INT 				//Element Type
				+ (2 * ByteUtils.SIZE_OF_LONG) 			//UUID & CUUID
				+ ByteUtils.SIZE_OF_INT					//Anchor Type
				+ (3 * ByteUtils.SIZE_OF_INT)			//Stroke size, outline color, fill color
				+ ByteUtils.SIZE_OF_INT					//NPoints
				+ (polygon.npoints * 2 * polygon.npoints);		//polyon points
	
		CalicoPacket packet = new CalicoPacket(packetSize);
		
		packet.putInt(NetworkCommand.ELEMENT_ADD);
		packet.putInt(ComposableElement.TYPE_ARROWHEAD);
		packet.putLong(uuid);
		packet.putLong(cuuid);
		packet.putInt(anchorType);
		packet.putFloat(strokeSize);
		packet.putColor(outlineColor);
		packet.putColor(fillColor);
		
		packet.putInt(polygon.npoints);
		for (int i = 0; i < polygon.npoints; i++)
		{
			packet.putInt(polygon.xpoints[i]);
			packet.putInt(polygon.ypoints[i]);
		}
		
		
		return packet;
	}
	
	public CalicoPacket getPacket()
	{
		return getPacket(this.uuid, this.cuuid);
	}
	
}
