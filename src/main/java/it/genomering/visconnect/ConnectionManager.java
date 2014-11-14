package it.genomering.visconnect;

import it.genomering.structure.Genome;
import it.genomering.structure.GenomeEvent;
import it.genomering.structure.SuperGenome;
import it.genomering.structure.SuperGenomeEvent;
import it.genomering.structure.SuperGenomeListener;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;

import mayday.vis3.model.ViewModel;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.model.ViewModelListener;

/** connect to ONE visualizer per Genome
 * @author battke
 */
public class ConnectionManager implements ViewModelListener, SuperGenomeListener {

	protected ArrayList<ViewModel> viewModels = new ArrayList<ViewModel>(); // in the order of the genomes in the SuperGenome
	protected SuperGenome superGenome;
	
	public ConnectionManager(SuperGenome sg) {
		superGenome = sg;
		sg.addListener(this);
		init();
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		if (vme.getChange()==ViewModelEvent.VIEWMODEL_CLOSED)
			viewModels.set(viewModels.indexOf(vme.getSource()),null);	
		else {
			Genome g = getGenome((ViewModel)vme.getSource());
			g.fireChange(GenomeEvent.CONNECTED_VIEWMODEL_CHANGED);
		}
	}
	
	protected void init() {
		for (ViewModel vm : viewModels)
			if (vm!=null)
				vm.removeViewModelListener(this);
		viewModels.clear();
		for (@SuppressWarnings("unused") Genome g : superGenome.getGenomes())
			viewModels.add(null);		
	}
	
	@Override
	public void superGenomeChanged(SuperGenomeEvent evt) {
		if (evt.getChange()==SuperGenomeEvent.GENOMES_CHANGED) {
			init();
		}
	}
	
	
	public Genome getGenome(ViewModel vm) {
		return superGenome.getGenomes().get(viewModels.indexOf(vm)); // slow but ok for <15 models only
	}
	
	public ViewModel getViewModel(Genome g) {
		return viewModels.get(g.getIndex());
	}

	public List<Genome> getGenomes() {
		return superGenome.getGenomes();
	}

	public void clear() {
		init();
	}
	
	public void map(Genome g, ViewModel viewModel) {
		ViewModel prev = viewModels.get(g.getIndex());
		if (prev!=null)
			prev.removeViewModelListener(this);
		viewModels.set(g.getIndex(), viewModel);
		if (viewModel!=null)
			viewModel.addViewModelListener(this);
		g.fireChange(GenomeEvent.CONNECTED_VIEWMODEL_CHANGED);
	}

	public void removeNotify() {
		for (ViewModel vm : viewModels)
			if (vm!=null)
				vm.removeViewModelListener(this);
		viewModels.clear();
	}
	
	@SuppressWarnings("serial")
	public class ConfigureConnectionsAction extends AbstractAction {

		public ConfigureConnectionsAction() {
			super("Connect Genomes to Visualizers...");
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			new ConnectionManagerGUI(ConnectionManager.this); // all done here
		}

	}
	
}
