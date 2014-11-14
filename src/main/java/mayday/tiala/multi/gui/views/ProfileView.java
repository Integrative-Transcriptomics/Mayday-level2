package mayday.tiala.multi.gui.views;

import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;
import mayday.tiala.multi.data.AlignmentStoreListener;
import mayday.tiala.multi.gui.plots.TialaProfilePlotComponent;
import mayday.vis3.components.DetachablePlot;
import mayday.vis3.model.Visualizer;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class ProfileView extends DetachablePlot implements AlignmentStoreListener {

	protected TialaProfilePlotComponent profilePlot ;
	protected AlignmentStore store;
	protected int id;
	
	/**
	 * @param id
	 * @param store
	 * @param viz
	 */
	public ProfileView(int id, AlignmentStore store, Visualizer viz) {
		super(viz, viz.getViewModel().getDataSet().getName());
		
		this.id = id;
		this.store = store;
		profilePlot = new TialaProfilePlotComponent(id, store);
		profilePlot.setPreferredSize(null);
		
		setPlot(profilePlot);
		
		store.addListener(this);
	}
	
	private void updateSelectionColor() {
		if(profilePlot.isShowing()) {
			profilePlot.setSelectionColor(store.getSettings().getSelectionColor(id));
		}
	}
	
	public void addNotify() {
		super.addNotify();
		this.updateSelectionColor();
	}
	
	public void removeNotify() {
		store.removeListener(this);
		super.removeNotify();
	}

	@Override
	public void alignmentChanged(AlignmentStoreEvent evt) {
		switch(evt.getChange()) {
		case AlignmentStoreEvent.SELECTION_COLOR_CHANGED:
			this.updateSelectionColor();
			break;
		}
	}

	public void dispose() {
		store.removeListener(this);
	}
}
