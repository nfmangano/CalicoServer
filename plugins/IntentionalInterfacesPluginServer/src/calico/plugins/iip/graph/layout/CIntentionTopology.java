package calico.plugins.iip.graph.layout;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import calico.networking.netstuff.CalicoPacket;
import calico.plugins.iip.IntentionalInterfacesNetworkCommands;

public class CIntentionTopology
{
	public class Cluster
	{
		private final long rootCanvasId;
		private final Point center = new Point();
		private final List<Integer> radii = new ArrayList<Integer>();
		private final Rectangle boundingBox = new Rectangle();
		private final Rectangle outerBox = new Rectangle();

		Cluster(CIntentionClusterLayout clusterLayout)
		{
			rootCanvasId = clusterLayout.getCluster().getRootCanvasId();
			center.setLocation(clusterLayout.getCluster().getLocation());

			for (Double radius : clusterLayout.getCluster().getRingRadii())
			{
				radii.add(radius.intValue());
			}

			boundingBox.setSize(clusterLayout.getBoundingBox());
			Point layoutCenter = clusterLayout.getLayoutCenterWithinBounds(boundingBox.getSize());
			boundingBox.setLocation(center.x - layoutCenter.x, center.y - layoutCenter.y);
			outerBox.setSize(clusterLayout.getOuterBox());
			outerBox.setLocation((int)Math.round(boundingBox.getCenterX()) - outerBox.width/2, (int)Math.round(boundingBox.getCenterY()) - outerBox.height/2);
		}

		void serialize(StringBuilder buffer)
		{
			buffer.append(rootCanvasId);
			buffer.append("[");
			buffer.append(center.x);
			buffer.append(",");
			buffer.append(center.y);
			buffer.append(",");
			buffer.append(boundingBox.x);
			buffer.append(",");
			buffer.append(boundingBox.y);
			buffer.append(",");
			buffer.append(boundingBox.width);
			buffer.append(",");
			buffer.append(boundingBox.height);
			buffer.append(",");			
			buffer.append(outerBox.x);
			buffer.append(",");
			buffer.append(outerBox.y);
			buffer.append(",");
			buffer.append(outerBox.width);
			buffer.append(",");
			buffer.append(outerBox.height);			
			buffer.append(":");

			for (Integer radius : radii)
			{
				buffer.append(radius);
				buffer.append(",");
			}
			buffer.setLength(buffer.length() - 1);
			buffer.append("]");
		}

		public Point getCenter()
		{
			return center;
		}

		public List<Integer> getRadii()
		{
			return radii;
		}
	}

	private final List<Cluster> clusters = new ArrayList<Cluster>();

	public CIntentionTopology()
	{
	}

	public CalicoPacket createPacket()
	{
		CalicoPacket p = new CalicoPacket();
		p.putInt(IntentionalInterfacesNetworkCommands.CIC_TOPOLOGY);
		p.putString(serialize());
		return p;
	}

	public void clear()
	{
		clusters.clear();
	}

	public List<Cluster> getClusters()
	{
		return clusters;
	}

	public void addCluster(CIntentionClusterLayout clusterLayout)
	{
		clusters.add(new Cluster(clusterLayout));
	}

	private String serialize()
	{
		StringBuilder buffer = new StringBuilder();
		for (Cluster cluster : clusters)
		{
			buffer.append("C");
			cluster.serialize(buffer);
		}
		return buffer.toString();
	}
	
	public Rectangle getTopologyBounds()
	{
		int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
				maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
		
		for (Cluster c : clusters)
		{
			if (minX > c.outerBox.x)
				minX = c.outerBox.x;
			if (minY > c.outerBox.y)
				minY = c.outerBox.y;
			if (maxX < c.outerBox.x + c.outerBox.width)
				maxX = c.outerBox.x + c.outerBox.width;
			if (maxY < c.outerBox.y + c.outerBox.height)
				maxY = c.outerBox.y + c.outerBox.height;
		}
		
		return new Rectangle(minX, minY,
				maxX - minX, maxY - minY);
	}
}
