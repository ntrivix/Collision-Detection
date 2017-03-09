package ga.polygon;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Observable;
import java.util.PriorityQueue;

import ga.Solver;
import ga.primitives.Interval;
import ga.primitives.IntervalEdge;
import ga.primitives.PLine;
import ga.rbTree.RedBlackTree;
import ga.primitives.Interval.Projection;

public class PolygonModel extends Observable {
	private ArrayList<PLine> lines = new ArrayList<>();
	private ArrayList<IntervalEdge> i_edges = new ArrayList<>();
	
	private ArrayList<Interval> visibleProjections;
	private IntervalEdge sweepLine;
	private PLine heap_top;

	private double start_x = -1, start_y = -1;
	private boolean closed = false;
	private Projection p;

	public PolygonModel(Projection p) {
		super();
		this.p = p;
		visibleProjections = new ArrayList<>();
	}

	private PLine addLine(double x0, double y0, double x1, double y1) throws PolygonClosedException {
		PLine resLine = null;
		if (!isClosed()) {
			if (lines.isEmpty() || (Math.sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0)) > 5)) {
				if (lines.isEmpty()) {
					start_x = x0;
					start_y = y0;
				}
				if (Math.abs(x1 - start_x) < 5 && Math.abs(y1 - start_y) < 5) {
					closed = true;
					x1 = start_x;
					y1 = start_y;
				}
				lines.add(resLine = new PLine(x0, y0, x1, y1, this));
				i_edges.add(resLine.getProjection().getStart());
				i_edges.add(resLine.getProjection().getEnd());
				setChanged();
				notifyObservers();
			}
		} else
			throw new PolygonClosedException();
		return resLine;
	}

	public PLine addPoint(double x, double y) throws PolygonClosedException {
		PLine res = null;
		if (!lines.isEmpty()) {
			return addLine(lines.get(lines.size() - 1).getX1(), lines.get(lines.size() - 1).getY1(), x, y);
		} else {
			if (start_x < 0 && start_y < 0) {
				start_x = x;
				start_y = y;
			} else {
				return addLine(start_x, start_y, x, y);
			}
		}
		return res;
	}

	protected boolean isClosed() {
		return closed;
	}

	public ArrayList<IntervalEdge> getI_edges() {
		return i_edges;
	}

	public ArrayList<Interval> getVisibleProjections() {
		visibleProjections = new ArrayList<>();
		Collections.sort(i_edges);
		RedBlackTree<Interval> pq = new RedBlackTree<>();
		Double last_interval_end =null;
		for (IntervalEdge intervalEdge : i_edges) {
			sweepLine = intervalEdge;
			intervalEdge.getInterval().setProcessing(true);
			System.out.println(intervalEdge);

			PLine last_interval_parent;
			
			try {
				heap_top = pq.treeMinimum(pq.getRoot()).key.getParent();
				System.out.println(System.identityHashCode(heap_top));
			} catch (Exception e) {
				heap_top = null;
			}
			
		
			try {
				last_interval_parent = visibleProjections.get(visibleProjections.size() - 1).getParent();
			} catch (Exception e) {
				last_interval_parent = null;
			}
			setChanged();
			notifyObservers();
			if (heap_top != null && heap_top == last_interval_parent) {
				visibleProjections.get(visibleProjections.size() - 1).getEnd().setPosition(intervalEdge.getPosition());
			} else if (last_interval_end != null && Math.abs(last_interval_end-intervalEdge.getPosition())>0.000001){
				Interval interval = new Interval(
						last_interval_end,
						intervalEdge.getPosition());
				interval.setParent(pq.treeMinimum(pq.getRoot()).key.getParent());
				visibleProjections.add(interval);
				pq.treeMinimum(pq.getRoot()).key.getParent().setProjection_solve(interval);
			}
			if (intervalEdge.getStartOrEnd() == IntervalEdge.START) {
				pq.insert(intervalEdge.getInterval());
				System.out.println(pq.treeMinimum(pq.getRoot()).key.getParent());
			} else {
				intervalEdge.getInterval().setActive(false);
				pq.remove(intervalEdge.getInterval());
			}
			try {
				last_interval_end = visibleProjections.get(visibleProjections.size()-1).getEnd().getPosition();
			} catch (Exception e) {
				last_interval_end = intervalEdge.getPosition();
			}	
			setChanged();
			notifyObservers();
			try {
				Thread.sleep(Solver.SOLVER_SPEED);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (Interval proj : visibleProjections) {
			proj.getParent().setProjection_solve(proj);
		}
		heap_top = null;
		sweepLine = null;
		setChanged();
		notifyObservers();
		return visibleProjections;
	}

	public Projection getOrijentation() {
		return p;
	}

	public Interval findInterval(double y) {
		int top = 0;
		int bottom = visibleProjections.size()-1;
		while (top <= bottom) {
			int mid = (bottom + top) / 2;
			int pom = visibleProjections.get(mid).contains(y);
			if (pom == 0)
				return visibleProjections.get(mid);
			if (pom < 0) {
				// u gornjoj polovini
				bottom = mid - 1;
			} else {
				// u donjoj polovini
				top = mid + 1;
			}
		}
		return null;
	}

	public double getIntersectionPoint(double y) {
		Interval i = findInterval(y);
		if (i != null) {
			return i.getParent().getXForY(y);
		}
		return -1;
	}

	public void draw(Graphics2D g) {
		for (PLine pLine : lines) {
			pLine.draw(g);
		}
		if (sweepLine != null)
			sweepLine.draw(g);
		if (heap_top != null)
			heap_top.draw(g, Color.ORANGE);
	}

	public ArrayList<PLine> getLines() {
		return lines;
	}

	public double[] getLastPoint(){

		if (lines.isEmpty()){
			if (start_x > 0){
				return new double[]{start_x, start_y};
			}
			return null;
		}
		return new double[]{lines.get(lines.size()-1).getX1(),lines.get(lines.size()-1).getY1()};
	}
	
}
