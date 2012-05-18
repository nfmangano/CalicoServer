package calico.plugins.iip.graph;

import calico.utils.Geometry;
import edu.umd.cs.piccolo.util.PBounds;

public class CCanvasLinkAlignment
{
	private enum CellEdge
	{
		TOP
		{
			@Override
			boolean findIntersection(PBounds cellBounds, double xOpposite, double yOpposite, double[] intersection)
			{
				int result = findLineSegmentIntersection(cellBounds.getX(), cellBounds.getY(), cellBounds.getX() + cellBounds.getWidth(), cellBounds.getY(),
						cellBounds.getCenterX(), cellBounds.getCenterY(), xOpposite, yOpposite, intersection);
				return result == 1;
			}
		},
		RIGHT
		{
			@Override
			boolean findIntersection(PBounds cellBounds, double xOpposite, double yOpposite, double[] intersection)
			{
				int result = findLineSegmentIntersection(cellBounds.getX(), cellBounds.getY() + cellBounds.getHeight(),
						cellBounds.getX() + cellBounds.getWidth(), cellBounds.getY() + cellBounds.getHeight(), cellBounds.getCenterX(),
						cellBounds.getCenterY(), xOpposite, yOpposite, intersection);
				return result == 1;
			}
		},
		BOTTOM
		{
			@Override
			boolean findIntersection(PBounds cellBounds, double xOpposite, double yOpposite, double[] intersection)
			{
				int result = findLineSegmentIntersection(cellBounds.getX(), cellBounds.getY(), cellBounds.getX(), cellBounds.getY() + cellBounds.getHeight(),
						cellBounds.getCenterX(), cellBounds.getCenterY(), xOpposite, yOpposite, intersection);
				return result == 1;
			}
		},
		LEFT
		{
			@Override
			boolean findIntersection(PBounds cellBounds, double xOpposite, double yOpposite, double[] intersection)
			{
				int result = findLineSegmentIntersection(cellBounds.getX() + cellBounds.getWidth(), cellBounds.getY(),
						cellBounds.getX() + cellBounds.getWidth(), cellBounds.getY() + cellBounds.getHeight(), cellBounds.getCenterX(),
						cellBounds.getCenterY(), xOpposite, yOpposite, intersection);
				return result == 1;
			}
		};

		abstract boolean findIntersection(PBounds cellBounds, double xOpposite, double yOpposite, double[] intersection);
	}

	/**
	 * Compute the intersection between two line segments, or two lines of infinite length.
	 * 
	 * @param x0
	 *            X coordinate first end point first line segment.
	 * @param y0
	 *            Y coordinate first end point first line segment.
	 * @param x1
	 *            X coordinate second end point first line segment.
	 * @param y1
	 *            Y coordinate second end point first line segment.
	 * @param x2
	 *            X coordinate first end point second line segment.
	 * @param y2
	 *            Y coordinate first end point second line segment.
	 * @param x3
	 *            X coordinate second end point second line segment.
	 * @param y3
	 *            Y coordinate second end point second line segment.
	 * @param intersection
	 *            [2] Preallocated by caller to double[2]
	 * @return -1 if lines are parallel (x,y unset), -2 if lines are parallel and overlapping (x, y center) 0 if
	 *         intesrection outside segments (x,y set) +1 if segments intersect (x,y set)
	 */
	public static int findLineSegmentIntersection(double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3, double[] intersection)
	{
		// TODO: Make limit depend on input domain
		final double LIMIT = 1e-5;
		final double INFINITY = 1e10;

		double x, y;

		//
		// Convert the lines to the form y = ax + b
		//

		// Slope of the two lines
		double a0 = equals(x0, x1, LIMIT) ? INFINITY : (y0 - y1) / (x0 - x1);
		double a1 = equals(x2, x3, LIMIT) ? INFINITY : (y2 - y3) / (x2 - x3);

		double b0 = y0 - a0 * x0;
		double b1 = y2 - a1 * x2;

		// Check if lines are parallel
		if (equals(a0, a1))
		{
			if (!equals(b0, b1))
				return -1; // Parallell non-overlapping

			else
			{
				if (equals(x0, x1))
				{
					if (Math.min(y0, y1) < Math.max(y2, y3) || Math.max(y0, y1) > Math.min(y2, y3))
					{
						double twoMiddle = y0 + y1 + y2 + y3 - min(y0, y1, y2, y3) - max(y0, y1, y2, y3);
						y = (twoMiddle) / 2.0;
						x = (y - b0) / a0;
					}
					else
						return -1; // Parallell non-overlapping
				}
				else
				{
					if (Math.min(x0, x1) < Math.max(x2, x3) || Math.max(x0, x1) > Math.min(x2, x3))
					{
						double twoMiddle = x0 + x1 + x2 + x3 - min(x0, x1, x2, x3) - max(x0, x1, x2, x3);
						x = (twoMiddle) / 2.0;
						y = a0 * x + b0;
					}
					else
						return -1;
				}

				intersection[0] = x;
				intersection[1] = y;
				return -2;
			}
		}

		// Find correct intersection point
		if (equals(a0, INFINITY))
		{
			x = x0;
			y = a1 * x + b1;
		}
		else if (equals(a1, INFINITY))
		{
			x = x2;
			y = a0 * x + b0;
		}
		else
		{
			x = -(b0 - b1) / (a0 - a1);
			y = a0 * x + b0;
		}

		intersection[0] = x;
		intersection[1] = y;

		// Then check if intersection is within line segments
		double distanceFrom1;
		if (equals(x0, x1))
		{
			if (y0 < y1)
				distanceFrom1 = y < y0 ? length(x, y, x0, y0) : y > y1 ? length(x, y, x1, y1) : 0.0;
			else
				distanceFrom1 = y < y1 ? length(x, y, x1, y1) : y > y0 ? length(x, y, x0, y0) : 0.0;
		}
		else
		{
			if (x0 < x1)
				distanceFrom1 = x < x0 ? length(x, y, x0, y0) : x > x1 ? length(x, y, x1, y1) : 0.0;
			else
				distanceFrom1 = x < x1 ? length(x, y, x1, y1) : x > x0 ? length(x, y, x0, y0) : 0.0;
		}

		double distanceFrom2;
		if (equals(x2, x3))
		{
			if (y2 < y3)
				distanceFrom2 = y < y2 ? length(x, y, x2, y2) : y > y3 ? length(x, y, x3, y3) : 0.0;
			else
				distanceFrom2 = y < y3 ? length(x, y, x3, y3) : y > y2 ? length(x, y, x2, y2) : 0.0;
		}
		else
		{
			if (x2 < x3)
				distanceFrom2 = x < x2 ? length(x, y, x2, y2) : x > x3 ? length(x, y, x3, y3) : 0.0;
			else
				distanceFrom2 = x < x3 ? length(x, y, x3, y3) : x > x2 ? length(x, y, x2, y2) : 0.0;
		}

		return equals(distanceFrom1, 0.0) && equals(distanceFrom2, 0.0) ? 1 : 0;
	}

