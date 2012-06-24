package calico.plugins.iip.graph.layout;

import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CIntentionClusterGraph
{
	private static class Position
	{
		int xUnit;
		int yUnit;
		int unitSpan;

		int rowIndex;
		int columnIndex;

		CIntentionCluster cluster;

		Position(int rowIndex, int columnIndex)
		{
			xUnit = yUnit = unitSpan = -1;
			cluster = null;

			this.rowIndex = rowIndex;
			this.columnIndex = columnIndex;
		}

		boolean isEmpty()
		{
			return (cluster == null);
		}

		int getRightExtent()
		{
			return xUnit + unitSpan;
		}

		int getDownExtent()
		{
			return yUnit + unitSpan;
		}

		Point getPositionCenter()
		{
			double inset = ((unitSpan * CLUSTER_UNIT_SPAN) / 2);
			return new Point((int) ((xUnit * CLUSTER_UNIT_SPAN) + inset), (int) ((yUnit * CLUSTER_UNIT_SPAN) + inset));
		}
	}

	private static class UnitGraph
	{
		private final List<Integer> boundary = new ArrayList<Integer>();
		private int xUnit = 0;

		private int getBoundary(int xUnit)
		{
			for (int i = boundary.size(); i <= xUnit; i++)
			{
				boundary.add(0);
			}
			return boundary.get(xUnit);
		}

		private int getMaxBoundary(Position position)
		{
			int yMax = getBoundary(position.xUnit);
			for (int x = 1; x < position.unitSpan; x++)
			{
				yMax = Math.max(yMax, getBoundary(position.xUnit + x));
			}
			return yMax;
		}

		void clear()
		{
			boundary.clear();
			xUnit = 0;
		}

		void nextRow()
		{
			xUnit = 0;
		}

		void layout(Position position)
		{
			if (position.isEmpty())
			{
				position.unitSpan = 1; // 0 to auto-collapse empty cells
			}
			else
			{
				position.unitSpan = (int) Math.ceil(position.cluster.getOccupiedSpan() / CLUSTER_UNIT_SPAN);
			}

			position.xUnit = xUnit;
			position.yUnit = getMaxBoundary(position);

			for (int i = position.xUnit; i < (position.xUnit + position.unitSpan); i++)
			{
				boundary.set(i, position.yUnit + position.unitSpan);
			}

			xUnit += position.unitSpan;
		}
	}

	private enum Direction
	{
		DOWN,
		RIGHT;
	}

	static void initialize()
	{
		CLUSTER_UNIT_SPAN = CIntentionCluster.getUnitSpan();
	}

	private static double CLUSTER_UNIT_SPAN;

	private final List<List<Position>> graph = new ArrayList<List<Position>>();
	private int columnCount = 0;
	private final Long2ObjectOpenHashMap<Position> positionsByRootCanvasId = new Long2ObjectOpenHashMap<Position>();

	private boolean isActive = false;
	private final UnitGraph unitGraph = new UnitGraph();

	public CIntentionClusterGraph()
	{
		graph.add(new ArrayList<Position>()); // install one row
		addColumn(); // install one column
	}

	void reset()
	{
		unitGraph.clear();
		isActive = false;
		resetClusters();
	}

	private void activate()
	{
		if (isActive)
			return;

		for (List<Position> row : graph)
		{
			for (Position position : row)
			{
				unitGraph.layout(position);
			}
			unitGraph.nextRow();
		}

		isActive = true;
	}

	private List<Position> getRow(int rowIndex)
	{
		for (int i = graph.size(); i <= rowIndex; i++)
		{
			List<Position> newRow = new ArrayList<Position>();
			for (int j = 0; j < columnCount; j++)
			{
				newRow.add(new Position(i, j));
			}
			graph.add(newRow);
		}
		return graph.get(rowIndex);
	}

	private void addColumn()
	{
		for (int i = 0; i < graph.size(); i++)
		{
			List<Position> row = graph.get(i);
			row.add(new Position(i, columnCount));
		}
		columnCount++;
	}

	private Position getPosition(int rowIndex, int columnIndex)
	{
		List<Position> row = getRow(rowIndex);
		for (int i = columnCount; i <= columnIndex; i++)
		{
			addColumn();
		}
		return row.get(columnIndex);
	}

	private List<Position> getInsertZone(Position anchor)
	{
		List<Position> zone = new ArrayList<Position>();
		if (anchor.rowIndex > 0)
			zone.add(getPosition(anchor.rowIndex - 1, anchor.columnIndex + 1));
		zone.add(getPosition(anchor.rowIndex, anchor.columnIndex + 1));
		zone.add(getPosition(anchor.rowIndex + 1, anchor.columnIndex + 1));
		zone.add(getPosition(anchor.rowIndex + 1, anchor.columnIndex));
		if (anchor.columnIndex > 0)
			zone.add(getPosition(anchor.rowIndex + 1, anchor.columnIndex - 1));
		return zone;
	}

	private Direction getShallowestDirection()
	{
		int maxRightExtent = 0;
		int maxDownExtent = 0;

		for (List<Position> row : graph)
		{
			maxRightExtent = Math.max(maxRightExtent, row.get(row.size() - 1).getRightExtent());
		}
		for (Position position : graph.get(graph.size() - 1))
		{
			maxDownExtent = Math.max(maxDownExtent, position.getDownExtent());
		}

		return (maxRightExtent > maxDownExtent) ? Direction.DOWN : Direction.RIGHT;
	}

	private Position findEmptyPosition()
	{
		for (List<Position> row : graph)
		{
			for (Position position : row)
			{
				if (position.isEmpty())
				{
					return position;
				}
			}
		}

		switch (getShallowestDirection())
		{
			case RIGHT:
				addColumn();
				return getPosition(0, columnCount - 1);
			case DOWN:
				return getRow(graph.size()).get(0);
			default:
				throw new IllegalArgumentException("Unknown direction " + getShallowestDirection());
		}
	}

	private Position findEmptyPositionInInsertionZone(Position anchor)
	{
		for (Position position : getInsertZone(anchor))
		{
			if (position.isEmpty())
			{
				return position;
			}
		}
		return null;
	}

	private void shiftColumnsOverFrom(int columnIndex)
	{
		for (List<Position> row : graph)
		{
			row.add(null);
			for (int i = columnCount; i > columnIndex; i--)
			{
				Position position = row.get(i - 1);
				position.columnIndex = i;
				row.set(i, position);
			}
			row.set(columnIndex, new Position(row.get(0).rowIndex, columnIndex));
		}
		columnCount++;
	}

	private void shiftRowsDownFrom(int rowIndex)
	{
		List<Position> newRow = new ArrayList<Position>();
		for (int i = 0; i < columnCount; i++)
		{
			newRow.add(new Position(rowIndex, i));
		}
		graph.add(rowIndex, newRow);

		for (int i = rowIndex + 1; i < graph.size(); i++)
		{
			List<Position> shiftedRow = graph.get(i);
			for (Position position : shiftedRow)
			{
				position.rowIndex = i;
			}
		}
	}

	private void resetClusters()
	{
		for (List<Position> row : graph)
		{
			for (Position position : row)
			{
				if (!position.isEmpty())
				{
					position.cluster.reset();
				}
			}
		}
	}

	void replaceCluster(long originalRootCanvasId, CIntentionCluster newCluster)
	{
		activate();

		Position position = positionsByRootCanvasId.get(originalRootCanvasId);
		position.cluster = newCluster;
		positionsByRootCanvasId.put(newCluster.getRootCanvasId(), position);
		reset();
	}

	void insertCluster(CIntentionCluster cluster)
	{
		activate();

		Position position = findEmptyPosition();
		position.cluster = cluster;
		positionsByRootCanvasId.put(cluster.getRootCanvasId(), position);

		reset();
	}

	void insertCluster(long contextCanvasId, CIntentionCluster cluster)
	{
		activate();

		Position anchor = positionsByRootCanvasId.get(contextCanvasId);

		if (findEmptyPositionInInsertionZone(anchor) == null)
		{
			switch (getShallowestDirection())
			{
				case RIGHT:
					shiftColumnsOverFrom(anchor.columnIndex + 1);
					break;
				case DOWN:
					shiftRowsDownFrom(anchor.rowIndex + 1);
					break;
			}
		}

		Position position = findEmptyPositionInInsertionZone(anchor);
		position.cluster = cluster;
		positionsByRootCanvasId.put(cluster.getRootCanvasId(), position);

		reset();
	}

	void removeClusterIfAny(long rootCanvasId)
	{
		Position position = positionsByRootCanvasId.remove(rootCanvasId);
		if (position != null)
		{
			position.cluster = null;
			reset();
		}
	}

	List<CIntentionCluster> layoutClusters(Collection<Long> movedCellIds)
	{
		activate();

		List<CIntentionCluster> clusters = new ArrayList<CIntentionCluster>();

		for (List<Position> row : graph)
		{
			for (Position position : row)
			{
				if (!position.isEmpty())
				{
					position.cluster.layoutClusterAsCircles(position.getPositionCenter(), movedCellIds);
					clusters.add(position.cluster);
				}
			}
		}

		return clusters;
	}
}
