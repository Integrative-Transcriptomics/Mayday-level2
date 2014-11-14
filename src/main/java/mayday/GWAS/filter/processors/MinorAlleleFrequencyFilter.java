package mayday.GWAS.filter.processors;

import java.util.Arrays;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Haplotypes;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SubjectList;
import mayday.GWAS.filter.AbstractDataProcessor;

public class MinorAlleleFrequencyFilter extends AbstractDataProcessor<SNP, Double> {

	@Override
	public void dispose() {}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : new Class<?>[]{Double.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNP.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "Minor Allele Frequency";
	}

	@Override
	protected Double convert(SNP value) {
		
		//f(a) = (1 x Aa + 2x aa) / ( 2x(AA + Aa + aa) )
		
		DataStorage ds = snpList.getDataStorage();
		
		int snpIndex = value.getIndex();
		SubjectList subjects = ds.getSubjects();
		int numSubjects = subjects.size();
		
		int[] sums = new int[5];
		
		for(int i = 0; i < numSubjects; i++) {
			Subject s = subjects.get(i);
			int sIndex = s.getIndex();
			Haplotypes hps = ds.getHaplotypes().get(sIndex);
			char snpA = hps.getSNPA(snpIndex);
			char snpB = hps.getSNPB(snpIndex);
			
			char[] sorted = sortNucs(snpA, snpB);
			
			sums[getNucIndex(sorted[0])]++;
			sums[getNucIndex(sorted[1])]++;
		}
		
		int minor = min(sums);
		
		double freq = (double)minor / ((double)numSubjects * 2.);
		return freq;
	}
	
	private int min(int[] sums) {
		int maxIndex = maxIndex(sums);
		int min = Integer.MAX_VALUE;
		int minIndex = -1;
		
		for(int i = 0; i < sums.length-1; i++) {
			if(i == maxIndex) { //exclude the maximum since this has already been used for the major allele
				continue;
			} else {
				if(sums[i] < min && sums[i] != 0) {
					min = sums[i];
					minIndex = i;
				}
			}
		}
		
		if(minIndex == -1) {
			return 0;
		} else {
			return min;
		}
	}
	
	private int maxIndex(int[] sums) {
		//determine the index of the major allele value
		int index = 0;
		int max = 0;
		for(int i = 0; i < sums.length-1; i++) {
			if(sums[i] > max) {
				max = sums[i];
				index = i;
			}
		}
		return index;
	}
	
	private char[] sortNucs(char c1, char c2) {
		char[] nucs = new char[]{c1,c2};
		Arrays.sort(nucs);
		return nucs;
	}
	
	/*
	 * in the order: A,C,G,T,-
	 */
	private int getNucIndex(char c) {
		switch(c) {
		case 'A':
			return 0;
		case 'C':
			return 1;
		case 'G':
			return 2;
		case 'T':
			return 3;
		case '-':
			return 4;
		default:
			return 4;
		}
	}

	@Override
	public String getName() {
		return "Minor Allele Frequency";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.maf";
	}

	@Override
	public String getDescription() {
		return "Filter SNPs based on a minor allele frequency threshold";
	}
}
