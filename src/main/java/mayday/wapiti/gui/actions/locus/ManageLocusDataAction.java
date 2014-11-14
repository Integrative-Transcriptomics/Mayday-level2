
package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListSelectionModel;

import mayday.core.gui.listdialog.BaseFrame;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class ManageLocusDataAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public ManageLocusDataAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Inspect Locus Data");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}


	public void actionPerformed(ActionEvent e) {
		// create a list of all locus data currently available
		final LinkedList<LocusData> lld = new LinkedList<LocusData>(LocusMapContainer.INSTANCE.values());
		for (int i=0; i!=lld.size(); ++i)
			lld.set(i, new ReadOnlyLocusData(lld.get(i)));
		
		for (final Experiment exx : transMatrix.getExperiments()) {
			if (exx.getInitialState().hasLocusInformation()) {
				LocusData ld = exx.getInitialState().getLocusData();
				if (!lld.contains(ld)) {
					lld.add(new EditableExperimentLocusData(exx));
				}
			}
		}
		
		
		
		BaseFrame bf = new BaseFrame("Locus Data") {
			
			JList jl  = new JList(new DefaultListModel());
			
			@Override
			public int getListSize() {
				return lld.size();
			}
			
			@Override
			public JComponent getList() {
				for (LocusData ld : lld)
					((DefaultListModel)jl.getModel()).addElement(ld);
				jl.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				return jl;
			}
			
			@Override
			public JComponent getDescription() {
				return new JLabel("Locus Data objects in memory: ");
			}
			
			@Override
			public void fillListActions(List<Object> actions) {
				actions.add(new InspectLocusDataAction(jl));
				actions.add(null);
			}
		};
		
		bf.setVisible(true);
		bf.setSize(650,400);
		
	}	
	
	
	public class EditableExperimentLocusData implements LocusData {
		protected Experiment exx;
		
		public EditableExperimentLocusData(Experiment ex) {
			exx=ex;
		}
		
		public ChromosomeSetContainer asChromosomeSetContainer() {						
			return exx.getInitialState().getLocusData().asChromosomeSetContainer();
		}
		
		public String toString() {
			return exx.getName()+" ["+exx.getInitialState().getLocusData().toString()+"]";
		}
		
	}
	
	public class ReadOnlyLocusData implements LocusData {
		protected LocusData _ld;
		
		public ReadOnlyLocusData(LocusData ld) {
			_ld=ld;
		}
		
		public ChromosomeSetContainer asChromosomeSetContainer() {						
			return _ld.asChromosomeSetContainer();
		}
		
		public String toString() {
			return "<html><i>"+_ld.toString()+" </i>(read only)";
		}
		
	}
	
}