package mayday.wapiti.containers.identifiermapping;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.generic.ComponentPlaceHolderSetting;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.wapiti.containers.identifiermapping.importer.IDImport;

public class IdentifierMapSetting extends HierarchicalSetting {

	protected ExtendableObjectSelectionSetting<IdentifierMap> selection;

	public IdentifierMapSetting() {
		super("Identifier mapping" );
		addSetting( selection = new ExtendableObjectSelectionSetting<IdentifierMap>(
				"Use existing", null, 0, IdentifierMapContainer.INSTANCE.list()
		));
		addSetting( new ComponentPlaceHolderSetting("Import from file...", new JButton(new FileImportAction())));
	}
	
	@SuppressWarnings("serial") 
	public class FileImportAction extends AbstractAction {

		public FileImportAction() {
			super("Import from file...");
		}
		
		public void actionPerformed(ActionEvent e) {
			IdentifierMap lm = IDImport.run();
			if (lm!=null) {
				selection.addPredefined(lm);
				selection.setObjectValue(lm);
			}			
		}

	}
	
	public SettingComponent getGUIElement() {
		// update locus map list
		selection.updatePredefined(IdentifierMapContainer.INSTANCE.list());
		return super.getGUIElement();
	}
	
	
	public IdentifierMap getIdentifierMap() {
		return selection.getObjectValue();
	}
	
	public void setIdentifierMap(IdentifierMap l) {
		selection.setObjectValue(l);
	}
	
	public IdentifierMapSetting clone() {
		return (IdentifierMapSetting)reflectiveClone();
	}


}
