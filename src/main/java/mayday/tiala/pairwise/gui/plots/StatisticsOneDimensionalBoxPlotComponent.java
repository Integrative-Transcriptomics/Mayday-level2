package mayday.tiala.pairwise.gui.plots;

import java.util.HashMap;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.vis3.plots.boxplot.BoxPlotComponent;


@SuppressWarnings("serial")
public class StatisticsOneDimensionalBoxPlotComponent extends BoxPlotComponent {
	
	protected AlignmentStore store;
	
	public void setXLabeling(HashMap<Double, String> map) {
		super.setXLabeling(new HashMap<Double, String>());
	}
	
	public StatisticsOneDimensionalBoxPlotComponent(AlignmentStore Store) {
		super();
		store = Store;
		setPreferredSize(null);
	}

	@Override
	public String getAutoTitleX(String xtitle) {
		if (store != null)
			return store.getProbeStatistic().toString();
		return xtitle;
	}
	
}
