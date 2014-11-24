package mayday.Reveal.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.visualizations.SNPExpHeatMap.SNPExpressionCalculator;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
import mayday.core.tasks.AbstractTask;

@SuppressWarnings("serial")
public class CalculateSNPMatricesAction extends RevealAction {
	
	public CalculateSNPMatricesAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PathSetting file1 = new PathSetting("Affected", null, null, false, true, true);
		PathSetting file2 = new PathSetting("Unaffected", null, null, false, true, true);
		BooleanSetting weighted = new BooleanSetting("Weighted Expressions", null, false);
		
		HierarchicalSetting setting = new HierarchicalSetting("Choose files...");
		setting.addSetting(file1);
		setting.addSetting(file2);
		setting.addSetting(weighted);
		
		SettingDialog dialog = new SettingDialog(null, "Save matrices to file", setting);
		dialog.showAsInputDialog();
		
		if(!dialog.closedWithOK()) {
			return;
		}
		
		final File output1 = new File(file1.getStringValue());
		final File output2 = new File(file2.getStringValue());
		final boolean weightedExp = weighted.getBooleanValue();
		
		AbstractTask t = new AbstractTask("Export SNP Expression Matrices") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				DataStorage ds = projectHandler.getSelectedProject();
				SNPList snps = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
				SNPExpressionCalculator c = new SNPExpressionCalculator(ds, snps);
				writeLog("Calculating SNP Expression Vectors for " + snps.size() + " SNPs...\n");
				DoubleMatrix[] matrices = c.calculateSNPVectors(weightedExp);
				
				DoubleMatrix affected = matrices[0];
				DoubleMatrix unaffected = matrices[1];
				
				GeneList genes = ds.getGenes();
				
				writeLog("Wrtiting affected matrix to file...\n");
				write(snps, genes, affected, output1);
				writeLog("Wrtiting unaffected matrix to file...\n");
				write(snps, genes, unaffected, output2);
				writeLog("Done");
			}
		};
		t.start();
	}
	
	private void write(SNPList snps, GeneList genes, DoubleMatrix m, File output) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			//header row
			bw.write("SNPID");
			for(int i = 0; i < m.ncol(); i++) {
				bw.write("\t" + genes.getGene(i).getName());
			}
			bw.newLine();
			
			for(int i = 0; i < m.nrow(); i++) {
				bw.write(snps.get(i).getID());
				for(int j = 0; j < m.ncol(); j++) {
					bw.write("\t" + m.getValue(i, j));
				}
				bw.newLine();
			}
			
			bw.close();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
