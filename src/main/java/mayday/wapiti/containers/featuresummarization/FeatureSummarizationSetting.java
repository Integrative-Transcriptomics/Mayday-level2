package mayday.wapiti.containers.featuresummarization;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.wapiti.containers.featuresummarization.importer.FMImport;

public class FeatureSummarizationSetting extends HierarchicalSetting {

	protected ExtendableObjectSelectionSetting<IFeatureSummarizationMap> selection;

	public FeatureSummarizationSetting() {
		super("Feature summarization" );
		addSetting( selection = new ExtendableObjectSelectionSetting<IFeatureSummarizationMap>(
				"Use existing", null, 0, FeatureSummarizationMapContainer.INSTANCE.list()
		));
		addSetting( new ComponentPlaceHolderSetting("Import from file...", new JButton(new FileImportAction())));
	}
	
	@SuppressWarnings("serial") 
	public class FileImportAction extends AbstractAction {

		public FileImportAction() {
			super("Import from file...");
		}
		
		public void actionPerformed(ActionEvent e) {
			FeatureSummarizationMap lm = FMImport.run(); // TODO
			if (lm!=null) {
				selection.addPredefined(lm);
				selection.setObjectValue(lm);
			}			
		}

	}
	
	public SettingComponent getGUIElement() {
		// update locus map list
		selection.updatePredefined(FeatureSummarizationMapContainer.INSTANCE.list());
		return super.getGUIElement();
	}
	
	
	public IFeatureSummarizationMap getFeatureSummarizationMap() {
		return selection.getObjectValue();
	}
	
	public void setFeatureSummarizationMap(IFeatureSummarizationMap l) {
		selection.setObjectValue(l);
	}
	
	public FeatureSummarizationSetting clone() {
		return (FeatureSummarizationSetting)reflectiveClone();
	}


}
