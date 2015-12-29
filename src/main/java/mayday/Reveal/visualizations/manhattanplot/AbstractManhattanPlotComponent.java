package mayday.Reveal.visualizations.manhattanplot;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.Collection;
import java.util.Set;

import mayday.Reveal.data.SNV;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public abstract class AbstractManhattanPlotComponent extends ChartComponent {
	
	protected ManhattanPlot plot;
	
	protected DataSeries selectionLayer;
	protected DataSeries snpLayer;
	
	
	public AbstractManhattanPlotComponent(ManhattanPlot plot) {
		this.plot = plot;
	}

	@Override
	public void createView() {
		snpLayer = viewSNPs(plot.snps, false);
		snpLayer.setColor(Color.DARK_GRAY);
		addDataSeries(snpLayer);
		select();
	}
	
	public abstract DataSeries doSelect(Collection<SNV> snps);
	
	public abstract DataSeries viewSNPs(Collection<SNV> snps, boolean isSelectionLayer);
	
	public void select() {
		Color selectionColor = plot.setting.getSelectionColor();
		if(selectionLayer != null) {
			removeDataSeries(selectionLayer);
		}
		
		Set<SNV> selectedSNPs = plot.getViewModel().getSelectedSNPs();
		selectionLayer = doSelect(selectedSNPs);
		
		if(selectionLayer != null) {
			selectionLayer.setColor(selectionColor);
			selectionLayer.setStroke(new BasicStroke(3));
			addDataSeries(selectionLayer);
		}
		
		clearBuffer();
		repaint();
	}
	
	public void setup(PlotContainer plotContainer) {		
		if (firstTime) {
			viewModel = plotContainer.getViewModel();
			
			getZoomController().setTarget(fareapanel);
			getZoomController().setAllowXOnlyZooming(true);
			getZoomController().setAllowYOnlyZooming(true);
		}
		plotContainer.addViewSetting(chartSettings, plot);
	}
}
