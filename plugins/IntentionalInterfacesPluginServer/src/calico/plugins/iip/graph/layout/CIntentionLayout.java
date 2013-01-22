package calico.plugins.iip.graph.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.util.List;

import calico.controllers.CCanvasController;
import calico.plugins.iip.CCanvasLink;
import calico.plugins.iip.IntentionalInterfaceState;
import calico.plugins.iip.controllers.CCanvasLinkController;

public class CIntentionLayout
{
	private static final CIntentionLayout INSTANCE = new CIntentionLayout();

	public static CIntentionLayout getInstance()
	{
		return INSTANCE;
	}
	
	private static int calculateCellDiameter(Dimension cellSize)
	{
		return ((int) Math.sqrt((cellSize.height * cellSize.height) + (double) (cellSize.width * cellSize.width)));
	}

	public static Point centerCanvasAt(int x, int y)
	{
		return new Point(x - (CIntentionLayout.INTENTION_CELL_SIZE.width / 2), y - (CIntentionLayout.INTENTION_CELL_SIZE.height / 2));
	}
	
	public static final Dimension INTENTION_CELL_DEFAULT_SIZE = new Dimension(200, 130);

	public static final Dimension INTENTION_CELL_SIZE = new Dimension(200, 130);
	static final int INTENTION_CELL_DIAMETER = calculateCellDiameter(INTENTION_CELL_SIZE);

	private final CIntentionClusterGraph graph = new CIntentionClusterGraph();
	private final CIntentionTopology topology = new CIntentionTopology();

	public CIntentionTopology getTopology()
	{
		return topology;
	}

	public void populateState(IntentionalInterfaceState state)
	{
		state.setTopologyPacket(topology.createPacket());
		state.setClusterGraphPacket(graph.createPacket());
	}
	
	public void inflateStoredClusterGraph(String graphData)
	{
		graph.inflateStoredData(graphData);
	}

	public long getRootCanvasId(long canvasId)
	{
		while (true)
		{ // walk to the root of the cluster
			Long linkId = CCanvasLinkController.getInstance().getIncomingLink(canvasId);
			if (linkId == null)
			{
				break;
			}
			else
			{
				CCanvasLink link = CCanvasLinkController.getInstance().getLink(linkId);
				canvasId = link.getAnchorA().getCanvasId();
			}
		}
		return canvasId;
	}

	public void replaceCluster(long originalRootCanvasId, long newRootCanvasId)
	{
		CIntentionCluster cluster = new CIntentionCluster(newRootCanvasId);
		graph.replaceCluster(originalRootCanvasId, cluster);
	}

	public void insertCluster(long rootCanvasId)
	{
		CIntentionCluster cluster = new CIntentionCluster(rootCanvasId);
		graph.insertCluster(cluster);
	}

	public void insertCluster(long contextCanvasId, long rootCanvasId)
	{
		CIntentionCluster cluster = new CIntentionCluster(rootCanvasId);
		graph.insertCluster(getRootCanvasId(contextCanvasId), cluster);
	}

	public void removeClusterIfAny(long rootCanvasId)
	{
		graph.removeClusterIfAny(rootCanvasId);
	}

	public List<CIntentionClusterLayout> layoutGraph()
	{
		topology.clear();

		List<CIntentionClusterLayout> clusterLayouts = graph.layoutClusters();
		for (CIntentionClusterLayout clusterLayout : clusterLayouts)
		{
			topology.addCluster(clusterLayout);
		}

		graph.reset();
		
		return clusterLayouts;
	}

	static int getCanvasIndex(long canvasId)
	{
		if (canvasId < 0L)
		{
			return -1;
		}
		return CCanvasController.canvases.get(canvasId).getIndex();
	}
	
	public int getClusterCount()
	{
		return graph.getClusterCount();
	}
}
