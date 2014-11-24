package mayday.Reveal.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.filter.RuleSet;

public class SNPLists {

	public static SNPList createUniqueSNPList(Collection<SNPList> snpLists) {
		if(snpLists.size() == 0)
			return new SNPList("Merged SNPs", null);
		
		if(snpLists.size() == 1) {
			SNPList src = snpLists.iterator().next();
			SNPList trg = new SNPList(src.getAttribute().getName(), src.getDataStorage());
			trg.getAttribute().setInformation(src.getAttribute().getInformation());
			trg.addAll(src);
			return trg;
		} else {
			return mergeSNPLists(snpLists, snpLists.iterator().next().getDataStorage());
		}
	}

	private static SNPList mergeSNPLists(Collection<SNPList> snpLists,
			DataStorage dataStorage) {
		
		Set<SNP> mergedSNPs = new HashSet<SNP>();
		
		SNPList newList = new SNPList("Merged SNPs", dataStorage);
		String info = "[";
		
		for(Iterator<SNPList> it = snpLists.iterator(); it.hasNext();) {
			SNPList next = it.next();
			info += next.getAttribute().getName() + "; ";
			mergedSNPs.addAll(next);
		}
		
		info = info.substring(0, info.length()-2) + "]";
		
		newList.getAttribute().setInformation(info);
		newList.addAll(mergedSNPs);
		
		return newList;
	}
	
	public static int countSNPs(DataStorage dataStorage, RuleSet ruleSet) {
		int count=0;
		for (SNP snp : dataStorage.getGlobalSNPList()) {
			Boolean pf = ruleSet.passesFilter(snp);
			if (pf==null || pf==true) 
				++count;
		}
		return count;
	}

	public static String createUniqueSNPListName(Set<SNPList> selectedSNPLists) {
		if(selectedSNPLists.size() == 0) {
			return "";
		}
		
		StringBuffer name = new StringBuffer();
		for(SNPList snpList : selectedSNPLists) {
			name.append(snpList.getAttribute().getName());
			name.append(",");
		}
		String resultingName = name.toString();
		return resultingName.substring(0, resultingName.length()-1);
	}
	
	public static double[] getGenotypeDistribution(DataStorage data, int snpIndex, boolean affected) {
		int[] count = new int[10];
		Arrays.fill(count, 0);
		
		//FIXME maybe something is wrong here!
		
		if(affected) {
			ArrayList<Subject> affectedPersons = data.getSubjects().getAffectedSubjects();
			for(Subject p : affectedPersons) {
				Haplotypes h = data.getHaplotypes().get(p.getIndex());
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[ATCGColors.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)affectedPersons.size();
			}
			return result;
		} else {
			ArrayList<Subject> unaffectedPersons = data.getSubjects().getUnaffectedSubjects();
			for(Subject p : unaffectedPersons) {
				Haplotypes h = data.getHaplotypes().get(p.getIndex());
				
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[ATCGColors.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)unaffectedPersons.size();
			}
			return result;
		}
	}
}
