package calico.plugins.iip.graph.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import calico.components.CCanvas;
import calico.controllers.CCanvasController;
import calico.plugins.iip.CIntentionCell;
import calico.plugins.iip.controllers.CCanvasLinkController;
import calico.plugins.iip.controllers.CIntentionCellController;

public class CIntentionLayout
{
	enum LayoutMode
	{
		TREE,
		CONCENTRIC
	}

	private static final CIntentionLayout INSTANCE = new CIntentionLayout();

	public static CIntentionLayout getInstance()
	{
		return INSTANCE;
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

	public static boolean centerCanvasAt(long canvasId, int xArc, Point center, double radius)
	{
		double theta = xArc / radius;
		int x = center.x + (int) (radius * Math.cos(theta));
		int y = center.y + (int) (radius * Math.sin(theta));

		System.out.println(String.format("[%d] (%d, %d) for xArc %d and radius %f", getCanvasIndex(canvasId), x, y, xArc, radius));

		return centerCanvasAt(canvasId, x, y);
	}

	static final Dimension INTENTION_CELL_SIZE = new Dimension(100, 60);
	static final int INTENTION_CELL_DIAMETER = calculateCellDiameter(INTENTION_CELL_SIZE);
	static final int RING_SEPARATION = 100 + INTENTION_CELL_DIAMETER;
	static final LayoutMode LAYOUT_MODE = LayoutMode.CONCENTRIC;

	private static final ClusterSorter CLUSTER_SORTER = new ClusterSorter();

	private final List<CIntentionCluster> clusters = new ArrayList<CIntentionCluster>();

	public void populateLayout()
	{
		clusters.clear();
		for (CIntentionCell cell : CIntentionCellController.getInstance().getAllCells())
		{
			boolean hasIncomingLink = false;
			for (Long anchorId : CCanvasLinkController.getInstance().getAnchorIdsForCanvasId(cell.getCanvasId()))
			{
				if (CCanvasLinkController.getInstance().isDestination(anchorId))
				{
					hasIncomingLink = true;
					break;
				}
			}
			if (!hasIncomingLink)
			{
				clusters.add(new CIntentionCluster(cell.getCanvasId()));
			}
		}

		Collections.sort(clusters, CLUSTER_SORTER);

		for (CIntentionCluster cluster : clusters)
		{
			cluster.populateCluster();
		}

		describeClusters();
	}

	public Set<Long> layoutGraph()
	{
		Set<Long> movedCells = new HashSet<Long>();

		switch (LAYOUT_MODE)
		{
			case TREE:
				layoutGraphAsTree(movedCells);
				break;
			case CONCENTRIC:
				layoutClusterAsCircles(movedCells);
				break;
		}

		return movedCells;
	}

	private void layoutGraphAsTree(Set<Long> movedCells)
	{
		Point clusterRoot = new Point();
		for (CIntentionCluster cluster : clusters)
		{
			cluster.layoutClusterAsTree(clusterRoot, movedCells);
			clusterRoot.y += (cluster.getLayoutSize().height + RING_SEPARATION);
		}
	}

	private void layoutClusterAsCircles(Set<Long> movedCells)
	{
		Point clusterCenter = new Point();

		CIntentionCluster rootCluster = clusters.get(0);
		List<Double> ringRadii = rootCluster.getRingRadii();
		rootCluster.layoutClusterAsCircles(clusterCenter, movedCells, ringRadii);
		double occupiedRadius = (ringRadii.get(ringRadii.size() - 1) + INTENTION_CELL_DIAMETER);

		double theta = 0.0;
		double maxClusterRadius = 0.0;
		for (int i = 1; i < clusters.size(); i++)
		{
			CIntentionCluster cluster = clusters.get(i);
			ringRadii = cluster.getRingRadii();
			double clusterRadius = ringRadii.get(ringRadii.size() - 1);

			double clusterThetaSpan = 2 * Math.asin((clusterRadius + INTENTION_CELL_DIAMETER) / (occupiedRadius + clusterRadius));
			if ((theta + clusterThetaSpan) > (2 * Math.PI))
			{
				occupiedRadius += (maxClusterRadius + INTENTION_CELL_DIAMETER);
				maxClusterRadius = 0.0;
				theta = 0.0;
			}

			if (theta > 0.0)
			{
				theta += (clusterThetaSpan / 2.0);
			}

			double aggregateRadius = occupiedRadius + clusterRadius;
			clusterCenter.setLocation(Math.sin(theta) * aggregateRadius, -(Math.cos(theta) * aggregateRadius));
			cluster.layoutClusterAsCircles(clusterCenter, movedCells, ringRadii);

			theta += (clusterThetaSpan / 2.0);
			if (clusterRadius > maxClusterRadius)
			{
				maxClusterRadius = clusterRadius ;
			}
		}
	}

	private void describeClusters()
	{
		StringBuilder buffer = new StringBuilder();

		for (CIntentionCluster cluster : clusters)
		{
			buffer.append("Cluster for canvas #");
			buffer.append(getCanvasIndex(cluster.getRootCanvasId()));
			buffer.append(" has max projected spans ");
			cluster.describeMaxProjectedSpans(buffer);
			buffer.append("\n");
		}

		System.out.println(buffer.toString());
	}

	static int getCanvasIndex(long canvasId)
	{
		return CCanvasController.canvases.get(canvasId).getIndex();
	}

	private static class ClusterSorter implements Comparator<CIntentionCluster>
	{
		@Override
		public int compare(CIntentionCluster first, CIntentionCluster second)
		{
			CCanvas firstCanvas = CCanvasController.canvases.get(first.getRootCanvasId());
			CCanvas secondCanvas = CCanvasController.canvases.get(second.getRootCanvasId());

			return firstCanvas.getIndex() - secondCanvas.getIndex();
		}
	}
}
