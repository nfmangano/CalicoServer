package calico.plugins.iip.graph.layout;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import calico.networking.netstuff.CalicoPacket;
import calico.plugins.iip.IntentionalInterfacesNetworkCommands;

public class CIntentionTopology
{
	public class Cluster
	{
		private final Point center = new Point();
		private final List<Integer> radii = new ArrayList<Integer>();

		Cluster(Point center, List<Double> radii)
		{
			this.center.setLocation(center);

			for (Double radius : radii)
			{
				this.radii.add(radius.intValue());
			}
		}

		void serialize(StringBuilder buffer)
		{
			buffer.append("[");
			buffer.append(center.x);
			buffer.append(",");
			buffer.append(center.y);
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

	public void addCluster(Point center, List<Double> radii)
	{
		clusters.add(new Cluster(center, radii));
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
}
