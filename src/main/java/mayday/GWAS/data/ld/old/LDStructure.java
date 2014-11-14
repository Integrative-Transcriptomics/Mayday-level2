package mayday.GWAS.data.ld.old;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPPair;
import mayday.GWAS.data.ld.LDResults;
import mayday.GWAS.data.meta.MetaInformationPlugin;

public class LDStructure extends MetaInformationPlugin {
	
	public static final String MYTYPE = "LDS";
	
	private HashMap<SNP, LDBlock> snpsToBlocks = new HashMap<SNP, LDBlock>();
	private HashMap<LDBlockPair, Boolean> hasEdge = new HashMap<LDBlockPair, Boolean>();
	private double threshold = -1;
	

	private int blockCount = 0;
	
	public LDStructure(DataStorage ds) {
		this.dataStorage = ds;
	}
	
	public double getThreshold() {
		return this.threshold;
	}
	
	public void calculateLDStructure(final double threshold, LDResults ldResults) {
		this.threshold = threshold;
		Set<SNPPair> snpPairs = ldResults.keySet();
		
		for(SNPPair sp : snpPairs) {
			if(ldResults.get(sp).compareTo(threshold) > 0) {
				LDBlock containingSet1 = snpsToBlocks.get(sp.snp1);
				LDBlock containingSet2 = snpsToBlocks.get(sp.snp2);
				if(containingSet1 == null && containingSet2 == null) {
					LDBlock newSet = new LDBlock(blockCount++);
					newSet.add(sp.snp1);
					newSet.add(sp.snp2);
					snpsToBlocks.put(sp.snp1, newSet);
					snpsToBlocks.put(sp.snp2, newSet);
				} else if(containingSet1 == null && containingSet2 != null) {
					containingSet2.add(sp.snp1);
					snpsToBlocks.put(sp.snp1, containingSet2);
				} else if(containingSet2 == null && containingSet1 != null) {
					containingSet1.add(sp.snp2);
					snpsToBlocks.put(sp.snp2, containingSet1);
				} else {
					containingSet1.addAll(containingSet2);
					for(SNP s : containingSet2) {
						snpsToBlocks.put(s, containingSet1);
					}
					containingSet2.clear();
				}
			}
		}
		
		HashSet<Set<SNP>> set = new HashSet<Set<SNP>>();
		set.addAll(snpsToBlocks.values());
	}
	
	public boolean hasLDEdge(SNPPair sp) {
		LDBlock containingSet1 = snpsToBlocks.get(sp.snp1);
		LDBlock containingSet2 = snpsToBlocks.get(sp.snp2);
		
		if(containingSet1 == null) {
			LDBlock b = new LDBlock(blockCount++);
			b.add(sp.snp1);
			snpsToBlocks.put(sp.snp1, b);
			containingSet1 = b;
		}
		
		if(containingSet2 == null) {
			LDBlock b = new LDBlock(blockCount++);
			b.add(sp.snp2);
			snpsToBlocks.put(sp.snp2, b);
			containingSet2 = b;
		}
		
		if(containingSet1 == containingSet2)
			//do not draw edges between snps that are in ld
			return true;
		
		LDBlockPair bp = new LDBlockPair(containingSet1, containingSet2);
		
		if(hasEdge.get(bp) != null) {
			return hasEdge.get(bp);
		} else	{
			hasEdge.put(bp, true);
			return false;
		}
	}
	
	public Set<LDBlock> getBlocks() {
		return new HashSet<LDBlock>(snpsToBlocks.values());
	}
	
	public void resetEdges() {
		this.hasEdge.clear();
	}

	public int getNumLDBlocks() {
		return snpsToBlocks.size();
	}

	public LDBlock getBlock(SNP s) {
		return this.snpsToBlocks.get(s);
	}

	@Override
	public void serialize(BufferedWriter bw) throws IOException {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean deSerialize(String serial) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getName() {
		return "LD Structure Information";
	}

	@Override
	public String getType() {
		return "data.meta." + MYTYPE;
	}

	@Override
	public String getDescription() {
		return "Provides LD Structure Information for SNPs";
	}

	@Override
	public Class<?> getResultClass() {
		return LDBlock.class;
	}
}
