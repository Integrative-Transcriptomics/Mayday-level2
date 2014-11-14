package mayday.GWAS.gui;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.listeners.ProjectEvent;
import mayday.GWAS.listeners.ProjectEventListener;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.GWAS.visualizations.manhattanplot_old.ManhattanPlot;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class RevealOverviewPanel extends JPanel implements ProjectEventListener {

	private RevealVisualization plot;
	private RevealGUI gui;
	
	/**
	 * @param gui
	 */
	public RevealOverviewPanel(RevealGUI gui) {
		this.gui = gui;
		this.setLayout(new BorderLayout());
		plot = new ManhattanPlot(gui.getProjectHandler());
		this.add(plot, BorderLayout.CENTER);
		
		gui.getProjectHandler().getProjectEventHandler().addProjectEventListener(this);
	}
	
	/**
	 * @param index
	 */
	public void setProject(DataStorage dataStorage) {
		if(dataStorage != null) {
			plot.setData(dataStorage);
			plot.setViewModel(gui.getProjectHandler().getViewModel(dataStorage));
			plot.updatePlot();
		} else {
			clearOverviewPlot();
		}
	}
	
	private void clearOverviewPlot() {
		plot.setData(null);
		plot.updatePlot();
	}

	@Override
	public void projectChanged(ProjectEvent pe) {
		switch(pe.getChange()) {
		case ProjectEvent.PROJECT_ADDED:
			repaint();
			break;
		case ProjectEvent.PROJECT_REMOVED:
			clearOverviewPlot();
			break;
		case ProjectEvent.PROJECT_CHANGED:
			plot.updatePlot();
			break;
		case ProjectEvent.PROJECT_SELECTION_CHANGED:
			DataStorage data = gui.getProjectHandler().getSelectedProject();
			setProject(data);
			break;
		case ProjectEvent.METAINFO_SELECTION_CHANGED:
			plot.updatePlot();
			break;
		}
	}
}
