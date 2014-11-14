package mayday.wapiti.containers.loci.merging;

import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JOptionPane;

import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.ExtendableObjectListSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginInstanceSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.importer.LocusImport;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapContainer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class LocusMergeSetting extends HierarchicalSetting {

	protected ExtendableObjectListSetting<LocusData> inputData;
	protected StringSetting outputName;
	protected PluginInstanceSetting<LocusMergeMethod> merger;
	protected TransMatrix tm;
	
	
	public LocusMergeSetting(String Name, TransMatrix tm) {
		super(Name);
		Set<LocusData> av = new HashSet<LocusData>();
		av.addAll(LocusMapContainer.INSTANCE.list());
		
		for (Experiment e : tm.getExperiments())
			if (e.hasLocusInformation())
				av.add(e.getLocusData());		
		
		outputName = new StringSetting( "Locus Set Name", "Specify a new name for the merged Locus Data", "", false);
		inputData = new ExtendableObjectListSetting<LocusData>("Input Locus Data",null,new LinkedList<LocusData>(av));
		merger = new PluginInstanceSetting<LocusMergeMethod>("Method","Select the algorithm used to merge locus data",LocusMergeMethod.MC);
		
		addSetting( outputName );
		addSetting( merger ); 
		addSetting( inputData ); 
		addSetting( new ComponentPlaceHolderSetting("Import from file...", new JButton(new FileImportAction())));
		this.tm = tm; 
	}
	
	@SuppressWarnings("serial")
	public class FileImportAction extends AbstractAction {

		public FileImportAction() {
			super("Import from file...");
		}
		
		public void actionPerformed(ActionEvent e) {
			LocusMap lm = LocusImport.run();
			if (lm!=null) {
				inputData.addChoice(lm, true);
			}			
		}

	}
	
	public List<LocusData> getInputData() {
		return inputData.getSelection();
	}
	
	public LocusMergeSetting clone() {
		LocusMergeSetting lms = new LocusMergeSetting(name, tm);
		lms.fromPrefNode(this.toPrefNode());
		return lms;
	}

	public void execute() {
		LocusMap lm = merger.getInstance().run(inputData.getSelection(), outputName.getStringValue());
		if (lm!=null) {
//			for (String k : lm.keySet())
//				System.out.println(k+"  "+lm.get(k));
			LocusMapContainer.INSTANCE.add(lm);
			JOptionPane.showMessageDialog(null, 
					"The new Locus Data object \""+lm.getName()+"\" contains "+lm.size()+" loci.", 
					"New Locus Data", 
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
}
