package calico.plugins.iip.graph.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import calico.controllers.CCanvasController;
import calico.plugins.iip.CCanvasLink;
import calico.plugins.iip.CIntentionCell;
import calico.plugins.iip.IntentionalInterfaceState;
import calico.plugins.iip.controllers.CCanvasLinkController;
import calico.plugins.iip.controllers.CIntentionCellController;

public class CIntentionLayout
{
	private static final CIntentionLayout INSTANCE = new CIntentionLayout();

	public static CIntentionLayout getInstance()
	{
		return INSTANCE;
	}
	
	public static void initialize()
	{
		CIntentionClusterGraph.initialize();
	}

	private static int calculateCellDiameter(Dimension cellSize)
	{
		return ((int) Math.sqrt((cellSize.height * cellSize.height) + (double) (cellSize.width * cellSize.width)));
	}

	public static boolean centerCanvasAt(long canvasId, int x, int y)
	{
		CIntentionCell cell = CIntentionCellController.getInstance().getCellByCanvasId(canvasId);
		return cell.setLocation(x - (CIntentionLayout.INTENTION_CELL_SIZE.width / 2), y - (CIntentionLayout.INTENTION_CELL_SIZE.height / 2));
	}

	public static Point getCanvasCenter(long canvasId)
	{
		CIntentionCell cell = CIntentionCellController.getInstance().getCellByCanvasId(canvasId);
		return new Point(cell.getLocation().x - (CIntentionLayout.INTENTION_CELL_SIZE.width / 2), cell.getLocation().y
				- (CIntentionLayout.INTENTION_CELL_SIZE.height / 2));
	}

	public static final Dimension INTENTION_CELL_SIZE = new Dimension(200, 130);
	static final int INTENTION_CELL_DIAMETER = calculateCellDiameter(INTENTION_CELL_SIZE);

	// transitory per layout execution
	private final Set<Long> movedCellIds = new HashSet<Long>();
	private final CIntentionClusterGraph graph = new CIntentionClusterGraph();
	private final CIntentionTopology topology = new CIntentionTopology();

	public Set<Long> getMovedCells()
	{
		return movedCellIds;
	}

	public CIntentionTopology getTopology()
	{
		return topology;
	}

	public void populateState(IntentionalInterfaceState state)
	{
		state.setTopologyPacket(topology.createPacket());
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

	public void layoutGraph()
	{
		movedCellIds.clear();
		topology.clear();

		for (CIntentionCluster cluster : graph.layoutClusters(movedCellIds))
		{
			topology.addCluster(cluster);
		}

		graph.reset();
	}

	static int getCanvasIndex(long canvasId)
	{
		if (canvasId < 0L)
		{
			return -1;
		}
		return CCanvasController.canvases.get(canvasId).getIndex();
	}
}
