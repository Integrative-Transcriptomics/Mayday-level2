package mayday.GWAS.filter.processors;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Haplotypes;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.Subject;
import mayday.GWAS.data.SubjectList;
import mayday.GWAS.filter.AbstractDataProcessor;

public class NoCallRateFilter extends AbstractDataProcessor<SNP, Double> {

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
		return "No-Call Frequency";
	}

	@Override
	protected Double convert(SNP value) {
		DataStorage ds = snpList.getDataStorage();
		
		int snpIndex = value.getIndex();
		SubjectList subjects = ds.getSubjects();
		int numSubjects = subjects.size();
		
		int noCall = 0;
		
		for(int i = 0; i < numSubjects; i++) {
			Subject s = subjects.get(i);
			int sIndex = s.getIndex();
			Haplotypes hps = ds.getHaplotypes().get(sIndex);
			char snpA = hps.getSNPA(snpIndex);
			char snpB = hps.getSNPB(snpIndex);
			
			if(snpA == '-') {
				noCall++;
			}
			
			if(snpB == '-') {
				noCall++;
			}
		}
		
		double freq = (double)noCall / ((double)numSubjects * 2.);
		return freq;
	}

	@Override
	public String getName() {
		return "SNP No-Call Frequency";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.nocall";
	}

	@Override
	public String getDescription() {
		return "Filter SNPs based on the frequency of missing SNP calls";
	}
}
