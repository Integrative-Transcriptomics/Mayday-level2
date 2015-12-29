package mayday.tiala.pairwise.gui.views;

import java.util.Collection;

import mayday.tiala.pairwise.data.AlignmentStore;
import mayday.tiala.pairwise.data.AlignmentStoreEvent;
import mayday.tiala.pairwise.data.AlignmentStoreListener;
import mayday.vis3.CollectionValueProvider;
import mayday.vis3.components.DetachableComponent;
import mayday.vis3.plotsWithoutModel.histogram.HistogramWithMeanComponent;

@SuppressWarnings("serial")
public class ScoreView extends DetachableComponent {
	
	public ScoreView(AlignmentStore Store) {
		
		super("Pairwise distances");
        
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
			return store.getAlignedDataSets().getScores().values();
		}
    	
    }
	
}
