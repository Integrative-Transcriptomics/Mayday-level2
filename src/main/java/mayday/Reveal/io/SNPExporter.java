package mayday.Reveal.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.utilities.SNVLists;
import mayday.core.tasks.AbstractTask;

public class SNPExporter {

	private DataStorage ds;
	
	public SNPExporter(DataStorage ds) {
		this.ds = ds;
	}
	
	public void exportSNPsGeneWise(final File folder, final boolean overwrite) {
		
		AbstractTask exportTask = new AbstractTask("Export SNPs gene wise") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				if(ds == null) {
					writeLog("No project has been selected!\nExport canceled!");
					return;
				}
				
				GeneList genes = ds.getGenes();
				int numGenes = genes.size();
				File[] files = new File[numGenes];
				
				Map<Gene, BufferedWriter> geneToWriter = new HashMap<Gene, BufferedWriter>();
				
				String folderPath = folder.getAbsolutePath();
				
				//create the output files
				for(int i = 0; i < files.length; i++) {
					files[i] = new File(folderPath + "/" + genes.getGene(i).getName() + ".snps");
					if(files[i].exists() && !overwrite) {
						this.writeLog("File " + files[i].getName() + " already exists!\nOverwritting is disabled!\nExport canceled!");
						return;
					}
					BufferedWriter bw = new BufferedWriter(new FileWriter(files[i]));
					geneToWriter.put(genes.getGene(i), bw);
				}
				
				SNVList snps = SNVLists.createUniqueSNVList(ds.getProjectHandler().getSelectedSNVLists());
				
				if(snps == null) {
					writeLog("No SNPs have been selected for export");
					return;
				}
				
				for(SNV s : snps) {
					if(hasBeenCancelled()) {
						for(BufferedWriter bw : geneToWriter.values()) {
							bw.close();
						}
						for(int i = 0; i < files.length; i++) {
							files[i].delete();
						}
						return;
					}
					
					String geneName = s.getGene();
					
					if(geneName.equals("no_gene")) {
						writeLog("SNP " + s.getID() + " does not hava an associated gene. Skipped!\n");
						continue;
					}
					
					Gene g = genes.getGene(geneName);
					
					BufferedWriter bw = geneToWriter.get(g);
					bw.write(s.getID());
					bw.newLine();
				}
				
				//close all writer
				for(BufferedWriter bw : geneToWriter.values()) {
					bw.close();
				}
				
				writeLog("Exporting SNPs gene wise has finished!");
			}
		};
		
		exportTask.start();
	}
}
