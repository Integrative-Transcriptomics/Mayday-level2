package mayday.GWAS.io;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import mayday.GWAS.actions.RevealTask;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.ld.old.LDBlock;
import mayday.GWAS.data.ld.old.LDStructure;
import mayday.GWAS.utilities.SNPLists;

public class LDStructureParser extends AbstractDataParser {
	
	public LDStructureParser() {
		super();
	}
	
	public void setDataStorage(DataStorage dataStorage) {
		this.ds = dataStorage;
	}
	
	public LDStructureParser(DataStorage ds) {
		super(ds);
	}

	@Override
	public void read(File input) {
		// TODO Auto-generated method stub
	}

	public void write(File output, RevealTask task) {
		try  {
			BufferedWriter bw = new BufferedWriter(new FileWriter(output));
			SNPList snps = SNPLists.createUniqueSNPList(ds.getProjectHandler().getSelectedSNPLists());
			LDStructure ldS = ds.getLDStructure(0);
			
			if(ldS == null) {
				bw.close();
				return;
			}
			
			String noBlockPrefix = "NB";
			int noBlockCounter = 1;
			
			double numSNPs = snps.size();
			int count = 0;
			
			for(SNP s : snps) {
				LDBlock b = ldS.getBlock(s);
				
				if(task != null && !task.hasBeenCancelled()) {
					task.reportCurrentFractionalProgressStatus((count++)/numSNPs);
				}
				
				if(b == null) {
					bw.write(s.getID() + "\t" + noBlockPrefix + (noBlockCounter++));
				} else {
					bw.write(s.getID() + "\tB" + b.getIndex());
				}
				bw.newLine();
			}
			
			bw.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		this.write(output, null);
	}

	@Override
	public String getName() {
		return "Linkage Disequilibrium Structure Parser";
	}

	@Override
	public String getType() {
		return "data.lds";
	}

	@Override
	public String getDescription() {
		return "Read/Write LDS files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
