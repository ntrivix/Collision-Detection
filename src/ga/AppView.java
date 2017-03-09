package ga;

import java.awt.BorderLayout;
import java.awt.HeadlessException;

import javax.swing.JFrame;

import ga.polygon.PolygonPanel;

public class AppView extends JFrame {
	
	private ToolbarView toolbarView;
	
	private static AppView instance;
	
	public static AppView getInstance(){
		if (instance == null){
			instance = new AppView();
		}
		return instance;
	}
	
	private AppView() throws HeadlessException {
		super();
		this.setTitle("GA projekat");
		this.setSize(1024, 768);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		
		this.add(toolbarView = new ToolbarView(), BorderLayout.PAGE_START);
		
		this.add(PolygonPanel.getInstance());
		
		this.setVisible(true);
	}

	public ToolbarView getToolbarView() {
		return toolbarView;
	}
	
	
	
}
