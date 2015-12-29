package mayday.wapiti.gui.actions.locus;

import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JList;

import mayday.core.gui.MaydayFrame;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.gui.CSCInspectorPanel;
import mayday.wapiti.gui.actions.locus.ManageLocusDataAction.EditableExperimentLocusData;

@SuppressWarnings("serial")
public class InspectLocusDataAction extends AbstractLocusDataAction {

	public InspectLocusDataAction(JList jl) {
		super("Inspect selected...", jl);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		List<LocusData> lds = getSelected();
	
		if (lds.size()==0)
			return;
		
		boolean editable=true;
		for (LocusData ld : lds)
			if (!(ld instanceof EditableExperimentLocusData)) 
				editable=false;			
		
		CSCInspectorPanel csci = new CSCInspectorPanel(editable, getSelectedCSC().toArray(new ChromosomeSetContainer[0]));
		
		MaydayFrame mf = new MaydayFrame("Locus Data Inspector "+(editable?"& Editor":"(read-only)")); 
		mf.add(csci);
		mf.pack();
		mf.setSize(800, 600);
		mf.setVisible(true);
	}

}
