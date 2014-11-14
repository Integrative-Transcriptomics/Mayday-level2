package mayday.wapiti.gui.actions.locus;

import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import mayday.genetics.advanced.LocusData;
import mayday.genetics.basic.ChromosomeSetContainer;

@SuppressWarnings("serial")
public abstract class AbstractLocusDataAction extends AbstractAction {

	protected ListSelectionModel sm;
	protected ListModel m;
	
	public AbstractLocusDataAction(String title, JList jl) {
		super("Inspect selected...");
		sm = jl.getSelectionModel();
		m = jl.getModel();
	}
	
	protected List<ChromosomeSetContainer> getSelectedCSC() {
		LinkedList<ChromosomeSetContainer> lds = new LinkedList<ChromosomeSetContainer>();
		for (int i=sm.getMinSelectionIndex(); i<=sm.getMaxSelectionIndex(); ++i)
			if (sm.isSelectedIndex(i))
				lds.add(((LocusData)m.getElementAt(i)).asChromosomeSetContainer());
		return lds;
	}
	
	protected List<LocusData> getSelected() {
		LinkedList<LocusData> lds = new LinkedList<LocusData>();
		for (int i=sm.getMinSelectionIndex(); i<=sm.getMaxSelectionIndex(); ++i)
			if (sm.isSelectedIndex(i))
				lds.add((LocusData)m.getElementAt(i));
		return lds;
	}

}
