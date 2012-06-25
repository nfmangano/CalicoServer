package calico.plugins.iip.graph.layout;

import java.awt.Point;

class CIntentionArcTransformer
{
	private final Point center;
	private final double radius;
	private final int ringSpan;
	private final double offset;

	CIntentionArcTransformer(Point center, double radius, int ringSpan, int firstArcSpan)
	{
		this.center = center;
		this.radius = radius;
		this.ringSpan = ringSpan;

		offset = ((7 * ringSpan) / 8.0) - (firstArcSpan / 2.0);

		// System.out.println("Offset " + offset + " for radius " + radius + " and ring span " + ringSpan +
		// " and first arc " + firstArcSpan);
	}

	Point centerCanvasAt(int xArc)
	{
		int xShiftedArc = (int) ((xArc + offset) % ringSpan);
		return centerCanvasAt(xShiftedArc, center, radius);
	}

	double calculateIdealPosition(int parentArcPosition, double parentRingRadius)
	{
		return (radius / parentRingRadius) * parentArcPosition;
	}

	private Point centerCanvasAt(int xArc, Point center, double radius)
	{
		double theta = xArc / radius;
		int x = center.x + (int) (radius * Math.cos(theta));
		int y = center.y + (int) (radius * Math.sin(theta));

		// System.out.println(String.format("[%d] (%d, %d) for xArc %d and radius %f",
		// CIntentionLayout.getCanvasIndex(canvasId), x, y, xArc, radius));

		return CIntentionLayout.centerCanvasAt(x, y);
	}
}
