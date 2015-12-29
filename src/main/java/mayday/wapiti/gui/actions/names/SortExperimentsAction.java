
package mayday.wapiti.gui.actions.names;

import java.awt.event.ActionEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.swing.AbstractAction;

import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.gui.layeredpane.PanelPositioner;
import mayday.wapiti.gui.layeredpane.ReorderableHorizontalPanel;
import mayday.wapiti.gui.layeredpane.SelectionModel;
import mayday.wapiti.transformations.matrix.TransMatrix;

@SuppressWarnings("serial")
public class SortExperimentsAction extends AbstractAction {

	
	private final TransMatrix transMatrix;
//	private final SelectionModel selection;

	public SortExperimentsAction(TransMatrix transMatrix, SelectionModel sm) {
		super("Sort Experiments By Name");
		this.transMatrix = transMatrix;
//		this.selection = sm;
	}

	public void actionPerformed(ActionEvent e) {
//		if (selection.size()>0) {
			
			LinkedList<Experiment> le = new LinkedList<Experiment>(transMatrix.getExperiments());
						
			Collections.sort(le, new Comparator<Experiment>() {
				public int compare(Experiment o1, Experiment o2) {
					// try numeric sorting
					try {
						Double d1 = Double.parseDouble(o1.getName());
						Double d2 = Double.parseDouble(o2.getName());
						return d1.compareTo(d2);
					} catch (Exception e) {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
				}				
			});

			LinkedList<ReorderableHorizontalPanel> rhps = new LinkedList<ReorderableHorizontalPanel>();
			for (Experiment ex : le)
				rhps.add(ex.getGUIElement());
			
			PanelPositioner pp = transMatrix.getPane().getPositioner();
			
			
			
			for (int i=0; i!=le.size(); ++i)
				pp.movePanelToIndex(rhps.get(i), i);			
			
			transMatrix.getPane().validate();
//		}
	}
}