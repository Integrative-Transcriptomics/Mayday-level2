package mayday.tiala.pairwise.gui.plots;

import mayday.vis3.gui.PlotContainer;

@SuppressWarnings("serial")
public class StatisticsProfilePlotComponent extends AutoTimepointProfilePlotComponent {
	
	public void setup(PlotContainer cont) {
		super.setup(cont);		
		settings.getShowDots().setBooleanValue(true);
	}
	
	public StatisticsProfilePlotComponent() {
		super();
		setPreferredSize(null);
	}
	

	@Override
	public String getAutoTitleY(String ytitle) {
		return "Statistic";
	}

}
