package calico.utils;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;

public class Geometry {

    public static double angle(Point2D newMidPoint, Point2D newBoundsRightCorner) {
    	 
        double dx = newBoundsRightCorner.getX() - newMidPoint.getX();
        double dy = newBoundsRightCorner.getY() - newMidPoint.getY();
        double angle = 0.0d;
 
        if (dx == 0.0) {
            if(dy == 0.0)     angle = 0.0;
            else if(dy > 0.0) angle = Math.PI / 2.0;
            else              angle = (Math.PI * 3.0) / 2.0;
        }
        else if(dy == 0.0) {
            if(dx > 0.0)      angle = 0.0;
            else              angle = Math.PI;
        }
        else {
            if(dx < 0.0)      angle = Math.atan(dy/dx) + Math.PI;
            else if(dy < 0.0) angle = Math.atan(dy/dx) + (2*Math.PI);
            else              angle = Math.atan(dy/dx);
        }
        return angle;
    }	
    
	public static Point2D getMidPoint2D(Polygon p)
	{
		Point2D ret;
		if (p.getBounds() != null)
		{
			Rectangle bounds = p.getBounds();
			double xAvg = bounds.getCenterX();
			double yAvg = bounds.getCenterY();
			
			ret = new Point2D.Double(xAvg, yAvg);
		}
		else
		{
			long xSum = 0, ySum = 0;
			int totalPoints = p.npoints;
			for (int i = 0; i < p.npoints; i++)
			{
				if (p.xpoints[i] == 0 || p.ypoints[i] == 0)
				{
					totalPoints--;
					continue;
				}
				xSum += p.xpoints[i];
				ySum += p.ypoints[i];
			}
			double xAvg = ((double)xSum) / totalPoints;
			double yAvg = ((double)ySum) / totalPoints;
			
			ret = new Point2D.Double(xAvg, yAvg);
		}
		
		if (ret.getX() == 0 || ret.getY() == 0)
		{
			System.err.println("A midpoint returned zero!");
//			(new Exception()).printStackTrace();
		}
		return ret;
	}
	
	public static Polygon getPolyFromPath(PathIterator it) {

		Polygon p = new Polygon();
		float[] point = new float[6];
		int type;
		while (!it.isDone()) {
			type = it.currentSegment(point);
			if (type == PathIterator.SEG_MOVETO
					|| type == PathIterator.SEG_LINETO
					|| type == PathIterator.SEG_CLOSE)
				p.addPoint(Math.round(point[0]), Math.round(point[1]));
			else if (type == PathIterator.SEG_QUADTO) {
				p.addPoint(Math.round(point[0]), Math.round(point[1]));
				p.addPoint(Math.round(point[2]), Math.round(point[3]));
			} else if (type == PathIterator.SEG_CUBICTO) {
				p.addPoint(Math.round(point[0]), Math.round(point[1]));
				p.addPoint(Math.round(point[2]), Math.round(point[3]));
				p.addPoint(Math.round(point[4]), Math.round(point[5]));
			}
			else
			{
				System.out.println("Missed a pointset from path! It is size: " + point.length + ", and type: " + type);
				(new Exception()).printStackTrace();
			}
			it.next();
		}
		return p;
	}
	
	public static GeneralPath getBezieredPoly(Polygon pts)
	{
		GeneralPath p = new GeneralPath();
		if (pts.npoints > 0)
		{
			p.moveTo(pts.xpoints[0], pts.ypoints[0]);
			if (pts.npoints >= 4)
			{
				for (int i = 1; i+3 < pts.npoints; i += 3)
				{
					p.curveTo(pts.xpoints[i], pts.ypoints[i], 
							pts.xpoints[i+1], pts.ypoints[i+1], 
							pts.xpoints[i+2], pts.ypoints[i+2]);
				}
			}
			else
			{
				for (int i = 1; i < pts.npoints; i++)
				{
					p.lineTo(pts.xpoints[i], pts.ypoints[i]);
				}
			}
		}
		return p;
	}
	
	public static Polygon getRoundedPolygon(Rectangle newBounds) {
		return getRoundedPolygon(newBounds, 0);
	}
	
