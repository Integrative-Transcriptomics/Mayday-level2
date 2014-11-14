package mayday.GWAS.visualizations.manhattanplot_old;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;

import javax.swing.JComponent;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ManhattanPlot extends RevealVisualization {

	protected ManhattanPlotSetting setting;
	protected ManhattanPlotComponent plotComponent;
	
	private int axisSpace = 20;
	
	/**
	 * @param projectHandler
	 */
	public ManhattanPlot(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		this.plotComponent = new ManhattanPlotComponent(this);
		
		this.setLayout(new BorderLayout());
		this.add(plotComponent, BorderLayout.CENTER);
		this.add(new XAxis(), BorderLayout.SOUTH);
		this.add(new YAxis(), BorderLayout.WEST);
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			updatePlot();
			break;
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			updatePlot();
			break;
		}
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		this.setting = new ManhattanPlotSetting(this);
		return setting;
	}
	
	public void updatePlot() {
		if(getData() != null) {
			repaint();
		}
	}
	
	private class XAxis extends JComponent {
		
		public XAxis() {
			setPreferredSize(new Dimension(axisSpace, axisSpace));
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, getWidth(), getHeight());
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			
			Line2D line = new Line2D.Double(0, 1, getWidth(), 1);
			Line2D arrow = new Line2D.Double(getWidth()-axisSpace/5., axisSpace/5., getWidth(), 0);
			Line2D verticalZeroTick = new Line2D.Double(axisSpace-1, 0, axisSpace-1, getHeight());
			
			g2.draw(line);
			g2.draw(arrow);
			g2.draw(verticalZeroTick);
		}
	}
	
	private class YAxis extends JComponent {
		
		public YAxis() {
			setPreferredSize(new Dimension(axisSpace, axisSpace));
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			g2.setBackground(Color.WHITE);
			g2.clearRect(0, 0, getWidth(), getHeight());
			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(2));
			
			Line2D line = new Line2D.Double(getWidth()-1, getHeight(), getWidth()-1, 0);
			Line2D arrow = new Line2D.Double(getWidth()-1, 0, getWidth()-axisSpace/5.-1, axisSpace/5.);
			
			g2.draw(line);
			g2.draw(arrow);
		}
	}
	
	public void setData(DataStorage data) {
		this.data = data;
		if(viewModel != null)
			this.getViewModel().removeViewModelListener(this);
		if(data != null) {
			setViewModel(data.getProjectHandler().getViewModel(data));
			viewModel.addViewModelListener(this);
		}
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		// TODO Auto-generated method stub
		return null;
	}
}
