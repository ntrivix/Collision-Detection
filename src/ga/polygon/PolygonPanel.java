package ga.polygon;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import ga.AppView;
import ga.Solver;
import ga.primitives.Interval.Projection;

public class PolygonPanel extends JPanel implements Observer {

	private PolygonModel p1, p2;
	private static PolygonPanel instance;
	private Solver solver;
	private double[] res;

	private Line2D.Double drawingLine;
	private int moveOffset = 0;

	public static PolygonPanel getInstance() {
		if (instance == null) {
			instance = new PolygonPanel();
		}
		return instance;
	}

	private PolygonPanel() {
		super();
		setBackground(Color.white);
		resetAll();
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				super.mouseMoved(e);
				try {
					if (!p1.isClosed()) {

						drawingLine = new Line2D.Double(p1.getLastPoint()[0], p1.getLastPoint()[1], e.getX(), e.getY());
						repaint();
						return;
					}
					if (!p2.isClosed()) {

						drawingLine = new Line2D.Double(p2.getLastPoint()[0], p2.getLastPoint()[1], e.getX(), e.getY());
						repaint();
						return;
					}
				} catch (Exception noLastPoint) {
					drawingLine = null;
					repaint();
				}
				drawingLine = null;
			}

		});
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				super.mouseClicked(e);

				if (p1.isClosed()) {
					try {
						p2.addPoint(e.getX(), e.getY());
					} catch (PolygonClosedException e1) {

					}
				} else {
					try {
						p1.addPoint(e.getX(), e.getY());
					} catch (PolygonClosedException e1) {

					}
				}
			}

		});
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		if (drawingLine != null)
			g2.draw(drawingLine);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(moveOffset, 0);
		p1.draw(g2);
		g2.translate(-moveOffset, 0);
		p2.draw(g2);
		
		solver.draw(g2, moveOffset);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!solver.isActive() && p1.isClosed() && p2.isClosed()) {
			AppView.getInstance().getToolbarView().getStart().setEnabled(true);
		} else {
			AppView.getInstance().getToolbarView().getStart().setEnabled(false);
		}
		repaint();
	}

	public void resetAll() {
		moveOffset = 0;
		drawingLine = null;
		res = new double[2];
		p1 = new PolygonModel(Projection.RIGHT);
		p2 = new PolygonModel(Projection.LEFT);
		solver = new Solver(p1, p2);
		p1.addObserver(this);
		p2.addObserver(this);
		solver.addObserver(this);
		repaint();
	}

	public PolygonModel getP1() {
		return p1;
	}

	public PolygonModel getP2() {
		return p2;
	}

	public double[] getRes() {
		return res;
	}

	public void setRes(double[] res) {
		this.res = res;
		repaint();
	}

	public Solver getSolver() {
		return solver;
	}

	public double getMoveOffset() {
		return moveOffset;
	}

	public void setMoveOffset(int moveOffset) {
		this.moveOffset = moveOffset;
	}

}
