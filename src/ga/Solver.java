package ga;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Observable;
import java.util.TreeSet;

import ga.polygon.PolygonModel;
import ga.primitives.Interval;
import ga.primitives.IntervalEdge;
import ga.primitives.PLine;

public class Solver extends Observable {
	public static int SOLVER_SPEED = 300;
	private PolygonModel pl, pr;
	private boolean isSolved = false;
	private boolean isActive = false;
	
	private double minDist = Double.MAX_VALUE;
	private double[] res = new double[2];

	private double[] last_check = new double[2];
	private double[] last_check_point = new double[2];
	private double last_check_len;

	public Solver(PolygonModel pl, PolygonModel pr) {
		super();
		this.pl = pl;
		this.pr = pr;
	}

	private void _wait() {
		try {
			Thread.sleep(SOLVER_SPEED);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.setChanged();
		this.notifyObservers();
	}

	private void checkRes(PLine line, Interval i) {
		last_check_point[0] = line.getX0();
		last_check_point[1] = line.getY0();
		if (i == null) {
			last_check_len = 0;
			_wait();
			return;
		}
		double x = i.getParent().getXForY(line.getY0());
		double dist = Math.abs(x - line.getX0());
		last_check[0] = Math.min(line.getX0(), x);
		last_check[1] = line.getY0();
		last_check_len = dist;
		if (dist < minDist) {
			minDist = dist;
			res[0] = last_check[0];
			res[1] = line.getY0();
		}
		_wait();
	}

	public double[] getRes() {
		pl.getVisibleProjections();
		pr.getVisibleProjections();
		isActive = true;
		for (PLine line : pl.getLines()) {
			Interval i = pr.findInterval(line.getY0());
			checkRes(line, i);
		}

		for (PLine line : pr.getLines()) {
			Interval i = pl.findInterval(line.getY0());
			checkRes(line, i);
		}
		isSolved = true;
		isActive = false;
		this.setChanged();
		this.notifyObservers();
		return res;
	}

	public boolean isSolved() {
		return isSolved;
	}

	public boolean isActive() {
		return isActive;
	}

	public double getMinDist() {
		return minDist;
	}

	public void draw(Graphics2D g, int offset) {
		g.setPaint(Color.MAGENTA);
		if (isActive && !isSolved) {
			Line2D line = new Line2D.Double((int) last_check[0], (int) last_check[1],
					(int) (last_check[0] + last_check_len), (int) (last_check[1]));
			g.draw(line);
			g.fillRect((int) last_check_point[0] - 3, (int) (last_check_point[1] - 3), 6, 6);

			g.setPaint(Color.GREEN);
			Line2D line1 = new Line2D.Double((int) res[0], (int) res[1], (int) (res[0] + minDist), (int) (res[1]));
			g.draw(line1);
		}
		if (isSolved) {
			g.setPaint(Color.GREEN);
			Line2D line1 = new Line2D.Double((int) res[0]+ offset, (int) res[1], (int) (res[0] + minDist),
					(int) (res[1]));
			g.draw(line1);
		}
	}

}
