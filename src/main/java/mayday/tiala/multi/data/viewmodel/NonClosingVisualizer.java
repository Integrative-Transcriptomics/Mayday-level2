package mayday.tiala.multi.data.viewmodel;

import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;
import mayday.vis3.model.Visualizer;
import mayday.vis3.model.VisualizerMember;

/**
 * A visualizer that doesn't close when the last probelist is removed.
 * @author battke
 *
 */
public class NonClosingVisualizer extends Visualizer {

	public NonClosingVisualizer() {
		super();
	}
	
	public void setViewModel(ViewModel vm) {
		viewModel = vm;
		viewModel.addViewModelListener(new ViewModelListener() {
			public void viewModelChanged(ViewModelEvent vme) {
				if (viewModel.getProbeLists(false).size()==0) {
					viewModel.removeViewModelListener(this);
					while (openPlots.size()>0)
						removePlot(openPlots.get(0));
				}
			}
		});
		vm.getDataSet().addDataSetListener(this);		
		addVisualizer(vm.getDataSet(), this);
	}
	
	public void dispose() {
		removeVisualizer(viewModel.getDataSet(), this);
		viewModel.dispose();
	}

	public void removePlot(VisualizerMember plot) {
		if (openPlots.contains(plot)) {
			openPlots.remove(plot);
			plot.closePlot();
			updateTitles();
			updateVisualizerMenus();
		}
		// do not close complete visualizer
	}
}
