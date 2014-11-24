package mayday.Reveal.functions.prerequisite;

import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.meta.StatisticalTestResult;

public class PrerequisiteChecker {
	
	public static String ERROR_MESSAGE = "";
	
	public static boolean checkPrerequisites(DataStorage ds, List<Integer> prerequisites) {
		boolean fulfilled = true;
		for(int i = 0; i < prerequisites.size(); i++) {
			int prerequisite = prerequisites.get(i);
			
			switch(prerequisite) {
			case Prerequisite.GENOME:
				if(ds.getGenome() == null) {
					ERROR_MESSAGE = "Genome is missing";
					fulfilled = false;
				}
				break;
			case Prerequisite.GENOME_ANNOTATION:
				break;
			case Prerequisite.LD:
				if(ds.getLDStructures().size() == 0) {
					ERROR_MESSAGE = "LD Information is missing";
					fulfilled = false;
				}
				break;
			case Prerequisite.SINGLE_LOCUS_RESULT:
				if(!ds.getMetaInformationManager().containsKey("SLRS"))
					fulfilled = false;
				break;
			case Prerequisite.TWO_LOCUS_RESULT:
				if(!ds.getMetaInformationManager().containsKey("TLRS"))
					fulfilled = false;
				break;
			case Prerequisite.STAT_TEST_RESULT:
				if(!ds.getMetaInformationManager().containsKey(StatisticalTestResult.MYTYPE))
					fulfilled = false;
				break;
			case Prerequisite.SNP_LIST_SELECTED:
				if(ds.getProjectHandler().getSelectedSNPLists().size() == 0) {
					ERROR_MESSAGE = "No SNPList selected";
					fulfilled = false;
				}
				break;
			}
		}
		
		return fulfilled;
	}
		
}
