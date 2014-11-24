package mayday.Reveal.statistics.DifferenceOfProportions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import javax.swing.JOptionPane;

import mayday.Reveal.actions.RevealTask;
import mayday.Reveal.actions.snplist.SNPListPlugin;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.statistics.StatisticalTest;
import mayday.Reveal.utilities.ContingencyTable;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.utilities.SNPLists;
import mayday.core.math.pcorrection.PCorrectionPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.PluginTypeSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

import org.apache.commons.math.MathException;

public class DifferenceOfProportionsPlugin extends SNPListPlugin {

	@Override
	public String getName() {
		return "Difference of Proportions";
	}

	@Override
	public String getType() {
		return "statistics.dop";
	}

	@Override
	public String getDescription() {
		return "Difference of proportions for 2x2 contingency tables";
	}

	@Override
	public String getMenuName() {
		return "Difference of Proportions";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		final SNPList allSNPs = SNPLists.createUniqueSNPList(snpLists);
		
		RevealTask task = new RevealTask("Difference of Proportions", getProjectHandler()) {
			
			@Override
			protected void initialize() {}
			
			@Override
			protected void doWork() throws Exception {
				DataStorage dataStorage = getProjectHandler().getSelectedProject(); 
				
				if(dataStorage == null) {
					JOptionPane.showMessageDialog(null, "No project has been selected!");
					return;
				}
				
				DifferenceOfProportions test = new DifferenceOfProportions();		
				
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
