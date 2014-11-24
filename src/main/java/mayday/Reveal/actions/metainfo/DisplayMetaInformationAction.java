package mayday.Reveal.actions.metainfo;

import java.awt.event.ActionEvent;

import mayday.Reveal.actions.RevealAction;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.data.meta.TLResults;

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
