package mayday.Reveal.visualizations.SLProfilePlot;

import java.util.LinkedList;
import java.util.List;

import mayday.core.settings.AbstractSetting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.vis2base.ChartComponent;
import mayday.vis3.vis2base.ChartSetting;
import mayday.vis3.vis2base.GridSetting;
import mayday.vis3.vis2base.VisibleRectSetting;

public class SynchronizedChartSetting extends HierarchicalSetting {

	private List<ChartSetting> chartSettings;
	protected ChartSetting synchronizedSetting;
	
	private SLProfilePlot plot;
	
	@SuppressWarnings("serial")
	public SynchronizedChartSetting(SLProfilePlot plot) {
		super("ChartsSetting");
		
		this.plot = plot;
		this.chartSettings = new LinkedList<ChartSetting>();
		
		addSetting(synchronizedSetting = new ChartSetting(new ChartComponent() {	
			@Override
			public String getAutoTitleY(String ytitle) {
				return ytitle;
			}
			@Override
			public String getAutoTitleX(String xtitle) {
				return xtitle;
			}
			@Override
			public void createView() {}
		}));
		
		synchronizedSetting.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {			
				synchronizeSettings();
			}
		});
		
		this.addChangeListener(new SynchronizedChartSettingChangeListener());
		
		synchronizedSetting.getGrid().getYmaj().setDoubleValue(0.);
		synchronizedSetting.getGrid().getYmin().setDoubleValue(0.5);
		synchronizedSetting.getGrid().getXmaj().setDoubleValue(0.);
		synchronizedSetting.getGrid().getXmin().setDoubleValue(0.);
		synchronizedSetting.getAntialias().setBooleanValue(true);
	}
	
	public void addChartSetting(ChartSetting chartSetting) {
		this.chartSettings.add(chartSetting);
		synchronizeSettings();
	}
	
	public void removeChartSetting(ChartSetting chartSetting) {
		this.chartSettings.remove(chartSetting);
		synchronizeSettings();
	}
	
	public void removeAllChartSettings() {
		this.chartSettings.clear();
	}
	
	public void addChartSettings(List<ChartSetting> chartSettings) {
		this.chartSettings.addAll(chartSettings);
		synchronizeSettings();
	}
	
	public void synchronizeSettings() {
		//synchronize grid
		GridSetting grid = this.synchronizedSetting.getGrid();
		
		//synchronize visible area
		VisibleRectSetting visibleRect = this.synchronizedSetting.getVisibleRect();
		
		visibleRect.getXmax().setDoubleValue(getXMax());
		visibleRect.getXmin().setDoubleValue(getXMin());
		visibleRect.getYmax().setDoubleValue(getYMax());
		visibleRect.getYmin().setDoubleValue(getYMin());
		
		//synchronize font size
		IntSetting fontSize = this.synchronizedSetting.getFontSize();
		
		//synchronize x axis labels every n positions
		IntSetting expLabelSkip = this.synchronizedSetting.getExperimentLabelSkip();
		
		//synchronize colors
		AbstractSetting colors = this.synchronizedSetting.getColors();
		
		//synchronize opacity
		IntSetting alpha = this.synchronizedSetting.getAlpha();
		
		//synchronize anti-aliasing
		BooleanSetting antialias = this.synchronizedSetting.getAntialias();
		
		for(ChartSetting s: this.chartSettings) {
			GridSetting sGrid = s.getGrid();
			sGrid.fromPrefNode(grid.toPrefNode());
			VisibleRectSetting sRect = s.getVisibleRect();
			sRect.fromPrefNode(visibleRect.toPrefNode());
			IntSetting sfS = s.getFontSize();
			sfS.fromPrefNode(fontSize.toPrefNode());
			IntSetting sxLs = s.getExperimentLabelSkip();
			sxLs.fromPrefNode(expLabelSkip.toPrefNode());
			AbstractSetting sColors = s.getColors();
			sColors.fromPrefNode(colors.toPrefNode());
			IntSetting sAlpha = s.getAlpha();
			sAlpha.fromPrefNode(alpha.toPrefNode());
			BooleanSetting sAntialias = s.getAntialias();
			sAntialias.fromPrefNode(antialias.toPrefNode());
		}
		
		fireChanged();
	}
	
	private Double getXMin() {
		double min = Double.MAX_VALUE;
		for(int i = 0; i < chartSettings.size(); i++) {
			SLProfilePlotComponent c = (SLProfilePlotComponent) plot.getComponent(i);
			double value = c.getMinLocation();
			if(value < min)
				min = value;
		}
		return min;
	}

	private Double getXMax() {
		double max = Double.MIN_VALUE;
		for(int i = 0; i < chartSettings.size(); i++) {
			SLProfilePlotComponent c = (SLProfilePlotComponent) plot.getComponent(i);
			double value = c.getMaxLocation();
			if(value > max)
				max = value;
		}
		return max;
	}

	private Double getYMin() {
		double min = Double.MAX_VALUE;
		for(int i = 0; i < chartSettings.size(); i++) {
			SLProfilePlotComponent c = (SLProfilePlotComponent) plot.getComponent(i);
			double value = c.Y.getMinValue(plot.snps);
			if(value < min)
				min = value;
		}
		return min;
	}

	private Double getYMax() {
		double max = Double.MIN_VALUE;
		for(int i = 0; i < chartSettings.size(); i++) {
			SLProfilePlotComponent c = (SLProfilePlotComponent) plot.getComponent(i);
			double value = c.Y.getMaxValue(plot.snps);
			if(value > max)
				max = value;
		}
		return max;
	}

	public SynchronizedChartSetting clone() {
		SynchronizedChartSetting cp = new SynchronizedChartSetting(plot);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}
	
	private class SynchronizedChartSettingChangeListener implements SettingChangeListener {
		@Override
		public void stateChanged(SettingChangeEvent e) {
			plot.updatePlot();
		}		
	}
}
