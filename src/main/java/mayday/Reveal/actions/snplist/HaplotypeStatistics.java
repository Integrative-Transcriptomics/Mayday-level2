package mayday.Reveal.actions.snplist;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;
import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.HaplotypesList;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.Subject;
import mayday.Reveal.data.SubjectList;
import mayday.Reveal.utilities.SNVLists;
import mayday.core.Probe;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.PathSetting;

public class HaplotypeStatistics extends SNVListPlugin {

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
		return "Calculates a SNV Derived Expression Matrix and writes it a user-defined output file.";
	}

	@Override
	public String getMenuName() {
		return "SNV Derived Expression Matrix";
	}

	@Override
	public void run(Collection<SNVList> snpLists) {
		SNVList unionList = SNVLists.createUniqueSNVList(snpLists);
		DataStorage ds = this.projectHandler.getSelectedProject();
		
		SubjectList subjects = ds.getSubjects();
		List<Subject> affected = subjects.getAffectedSubjects();
		List<Subject> unaffected = subjects.getUnaffectedSubjects();
		
		HaplotypesList haplotypes = ds.getHaplotypes();
		GeneList genes = ds.getGenes();
		
		HierarchicalSetting settting = new HierarchicalSetting("Path Settings");
		
		PathSetting outputPath = new PathSetting("Expression Matrix File Path", null, null,false, false, false);
		settting.addSetting(outputPath);
		
		SettingDialog sd = new SettingDialog(null, "Expression Matrix Output file ...", settting);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		try {
			File f = new File(outputPath.getStringValue());
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			
			//write header
			bw.write("ProbeID\t");
			for(int i = 0; i < genes.size(); i++) {
				Probe pb = genes.getProbe(i);
				bw.write(pb.getDisplayName());
				//has end of line been reached?
				if(i != genes.size()-1) {
					bw.write("\t");
				}
			}
			bw.write("\n");
			
			//write expression matrix
			for(SNV snp : unionList) {
				
				bw.write(snp.getID() + "\t");
				
				for(int i = 0; i < genes.size(); i++) {
					Probe probe = genes.getProbe(i);
					char reference = snp.getReferenceNucleotide();
					
					double homoAf = 0;
					double heteroAf = 0;
					double refAf = 0;
					
					double homoUn = 0;
					double heteroUn = 0;
					double refUn = 0;
					
					int numHomoAf = 0;
					int numHetAf = 0;
					int numRefAf = 0;
					
					int numHomoUAf = 0;
					int numHetUAf = 0;
					int numRefUAf = 0;
					
					int snpIndex = snp.getIndex();
					
					for(Subject s : affected) {
						int subjectIndex = s.getIndex();
						char a = haplotypes.get(subjectIndex).getSNPA(snpIndex);
						char b = haplotypes.get(subjectIndex).getSNPB(snpIndex);
						
						if(a == reference && b == reference) {
							refAf += probe.getValue(subjectIndex);
							numRefAf++;
						} else if((a == reference && b != reference) || (b == reference && a != reference)) {
							heteroAf += probe.getValue(subjectIndex);
							numHetAf++;
						} else if(a != reference && b != reference) {
							homoAf += probe.getValue(subjectIndex);
							numHomoAf++;
						}
					}
					
					for(Subject s : unaffected) {
						int subjectIndex = s.getIndex();
						char a = haplotypes.get(subjectIndex).getSNPA(snpIndex);
						char b = haplotypes.get(subjectIndex).getSNPB(snpIndex);
						
						if(a == reference && b == reference) {
							refUn += probe.getValue(subjectIndex);
							numRefUAf++;
						} else if((a == reference && b != reference) || (b == reference && a != reference)) {
							heteroUn += probe.getValue(subjectIndex);
							numHetUAf++;
						} else if(a != reference && b != reference) {
							homoUn += probe.getValue(subjectIndex);
							numHomoUAf++;
						}
					}
					
					double meanRefAf = refAf / numRefAf;
					double meanHetAf = heteroAf / numHetAf;
					double meanHomAf = homoAf / numHomoAf;
					
					double meanRefUAf = refUn / numRefUAf;
					double meanHetUAf = heteroUn / numHetUAf;
					double meanHomUAf = homoUn / numHomoUAf;
					
					double x = -1;
					double yAf = meanHomAf / meanHetAf;
					double yUAf = meanHomUAf / meanHetUAf;
					double z = 1;
					
					double afExp = x*meanRefAf + yAf*meanHetAf + z*meanHomAf;
					double unExp = x*meanRefUAf + yUAf*meanHetUAf + z*meanHomUAf;
					
					double effect = afExp - unExp;
					
					bw.write(Double.toString(Math.round(effect * 1000.) / 1000.));
					
					//has line end been reached?
					if(i != genes.size()-1) {
						bw.write("\t");
					}
				}
				
				bw.write("\n");
			}
			
			bw.flush();
			bw.close();
			
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
