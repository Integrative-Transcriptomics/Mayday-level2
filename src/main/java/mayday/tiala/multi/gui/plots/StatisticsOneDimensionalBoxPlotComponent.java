package mayday.tiala.multi.gui.plots;

import java.util.HashMap;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.vis3.plots.boxplot.BoxPlotComponent;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class StatisticsOneDimensionalBoxPlotComponent extends BoxPlotComponent {
	
	protected AlignmentStore store;
	protected int ID;
	
	public void setXLabeling(HashMap<Double, String> map) {
		super.setXLabeling(new HashMap<Double, String>());
	}
	
	public StatisticsOneDimensionalBoxPlotComponent(int ID, AlignmentStore Store) {
		super();
		store = Store;
		this.ID = ID;
		setPreferredSize(null);
	}
	
	@Override
	public String getAutoTitleX(String xtitle) {
		if (store != null)
			return store.getProbeStatistic(ID).toString();
		return xtitle;
	}
}
