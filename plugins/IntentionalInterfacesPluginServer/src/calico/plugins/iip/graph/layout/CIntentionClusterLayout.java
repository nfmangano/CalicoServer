package calico.plugins.iip.graph.layout;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class CIntentionClusterLayout
{
	public class CanvasPosition
	{
		public final long canvasId;
		public final Point location;

		CanvasPosition(long canvasId, Point location)
		{
			this.canvasId = canvasId;
			this.location = location;
		}

		void translateBy(int x, int y)
		{
			location.x += x;
			location.y += y;
		}
	}

	private final CIntentionCluster cluster;
	private final List<CanvasPosition> canvasPositions = new ArrayList<CanvasPosition>();

	// transitory value, per getBoundingBox()
	private boolean isCalculated = false;
	private final Point rootCanvasPosition = new Point();
	private final Dimension boundingBox = new Dimension();
	private final Dimension outerBox = new Dimension();

	CIntentionClusterLayout(CIntentionCluster cluster)
	{
		this.cluster = cluster;
	}

	void reset()
	{
		isCalculated = false;
		rootCanvasPosition.setLocation(0, 0);
		boundingBox.setSize(0, 0);
	}

	public CIntentionCluster getCluster()
	{
		return cluster;
	}

	public List<CanvasPosition> getCanvasPositions()
	{
		return canvasPositions;
	}

	void addCanvas(long canvasId, Point location)
	{
		canvasPositions.add(new CanvasPosition(canvasId, location));
	}

	Point getLayoutCenterWithinBounds(Dimension bounds)
	{
		if (!isCalculated)
			calculate();

		int xInset = (bounds.width - boundingBox.width) / 2;
		int yInset = (bounds.height - boundingBox.height) / 2;
		return new Point(rootCanvasPosition.x + xInset + (CIntentionLayout.INTENTION_CELL_SIZE.width / 2), rootCanvasPosition.y + yInset
				+ (CIntentionLayout.INTENTION_CELL_SIZE.height / 2));
	}

	public Dimension getBoundingBox()
	{
		if (!isCalculated)
			calculate();

		return boundingBox;
	}
	
	public Dimension getOuterBox()
	{
		return outerBox;
	}
	
	public void setOuterBox(Dimension outerBox)
	{
		if (outerBox != null)
			this.outerBox.setSize(outerBox);  
	}

	private void calculate()
	{
		boundingBox.width = (int) Math.ceil(cluster.getOccupiedSpan());// (xMax - xMin) /*+ 20*/;
		boundingBox.height = (int) Math.ceil(cluster.getOccupiedSpan()); //(yMax - yMin) /*+ 20*/;
		
		int xMin = Integer.MAX_VALUE;
		int xMax = -Integer.MAX_VALUE;
		int yMin = Integer.MAX_VALUE;
		int yMax = -Integer.MAX_VALUE;

		CanvasPosition rootCanvas = null;

		for (CanvasPosition position : canvasPositions)
		{
			xMin = Math.min(position.location.x, xMin);
			yMin = Math.min(position.location.y, yMin);
			xMax = Math.max(position.location.x + CIntentionLayout.INTENTION_CELL_SIZE.width, xMax);
			yMax = Math.max(position.location.y + CIntentionLayout.INTENTION_CELL_SIZE.height, yMax);

			if (position.canvasId == cluster.getRootCanvasId())
			{
				rootCanvas = position;
			}
		}

//		rootCanvasPosition.x = (rootCanvas.location.x - (xMin /* - 10*/)); // clumsy handling of the buffer spacing
//		rootCanvasPosition.y = (rootCanvas.location.y - (yMin /*- 10*/));
		rootCanvasPosition.x = (boundingBox.width/2 - CIntentionLayout.INTENTION_CELL_SIZE.width/2);
		rootCanvasPosition.y = (boundingBox.height/2 - CIntentionLayout.INTENTION_CELL_SIZE.height/2);

//		boundingBox.setSize(CIntentionClusterGraph.getClusterDimensions());
//		boundingBox.width = (int) Math.ceil(cluster.getOccupiedSpan());// (xMax - xMin) /*+ 20*/;
//		boundingBox.height = (int) Math.ceil(cluster.getOccupiedSpan()); //(yMax - yMin) /*+ 20*/;

		isCalculated = true;
	}
}
