package mayday.Reveal.actions.io;

import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import mayday.Reveal.actions.RevealAction;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.SNVPair;
import mayday.Reveal.data.ld.old.LDBlock;
import mayday.Reveal.data.ld.old.LDStructure;
import mayday.Reveal.data.meta.TLResults;
import mayday.Reveal.data.meta.TwoLocusResult;
import mayday.Reveal.utilities.SNVLists;
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
			
			SNVList snps = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
			
			LDStructure lds = ds.getLDStructure(0);
			Set<LDBlock> blocks = lds.getBlocks();
			int groupCounter = ds.getGlobalSNVList().size()+1;
			
			
			HashMap<LDBlock, List<SNV>> blocksToSNPs = new HashMap<LDBlock, List<SNV>>();
			
			for(SNV s: snps) {
				LDBlock b = lds.getBlock(s);
				if(b != null) {
					if(blocksToSNPs.containsKey(b)) {
						List<SNV> blockSNPs = blocksToSNPs.get(b);
						blockSNPs.add(s);
					} else {
						List<SNV> blockSNPs = new ArrayList<SNV>();
						blockSNPs.add(s);
						blocksToSNPs.put(b, blockSNPs);
					}
				}
			}
			
			for(SNV s: snps) {
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
						List<SNVPair> snpPairs = tlr.get(gp);
						for(SNVPair sp : snpPairs) {
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
