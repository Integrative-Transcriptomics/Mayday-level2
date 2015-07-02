package mayday.Reveal.visualizations.SNVSummaryPlot;

import java.util.ArrayList;
import java.util.Arrays;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Haplotypes;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.utilities.RevealUtilities;

public class SummaryPlotFunctions {
	
	public static double[] getFrequencyForPairs(Integer snpIndex, boolean affected, DataStorage ds) {
		int[] count = new int[10];
		Arrays.fill(count, 0);
		
		HaplotypesList haplotypesList = ds.getHaplotypes();
		SubjectList personList = ds.getSubjects();
		
		if(affected) {
			ArrayList<Subject> affectedPersons = personList.getAffectedSubjects();
			for(Subject p : affectedPersons) {
				Haplotypes h = haplotypesList.get(p.getIndex());
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[RevealUtilities.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)affectedPersons.size();
			}
			return result;
		} else {
			ArrayList<Subject> unaffectedPersons = personList.getUnaffectedSubjects();
			for(Subject p : unaffectedPersons) {
				Haplotypes h = haplotypesList.get(p.getIndex());
				
				char[] snpPairs = new char[]{h.getSNPA(snpIndex), h.getSNPB(snpIndex)};
				Arrays.sort(snpPairs);
				count[RevealUtilities.getPairIndex(snpPairs[0], snpPairs[1])]++;
			}
			double[] result = new double[10];
			for(int j = 0; j < result.length; j++) {
				result[j] = count[j] / (double)unaffectedPersons.size();
			}
			return result;
		}
	}
}
