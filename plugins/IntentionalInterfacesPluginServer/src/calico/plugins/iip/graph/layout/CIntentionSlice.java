package calico.plugins.iip.graph.layout;

import java.awt.Point;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CIntentionSlice
{
	private static final DecimalFormat WEIGHT_FORMAT = new DecimalFormat("0.00");

	private final long rootCanvasId;
	private final List<Long> canvasIds = new ArrayList<Long>();
	private final List<Arc> arcs = new ArrayList<Arc>();

	// transitory per layout execution
	private double populationWeight;
	private double maxArcWeight;
	private double assignedWeight;
	private int layoutSpan;

	CIntentionSlice(long rootCanvasId)
	{
		this.rootCanvasId = rootCanvasId;
	}

	long getRootCanvasId()
	{
		return rootCanvasId;
	}

	void addCanvas(long parentCanvasId, long canvasId, int ringIndex)
	{
		canvasIds.add(canvasId);
		getArc(ringIndex).addCanvas(parentCanvasId, canvasId);
	}

	int getLayoutSpan()
	{
		return layoutSpan;
	}

	int size()
	{
		return canvasIds.size();
	}

	int arcSize(int ringIndex)
	{
		return getArc(ringIndex).canvasCount;
	}

	void setPopulationWeight(int totalInOrbit)
	{
		populationWeight = (canvasIds.size() / (double) totalInOrbit);
	}

	public double getWeight()
	{
		return assignedWeight;
	}

	void setWeight(double weight)
	{
		assignedWeight = weight;

		System.out.println(String.format("Slice for canvas %d has %d canvases and max weight %s with normalized weight %s%%",
				CIntentionLayout.getCanvasIndex(rootCanvasId), canvasIds.size(), WEIGHT_FORMAT.format(getMaxArcWeight()), toPercent(assignedWeight)));

		for (Arc arc : arcs)
		{
			arc.calculateArcSpanProjection();

			System.out.println(String.format("Slice for canvas %d has projected span %d for ring %d", CIntentionLayout.getCanvasIndex(rootCanvasId),
					arc.arcSpanProjection, arc.ringIndex));
		}
	}

	void setArcWeight(int ringIndex, double weight)
	{
		getArc(ringIndex).setWeight(weight);
	}

	void calculateMaxArcWeight()
	{
		maxArcWeight = 0.0;
		for (Arc arc : arcs)
		{
			if (arc.weight > maxArcWeight)
			{
				maxArcWeight = arc.weight;
			}
		}
	}

	double getMaxArcWeight()
	{
		return maxArcWeight;
	}

	int getMaxProjectedSpan(int ringIndex)
	{
		return getArc(ringIndex).arcSpanProjection;
	}

	void layoutSliceAsTree(Point root, int ringSpan, Set<Long> movedCells)
	{
		int sliceWidth = (int) (ringSpan * assignedWeight);
		int y = root.y;
		int maxOccupiedArc = 0;

		System.out.println("Layout slice for canvas " + CIntentionLayout.getCanvasIndex(rootCanvasId) + " at width " + sliceWidth);

		for (Arc arc : arcs)
		{
			if (!arc.isEmpty())
			{
				maxOccupiedArc = arc.ringIndex;
				int arcOccupancySpan = arc.canvasCount * CIntentionLayout.INTENTION_CELL_DIAMETER;
				int x = root.x + ((sliceWidth - arcOccupancySpan) / 2);

				for (CanvasGroup group : arc.canvasGroups.values())
				{
					for (Long canvasId : group.groupCanvasIds)
					{
						if (CIntentionLayout.centerCanvasAt(canvasId, x, y))
						{
							movedCells.add(canvasId);
						}
						x += CIntentionLayout.INTENTION_CELL_DIAMETER;
					}
				}
			}
			y += CIntentionCluster.RING_SEPARATION;
		}

		layoutSpan = sliceWidth;
	}

	int calculateLayoutSpan(int ringSpan)
	{
		return (int) (ringSpan * assignedWeight);
	}

	void layoutArc(CIntentionArcTransformer arcTransformer, int ringIndex, int ringSpan, int arcStart, Set<Long> movedCells)
	{
		int sliceWidth = calculateLayoutSpan(ringSpan);

		Arc arc = arcs.get(ringIndex);
		if (!arc.isEmpty())
		{
			int arcOccupancySpan = (arc.canvasCount - 1) * CIntentionLayout.INTENTION_CELL_DIAMETER;
			int xArc = arcStart + ((sliceWidth - arcOccupancySpan) / 2);

			for (CanvasGroup group : arc.canvasGroups.values())
			{
				// the ideal center for `group.getSpan() is the projection of a vector from `rootCanvasId through
				// `group.parentCanvasId onto `arc

				for (Long canvasId : group.groupCanvasIds)
				{
					if (arcTransformer.centerCanvasAt(canvasId, xArc))
					{
						movedCells.add(canvasId);
					}
					xArc += CIntentionLayout.INTENTION_CELL_DIAMETER;
				}
			}
		}

		layoutSpan = sliceWidth;
	}

	private Arc getArc(int ringIndex)
	{
		for (int i = arcs.size(); i <= ringIndex; i++)
		{
			arcs.add(new Arc(i));
		}
		return arcs.get(ringIndex);
	}

	private class Arc
	{
		private int ringIndex;
		private final Map<Long, CanvasGroup> canvasGroups = new LinkedHashMap<Long, CanvasGroup>();
		int canvasCount = 0;
		private double weight;
		private int arcSpanProjection;

		Arc(int ringIndex)
		{
			this.ringIndex = ringIndex;
		}

		void addCanvas(long parentCanvasId, long canvasId)
		{
			canvasCount++;
			getCanvasGroup(parentCanvasId).addCanvas(canvasId);
		}

		boolean isEmpty()
		{
			return canvasGroups.isEmpty();
		}

		void setWeight(double weight)
		{
			this.weight = weight;
		}

		void calculateArcSpanProjection()
		{
			arcSpanProjection = (int) ((canvasCount * CIntentionLayout.INTENTION_CELL_DIAMETER) * (1.0 / assignedWeight));
		}

		private CanvasGroup getCanvasGroup(long parentCanvasId)
		{
			CanvasGroup group = canvasGroups.get(parentCanvasId);
			if (group == null)
			{
				group = new CanvasGroup(parentCanvasId);
				canvasGroups.put(parentCanvasId, group);
			}
			return group;
		}
	}

	private class CanvasGroup
	{
		private final long parentCanvasId;
		private final List<Long> groupCanvasIds = new ArrayList<Long>();

		CanvasGroup(long parentCanvasId)
		{
			this.parentCanvasId = parentCanvasId;
		}

		void addCanvas(long canvasId)
		{
			groupCanvasIds.add(canvasId);
		}

		int getSpan()
		{
			return groupCanvasIds.size() * CIntentionLayout.INTENTION_CELL_DIAMETER;
		}
	}

	private static String toPercent(double d)
	{
		return String.valueOf((int) (d * 100.0));
	}
}
