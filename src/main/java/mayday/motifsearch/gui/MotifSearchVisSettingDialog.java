package mayday.motifsearch.gui;

import java.awt.Window;

import mayday.core.settings.*;
import mayday.motifsearch.gui.visual.VisualisationGUI;
import mayday.motifsearch.interfaces.IMotifSearchAlgoStarter;

public class MotifSearchVisSettingDialog extends SettingsDialog {
    private static final long serialVersionUID = 1L;

    private IMotifSearchAlgoStarter selectedAlgorithm; 

    public MotifSearchVisSettingDialog(Window owner, String title,
	    Settings settings,  IMotifSearchAlgoStarter selectedAlgorithm) {
	super(owner, title, settings);
	this.selectedAlgorithm = selectedAlgorithm;
	//this.applyButton.setText("parse and visualize");
    }

    @Override
    public boolean applyAndSave() {
	boolean isSettingsPrepared =  super.applyAndSave();
	VisualisationGUI.visualizeFromFolderPathForAlgorithm(this.settings.getChild("parsing folder:", true).getValueString(), this.selectedAlgorithm.clone());
	return isSettingsPrepared;
    }
}
