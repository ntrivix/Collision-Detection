package ga.primitives;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import ga.polygon.PolygonModel;

public class PLine {
	private double x0, y0, x1, y1;
	private double topx, bottomx;
	private Interval projection;
	private Interval projection_solve;
	private PolygonModel pModel;

	public PLine(double x0, double y0, double x1, double y1, PolygonModel pModel) {
		super();

		this.x0 = x0;
		this.y0 = y0;
		this.x1 = x1;
		this.y1 = y1;

		if (y1 < y0) {
			topx = x1;
			bottomx = x0;
		} else {
			topx = x0;
			bottomx = x1;
		}

		this.pModel = pModel;
		projection = new Interval(y0, y1);
		projection.setProjection(pModel.getOrijentation());
		projection.setParent(this);
	}

	public double getX0() {
		return x0;
	}

	public double getY0() {
		return y0;
	}

	public double getX1() {
		return x1;
	}

	public double getY1() {
		return y1;
	}

	public Interval getProjection() {
		return projection;
	}

	public void draw(Graphics2D g) {
		
		if (!projection.isActive())
			draw(g, Color.LIGHT_GRAY);
		else {
			if (projection.isProcessing())
				draw(g, Color.GREEN);
			else
				draw(g, Color.black);
		}
	}
	
	public void draw(Graphics2D g, Color c) {
		g.setPaint(c);
		
		Line2D line = new Line2D.Double(x0, y0, x1, y1);
		g.draw(line);

		g.setPaint(Color.red);
		if (projection_solve != null) {
			double pStarty = projection_solve.getStart().getPosition();
			double pEndy = projection_solve.getEnd().getPosition();
			line = new Line2D.Double(getXForY(pStarty), pStarty, getXForY(pEndy), pEndy);
			g.draw(line);
		}
		 

	}

	public double getXForY(double y) {
		// if (y - y0 >= 0.0000000001 && y - y1 <= -0.000000000001) {
		if (Math.abs(x0 - x1) < 0.0000001) {
			return x0;
		}
		double k = (y1 - y0) / (x1 - x0);
		double n = y0 - k * x0;
		return (y - n) / k;
		// }
		// return -1;
	}

	public double getTopx() {
		return topx;
	}

	public double getBottomx() {
		return bottomx;
	}

	public Interval getProjection_solve() {
		return projection_solve;
	}

	public void setProjection_solve(Interval projection_solve) {
		this.projection_solve = projection_solve;
	}

	@Override
	public String toString() {
		return Integer.toString(System.identityHashCode(this));
	}

}
