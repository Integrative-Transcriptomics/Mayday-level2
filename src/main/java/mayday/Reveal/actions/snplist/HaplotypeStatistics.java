package mayday.Reveal.actions.snplist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.utilities.SNPLists;
import mayday.core.Probe;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.PathSetting;

public class HaplotypeStatistics extends SNPListPlugin {

	@Override
	public String getName() {
		return "SNPList Statistics";
	}

	@Override
	public String getType() {
		return "data.snplist.stats";
	}

	@Override
	public String getDescription() {
		return "Calculates simple SNPList statistics";
	}

	@Override
	public String getMenuName() {
		return "Basic Statistics";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		SNPList unionList = SNPLists.createUniqueSNPList(snpLists);
		DataStorage ds = this.projectHandler.getSelectedProject();
		
		SubjectList subjects = ds.getSubjects();
		List<Subject> affected = subjects.getAffectedSubjects();
		List<Subject> unaffected = subjects.getUnaffectedSubjects();
		
		double numAffected = affected.size();
		double numUnaffected = unaffected.size();
		
		HaplotypesList haplotypes = ds.getHaplotypes();
		GeneList genes = ds.getGenes();
		
		PathSetting outputPath = new PathSetting("ExpMatrix file path", null, null,false, true, false);
		PathSetting outputPath2 = new PathSetting("Distribution file path", null, null, false, true, false);
		BooleanSetting affectedSetting = new BooleanSetting("Affected",null,false);
		
		HierarchicalSetting set = new HierarchicalSetting("Path Settings");
//		set.addSetting(outputPath);
		set.addSetting(outputPath2);
		set.addSetting(affectedSetting);
		
		SettingDialog sd = new SettingDialog(null, "Output file ...", set);
		
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		File fDist = new File(outputPath2.getStringValue());
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(fDist));
			
			SNP snp = unionList.get("rs1520458");
			
			List<Subject> chosenSubjects = null;
			
			if(affectedSetting.getBooleanValue()) {
				chosenSubjects = affected;
			} else {
				chosenSubjects = unaffected;
			}
			
			int numChosen = chosenSubjects.size();
			
			double[] refDist = new double[numChosen];
			double[] hetDist = new double[numChosen];
			double[] homDist = new double[numChosen];
			
			char reference = snp.getReferenceNucleotide();
			int snpIndex = snp.getIndex();
			
			Probe probe = genes.getGene("DRD4");
			
			for(int i = 0; i < numChosen; i++) {
				int subjectIndex = chosenSubjects.get(i).getIndex();
				char a = haplotypes.get(subjectIndex).getSNPA(snpIndex);
				char b = haplotypes.get(subjectIndex).getSNPB(snpIndex);
				
				if(a == reference && b == reference) {
					refDist[i] = probe.getValue(subjectIndex);
					hetDist[i] = Double.NaN;
					homDist[i] = Double.NaN;
				} else if((a == reference && b != reference) || (b == reference && a != reference)) {
					refDist[i] = Double.NaN;
					hetDist[i] = probe.getValue(subjectIndex);
					homDist[i] = Double.NaN;
				} else if(a != reference && b != reference) {
					refDist[i] = Double.NaN;
					hetDist[i] = Double.NaN;
					homDist[i] = probe.getValue(subjectIndex);
				}
			}
			
			bw.write("SubjectID\tReference\tHeterozygous\tHomozygous\n");
			
			for(int i = 0; i < numChosen; i++) {
				bw.write(chosenSubjects.get(i).getID()+"\t");
				bw.write(refDist[i]+"\t");
				bw.write(hetDist[i]+"\t");
				bw.write(homDist[i]+"\n");
			}
			
			bw.flush();
			bw.close();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
		
		
		
//		File f = new File(outputPath.getStringValue());
//		
//		try {
//			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
//			
//			bw.write("ProbeID\t");
//			
//			for(SNP snp : unionList) {
//				bw.write(snp.getID()+"\t");
//			}
//			
//			bw.write("\n");
//			
//			for(Probe probe : genes) {
//				
//				bw.write(probe.getName() + "\t");
//				
//				for(SNP snp : unionList) {
//					char reference = snp.getReferenceNucleotide();
//					
//					double homoAf = 0;
//					double heteroAf = 0;
//					double refAf = 0;
//					
//					double homoUn = 0;
//					double heteroUn = 0;
//					double refUn = 0;
//					
//					int numHomoAf = 0;
//					int numHetAf = 0;
//					int numRefAf = 0;
//					
//					int numHomoUAf = 0;
//					int numHetUAf = 0;
//					int numRefUAf = 0;
//					
//					int snpIndex = snp.getIndex();
//					
//					for(Subject s : affected) {
//						int subjectIndex = s.getIndex();
//						char a = haplotypes.get(subjectIndex).getSNPA(snpIndex);
//						char b = haplotypes.get(subjectIndex).getSNPB(snpIndex);
//						
//						if(a == reference && b == reference) {
//							refAf += probe.getValue(subjectIndex);
//							numRefAf++;
//						} else if((a == reference && b != reference) || (b == reference && a != reference)) {
//							heteroAf += probe.getValue(subjectIndex);
//							numHetAf++;
//						} else if(a != reference && b != reference) {
//							homoAf += probe.getValue(subjectIndex);
//							numHomoAf++;
//						}
//					}
//					
//					for(Subject s : unaffected) {
//						int subjectIndex = s.getIndex();
//						char a = haplotypes.get(subjectIndex).getSNPA(snpIndex);
//						char b = haplotypes.get(subjectIndex).getSNPB(snpIndex);
//						
//						if(a == reference && b == reference) {
//							refUn += probe.getValue(subjectIndex);
//							numRefUAf++;
//						} else if((a == reference && b != reference) || (b == reference && a != reference)) {
//							heteroUn += probe.getValue(subjectIndex);
//							numHetUAf++;
//						} else if(a != reference && b != reference) {
//							homoUn += probe.getValue(subjectIndex);
//							numHomoUAf++;
//						}
//					}
//					
//					double ref = (refAf + refUn) / (numRefAf + numRefUAf);
//					
//					double afExp = (homoAf + 2 * heteroAf) / (numHomoAf + numHetAf);
//					double unExp = (homoUn + 2 * heteroUn) / (numHomoUAf + numHetUAf);
//					
//					double effect = afExp - unExp;
//					
////					System.out.print(Math.round(afExp * 100.) / 100. + "\t");
////					System.out.print(Math.round(unExp * 100.) / 100. + "\t");
//					bw.write(Math.round(effect * 1000.) / 1000. + "\t");
//					
//					double refEff = refAf/numAffected - refUn/numUnaffected;
//					double heteroEff = heteroAf/numAffected - heteroUn/numUnaffected;
//					double homoEff = homoAf/numAffected - homoUn/numUnaffected;
//					
//					double effect2 = (homoEff + 2 * heteroEff) - refEff;
//					
////					System.out.print(Math.round(refEff * 100.) / 100. + "\t");
////					System.out.print(Math.round(heteroEff * 100.) / 100. + "\t");
////					System.out.print(Math.round(homoEff * 100.) / 100. + "\t");
////					
////					System.out.print(Math.round(effect2 * 100.) / 100. + "\n");
//				}
//				
//				bw.write("\n");
//			}
//			
//			bw.flush();
//			bw.close();
//			
//		} catch(Exception ex) {
//			ex.printStackTrace();
//		}
	}
}
