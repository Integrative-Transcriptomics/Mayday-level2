package mayday.tiala.multi.gui.views;

import java.util.Collection;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.vis3.CollectionValueProvider;
import mayday.vis3.components.DetachableComponent;
import mayday.vis3.plotsWithoutModel.histogram.HistogramWithMeanComponent;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ScoreView extends DetachableComponent {
	
	protected final int number;
	
	public ScoreView(final int number, AlignmentStore Store) {
		
		super("Pairwise distances");
		this.number = number;
        
        HistogramWithMeanComponent ahist = new HistogramWithMeanComponent();
        
        new StoreValueProviderAdapter(ahist.getHistogramPlotComponent().getValueProvider(),Store);
        
		setPlot(ahist);		
	}
	
    protected class StoreValueProviderAdapter implements AlignmentStoreListener {
    	
    	protected CollectionValueProvider valueProvider;
    	protected AlignmentStore store;
    	
    	public StoreValueProviderAdapter(CollectionValueProvider cvp, AlignmentStore Store) {
    		store = Store;
    		store.addListener(this);
    		valueProvider = cvp;
    		valueProvider.setValues(computeScores());
    	}

		public void alignmentChanged(AlignmentStoreEvent evt) {
			if (evt.getChange()==AlignmentStoreEvent.SCORING_CHANGED || evt.getChange()==AlignmentStoreEvent.SHIFT_CHANGED)
				valueProvider.setValues(computeScores());					
		}
		
		protected Collection<Double> computeScores() {
			return store.getAlignedDataSets().getScores(number).values();
		}
    }
}
