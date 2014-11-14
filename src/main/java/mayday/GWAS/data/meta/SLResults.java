package mayday.GWAS.data.meta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Set;

import mayday.GWAS.data.Gene;
import mayday.GWAS.data.SNP;
import mayday.core.io.ReadyBufferedReader;

public class SLResults extends MetaInformationPlugin {

	public static final String MYTYPE = "SLRS";
	
	private HashMap<Gene, SingleLocusResult> slrs;
	private double minPValue = Double.MAX_VALUE;
	
	public SLResults() {
		this.slrs = new HashMap<Gene, SingleLocusResult>();
	}
	
	public SingleLocusResult put(Gene g, SingleLocusResult r) {
		double singleMinP = r.minPValue;
		if(singleMinP < minPValue) {
			minPValue = singleMinP;
		}
		return slrs.put(g, r);
	}
	
	/**
	 * @return max p value of the single locus results considering all snps and all genes
	 */
	public double getMinPValue() {
		return this.minPValue;
	}
	
	@Override
	public String getName() {
		return "Single Locus Results";
	}

	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		//Header for Meta Information Plugins
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		for(Gene g : slrs.keySet()) {
			SingleLocusResult r = slrs.get(g);
			
			bw.append(">");
			bw.append(g.getName());
			bw.append("\n");
			
			if(r != null) {
				bw.append(slrs.get(g).serialize());
				bw.append("\n");
			} else
				bw.append("null\n");
		}
	}

	@Override
	public boolean deSerialize(String serial) {
		ReadyBufferedReader br = new ReadyBufferedReader(new StringReader(serial));
		
		try {		
			String line = br.readLine();
			while(line != null && line.startsWith(">")) {
				line = line.substring(1);
				Gene g = dataStorage.getGenes().getGene(line);
				SingleLocusResult slr = new SingleLocusResult(g);
				//g is not in the set of available genes
				//we don't need sl results for that g
				if(g == null) {
					while((line = br.readLine()) != null && 
							!(line.startsWith(">") || line.startsWith("$")))
						continue;
					continue;
				}
				
				//as long no new object modifier is seen we 
				//can add SNP stats to SLR
				while((line = br.readLine()) != null && 
						!(line.startsWith(">") || line.startsWith("$"))) {
					//if a line contains null, there are no slr for this gene!
					//also skip empty lines
					if(line.equals("null") || line.trim().equals(""))
						continue;
					
					String[] slrLine = line.split("\t");
					SNP snp = dataStorage.getGlobalSNPList().get(slrLine[0]);
					if(snp != null) {
						double beta = Double.parseDouble(slrLine[1]);
						double se = Double.parseDouble(slrLine[2]);
						double r2 = Double.parseDouble(slrLine[3]);
						double t = Double.parseDouble(slrLine[4]);
						double p = Double.parseDouble(slrLine[5]);
						
						SingleLocusResult.Statistics stats = slr.new Statistics(beta, se, r2, t, p);
						slr.put(snp, stats);
					}
				}
				
				//a new object modifier has been seen (either '>' or '$')
				slrs.put(g, slr);
			}
			
			br.close();
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "Single Locus Association Results";
	}
	
	public String toString() {
		return "Results for " + slrs.size() + " Genes";
	}

	public SingleLocusResult get(Gene g) {
		return slrs.get(g);
	}
	
	public Set<Gene> keySet() {
		return slrs.keySet();
	}

	@Override
	public Class<?> getResultClass() {
		return SingleLocusResult.Statistics.class;
	}
}
