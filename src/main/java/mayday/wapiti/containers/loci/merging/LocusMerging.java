package mayday.wapiti.containers.loci.merging;

import mayday.core.settings.SettingDialog;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class LocusMerging {

	public static void run(TransMatrix transMatrix) {
		
		LocusMergeSetting lms = new LocusMergeSetting("Select Locus Sources", transMatrix);
		
		SettingDialog sd = new SettingDialog(transMatrix.getFrame(), lms.getName(), lms);
		sd.showAsInputDialog();
		
		if (!sd.canceled())
			lms.execute();		

	}
	
}
