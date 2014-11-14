package mayday.GWAS.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GeneList;
import mayday.GWAS.data.GenePair;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.SNPPair;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.data.meta.TLResults;
import mayday.GWAS.data.meta.TwoLocusResult;

/**
 * @author jaeger
 *
 */
public class TLRParser extends AbstractDataParser {
	
	private DataStorage data;
	
	public TLRParser() {
		this(null);
	}
	
	public void setDataStorage(DataStorage ds) {
		this.ds = ds;
	}
	
	/**
	 * @param data
	 */
	public TLRParser(DataStorage data) {
		this.data = data;
	}
	
	/**
	 * @param tlrFile
	 */
	public void read(File tlrFile) {
		int line = 0;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(tlrFile));
			String strLine;
			Gene gene = null;
			TwoLocusResult tlr = null;
			GeneList genes = data.getGenes();
			SNPList snps = data.getGlobalSNPList();
			
			//get gene from filename
			String filename = tlrFile.getName();
			String geneName = filename.split("\\.")[1];
			gene = genes.getGene(geneName);
			TLResults tlrs = new TLResults() {
				@Override
				public String getName() {
					return "Two Locus Results";
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
				} else if(elements.length != 7){
					br.close();
					throw new IOException("File has a unknown format!");
				}
				
				if(line == 0) {
					tlr = new TwoLocusResult(gene);
				} else {
					Gene gene1 = genes.getGene(Integer.parseInt(elements[0]) - 1);
					Gene gene2 = genes.getGene(Integer.parseInt(elements[2]) - 1);
					
					String snpName1 = elements[1];
					String snpName2 = elements[3];
					
					String[] splitSNPNames = elements[1].split("_");
					
					
					
					if(splitSNPNames.length > 1) {
						gene1 = genes.getGene(splitSNPNames[0]);
						snpName1 = splitSNPNames[1];
					}
					
					splitSNPNames = elements[3].split("_");
					
					if(splitSNPNames.length > 1) {
						gene2 = genes.getGene(splitSNPNames[0]);
						snpName2 = splitSNPNames[1];
					}
					
					SNP snp1 = snps.get(snpName1);
					SNP snp2 = snps.get(snpName2);
					
					double beta = elements[4].equals("NA") ? Double.NaN : Double.parseDouble(elements[4]);
					double stat = elements[5].equals("NA") ? Double.NaN : Double.parseDouble(elements[5]);
					double p = elements[6].equals("NA") ? Double.NaN : Double.parseDouble(elements[6]);
					
					GenePair gp = new GenePair(gene1, gene2);
					
					List<SNPPair> snpPairs = tlr.get(gp);
					
					if(snpPairs != null) {
						List<TwoLocusResult.Statistics> stats = tlr.statMapping.get(gp);
						SNPPair snpPair = new SNPPair(snp1, snp2);
						//assume snpPair is not already contained in the snpPairs list
						snpPairs.add(snpPair);
						stats.add(tlr.new Statistics(beta, stat, p));
					} else {
						snpPairs = new LinkedList<SNPPair>();
						tlr.put(gp, snpPairs);
						snpPairs.add(new SNPPair(snp1, snp2));
						tlr.statMapping.put(gp, new LinkedList<TwoLocusResult.Statistics>());
						List<TwoLocusResult.Statistics> stats = tlr.statMapping.get(gp);
						stats.add(tlr.new Statistics(beta, stat, p));
					}
				}
				line++;
			}
			
			br.close();
			
			if(tlr != null && tlr.size() > 0) {
				if(data.getMetaInformationManager().get(TLResults.MYTYPE) == null) {
					tlrs.put(gene, tlr);
					data.getMetaInformationManager().add(TLResults.MYTYPE, tlrs);
				} else {
					List<MetaInformation> metaInfoList = data.getMetaInformationManager().get(TLResults.MYTYPE);
					int last = metaInfoList.size()-1;
					TLResults oldTlrs = (TLResults) metaInfoList.get(last);
					oldTlrs.put(gene, tlr);
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
		return "TLR Parser";
	}

	@Override
	public String getType() {
		return "data.tlr";
	}

	@Override
	public String getDescription() {
		return "Read PLINK two locus results files";
	}

	@Override
	public String getCategory() {
		return "Data Import & Export";
	}
}
