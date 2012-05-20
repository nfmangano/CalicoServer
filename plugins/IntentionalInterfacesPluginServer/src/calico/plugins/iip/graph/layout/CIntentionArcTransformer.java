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

		System.out.println("Offset " + offset + " for radius " + radius + " and ring span " + ringSpan + " and first arc " + firstArcSpan);
	}

	boolean centerCanvasAt(long canvasId, int xArc)
	{
		int xShiftedArc = (int) ((xArc + offset) % ringSpan);
		return CIntentionLayout.centerCanvasAt(canvasId, xShiftedArc, center, radius);
	}
}
