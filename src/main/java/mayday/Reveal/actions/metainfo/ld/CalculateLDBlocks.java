package mayday.Reveal.actions.metainfo.ld;

import java.util.Collection;

import javax.swing.JOptionPane;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.SNPPair;
import mayday.Reveal.data.ld.LDBlocks;
import mayday.Reveal.data.ld.LDClustering;
import mayday.Reveal.data.ld.LDResults;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.utilities.RevealMenuConstants;
import mayday.Reveal.utilities.SNPLists;
import mayday.clustering.ClusterTask;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.tasks.AbstractTask;

public class CalculateLDBlocks extends RevealPlugin {
	
	@Override
	public String getName() {
		return "Calculate LD Blocks";
	}

	@Override
	public String getType() {
		return "data.meta.calcLDblocks";
	}

	@Override
	public String getDescription() {
		return "Calculates LD block stuctures using given LD results";
	}

	@Override
	public String getMenuName() {
		return "Calculate LD Blocks";
	}

	@Override
	public void run(Collection<SNPList> snpLists) {
		final DataStorage ds = projectHandler.getSelectedProject();
		
		if(ds == null) {
			JOptionPane.showMessageDialog(null, "No project has been selected!");
			return;
		}
		
		if(snpLists == null) {
			JOptionPane.showMessageDialog(null, "No SNPList has been selected!");
			return;
		}
		
		DoubleSetting r2T = new DoubleSetting("R2 Threshold", "Define the threshold for considering SNPs to be in LD", 0.85);
		
		SettingDialog sd = new SettingDialog(null, "Calculate LD Blocks Setting", r2T);
		sd.showAsInputDialog();
		
		if(!sd.closedWithOK())
			return;
		
		final double threshold = r2T.getDoubleValue();
		
		final SNPList unionList = SNPLists.createUniqueSNPList(snpLists);
		
		final MetaInformation mi = projectHandler.getSelectedMetaInformation();
		
		if (mi == null || !(mi instanceof LDResults)) {
			JOptionPane.showMessageDialog(null, "Please also select the LD results that should to be used for block calculation.");
			return;
		}
		
		AbstractTask task = new AbstractTask("Calculate LD Blocks") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				LDResults ldRes = (LDResults)mi;

				LDClustering clustering = new LDClustering(ldRes, unionList, threshold);
				
				ClusterTask cTask = new ClusterTask("LD-Block Calculation");
				cTask.setClAlg(clustering);
				clustering.setClusterTask(cTask);
				cTask.start();
				cTask.waitFor();
				
				//collect results and build resulting probe lists
				int [] clusterIndices = cTask.getClResult();
				clustering.setClusterTask(null);
				
				if(!cTask.hasBeenCancelled()) {
					LDBlocks blocks = new LDBlocks(unionList, clusterIndices);
					
					for(int i = 1; i < clusterIndices.length; i++) {
						SNP a = unionList.get(i-1);
						SNP b = unionList.get(i);
						Double r2 = ldRes.get(new SNPPair(a,b));
						
						System.out.println(unionList.get(i-1).getID() + "\t" + clusterIndices[i-1] + "\t" + r2);
					}
					
//					SNP a = unionList.get("chr8:32596288");
//					SNP b = unionList.get("chr8:32598990");
//					SNP c = unionList.get("rs10098433");
//					SNP d = unionList.get("chr8:32602962");
//					SNP e = unionList.get("chr8:32603168");
//					
//					System.out.println(a.getID() + "\t" + b.getID() + "\t" + ldRes.get(new SNPPair(a, b)));
//					System.out.println(a.getID() + "\t" + c.getID() + "\t" + ldRes.get(new SNPPair(a, c)));
//					System.out.println(a.getID() + "\t" + d.getID() + "\t" + ldRes.get(new SNPPair(a, d)));
//					System.out.println(a.getID() + "\t" + e.getID() + "\t" + ldRes.get(new SNPPair(a, d)));
//					System.out.println(b.getID() + "\t" + c.getID() + "\t" + ldRes.get(new SNPPair(b, c)));
//					System.out.println(b.getID() + "\t" + d.getID() + "\t" + ldRes.get(new SNPPair(b, d)));
//					System.out.println(b.getID() + "\t" + e.getID() + "\t" + ldRes.get(new SNPPair(b, e)));
//					System.out.println(c.getID() + "\t" + d.getID() + "\t" + ldRes.get(new SNPPair(c, d)));
//					System.out.println(c.getID() + "\t" + e.getID() + "\t" + ldRes.get(new SNPPair(c, e)));
//					System.out.println(d.getID() + "\t" + e.getID() + "\t" + ldRes.get(new SNPPair(d, e)));
					
					ds.getMetaInformationManager().add(LDBlocks.MYTYPE, blocks);
				}
			}
		};
		
		task.start();
	}

	@Override
	public String getMenu() {
		return RevealMenuConstants.META_INFORMATION+"/Linkage disequilibrium";
	}

	@Override
	public String getCategory() {
		return "Project/Meta-Information";
	}	
}
