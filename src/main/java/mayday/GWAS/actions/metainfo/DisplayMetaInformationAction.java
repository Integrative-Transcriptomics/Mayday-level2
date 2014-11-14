package mayday.GWAS.actions.metainfo;

import java.awt.event.ActionEvent;

import mayday.GWAS.actions.RevealAction;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.data.meta.SLResults;
import mayday.GWAS.data.meta.StatisticalTestResult;
import mayday.GWAS.data.meta.TLResults;

@SuppressWarnings("serial")
public class DisplayMetaInformationAction extends RevealAction {

	public DisplayMetaInformationAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		triggerAction(projectHandler.getSelectedMetaInformation());
	}
	
	public void triggerAction(MetaInformation metaInfo) {
		//TODO open new meta information table depending on the meta information type
		
		if(metaInfo instanceof SLResults || metaInfo instanceof TLResults) {
			System.out.println("New Meta Information Table for Locus Results");
		} 
		
		else if(metaInfo instanceof StatisticalTestResult) {
			System.out.println("New Meta Information Table for Statistical Test Results");
		}
		
		else {
			System.out.println("New Meta Information View for all other types of meta data");
		}
	}
}
