package mayday.GWAS.statistics.RelativeRisk;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.swing.JOptionPane;

import mayday.GWAS.actions.RevealTask;
import mayday.GWAS.actions.snplist.SNPListPlugin;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.HaplotypesList;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SubjectList;
import mayday.GWAS.data.meta.StatisticalTestResult;
import mayday.GWAS.statistics.StatisticalTest;
import mayday.GWAS.utilities.ContingencyTable;
import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.GWAS.utilities.SNPLists;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

import org.apache.commons.math.MathException;

public class RelativeRiskPlugin extends SNPListPlugin {

	@Override
	public String getName() {
		return "Relative Risk";
	}

	@Override
	public String getType() {
		return "statistics.relativerisk";
	}

	@Override
	public String getDescription() {
		return "Relative Risk for 2x2 contingency tables";
	}

	@Override
	public String getMenuName() {
		return "Relative Risk";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		final SNPList allSNPs = SNPLists.createUniqueSNPList(snpLists);
		
		RevealTask task = new RevealTask("Relative Risk", getProjectHandler()) {
			
			@Override
			protected void initialize() {}
			
			@Override
			protected void doWork() throws Exception {
				DataStorage dataStorage = getProjectHandler().getSelectedProject(); 
				
				if(dataStorage == null) {
					JOptionPane.showMessageDialog(null, "No project has been selected!");
					return;
				}
				
				RelativeRisk test = new RelativeRisk();		
				
				Set<PluginInfo> plis = PluginManager.getInstance().getPluginsFor(PCorrectionPlugin.MC);
				PluginTypeSetting<PCorrectionPlugin> method = new PluginTypeSetting<PCorrectionPlugin>("Correction Method",null, (PCorrectionPlugin)plis.iterator().next().getInstance(), plis);
				StringSetting statName = new StringSetting("Name", "Statistical Test Name", test.getName());
				BooleanSetting oneSided = new BooleanSetting("One Sided", "Choose to perform a one-sided or a two-sided test", false);
				
				HierarchicalSetting statSetting = new HierarchicalSetting("p-Value correction").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
						.addSetting(statName)
						.addSetting(oneSided)
						.addSetting(method);		
				SettingDialog sd = new SettingDialog(null, test.getName()+" Setting", statSetting);
				
				if(!sd.showAsInputDialog().closedWithOK()) {
					return;
				}
				
				
				StatisticalTestResult res = applyTest(this, test, dataStorage, allSNPs, oneSided.getBooleanValue());
				
				if(hasBeenCancelled()) {
					reportCurrentFractionalProgressStatus(0.0);
					return;
				}
				
				PCorrectionPlugin correctionPlugin = method.getInstance();
				res.setStatTestName(statName.getStringValue() + " [" + correctionPlugin.getPluginInfo().getName() + "]");
				res.correctPValues(correctionPlugin);
				
				if(hasBeenCancelled()) {
					reportCurrentFractionalProgressStatus(0.0);
					return;
				}
				
				dataStorage.getMetaInformationManager().add(StatisticalTestResult.MYTYPE, res);
				
				if(!hasBeenCancelled()) {
					reportCurrentFractionalProgressStatus(1.0);
				}
			}
		};
		
		task.start();
	}
	
	private StatisticalTestResult applyTest(RevealTask task, StatisticalTest test, DataStorage ds, SNPList snps, boolean one_sided) throws MathException {
		StatisticalTestResult res = new StatisticalTestResult(test.getName());
		
		SubjectList persons = ds.getSubjects();
		HaplotypesList haplotypes = ds.getHaplotypes();
		
		ArrayList<Subject> affectedPersons = persons.getAffectedSubjects();
		ArrayList<Subject> unaffectedPersons = persons.getUnaffectedSubjects();
		
		double numSNPs = snps.size();
		int count = 0;
		
		for(SNP snp : snps) {
			double[][] table = ContingencyTable.get2x2ContingencyTable(affectedPersons, unaffectedPersons, haplotypes, snp.getIndex());
			double p = test.test(table, one_sided);
			res.setPValue(snp, p);
			
			if(task != null && !task.hasBeenCancelled()) {
				task.reportCurrentFractionalProgressStatus((count++) / numSNPs);
			}
			
			if(task != null && task.hasBeenCancelled()) {
				return null;
			}
		}
		
		return res;
	}
	
	@Override
	public String getMenu() {
		return RevealMenuConstants.SNPLIST_MENU+"/Statistics";
	}

	@Override
	public String getCategory() {
		return super.getCategory()+"/Statistics";
	}
}
