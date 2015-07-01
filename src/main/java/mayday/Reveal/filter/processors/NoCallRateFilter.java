package mayday.Reveal.filter.processors;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.filter.AbstractDataProcessor;

public class NoCallRateFilter extends AbstractDataProcessor<SNV, Double> {

	@Override
	public void dispose() {}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : new Class<?>[]{Double.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNV.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "No-Call Frequency";
	}

	@Override
	protected Double convert(SNV value) {
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
