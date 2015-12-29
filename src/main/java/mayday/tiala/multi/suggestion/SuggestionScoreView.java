package mayday.tiala.multi.suggestion;

import mayday.vis3.components.DetachableComponent;
import mayday.vis3.plotsWithoutModel.histogram.HistogramWithMeanComponent;

/**
 * 
 * @author battke
 *
 */
@SuppressWarnings("serial")
public class SuggestionScoreView extends DetachableComponent {

	protected HistogramWithMeanComponent ahist = new HistogramWithMeanComponent();
	
	public SuggestionScoreView() {
		super("Pairwise distances");
		setPlot(ahist);		
	}

	public void setValues(ScoredAlignment sa) {
		ahist.getHistogramPlotComponent().getValueProvider().setValues(sa.getDistances().values());
	}
}
