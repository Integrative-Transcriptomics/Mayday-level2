package mayday.Reveal.statistics.plink;

import java.io.File;
import java.util.Collection;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.actions.RevealTask;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.gui.menu.SNPListPopupMenu;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class PlinkStatTestImportPlugin extends RevealPlugin {

	@Override
	public String getName() {
		return "PLINK Statistial Test Importer";
	}

	@Override
	public String getType() {
		return "statistics.plinkimport";
	}

	@Override
	public String getDescription() {
		return "PLINK Statistical Test Importer";
	}

	@Override
	public String getMenuName() {
		return "Import PLINK Statistics";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		String[] statTests = {"Chi2", 
				"Fisher's Exact", 
				"Hardy Weinberg", 
				"Armitage Trend", 
				"Linear Logistic Model", 
				"Likelihood Ratio Test", 
				"Wald Test"};
		
		String[] separators = {"Tabular", "Comma", "Semikolon", "Whitespace"};
		
		HierarchicalSetting setting = new HierarchicalSetting("PLINK Importer Setting");
		final PathSetting fileSetting = new PathSetting("PLINK File", 
				"PLINK file containing statistical test results.", null, false, true, false);
		final RestrictedStringSetting statTestSetting = new RestrictedStringSetting("PLINK Statistical Test", "Select the statistical test results that you want to import.", 0, statTests);
		final BooleanSetting headerSetting = new BooleanSetting("File has header line?", null, true);
		final RestrictedStringSetting separatorSetting = new RestrictedStringSetting("Separator", 
				"Select the file column separator.", 0, separators);
		
		setting.addSetting(statTestSetting);
		setting.addSetting(fileSetting);
		setting.addSetting(headerSetting);
		setting.addSetting(separatorSetting);
		
		SettingDialog d = new SettingDialog(projectHandler.getGUI(), setting.getName(), setting);
		d.setModal(true);
		d.setVisible(true);
		
		if(d.closedWithOK()) {
			
			RevealTask task = new RevealTask("Import PLINK Statistics", getProjectHandler()) {

				@Override
				protected void initialize() {}

				@Override
				protected void doWork() throws Exception {
					writeLog("\t- Parsing user settings ...");
					boolean hasHeader = headerSetting.getBooleanValue();
					String[] separatorsImpl = {"\t", ",", ";", " "};
					String separator = separatorsImpl[separatorSetting.getSelectedIndex()];
					File file = new File(fileSetting.getStringValue());
					
					DataStorage ds = getProjectHandler().getSelectedProject();
					IPlinkStatTestImporter importer = null;
					StatisticalTestResult r = null;
					
					//get import stat test instance
					switch(statTestSetting.getSelectedIndex()) {
					case 0: importer = new ImportChiSQ();
						break;
					case 1: importer = new ImportFisherExact();
						break;
					case 2: importer = new ImportHardyWeinberg();
						break;
					case 3: importer = new ImportArmitageTrend();
						break;
					case 4: importer = new ImportLinearLogisticModel();
						break;
					case 5: importer = new ImportLikelihoodRatio();
						break;
					case 6: importer = new ImportWald();
						break;
					}
					
					if(hasBeenCancelled()) {
						return;
					}
										
					//retrieve stat test results
					writeLog("\t- Importing statistical test results ...");
					r = importer.importTestResults(ds, file, hasHeader, separator);
					
					if(hasBeenCancelled()) {
						reportCurrentFractionalProgressStatus(0.0);
						return;
					}
					
					if(r != null) {
						ds.getMetaInformationManager().add(StatisticalTestResult.MYTYPE, r);
					}
					
					reportCurrentFractionalProgressStatus(1.0);
					writeLog("All done.");
				}
			};
			
			task.start();
		}
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.SNPLIST_MENU+"/Statistics";
	}

	@Override
	public String getCategory() {
		return SNPListPopupMenu.STATISTICS_CATEGORY;
	}
}
