package mayday.GWAS.actions.snplist;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.meta.Genome;
import mayday.GWAS.utilities.SNPLists;
import mayday.core.tasks.AbstractTask;

public class UpdateSNPReferences extends SNPListPlugin {

	@Override
	public String getName() {
		return "Update SNP Reference Nucleotides";
	}

	@Override
	public String getType() {
		return "data.snplist.updateSNPRef";
	}

	@Override
	public String getDescription() {
		return "Update SNP reference nucleotides according to the currently loaded genome";
	}

	@Override
	public String getMenuName() {
		return "Update SNP References";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		try {
			DataStorage ds = projectHandler.getSelectedProject();
			
			if(ds == null)
				throw new Exception("No project has been selected.");
			
			final SNPList snpList = SNPLists.createUniqueSNPList(snpLists);
			final Genome genome = ds.getGenome();
			
			AbstractTask task = new AbstractTask("Update SNPs") {
				@Override
				protected void initialize() {}

				@Override
				protected void doWork() throws Exception {
					writeLog("Updating SNP reference nucleotides...\n\t" +
							"This task can take several minutes, " +
							"depending on the number of SNPs and size of the genome.\n\t" +
							"Please be patient.\n");
				
					genome.updateReferenceNucleotides(snpList);

					writeLog("All selected SNPs have been updated.\n");
				}
			};
			
			task.start();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}
}
