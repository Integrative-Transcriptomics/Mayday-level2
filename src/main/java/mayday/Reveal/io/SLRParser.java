package mayday.Reveal.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.SingleLocusResult;

/**
 * @author jaeger
 *
 */
public class SLRParser extends AbstractDataParser {
	
	private DataStorage data;
	
	public SLRParser() {
		this(null);
	}
	
	public void setDataStorage(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param data
	 */
	public SLRParser(DataStorage data) {
		this.data = data;
	}
	
	/**
	 * @param slrFile
	 */
	public void read(File slrFile) {
		int line = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(slrFile));
			String strLine;
			Gene gene = null;
			SingleLocusResult slr = null;
			SLResults slrs = new SLResults() {
				@Override
				public String getName() {
					return "Single Locus Results";
				}
			};
			
			while((strLine = br.readLine()) != null) {
				//skip comment lines or empty lines
				if(strLine.startsWith("#") || strLine.trim().equals(""))
					continue;
				
				String[] elements = split(strLine);
				
				if(elements == null) {
					br.close();
					throw new IOException("File could not be read!");
				} else if(elements.length != 9){
					br.close();
					throw new IOException("File has a unknown format!");
				}
				
				if(line == 0) {
					String slrFileName = slrFile.getName();
					String[] nameSplit = slrFileName.split("\\.");
					if(nameSplit.length > 1) {
						String prefix = nameSplit[0]+".";
						String suffix = "." + nameSplit[nameSplit.length-1];
						String geneName = slrFileName.replaceFirst(prefix, "");
						String qassoc = ".qassoc";
						geneName = geneName.replace(suffix, "");
						geneName = geneName.replace(qassoc, "");
						gene = data.getGenes().getGene(geneName);
						slr = new SingleLocusResult(gene);
					} else {
						br.close();
						throw new IOException("Gene name could not be read from file name: " + slrFile.getName());
					}
				} else {
//					String chromosome = elements[1];
					String snpID = elements[1];
					String[] splitSNPName = elements[1].split("_");
				
					if(splitSNPName.length > 1) {
						snpID = splitSNPName[1];
					}
					
//					int bp = Integer.parseInt(elements[3]);
//					int nmiss = Integer.parseInt(elements[4]);
					
					double beta = elements[4].equals("NA") ? Double.NaN : Double.parseDouble(elements[4]);
					double se = elements[5].equals("NA") ? Double.NaN : Double.parseDouble(elements[5]);
					double r2 = elements[6].equals("NA") ? Double.NaN : Double.parseDouble(elements[6]);
					double t = elements[7].equals("NA") ? Double.NaN : Double.parseDouble(elements[7]);
					double p = elements[8].equals("NA") ? Double.NaN : Double.parseDouble(elements[8]);
					
					SingleLocusResult.Statistics statistics = slr.new Statistics(beta, se, r2, t, p);
					SNP snp = data.getGlobalSNPList().get(snpID); 
					slr.put(snp, statistics);
				}
				line++;
			}
			
			br.close();
			
			if(slr != null && slr.size() > 0) {
				if(data.getMetaInformationManager().get(SLResults.MYTYPE) == null) {
					slrs.put(gene, slr);
					data.getMetaInformationManager().add(SLResults.MYTYPE, slrs);
				} else {
					List<MetaInformation> metaInfoList = data.getMetaInformationManager().get(SLResults.MYTYPE);
					int last = metaInfoList.size()-1;
					SLResults oldSlrs = (SLResults) metaInfoList.get(last);
					oldSlrs.put(gene, slr);
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void write(File output) {
		// TODO Auto-generated method stub
	}

	@Override
	public String getName() {
		return "SLR Parser";
	}

	@Override
	public String getType() {
		return "data.slr";
	}

	@Override
	public String getDescription() {
		return "Read/Write PLINK single locus results files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
