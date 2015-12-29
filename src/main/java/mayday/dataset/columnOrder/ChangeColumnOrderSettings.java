package mayday.dataset.columnOrder;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.PathSetting;

public class ChangeColumnOrderSettings extends HierarchicalSetting{


	/**
	 * the selection of options (sort alphabetically, by MIOs or by parsing a file for user-defined ordering)
	 */
	SelectableHierarchicalSetting selection;
	/**
	 * a setting to hold the "filePathSetting". Isn't necessary but avoids confusion about the help dialog.
	 */
	HierarchicalSetting setting = new HierarchicalSetting("Define experiment order using file");
	
	/**
	 * the setting to retrieve the file path containing the user's sorting
	 */
	PathSetting filePathSetting;


	
	ChangeColumnOrderSettings(){
		
		super("Order columns of the expression matrix");
		
		filePathSetting = new PathSetting("Choose file", 
				"Choose a file that specifies the order in which columns are to be ordered. The file should contain all experiment names, each separated by a new line, in the requested order.", 
				null, false, true, true);
		
		setting.addSetting(filePathSetting);
		
		addSetting(selection = new SelectableHierarchicalSetting(
				"Re-order experiments", "Select an option to change the order of the experiments within the data set.",
				0, new Object[]{"Sort alphabetically by experiment name", "Sort experiments by meta information objects", setting}));
	
	}

	
	
	public ChangeColumnOrderSettings clone() {
		ChangeColumnOrderSettings s = new ChangeColumnOrderSettings();
		s.fromPrefNode(this.toPrefNode());
		return s;
	}


	public SelectableHierarchicalSetting getSelection(){
		return selection;
	}
	
	
	public String getSelectedPath(){
		return 	filePathSetting.getValueString();
	}
	
	public PathSetting getPathSetting(){
		return 	filePathSetting;
	}
	
	
	public HierarchicalSetting getSetting() {
		return setting;
	}
	
}
