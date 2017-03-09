package ga.primitives;

import java.awt.Graphics2D;
import java.util.ArrayList;

/**
 * @author Nikola
 *
 */
public class Interval implements Comparable<Interval>{
	
	private IntervalEdge start, end;
	private PLine parent;
	private Projection projection = Projection.RIGHT;
	
	private boolean active = true;
	private boolean processing = false;
	
	public static enum Projection {
		LEFT, RIGHT
	}
	
	public Interval(double p1, double p2){
		if (p1 > p2){
			double pom = p1;
			p1 = p2;
			p2 = pom;
		}
		start = new IntervalEdge(IntervalEdge.START, p1, this);
		end = new IntervalEdge(IntervalEdge.END, p2, this);
	}

	public IntervalEdge getStart() {
		return start;
	}

	public IntervalEdge getEnd() {
		return end;
	}

	public PLine getParent() {
		return parent;
	}

	public void setParent(PLine parent) {
		this.parent = parent;
	}
	
	public void setProjection(Projection p){
		this.projection = p;
	}

	public Projection getProjection() {
		return projection;
	}
	
	public int contains(double v){
		double start_d = start.getPosition()-v;
		double end_d = end.getPosition()-v;
		if (start_d > 0.0000001)
			return -1;
		if (end_d < -0.0000001)
			return 1;
		return 0;
	
	}

	@Override
	public int compareTo(Interval o) {
		System.out.println("compare "+this.getParent()+" -> "+o.getParent());
		if (this == o)
			return 0;
		
		int res1 = 1;
		int res0 = -1;
		
		if (this.projection == Projection.LEFT){
			res1 = -1;
			res0 = 1;
		}
		
		if (contains(o.getStart().getPosition()) == 0){
			double x = parent.getXForY(o.getStart().getPosition());
			double deviation = x - o.getParent().getTopx();
			if (deviation < -0.001){
				System.out.println(res1+"**");
				return res1;
			}
			if (deviation > 0.001){
				System.out.println(res0+"**");
				return res0;
			}
		}
		if (contains(o.getEnd().getPosition()) == 0){
			double x = parent.getXForY(o.getEnd().getPosition());
			double deviation = x - o.getParent().getBottomx();
			if (deviation < -0.001){
				System.out.println(res1+"***");
				return res1;
			}
			if (deviation > 0.001){
				System.out.println(res0+"***");
				return res0;
			}
		}
		if (o.contains(getStart().getPosition()) == 0){
			double x = o.parent.getXForY(getStart().getPosition());
			double deviation = x - getParent().getTopx();
			if (deviation < -0.001){
				System.out.println(res0+"****");
				return res0;
			}
			if (deviation > 0.001){
				System.out.println(res1+"****");
				return res1;
			}
		}
		if (o.contains(getEnd().getPosition()) == 0){
			double x = o.parent.getXForY(getEnd().getPosition());
			double deviation = x - getParent().getBottomx();
			if (deviation < -0.001){
				System.out.println(res0+"*****");
				return res0;
			}
			if (deviation > 0.001){
				System.out.println(res1+"*****");
				return res1;
			}
		}
		if (this.end.getPosition() - o.end.getPosition() > 0.01 )
		{
			System.out.println(res0+"-");
			return res0;
		}
		System.out.println(1+"-");
		return 1;
	}
	
	@Override
	public String toString() {
		return Integer.toString(System.identityHashCode(this));
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isProcessing() {
		return processing;
	}

	public void setProcessing(boolean processing) {
		this.processing = processing;
	}
	
	
	
}
