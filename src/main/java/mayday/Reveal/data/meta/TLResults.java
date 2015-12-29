package mayday.Reveal.data.meta;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVPair;

public class TLResults extends MetaInformationPlugin {

	public static final String MYTYPE = "TLRS";
	
	private HashMap<Gene, TwoLocusResult> tlrs;
	private int maxSNPPairs = 0;
	
	public TLResults() {
		this.tlrs = new HashMap<Gene, TwoLocusResult>();
	}
	
	public TwoLocusResult put(Gene g, TwoLocusResult r) {
		int numSNPPairs = r.getMaxSNPPairs();
		if(maxSNPPairs < numSNPPairs) {
			maxSNPPairs = numSNPPairs;
		}
		return tlrs.put(g, r);
	}
	
	public float getMaxSNPPairs() {
		return this.maxSNPPairs;
	}
	
	@Override
	public String getName() {
		return "Two Locus Results";
	}

	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		//Header for Meta Information Plugin
		bw.append("$META\n");
		bw.append(getCompleteType());
		bw.append("\n");
		
		for(Gene g : tlrs.keySet()) {
			TwoLocusResult r = tlrs.get(g);
			
			bw.append(">");
			bw.append(g.getName());
			bw.append("\n");
			
			if(r != null) {
				bw.append(tlrs.get(g).serialize());
				bw.append("\n");
			} else
				bw.append("null\n");
		}
	}

	@Override
	public boolean deSerialize(String serial) {
		BufferedReader br = new BufferedReader(new StringReader(serial));
		
		try {
			String line = br.readLine();
			while(line != null && line.startsWith(">")) {
				//skip first '>' from the gene
				line = line.substring(1);
				Gene g = dataStorage.getGenes().getGene(line);
				//g is not in the gene list, we don't need the tlr for g
				if(g == null) {
					while((line = br.readLine()) != null && 
							!(line.startsWith(">") || line.startsWith("$")))
						continue;
					continue;
				}
				
				TwoLocusResult tlr = new TwoLocusResult(g);
				
				line = br.readLine();
				//g is in the list, but we don't have results for g
				if(line.equals("null")) {
					line = br.readLine();
					continue;
				}
				
				while(line != null && line.startsWith(">>")) {
					//skip first two '>' from the gene pair
					line = line.substring(2);
					String[] genePairLine = line.split(",");
					Gene g1 = dataStorage.getGenes().getGene(genePairLine[0]);
					Gene g2 = dataStorage.getGenes().getGene(genePairLine[1]);
					
					if(g1 == null || g2 == null) {
						while((line = br.readLine()) != null && 
								!(line.startsWith(">>") || line.startsWith(">") || line.startsWith("$"))) {
							continue;
						}
						continue;
					}
					
					GenePair gp = new GenePair(g1, g2);
					List<SNVPair> snpPairs = new ArrayList<SNVPair>();
					List<TwoLocusResult.Statistics> stats = new ArrayList<TwoLocusResult.Statistics>();
					
					while((line = br.readLine()) != null && 
							!(line.startsWith(">>") || line.startsWith(">") || line.startsWith("$"))) {
						//skip empty lines
						if(line.trim().equals(""))
							continue;
						String[] tlrLine = line.split("\t");
						SNV snp1 = dataStorage.getGlobalSNVList().get(tlrLine[0]);
						SNV snp2 = dataStorage.getGlobalSNVList().get(tlrLine[1]);
						
						//if either snp is not in the list, skip this entry
						if(snp1 == null || snp2 == null)
							continue;
						
						//both snps are in the global snp list
						SNVPair sp = new SNVPair(snp1, snp2);
						snpPairs.add(sp);
						
						double beta = Double.parseDouble(tlrLine[2]);
						double stat = Double.parseDouble(tlrLine[3]);
						double p = Double.parseDouble(tlrLine[4]);
						
						TwoLocusResult.Statistics statistic = tlr.new Statistics(beta, stat, p);
						stats.add(statistic);
					}
					
					tlr.put(gp, snpPairs);
					tlr.statMapping.put(gp, stats);
				}
				
				tlrs.put(g, tlr);
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
		return "Two Locus Association Results";
	}
	
	public String toString() {
		return "Results for " + tlrs.size() + " Genes";
	}

	public Set<Gene> keySet() {
		return tlrs.keySet();
	}

	public TwoLocusResult get(Gene gene) {
		return tlrs.get(gene);
	}

	@Override
	public Class<?> getResultClass() {
		return TwoLocusResult.class;
	}
}
