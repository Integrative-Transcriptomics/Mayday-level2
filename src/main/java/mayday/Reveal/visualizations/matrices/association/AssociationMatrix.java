package mayday.Reveal.visualizations.matrices.association;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.SNVPair;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.SingleLocusResult;
import mayday.Reveal.data.meta.SingleLocusResult.Statistics;
import mayday.Reveal.data.meta.TLResults;
import mayday.Reveal.data.meta.TwoLocusResult;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.MultiArraySorter;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.ProbeList;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.linalg.vector.AbstractVector;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.structures.maps.BidirectionalHashMap;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AssociationMatrix extends RevealVisualization {

	AssociationMatrixSetting setting;
	
	private GeneList genesInRow;
	private GeneList genesInColumn;
	
	private CellObject[][] cellData;
	private HashMap<Gene, Integer> geneIDs;
	private SNVList snps;
	private HashMap<String, Set<SNV>> indexSnpMapping;
	
	public BidirectionalHashMap<Integer, Integer> sortedIndices;
	
	private MatrixComponent matrixComp;
	private ColorGradientPanel colorGradientPanel;
	
	private boolean useTlr = false;
	
	public AssociationMatrix(DataStorage ds) {
		setData(ds);
		
		MetaInformation mi = ds.getProjectHandler().getSelectedMetaInformation();
		
		if(mi != null && mi instanceof TLResults) {
			useTlr = true;
		}
		
		sortedIndices = new BidirectionalHashMap<Integer, Integer>();
		
		if(!useTlr) {
			
			Object[] selectedProbeLists = getData().getDataSet().getProbeListManager().getProbeListManagerView().getSelectedValues();
			GeneList allGenes = getData().getGenes();
			List<ProbeList> pls = new LinkedList<ProbeList>();
			
			for(int i = 0; i < selectedProbeLists.length; i++) {
				if(selectedProbeLists[i] instanceof ProbeList) {
					pls.add((ProbeList)selectedProbeLists[i]);
				}
			}
			
			if(pls.size() == 1) {
				ProbeList list = pls.get(0); 
				pls.add(list);
			}
			
			genesInRow = new GeneList(getData().getDataSet());
			genesInColumn = new GeneList(getData().getDataSet());
			
			if(pls.size() == 2) {
				ProbeList one = pls.get(0);
				ProbeList two = pls.get(1);
				for(int i = 0; i < one.getNumberOfProbes(); i++) {
					Gene g = allGenes.getGene(one.getProbe(i).getName());
					genesInColumn.addGene(g);
				}
				for(int i = 0; i < two.getNumberOfProbes(); i++) {
					Gene g = allGenes.getGene(two.getProbe(i).getName());
					genesInRow.addGene(g);
				}
			}
			
			indexSnpMapping = new HashMap<String, Set<SNV>>();
			
			String[] geneNamesRow = new String[genesInRow.size()];
			String[] geneNamesColumn = new String[genesInColumn.size()];
			
			geneIDs = new HashMap<Gene, Integer>();
			
			for(int i = 0; i < genesInRow.size(); i++) {
				geneIDs.put(genesInRow.getGene(i), i);
				geneNamesRow[i] = genesInRow.getGene(i).getDisplayName();
				sortedIndices.put(i, i);
			}
			
			for(int i = 0; i < genesInColumn.size(); i++) {
				geneIDs.put(genesInColumn.getGene(i), i);
				geneNamesColumn[i] = genesInColumn.getGene(i).getDisplayName();
			}
			
			snps = SNVLists.createUniqueSNVList(ds.getProjectHandler().getSelectedSNVLists());
			
			matrixComp = new MatrixComponent(this, geneNamesColumn, geneNamesRow);
			
			this.add(matrixComp, BorderLayout.CENTER);
		} else {
			int numGenes = ds.getGenes().size();
			int combis = numGenes * (numGenes + 1) / 2;
			
			Object[] selectedProbeLists = getData().getDataSet().getProbeListManager().getProbeListManagerView().getSelectedValues();
			GeneList allGenes = getData().getGenes();
			List<ProbeList> pls = new LinkedList<ProbeList>();
			
			for(int i = 0; i < selectedProbeLists.length; i++) {
				if(selectedProbeLists[i] instanceof ProbeList) {
					pls.add((ProbeList)selectedProbeLists[i]);
				}
			}
			
			genesInColumn = new GeneList(getData().getDataSet());
			
			if(pls.size() == 1) {
				ProbeList one = pls.get(0);
				for(int i = 0; i < one.getNumberOfProbes(); i++) {
					Gene g = allGenes.getGene(one.getProbe(i).getName());
					genesInColumn.addGene(g);
				}
			}
			
			indexSnpMapping = new HashMap<String, Set<SNV>>();
			
			String[] geneNames = new String[genesInColumn.size()];
			String[] geneCombinations = new String[combis];
			
			int count = 0;
			for(int i = 0; i < genesInColumn.size(); i++) {
				String name1 = genesInColumn.getGene(i).getDisplayName();
				geneNames[i] = name1;
				for(int j = i; j < genesInColumn.size(); j++) {
					String name2 = genesInColumn.getGene(j).getDisplayName();
					String s = name1 + ":" + name2;
					geneCombinations[count] = s;
					sortedIndices.put(count, count);
					count++;
				}
			}
			
			snps = SNVLists.createUniqueSNVList(ds.getProjectHandler().getSelectedSNVLists());
			
			matrixComp = new MatrixComponent(this, geneNames, geneCombinations);
			
			this.add(matrixComp, BorderLayout.CENTER);
		}
	}
	
	protected MatrixComponent getMatrixComponent() {
		return this.matrixComp;
	}
	
	private void calculateInitialSorting() {
		DoubleVector mostCounts = new DoubleVector(cellData.length);
		for(int i = 0; i < cellData.length; i++) {
			double counts = 0;
			for(int j = 0; j < cellData[0].length; j++) {
				Double value = cellData[i][j] == null ? 0 : cellData[i][j].getSizeValue();
				if(Double.compare(value, 0) > 0) {
					counts += value;
				}
			}
			mostCounts.set(i, counts);
			counts = 0;
		}
		sort(mostCounts);
	}

	protected CellObject[][] getDataMatrix() {
		return cellData;
	}
	
//	private void calculateMatrixTwo() {
//		LDBlocks ldBlocks = null;
//		if(setting.useLDBlocks()) {
//			MetaInformation metaInfo = getData().getProjectHandler().getSelectedMetaInformation();
//			
//			if(metaInfo == null) {
//				System.out.println("No meta information selected");
//				return;
//			}
//			
//			if(metaInfo instanceof LDBlocks) {
//				ldBlocks = (LDBlocks)metaInfo;
//			} else {
//				System.out.println("Please select correct meta information object");
//				return;
//			}
//		}
//		
//		TLResults tlrs = (TLResults) data.getMetaInformationManager().get(TLResults.MYTYPE).get(0);
//		GeneList genes = data.getGenes();
//		
//		Set<SNV> distinctSNPs = new HashSet<SNV>();
//		
//		Set<Double> distinctBeta = new HashSet<Double>();
//		double betaMin = Double.MAX_VALUE;
//		double betaMax = Double.MIN_VALUE;
//		
//		double pThreshold = 0.05;
//		
//		if(setting != null) {
//			pThreshold = setting.getPValue();
//		}
//		
//		Set<Integer> usedBlocks = new HashSet<Integer>();
//		
//		for(int i = 0; i < genesInColumn.size(); i++) {
//			Gene gene = genesInColumn.getGene(i);
//			TwoLocusResult tlr = tlrs.get(gene);
//
//			if(tlr != null) {
//				Set<GenePair> genePairs = tlr.keySet();
//				for(GenePair gp : genePairs) {
//					
//					//get snp pairs
//					List<SNVPair> snpPairs = tlr.get(gp);
//					List<TwoLocusResult.Statistics> stats = tlr.statMapping.get(gp);
//					
//					double currentIntensity = 0;
//					double pSum = 0;
//					double betaSum = 0;
//					
//					for(int j = 0; j < snpPairs.size(); j++) {
//						SNVPair sp = snpPairs.get(j);
//						
//						if(!snps.contains(sp.snp1) && !snps.contains(sp.snp2)) {
//							//skip if not at least one snp from the snp pair
//							//is contained in available snps list
//							continue; 
//						}
//						
//						if(setting.useLDBlocks()) {
//							if(ldBlocks.inLD(sp.snp1, sp.snp2)) {
//								int bid = ldBlocks.getBlockID(sp.snp1);
//								if(usedBlocks.contains(bid))
//									continue;
//								else
//									usedBlocks.add(bid);
//							}
//						}
//						
//						TwoLocusResult.Statistics sts = stats.get(j);
//						
//						Double beta = sts.beta;
//						Double p = sts.p;
//						
//						if(beta != null) {
//							betaSum += beta;
//						}
//						
//						if(p != null && p > 0) {
//							if(p <= pThreshold) {
//								pSum += -Math.log10(p);
//							}
//						}
//						
//						currentIntensity += 1;
//						
//						distinctSNPs.add(sp.snp1);
//						distinctSNPs.add(sp.snp2);
//					}
//					
//					double cellIntensity = currentIntensity;
//					
//					if(cellIntensity > 0) {
//						Gene gene1 = gp.gene1;
//						Gene gene2 = gp.gene2;
//						
//						int index1 = genes.indexOf(gene1);
//						int index2 = genes.indexOf(gene2);
//						
//						if(index2 < index1) {
//							Gene tmp = gene1;
//							gene1 = gene2;
//							gene2 = tmp;
//						}
//						
//						String geneCombi = gene1.getDisplayName() + ":" + gene2.getDisplayName();
//						Integer combiIndex = geneCombiIDs.get(geneCombi);
//						
//						if(combiIndex == null) {
//							geneCombi = gene2.getDisplayName() + ":" + gene1.getDisplayName();
//							combiIndex = geneCombiIDs.get(geneCombi);
//							
//							if(combiIndex == null)
//								System.err.println("Something is wrong! Gene combination cannot be found!");
//						}
//						
//						snpPairMatrix.setValue(combiIndex, i, cellIntensity);
//						
//						Double newBeta = betaSum / cellIntensity;
//						Double newP = pSum / cellIntensity;
//							
//						if(!Double.isInfinite(newBeta) && !Double.isNaN(newBeta)) {
//							betaMatrix.setValue(combiIndex, i, newBeta);
//							
//							if(Double.compare(betaMin, newBeta) > 0) {
//								betaMin = newBeta;
//							}
//							
//							if(Double.compare(betaMax, newBeta) < 0) {
//								betaMax = newBeta;
//							}
//							
//							distinctBeta.add(newBeta);
//						} else {
//							betaMatrix.setValue(combiIndex, i, 0);
//						}
//						
//						if(!Double.isInfinite(newP) && !Double.isNaN(newP)) {
//							pMatrix.setValue(combiIndex, i, newP);
//						} else {
//							pMatrix.setValue(combiIndex, i, 0);
//						}
//					}
//				}
//			}
//		}
//		
//		int numBeta = distinctBeta.size();
//		matrixComp.setGradient(betaMin, betaMax, numBeta);
//	}
	
	private CellObject[][] calculateMatrixTwo() {
		//TODO normalize by ld blocks
		
		TLResults tlrs = (TLResults) data.getMetaInformationManager().get(TLResults.MYTYPE).get(0);
		GeneList genes = data.getGenes();
		
		int numGenes = genes.size();
		HashMap<String, Integer> geneCombiIDs = new HashMap<String, Integer>();
		
		int pairIndex = 0;
		for(int i = 0; i < numGenes - 1; i++) {
			Gene g1 = genes.getGene(i);
			for(int j = i; j < numGenes; j++) {
				Gene g2 = genes.getGene(j);
				String gp = g1.getName() + ":" + g2.getName();
				geneCombiIDs.put(gp, pairIndex++);
			}
		}
		
		int numGenePairs = geneCombiIDs.size();
		
		CellObject[][] data = new CellObject[numGenePairs][numGenes];
		Set<Double> distinctBeta = new HashSet<Double>();
		
		double betaMin = Double.MAX_VALUE;
		double betaMax = Double.MIN_VALUE;
		
		double pMax = Double.MIN_VALUE;
		double snvCountMax = Double.MIN_VALUE;
		
		double pThreshold = setting.getPValueThreshold();
		
		for(int i = 0; i < numGenes; i++) {
			Gene gene = genes.getGene(i);
			TwoLocusResult tlr = tlrs.get(gene);

			if(tlr != null) {
				Set<GenePair> genePairs = tlr.keySet();
				for(GenePair gp : genePairs) {
					
					//get snp pairs
					List<SNVPair> snpPairs = tlr.get(gp);
					List<TwoLocusResult.Statistics> stats = tlr.statMapping.get(gp);
					
					double numSNVPairs = 0;
					double pSum = 0;
					double betaSum = 0;
					
					for(int j = 0; j < snpPairs.size(); j++) {
						SNVPair sp = snpPairs.get(j);
						
						if(!snps.contains(sp.snp1) && !snps.contains(sp.snp2)) {
							//skip if not at least one snv from the snv pair
							//is contained in available snvs list
							continue; 
						}
						
						//TODO LD block normalization
						
						TwoLocusResult.Statistics sts = stats.get(j);
						
						Double beta = sts.beta;
						Double p = sts.p;
						
						if(beta == null || p == null)
							//skip if beta or p are null -> no sufficient stat results for this snv pair
							continue;
						
						if(Double.isInfinite(beta) || Double.isNaN(beta))
							//skip if beta is not valid
							continue;
						
						betaSum += beta;
						
						if(Double.isInfinite(p) || Double.isNaN(p))
							//skip if p is not valid
							continue;
						
						if(p > 0 && p <= pThreshold) {
							pSum += -Math.log10(p);
						} else {
							//skip if p-value does not fulfill quality criterion
							continue;
						}
						
						numSNVPairs += 1;
					}
					
					if(numSNVPairs > 0) {
						Gene gene1 = gp.gene1;
						Gene gene2 = gp.gene2;
						
						int index1 = genes.indexOf(gene1);
						int index2 = genes.indexOf(gene2);
						
						if(index2 < index1) {
							Gene tmp = gene1;
							gene1 = gene2;
							gene2 = tmp;
						}
						
						String geneCombi = gene1.getName() + ":" + gene2.getName();
						Integer combiIndex = geneCombiIDs.get(geneCombi);
						
						Double newBeta = betaSum / numSNVPairs;
						Double newP = pSum / numSNVPairs;
						
						int method = setting.getSNVAggregationMethod();
						
						if(method == AssociationMatrixSetting.NUM_SNV_PAIRS) {
							data[combiIndex][i] = new CellObject(numSNVPairs, newBeta);
						} else if(method == AssociationMatrixSetting.AVERAGE_P_VALUE) {
							data[combiIndex][i] = new CellObject(newP, newBeta);
						}
						
						if(Double.compare(betaMin, newBeta) > 0) {
							betaMin = newBeta;
						}
						
						if(Double.compare(betaMax, newBeta) < 0) {
							betaMax = newBeta;
						}
						
						if(Double.compare(pMax, newP) < 0) {
							pMax = newP;
						}
						
						if(Double.compare(snvCountMax, numSNVPairs) < 0) {
							snvCountMax = numSNVPairs;
						}
						
						distinctBeta.add(newBeta);
					}
				}
			}
		}
		
		int method = setting.getSNVAggregationMethod();
		
		if(method == AssociationMatrixSetting.NUM_SNV_PAIRS) {
			normalizeData(data, snvCountMax);
		} else if(method == AssociationMatrixSetting.AVERAGE_P_VALUE) {
			normalizeData(data, pMax);
		}
		
		setting.setBetaGradient(betaMin, betaMax, distinctBeta.size());
		
		return data;
	}
	
	private void normalizeData(CellObject[][] data, double normalizationValue) {
		for(int i = 0; i < data.length; i++) {
			CellObject[] row = data[i];
			for(int j = 0; j < row.length; j++) {
				CellObject cg = row[j];
				if(cg == null)
					continue;
				cg.setSizeValue(cg.getSizeValue() / normalizationValue);
			}
		}
	}
	
//	private void calculateMatrixSingle() {
//		LDBlocks ldBlocks = null;
//		if(setting.useLDBlocks()) {
//			MetaInformation metaInfo = getData().getProjectHandler().getSelectedMetaInformation();
//			
//			if(metaInfo == null) {
//				System.out.println("No meta information selected");
//				return;
//			}
//			
//			if(metaInfo instanceof LDBlocks) {
//				ldBlocks = (LDBlocks)metaInfo;
//			} else {
//				System.out.println("Please select correct meta information object");
//				return;
//			}
//		}
//		
//		SLResults slrs = (SLResults) getData().getMetaInformationManager().get(SLResults.MYTYPE).get(0);
//		
//		double pThreshold = 0.05;
//		
//		if(setting != null) {
//			pThreshold = setting.getPValueThreshold();
//		}
//		
//		snpPairMatrix.clear();
//		betaMatrix.clear();
//		
//		Set<String> usedBlocks = new HashSet<String>();
//		
//		for(Probe probe : genesInColumn) {
//			Gene g1 = (Gene)probe;
//			SingleLocusResult slr = slrs.get(g1);
//			
//			if(slr == null)
//				continue;
//			
//			int geneIndex1 = geneIDs.get(g1);
//			for(SNV s : snps) {
//				//only look at SNPs that are contained in the respective selected snp list
//				if(!slr.containsKey(s))
//					continue;
//				
//				if(!genesInRow.contains(s.getGene()))
//					continue;
//				
//				Gene g2 = genesInRow.getGene(s.getGene());
//				int geneIndex2 = geneIDs.get(g2);
//				
//				String key = geneIndex2 + "" + geneIndex1;
//				
//				if(!indexSnpMapping.containsKey(key)) {
//					indexSnpMapping.put(key, new HashSet<SNV>());
//				}
//				
//				Statistics stat = slr.get(s);
//				Double beta = stat.beta;
//				Double p = stat.p;
//				
//				if(Double.compare(stat.p, pThreshold) < 0) {
//					if(setting.useLDBlocks()) {
//						int bid = ldBlocks.getBlockID(s);
//						if(bid != -1) {
//							if(usedBlocks.contains(bid)) {
//								double v = snpPairMatrix.getValue(geneIndex2, geneIndex1);
//								continue;
//							} else {
//								String s1 = bid + ":" + geneIndex2;
//								String s2 = bid + ":" + geneIndex2;
//								System.out.println(s1.hashCode());
//								System.out.println(s2.hashCode());
//								usedBlocks.add(bid + ":" + geneIndex2);
//							}
//						}
//					}
//					
//					double newValue = snpPairMatrix.getValue(geneIndex2, geneIndex1) + 1;
//					snpPairMatrix.setValue(geneIndex2, geneIndex1, newValue);
//					
//					if(p != null && Double.compare(p, 0) > 0) {
//						double newP = pMatrix.getValue(geneIndex2, geneIndex1) - Math.log10(p);
//						pMatrix.setValue(geneIndex2, geneIndex1, newP);
//					}
//					
//					indexSnpMapping.get(key).add(s);
//					
//					if(beta != null) {
//						double oldValue = betaMatrix.getValue(geneIndex2, geneIndex1);
//						betaMatrix.setValue(geneIndex2,  geneIndex1, oldValue + beta);
//					}
//				}
//			}
//		}
//		
//		Set<Double> distinctBeta = new HashSet<Double>();
//		double betaMin = Double.MAX_VALUE;
//		double betaMax = Double.MIN_VALUE;
//		
//		for(int i = 0; i < snpPairMatrix.nrow(); i++) {
//			for(int j = 0; j < snpPairMatrix.ncol(); j++) {
//				Double oldBeta = betaMatrix.getValue(i, j);
//				Double oldP = pMatrix.getValue(i, j);
//				Double count = snpPairMatrix.getValue(i, j);
//				if(Double.compare(count, 0) > 0) {
//					Double newBeta = oldBeta / count;
//					
//					if(!Double.isInfinite(newBeta) && !Double.isNaN(newBeta)) {
//						betaMatrix.setValue(i, j, newBeta);
//						
//						if(Double.compare(betaMin, newBeta) > 0) {
//							betaMin = newBeta;
//						}
//						
//						if(Double.compare(betaMax, newBeta) < 0) {
//							betaMax = newBeta;
//						}
//						
//						distinctBeta.add(newBeta);
//					} else {
//						betaMatrix.setValue(i, j, 0);
//					}
//					
//					Double newP = oldP / count;
//					
//					if(!Double.isInfinite(newP) && !Double.isNaN(newP)) {
//						pMatrix.setValue(i, j, newP);
//					} else {
//						pMatrix.setValue(i, j, 0);
//					}
//				}
//			}
//		}
//		
//		int numBeta = distinctBeta.size();
//		matrixComp.setGradient(betaMin, betaMax, numBeta);
//	}
	
	private CellObject[][] calculateMatrixSingle() {
		//TODO LD block normalization
		
		SLResults slrs = (SLResults) data.getMetaInformationManager().get(SLResults.MYTYPE).get(0);
		
		double pThreshold = setting.getPValueThreshold();
		GeneList genes = data.getGenes();
		int numGenes = genes.size();
		
		CellObject[][] data = new CellObject[numGenes][numGenes];
		
		double maxP = Double.MIN_VALUE;
		double maxSNVCount = Double.MIN_VALUE;
		
		double betaMin = Double.MAX_VALUE;
		double betaMax = Double.MIN_VALUE;
		HashSet<Double> distinctBeta = new HashSet<Double>();
		
		for(int i = 0; i < numGenes; i++) {
			Gene gene = genes.getGene(i);
			SingleLocusResult slr = slrs.get(gene);
			
			if(slr == null)
				//no sl results for that gene, skip completely
				continue;
			
			for(SNV s : snps) {
				if(!slr.containsKey(s))
					//only look at SNVs that are contained in the respective selected snvlist
					continue;
				
				Gene gene2 = genes.getGene(s.getGene());
				int rowIndex = genes.indexOf(gene2);
				
				Statistics stat = slr.get(s);
				Double beta = stat.beta;
				Double p = stat.p;
				
				if(p == null 
						|| Double.isInfinite(p) 
						|| Double.isNaN(p) 
						|| !(Double.compare(p, pThreshold) < 0)) {
					//skip if p is not valid
					continue;
				}
				
				if(beta == null || Double.isInfinite(beta) || Double.isNaN(beta)) {
					//skip if beta is not valid
					continue;
				}
				
				//TODO LD block normalization
				
				if(data[rowIndex][i] == null) {
					data[rowIndex][i] = new CellObject(0, 0);
				}
				
				CellObject cg = data[rowIndex][i];
				
				int method = setting.getSNVAggregationMethod();
				if(method == AssociationMatrixSetting.NUM_SNV_PAIRS) {
					cg.setSizeValue(cg.getSizeValue() + 1);
				} else if(method == AssociationMatrixSetting.AVERAGE_P_VALUE) {
					cg.setSizeValue(cg.getSizeValue() - Math.log10(p));
				}
				
				if(Double.compare(maxP, p) < 0) {
					maxP = p;
				}
				
				if(Double.compare(maxSNVCount, cg.getSizeValue()) < 0) {
					maxSNVCount = cg.getSizeValue();
				}
				
				if(Double.compare(betaMax, beta) < 0) {
					betaMax = beta;
				}
				
				if(Double.compare(betaMin, beta) > 0) {
					betaMin = beta;
				}
				
				distinctBeta.add(beta);
			}
		}
		
		int method = setting.getSNVAggregationMethod();
		if(method == AssociationMatrixSetting.NUM_SNV_PAIRS) {
			normalizeData(data, maxSNVCount);
		} else if(method == AssociationMatrixSetting.AVERAGE_P_VALUE) {
			normalizeData(data, maxP);
		}
		
		int numBeta = distinctBeta.size();
		setting.setBetaGradient(betaMin, betaMax, numBeta);
		
		return data;
	}
	
	public void sort(AbstractVector template) {
		Integer[] indices = MultiArraySorter.sort(template);
		//sort the indices
		sortedIndices = MultiArraySorter.sort(indices, sortedIndices);
		updatePlot();
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			break;
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			break;
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
			this.updatePlot();
			break;
		}
	}
	
	public void paintComponent(Graphics g) {
		Graphics2D g2d = (Graphics2D)g;
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, getWidth(), getHeight());
		g2d.setColor(Color.BLACK);
		super.paintComponent(g2d);
	}

	@Override
	public void updatePlot() {
		if(useTlr) {
			cellData = calculateMatrixTwo();
		} else {
			cellData = calculateMatrixSingle();
		}
		
		this.matrixComp.getAggregationComp().calculateAggregationData();
		
		if(this.colorGradientPanel != null) {
			if(setting.getShowGradients()) {
				this.colorGradientPanel.setVisible(true);
			} else {
				this.colorGradientPanel.setVisible(false);
			}
		}
		
		repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new AssociationMatrixSetting(this);
		matrixComp.setViewModel();
		
		if(useTlr) {
			cellData = calculateMatrixTwo();
		} else {
			cellData = calculateMatrixSingle();
		}
		
		calculateInitialSorting();
		
		colorGradientPanel = new ColorGradientPanel(this);
		colorGradientPanel.setVisible(false);
		this.add(colorGradientPanel, BorderLayout.EAST);
		
		return setting;
	}

	public Set<SNV> getSNPsInCell(int xIndex, int yIndex) {
		String key = xIndex + "" + yIndex;
		return indexSnpMapping.get(key);
	}

	public GeneList getGenes() {
		return this.genesInColumn;
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.SINGLE_LOCUS_RESULT);
		prerequisites.add(Prerequisite.GENE_EXPRESSION);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}
}
