package mayday.Reveal.actions.io;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import mayday.Reveal.actions.RevealAction;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.SingleLocusResult;
import mayday.Reveal.utilities.SNPLists;
import mayday.core.Probe;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

@SuppressWarnings("serial")
public class ExportSLRSNPsAction extends RevealAction {

	public ExportSLRSNPsAction(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PathSetting destinationFolder = new PathSetting("Destination Folder", null, null, true, true, true);
		BooleanSetting overwriteExisting = new BooleanSetting("Overwrite existing files", null, true);
		DoubleSetting slrThreshold = new DoubleSetting("SLR p-value threshold", null, 0.05);
		
		HierarchicalSetting settings = new HierarchicalSetting("Export SLR SNPs gene wise ...");
		settings.addSetting(destinationFolder);
		settings.addSetting(overwriteExisting);
		settings.addSetting(slrThreshold);
		
		SettingDialog dialog = new SettingDialog(null, "Export SNPs gene wise", settings);
		dialog.showAsInputDialog();
		
		if(!dialog.closedWithOK()) {
			return;
		}
		
		final File folder = new File(destinationFolder.getStringValue());
		final double t = slrThreshold.getDoubleValue();
		final boolean overwrite = overwriteExisting.getBooleanValue();
		
		AbstractTask task  = new AbstractTask("Export SLR SNPs") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				DataStorage ds = projectHandler.getSelectedProject();
				
				GeneList genes = ds.getGenes();
				int numGenes = genes.size();
				File[] files = new File[numGenes];
				
				Map<Gene, BufferedWriter> geneToWriter = new HashMap<Gene, BufferedWriter>();
				
				String folderPath = folder.getAbsolutePath();
				
				//create the output files
				for(int i = 0; i < files.length; i++) {
					files[i] = new File(folderPath + "/" + genes.getGene(i).getName() + ".slr");
					if(files[i].exists() && !overwrite) {
						this.writeLog("File " + files[i].getName() + " already exists!\nOverwritting is disabled!\nExport canceled!");
						return;
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(files[i]));
					geneToWriter.put(genes.getGene(i), bw);
				}
				
				SNPList selectedSNPs = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
				
				SLResults slrs = (SLResults) ds.getMetaInformationManager().get(SLResults.MYTYPE).get(0);
				
				for(Probe p : genes) {
					
					if(hasBeenCancelled()) {
						for(BufferedWriter bw : geneToWriter.values()) {
							bw.close();
						}
						for(int i = 0; i < files.length; i++) {
							files[i].delete();
						}
						return;
					}
					
					Gene g = (Gene)p;
					SingleLocusResult slr = slrs.get(g);
					BufferedWriter bw = geneToWriter.get(g);
					
					for(SNP s : slr.keySet()) {
						if(selectedSNPs != null && !selectedSNPs.contains(s)) {
							continue;
						}
						double pVal = slr.get(s).p;
						if(pVal < t) {
							bw.write(s.getID() + "\t" + pVal + "\t" + s.getGene());
							bw.newLine();
						}
					}
					
					bw.close();
				}
				
				writeLog("Exporting Single Locus Results for SNPs gene wise has finished!");	
			}
		};
		task.start();
	}
}
