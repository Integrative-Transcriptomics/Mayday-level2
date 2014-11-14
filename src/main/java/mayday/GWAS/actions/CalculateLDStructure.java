package mayday.GWAS.actions;

import java.awt.event.ActionEvent;

import mayday.GWAS.data.ProjectHandler;
import mayday.core.settings.Setting;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.tasks.AbstractTask;

@SuppressWarnings("serial")
public class CalculateLDStructure extends RevealAction {

	public CalculateLDStructure(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	private DoubleSetting thresholdSetting = new DoubleSetting("R2-Value Threshold", null, 0.8);
	private boolean enableSetting = true;
	
	public void enableSetting() {
		this.enableSetting = true;
	}
	
	public void disableSetting() {
		this.enableSetting = false;
	}
	
	public Setting getSetting() {
		return this.thresholdSetting;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(enableSetting) {
			SettingDialog dialog = new SettingDialog(null, "Set threshold for LD structure calculation", thresholdSetting);
			dialog.showAsInputDialog();
			
			if(!dialog.closedWithOK()) {
				return;
			}
		}
		
		AbstractTask calcTask = new AbstractTask("Calculate LD structure") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
//				double threshold = thresholdSetting.getDoubleValue();
//				DataStorage ds = projectHandler.getSelectedProject();
//				LDStructure ldStructure = new LDStructure(ds);
//				ldStructure.calculateLDStructure(threshold, ds.getLDResults().get(0));
//				TODO
//				ds.addLDStructure(ldStructure);
			}
		};
		
		calcTask.start();
	}
}