	/**
	 * Check if two points are on the same side of a given line. Algorithm from Sedgewick page 350.
	 * 
	 * @param x0
	 *            , y0, x1, y1 The line.
	 * @param px0
	 *            , py0 First point.
	 * @param px1
	 *            , py1 Second point.
	 * @return <0 if points on opposite sides. =0 if one of the points is exactly on the line >0 if points on same side.
	 */
	private static int sameSide(double x0, double y0, double x1, double y1, double px0, double py0, double px1, double py1)
	{
		int sameSide = 0;

		double dx = x1 - x0;
		double dy = y1 - y0;
		double dx1 = px0 - x0;
		double dy1 = py0 - y0;
		double dx2 = px1 - x1;
		double dy2 = py1 - y1;

		// Cross product of the vector from the endpoint of the line to the point
		double c1 = dx * dy1 - dy * dx1;
		double c2 = dx * dy2 - dy * dx2;

		if (c1 != 0 && c2 != 0)
			sameSide = c1 < 0 != c2 < 0 ? -1 : 1;
		else if (dx == 0 && dx1 == 0 && dx2 == 0)
			sameSide = !isBetween(y0, y1, py0) && !isBetween(y0, y1, py1) ? 1 : 0;
		else if (dy == 0 && dy1 == 0 && dy2 == 0)
			sameSide = !isBetween(x0, x1, px0) && !isBetween(x0, x1, px1) ? 1 : 0;

		return sameSide;
	}

	/**
	 * Return true if c is between a and b.
	 */
	private static boolean isBetween(double a, double b, double c)
	{
		return b > a ? c >= a && c <= b : c >= b && c <= a;
	}

	/**
	 * Return smallest of four numbers.
	 * 
	 * @param a
	 *            First number to find smallest among.
	 * @param b
	 *            Second number to find smallest among.
	 * @param c
	 *            Third number to find smallest among.
	 * @param d
	 *            Fourth number to find smallest among.
	 * @return Smallest of a, b, c and d.
	 */
	private static double min(double a, double b, double c, double d)
	{
		return Math.min(Math.min(a, b), Math.min(c, d));
	}

	/**
	 * Return largest of four numbers.
	 * 
	 * @param a
	 *            First number to find largest among.
	 * @param b
	 *            Second number to find largest among.
	 * @param c
	 *            Third number to find largest among.
	 * @param d
	 *            Fourth number to find largest among.
	 * @return Largest of a, b, c and d.
	 */
	private static double max(double a, double b, double c, double d)
	{
		return Math.max(Math.max(a, b), Math.max(c, d));
	}

	/**
	 * Check if two double precision numbers are "equal", i.e. close enough to a prespecified limit.
	 * 
	 * @param a
	 *            First number to check
	 * @param b
	 *            Second number to check
	 * @return True if the twho numbers are "equal", false otherwise
	 */
	private static boolean equals(double a, double b)
	{
		return equals(a, b, 1.0e-5);
	}

	/**
	 * Check if two double precision numbers are "equal", i.e. close enough to a given limit.
	 * 
	 * @param a
	 *            First number to check
	 * @param b
	 *            Second number to check
	 * @param limit
	 *            The definition of "equal".
	 * @return True if the twho numbers are "equal", false otherwise
	 */
	private static boolean equals(double a, double b, double limit)
	{
		return Math.abs(a - b) < limit;
	}

	/**
	 * Compute the length of the line from (x0,y0) to (x1,y1)
	 * 
	 * @param x0
	 *            , y0 First line end point.
	 * @param x1
	 *            , y1 Second line end point.
	 * @return Length of line from (x0,y0) to (x1,y1).
	 */
	public static double length(double x0, double y0, double x1, double y1)
	{
		double dx = x1 - x0;
		double dy = y1 - y0;

		return Math.sqrt(dx * dx + dy * dy);
	}
}
