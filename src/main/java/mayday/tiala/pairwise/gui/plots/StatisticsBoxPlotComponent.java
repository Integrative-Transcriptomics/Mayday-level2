package mayday.tiala.pairwise.gui.plots;


@SuppressWarnings("serial")
public class StatisticsBoxPlotComponent extends AutoTimepointBoxPlotComponent {
	
	public StatisticsBoxPlotComponent() {
		super();
		setPreferredSize(null);
	}

	@Override
	public String getAutoTitleY(String ytitle) {
		return "Statistic";
	}
	
	
}