	public static Polygon getRoundedPolygon(Rectangle newBounds, int padding) {
		Polygon coords = new Polygon();

		// The below was created by playing around with the applet from this
		// page: http://www.cse.unsw.edu.au/~lambert/splines/Bezier.html
		coords.addPoint(newBounds.x - padding, newBounds.y - padding + 5);
		coords.addPoint(newBounds.x - padding, newBounds.y - padding + 5);
		coords.addPoint(newBounds.x - padding, newBounds.y - padding); // upper
																		// left
																		// corner
		coords.addPoint(newBounds.x - padding + 5, newBounds.y - padding);
		coords.addPoint(newBounds.x - padding + 5, newBounds.y - padding);
		coords.addPoint(newBounds.x - padding + 5, newBounds.y - padding);

		coords.addPoint(newBounds.x + newBounds.width + padding - 5,
				newBounds.y - padding);
		coords.addPoint(newBounds.x + newBounds.width + padding - 5,
				newBounds.y - padding);
		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				- padding); // upper right corner
		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				- padding + 5);
		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				- padding + 5);
		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				- padding + 5);

		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				+ newBounds.height + padding - 5);
		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				+ newBounds.height + padding - 5);
		coords.addPoint(newBounds.x + newBounds.width + padding, newBounds.y
				+ newBounds.height + padding); // bottom right corner
		coords.addPoint(newBounds.x + newBounds.width + padding - 5,
				newBounds.y + newBounds.height + padding);
		coords.addPoint(newBounds.x + newBounds.width + padding - 5,
				newBounds.y + newBounds.height + padding);
		coords.addPoint(newBounds.x + newBounds.width + padding - 5,
				newBounds.y + newBounds.height + padding);

		coords.addPoint(newBounds.x - padding + 5, newBounds.y
				+ newBounds.height + padding);
		coords.addPoint(newBounds.x - padding + 5, newBounds.y
				+ newBounds.height + padding);
		coords.addPoint(newBounds.x - padding, newBounds.y + newBounds.height
				+ padding); // bottom left corner
		coords.addPoint(newBounds.x - padding, newBounds.y + newBounds.height
				+ padding - 5);
		coords.addPoint(newBounds.x - padding, newBounds.y + newBounds.height
				+ padding - 5);
		coords.addPoint(newBounds.x - padding, newBounds.y + newBounds.height
				+ padding - 5);

		coords.addPoint(newBounds.x - padding, newBounds.y - padding + 5); // connect
																			// back
																			// to
																			// top

		return coords;
	}
	
	/**
	 * Find the point on the line defined by x0,y0,x1,y1 a given fraction
	 * from x0,y0. 2D version of method above..
	 * 
	 * @param  x0, y0         First point defining the line
	 * @param  x1, y1         Second point defining the line
	 * @param  fractionFrom0  Distance from (x0,y0)
	 * @return x, y           Coordinate of point we are looking for
	 */
	public static double[] computePointOnLine (double x0, double y0,
			double x1, double y1,
			double fractionFrom0)
	{
		double[] p0 = {x0, y0, 0.0};
		double[] p1 = {x1, y1, 0.0};

		double[] p = Geometry.computePointOnLine (p0, p1, fractionFrom0);

		double[] r = {p[0], p[1]};
		return r;
	}
	
	/**
	 * Find the point on the line p0,p1 [x,y,z] a given fraction from p0.
	 * Fraction of 0.0 whould give back p0, 1.0 give back p1, 0.5 returns
	 * midpoint of line p0,p1 and so on. F
	 * raction can be >1 and it can be negative to return any point on the
	 * line specified by p0,p1.
	 * 
	 * @param p0              First coordinale of line [x,y,z].
	 * @param p0              Second coordinale of line [x,y,z].   
	 * @param fractionFromP0  Point we are looking for coordinates of
	 * @param p               Coordinate of point we are looking for
	 */
	public static double[] computePointOnLine (double[] p0, double[] p1, 
			double fractionFromP0)
	{
		double[] p = new double[3];

		p[0] = p0[0] + fractionFromP0 * (p1[0] - p0[0]);
		p[1] = p0[1] + fractionFromP0 * (p1[1] - p0[1]);
		p[2] = p0[2] + fractionFromP0 * (p1[2] - p0[2]);  

		return p;
	}
	
}
