package mayday.GWAS.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.SNPPair;
import mayday.GWAS.data.ld.LDResults;
import mayday.core.tasks.AbstractTask;

public class LDParser extends AbstractDataParser {
	
	private boolean header = true;
	
	public LDParser() {
		super(null);
	}
	
	public LDParser(DataStorage ds) {
		super(ds);
	}
	
	public void setDataStorage(DataStorage dataStorage) {
		this.ds = dataStorage;
	}
	
	public void setHeader(boolean header) {
		this.header = header;
	}
	
	public void read(File ldFile) {
		this.read(ldFile, null);
	}
	
	public void read(File ldFile, AbstractTask task) {
		try {
			if(task != null) {
				task.writeLog("Parsing LD file ...\n");
			}
			
			BufferedReader br = new BufferedReader(new FileReader(ldFile));
			String line = null;
			
			LDResults ldResults = new LDResults();
			
			boolean curHeader = header;
			
			SNPList global = ds.getGlobalSNPList();
			
			while((line = br.readLine()) != null) {
				if(curHeader == true) {
					curHeader = false;
					continue;
				}
					
				String[] splitted = split(line);
				
				String snpA;
				String snpB;
				
				if(splitted[2].contains("_")) {
					snpA = splitted[2].split("_")[1];
				} else {
					snpA = splitted[2];
				}
				
				if(splitted[5].contains("_")) {
					snpB = splitted[5].split("_")[1];
				} else {
					snpB = splitted[5];
				}
				
				double r2Value = Double.parseDouble(splitted[6]);
				
				SNP a = global.get(snpA);
				
				//if a is null, we don't need this pair
				if(a == null)
					continue;
				
				SNP b = global.get(snpB);
				
				//a is not null and b is not null
				if(b != null) {
					ldResults.put(new SNPPair(a, b), r2Value);
				}
			}
			
			br.close();
			
			ds.getMetaInformationManager().add(LDResults.MYTYPE, ldResults);
			
			if(task != null) {
				task.writeLog("Done!\n");
			}
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return "Linkage Disequilibirum File Parser";
	}

	@Override
	public String getType() {
		return "data.ld";
	}

	@Override
	public String getDescription() {
		return "Read/Write LD files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
