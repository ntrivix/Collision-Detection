package ga.primitives;

import java.awt.Color;
import java.awt.Graphics2D;

import ga.primitives.Interval.Projection;

public class IntervalEdge implements Comparable<IntervalEdge> {
	
	public static final int START = 0;
	public static final int END = 1;
	
	private int startOrEnd;
	
	private double position;
	
	private Interval interval;

	public IntervalEdge(int startOrEnd, double position, Interval interval) {
		super();
		this.startOrEnd = startOrEnd;
		this.position = position;
		this.interval = interval;
	}

	public double getPosition() {
		return position;
	}

	public void setPosition(double position) {
		this.position = position;
	}

	public int getStartOrEnd() {
		return startOrEnd;
	}

	public Interval getInterval() {
		return interval;
	}

	@Override
	public int compareTo(IntervalEdge o) {
		if (this.getPosition() - o.getPosition() >= 0.00000001)
			return 1;
		if (Math.abs(this.getPosition() - o.getPosition()) < 0.00000001)
		{
			if (startOrEnd < o.getStartOrEnd())
				return -1;
			if (startOrEnd > o.getStartOrEnd())
				return 1;
			
			//ako su oba starta ili oba enda
			double x_deviation = this.interval.getParent().getTopx() - o.interval.getParent().getTopx();
			double x_deviation_end = this.interval.getParent().getBottomx() - o.interval.getParent().getBottomx();
			if (startOrEnd == 0){
				//oba pocetka i jednaki su
				int pomres1 = 1;
				int pomres0 = -1;
				if (interval.getProjection() == Projection.LEFT)
				{
					pomres1 = -1;
					pomres0 = 1;
				}
				if (Math.abs(x_deviation) < 0.00000001){
					//pocetci su jednaki
					//uporedi krajeve
					
					if (x_deviation_end < 0){
						return pomres0;
					} else {
						return pomres1;
					}
				} else {
					if (x_deviation < 0){
						return pomres0;
					} else {
						return pomres1;
					}
				}
			} else {
				//oba kraja
				int start_cmp = this.interval.getStart().compareTo(o.interval.getStart());
				if (start_cmp > 0){
					return -1;
				}
				else
					return 1;
			}
			
			
			
		}
		return -1;
	}


	@Override
	public String toString() {
		return "*"+startOrEnd+"* S-"+interval.getStart().getPosition()+" E-"+interval.getEnd().getPosition()+" "+System.identityHashCode(interval.getParent())+"    position "+position;
	}
	
	public void draw(Graphics2D g){
		g.setColor(Color.ORANGE);
		g.fillRect((int)interval.getParent().getXForY(position)-5, (int)position-5, 10, 10);
	}
	
}
