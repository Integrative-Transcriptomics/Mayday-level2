package mayday.GWAS.io.project.factory.finalize;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.GWAS.io.project.factory.AbstractProjectDefinition;
import mayday.GWAS.io.project.factory.ProjectCreator;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.HierarchicalSetting;

public class FinalizationProjectDefinition extends AbstractProjectDefinition {

	private AbstractAction finalizeAction;
	
	@Override
	public HierarchicalSetting getSetting() {
		
		JButton finishBtn = new JButton(finalizeAction);
		
		HierarchicalSetting s = new HierarchicalSetting("All Done?");
		s.addSetting(new ComponentPlaceHolderSetting("Create Project", finishBtn));
		return s;
	}

	@Override
	public ProjectCreator getCreator() {
		return getPrevious().getCreator();
	}
	
	public boolean isFinal() {
		return true;
	}
	
	public void setFinalizeAction(AbstractAction action) {
		finalizeAction = action;
	}
}
