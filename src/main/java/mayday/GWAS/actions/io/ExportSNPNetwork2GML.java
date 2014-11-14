package mayday.GWAS.actions.io;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.GWAS.actions.RevealAction;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.Gene;
import mayday.GWAS.data.GenePair;
import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.SNP;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.SNPPair;
import mayday.GWAS.data.ld.old.LDBlock;
import mayday.GWAS.data.ld.old.LDStructure;
import mayday.GWAS.data.meta.TLResults;
import mayday.GWAS.data.meta.TwoLocusResult;
import mayday.GWAS.utilities.SNPLists;
import mayday.core.settings.SettingDialog;
import mayday.core.settings.typed.PathSetting;
import mayday.core.tasks.AbstractTask;

@SuppressWarnings("serial")
public class ExportSNPNetwork2GML extends RevealAction {

	public ExportSNPNetwork2GML(ProjectHandler projectHandler) {
		super(projectHandler);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		PathSetting path = new PathSetting("GML File", null, null, false, false, true);
		
		SettingDialog dialog = new SettingDialog(null, "Select GML file", path);
		dialog.showAsInputDialog();
		
		if(!dialog.closedWithOK()) {
			return;
		}
		
		final File gmlFile = new File(path.getStringValue());
		
		AbstractTask t = new AbstractTask("Export to GML") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				writeGML(gmlFile);
			}
		};
		
		t.start();
	}
	
	private void writeGML(File gmlFile) {
		try {
			DataStorage ds = projectHandler.getSelectedProject();
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(gmlFile));
			bw.write("#GML file created by Reveal");
			bw.newLine();
			bw.write("graph [id 0 version 0 graphics [] LabelGraphics []");
			bw.newLine();
			
			SNPList snps = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
			
			LDStructure lds = ds.getLDStructure(0);
			Set<LDBlock> blocks = lds.getBlocks();
			int groupCounter = ds.getGlobalSNPList().size()+1;
			
			
			HashMap<LDBlock, List<SNP>> blocksToSNPs = new HashMap<LDBlock, List<SNP>>();
			
			for(SNP s: snps) {
				LDBlock b = lds.getBlock(s);
				if(b != null) {
					if(blocksToSNPs.containsKey(b)) {
						List<SNP> blockSNPs = blocksToSNPs.get(b);
						blockSNPs.add(s);
					} else {
						List<SNP> blockSNPs = new ArrayList<SNP>();
						blockSNPs.add(s);
						blocksToSNPs.put(b, blockSNPs);
					}
				}
			}
			
			for(SNP s: snps) {
				//TODO color nodes according to lds
				if(lds.getBlock(s) != null) {
					if(blocksToSNPs.get(lds.getBlock(s)).size() > 1) {
						int groupID = lds.getBlock(s).getIndex()+groupCounter;
						bw.write("node [id " + s.getIndex() + " label \"" + s.getID() + "\n" + s.getGene() + "\" graphics [type \"rectangle\"] LabelGraphics [type \"text\"] gid " + groupID + " ]");
					} else {
						bw.write("node [id " + s.getIndex() + " label \"" + s.getID() + "\n" + s.getGene() + "\" graphics [type \"rectangle\"] LabelGraphics [type \"text\"]]");
					}
				} else {
					bw.write("node [id " + s.getIndex() + " label \"" + s.getID() + "\n" + s.getGene() + "\" graphics [type \"rectangle\"] LabelGraphics [type \"text\"]]");
				}
				bw.newLine();
			}
			
			for(LDBlock b : blocks) {
				if(blocksToSNPs.get(b) != null) {
					if(blocksToSNPs.get(b).size() > 1) {
						bw.write("node [id " + (groupCounter+b.getIndex()) + " label \"LD\" graphics [type \"roundrectangle\" fill \"#f5f5f5\" outline \"#000000\"] LabelGraphics [text \"LD\" alignment \"right\"] isGroup 1 ]");
						bw.newLine();
					}
				}
			}
			
			TLResults tlrs = (TLResults) ds.getMetaInformationManager().get(TLResults.MYTYPE).get(0);

			HashMap<IntPair, Integer> edgeWeights = new HashMap<IntPair, Integer>();
			
			for(Gene gene: tlrs.keySet()) {
				TwoLocusResult tlr = tlrs.get(gene);
				if(tlr != null) {
					for(GenePair gp : tlr.keySet()) {
						List<SNPPair> snpPairs = tlr.get(gp);
						for(SNPPair sp : snpPairs) {
							if(snps.contains(sp.snp1) && snps.contains(sp.snp2)) {
								int source = sp.snp1.getIndex();
								int target = sp.snp2.getIndex();
								
								if(lds.getBlock(sp.snp1) != null) {
									LDBlock b = lds.getBlock(sp.snp1);
									if(blocksToSNPs.get(b) != null) {
										if(blocksToSNPs.get(b).size() > 1) {
											source = b.getIndex() + groupCounter;
										}
									}
								}
								
								if(lds.getBlock(sp.snp2) != null) {
									LDBlock b = lds.getBlock(sp.snp2);
									if(blocksToSNPs.get(b) != null) {
										if(blocksToSNPs.get(b).size() > 1) {
											target = b.getIndex() + groupCounter;
										}
									}
								}
								
								if(source == target) {
									continue;
								}
								
								IntPair ip = new IntPair(source, target);
								if(edgeWeights.containsKey(ip)) {
									int weight = edgeWeights.get(ip);
									weight++;
									edgeWeights.put(ip, weight);
								} else {
									edgeWeights.put(ip, 1);
								}
							}
						}
					}
				}
			}
			
			
			for(IntPair ip : edgeWeights.keySet()) {
				bw.write("edge [source " + ip.a + " target " + ip.b + " label \"" + edgeWeights.get(ip).intValue() +"\" graphics [type \"line\" fill \"#000000\"] LabelGraphics [type \"text\"]]");
				bw.newLine();
			}
			
			bw.write("]");
			bw.close();
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	private class IntPair {
		int a;
		int b;
		
		public IntPair(int a, int b) {
			this.a = a;
			this.b = b;
		}
		
		public boolean equals(Object o) {
			if(o instanceof IntPair) {
				IntPair p = (IntPair)o;
				return (p.a == a && p.b == b) || (p.b == a && p.a == b);
			}
			return false;
		}
		
		public int hashCode() {
			return (a * 17) ^ b;
		}
	}
}
