package ga;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ga.polygon.PolygonPanel;

public class ToolbarView extends JToolBar {

	JSlider delay;
	JButton start, stop, pause, reset;
	
	public ToolbarView() {
		super("Toolbar");
		
		start = new JButton("Start");
		start.setEnabled(false);
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {						
						PolygonPanel.getInstance().getSolver().getRes();
						int move = 1;
						if (PolygonPanel.getInstance().getSolver().getMinDist() < Double.MAX_VALUE-1){
							int i = 0;
							while (PolygonPanel.getInstance().getSolver().getMinDist() - move * i > 0){
								PolygonPanel.getInstance().setMoveOffset(move*i);
								PolygonPanel.getInstance().repaint();
								try {
									Thread.sleep(50);
								} catch (InterruptedException e) {
								}
								i += 3;
							}
						}
						PolygonPanel.getInstance().setMoveOffset((int)Math.round(PolygonPanel.getInstance().getSolver().getMinDist()));
						PolygonPanel.getInstance().repaint();
					}
				});
				t.start();
			}
		});
		
		
		
		reset = new JButton("Reset");
		reset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				PolygonPanel.getInstance().resetAll();
			}
		});
		
		delay = new JSlider(JSlider.HORIZONTAL, 100, 5000, 300);
		delay.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				Solver.SOLVER_SPEED = delay.getValue();
			}
		});
		
		add(delay);
		add(start);
		add(reset);
	}

	public JSlider getDelay() {
		return delay;
	}

	public JButton getStart() {
		return start;
	}

	public JButton getStop() {
		return stop;
	}

	public JButton getPause() {
		return pause;
	}

	public JButton getReset() {
		return reset;
	}
	
	

}
