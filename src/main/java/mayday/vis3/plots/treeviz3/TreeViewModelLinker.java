package mayday.vis3.plots.treeviz3;

import mayday.tiala.pairwise.data.viewmodel.ViewModelLinker;
import mayday.vis3.model.ViewModel;

/**
 * @author Eugen Netz
 */
public class TreeViewModelLinker extends ViewModelLinker{
	
	public TreeViewModelLinker(ViewModel... viewModels) {
		super(viewModels);
	}
	
	@Override
	protected void synchronizeProbeListSelection(ViewModel sourceVM) {
		//Do nothing
	}
	
	/**
	 * Breaks the Link between two ViewModels
	 */
	public void breakLink(){
		for (ViewModel vm : allModels) {
			vm.removeViewModelListener(outputListener);
			vm.removeViewModelListener(inputListener);
		}
		allModels.get(1).dispose();
	}
	
	/**
	 * @return TRUE, if there are no plots open using the temporary visualizer
	 */
	public boolean hasNoPlots(){
		return allModels.get(1).getVisualizer().getMembers().isEmpty();
	}
}
