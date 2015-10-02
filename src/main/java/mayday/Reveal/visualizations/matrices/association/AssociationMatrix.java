package mayday.Reveal.visualizations.matrices.association;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
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
import mayday.Reveal.data.ld.LDBlocks;
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
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.linalg.matrix.DoubleMatrix;
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
	
	private DoubleMatrix matrix;
	private DoubleMatrix betaMatrix;
	private HashMap<Gene, Integer> geneIDs;
	private HashMap<String, Integer> geneCombiIDs;
	private SNVList snps;
	private HashMap<String, Set<SNV>> indexSnpMapping;
	
	public BidirectionalHashMap<Integer, Integer> sortedIndices;
	
	private MatrixComponent matrixComp;
	
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
			
			int numGenesRow = genesInRow.size();
			int numGenesColumn = genesInColumn.size();
			
			matrix = new DoubleMatrix(numGenesRow, numGenesColumn);
			betaMatrix = new DoubleMatrix(numGenesRow, numGenesColumn);
			
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
			
			matrix = new DoubleMatrix(combis, numGenes);
			betaMatrix = new DoubleMatrix(combis, numGenes);
			
			indexSnpMapping = new HashMap<String, Set<SNV>>();
			
			String[] geneNames = new String[genesInColumn.size()];
			String[] geneCombinations = new String[combis];
			
			int count = 0;
			geneCombiIDs = new HashMap<String, Integer>();
			for(int i = 0; i < genesInColumn.size(); i++) {
				String name1 = genesInColumn.getGene(i).getDisplayName();
				geneNames[i] = name1;
				for(int j = i; j < genesInColumn.size(); j++) {
					String name2 = genesInColumn.getGene(j).getDisplayName();
					String s = name1 + ":" + name2;
					geneCombinations[count] = s;
					geneCombiIDs.put(s, count);
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
		DoubleVector mostCounts = new DoubleVector(matrix.nrow());
		for(int i = 0; i < matrix.nrow(); i++) {
			double counts = 0;
			for(int j = 0; j < matrix.ncol(); j++) {
				Double value = matrix.getValue(i, j);
				if(Double.compare(value, 0) > 0) {
					counts += value;
				}
			}
			mostCounts.set(i, counts);
			counts = 0;
		}
		sort(mostCounts);
	}

	protected DoubleMatrix getDataMatrix() {
		return this.matrix;
	}
	
	protected DoubleMatrix getBetaMatrix() {
		return this.betaMatrix;
	}
	
	private void calculateMatrixTwo() {
		LDBlocks ldBlocks = null;
		if(setting.useLDBlocks()) {
			MetaInformation metaInfo = getData().getProjectHandler().getSelectedMetaInformation();
			
			if(metaInfo == null) {
				System.out.println("No meta information selected");
				return;
			}
			
			if(metaInfo instanceof LDBlocks) {
				ldBlocks = (LDBlocks)metaInfo;
			} else {
				System.out.println("Please select correct meta information object");
				return;
			}
		}
		
		TLResults tlrs = (TLResults) data.getMetaInformationManager().get(TLResults.MYTYPE).get(0);
		GeneList genes = data.getGenes();
//		Set<Double> distinctIntensities = new TreeSet<Double>();
//		cellsToGene.clear();
//		cellsToGenePairs.clear();
//		cellsToSNPs.clear();
//		maxCellIntensity = 0;
//		genesToCells.clear();
		Set<SNV> distinctSNPs = new HashSet<SNV>();
		
		Set<Double> distinctBeta = new HashSet<Double>();
		double betaMin = Double.MAX_VALUE;
		double betaMax = Double.MIN_VALUE;
		
		Set<Integer> usedBlocks = new HashSet<Integer>();
		
		for(int i = 0; i < genesInColumn.size(); i++) {
			Gene gene = genesInColumn.getGene(i);
			TwoLocusResult tlr = tlrs.get(gene);
//			List<Integer> cellIDs = new LinkedList<Integer>();
//			int cell = 0;
			if(tlr != null) {
				Set<GenePair> genePairs = tlr.keySet();
				for(GenePair gp : genePairs) {
					
					//get snp pairs
					List<SNVPair> snpPairs = tlr.get(gp);
					List<TwoLocusResult.Statistics> stats = tlr.statMapping.get(gp);
					
					double currentIntensity = 0;
					double betaSum = 0;
//					int snpCount = 0;
					
					for(int j = 0; j < snpPairs.size(); j++) {
						SNVPair sp = snpPairs.get(j);
						
						if(!snps.contains(sp.snp1) && !snps.contains(sp.snp2)) {
							//skip if not at least one snp from the snp pair
							//is contained in available snps list
							continue; 
						}
						
						if(setting.useLDBlocks()) {
							if(ldBlocks.inLD(sp.snp1, sp.snp2)) {
								int bid = ldBlocks.getBlockID(sp.snp1);
								if(usedBlocks.contains(bid))
									continue;
								else
									usedBlocks.add(bid);
							}
						}
						
						TwoLocusResult.Statistics sts = stats.get(j);
//						snpCount++;
						
//						//correct for ld structure
//						if(setting.useLDBlocks()) {
//							if(ldStructure.hasLDEdge(sp))
//								continue;
//						}
						
//						switch(setting.getDataValues()) {
//						case AssociationMatrixSetting.NUMMBER_OF_SNPS:
//							currentIntensity += 1;
//							break;
//						case AssociationMatrixSetting.P_VALUE:
//							currentIntensity += sts.p > 0 ? -Math.log10(sts.p) : 0;
//							break;
//						}
						
						Double beta = sts.beta;
						
						if(beta != null) {
							betaSum += beta;
						}
						
						currentIntensity += 1;
						
						distinctSNPs.add(sp.snp1);
						distinctSNPs.add(sp.snp2);
					}
					
					double cellIntensity = currentIntensity;
					
//					switch(setting.getDataValues()) {
//					case AssociationMatrixSetting.NUMMBER_OF_SNPS:
//						//nothing to do
//						break;
//					case AssociationMatrixSetting.P_VALUE:
//						//take mean p-value
//						if(cellIntensity > 0)
//							cellIntensity /= snpCount;
//						break;
//					}
					
					//determine the maximum edge weight to scale the edge widths
//					if(cellIntensity > maxCellIntensity)
//						maxCellIntensity = cellIntensity;
					
					if(cellIntensity > 0) {
//						distinctIntensities.add(cellIntensity);
						
						Gene gene1 = gp.gene1;
						Gene gene2 = gp.gene2;
						
						int index1 = genes.indexOf(gene1);
						int index2 = genes.indexOf(gene2);
						
						if(index2 < index1) {
							Gene tmp = gene1;
							gene1 = gene2;
							gene2 = tmp;
						}
						
						String geneCombi = gene1.getDisplayName() + ":" + gene2.getDisplayName();
						Integer combiIndex = geneCombiIDs.get(geneCombi);
						if(combiIndex == null) {
							
							geneCombi = gene2.getDisplayName() + ":" + gene1.getDisplayName();
							combiIndex = geneCombiIDs.get(geneCombi);
							
							if(combiIndex == null)
								System.out.println("Something is wrong!");
						}
						matrix.setValue(combiIndex, i, cellIntensity);
						
						Double newBeta = betaSum / cellIntensity;
							
						if(!Double.isInfinite(newBeta) && !Double.isNaN(newBeta)) {
							betaMatrix.setValue(combiIndex, i, newBeta);
							
							if(Double.compare(betaMin, newBeta) > 0) {
								betaMin = newBeta;
							}
							
							if(Double.compare(betaMax, newBeta) < 0) {
								betaMax = newBeta;
							}
							
							distinctBeta.add(newBeta);
						} else {
							betaMatrix.setValue(combiIndex, i, 0);
						}
						
//						cellIntensities.put(cell, cellIntensity);
						//store edges in the corresponding mapping structures
//						cellsToGenePairs.put(cell, gp);
//						cellsToGene.put(cell, gene.getName());
//						cellsToSNPs.put(cell, distinctSNPs);
//						distinctSNPs = new HashSet<SNP>();
//						cellIDs.add(cell);
						//increase edge identifier
//						cell++;
					}
				}
			}
//			genesToCells.put(gene.getName(), cellIDs);
		}
//		
//		distinctIntensitiesArray.clear();
//		for(Double intensity : distinctIntensities) {
//			distinctIntensitiesArray.add(intensity);
//		}
//		
//		if(setting.useLDBlocks()) {
//			ldStructure.resetEdges();
//		}
		
		int numBeta = distinctBeta.size();
		matrixComp.setGradient(betaMin, betaMax, numBeta);
	}
	
	private void calculateMatrixSingle() {
		LDBlocks ldBlocks = null;
		if(setting.useLDBlocks()) {
			MetaInformation metaInfo = getData().getProjectHandler().getSelectedMetaInformation();
			
			if(metaInfo == null) {
				System.out.println("No meta information selected");
				return;
			}
			
			if(metaInfo instanceof LDBlocks) {
				ldBlocks = (LDBlocks)metaInfo;
			} else {
				System.out.println("Please select correct meta information object");
				return;
			}
		}
		
		SLResults slrs = (SLResults) getData().getMetaInformationManager().get(SLResults.MYTYPE).get(0);
		
		double pThreshold = 0.05;
		
		if(setting != null) {
			pThreshold = setting.getPValue();
		}
		
		matrix.clear();
		betaMatrix.clear();
		
		Set<String> usedBlocks = new HashSet<String>();
		
		for(Probe p : genesInColumn) {
			Gene g1 = (Gene)p;
			SingleLocusResult slr = slrs.get(g1);
			
			if(slr == null)
				continue;
			
			int geneIndex1 = geneIDs.get(g1);
			for(SNV s : snps) {
				//only look at SNPs that are contained in the respective selected snp list
				if(!slr.containsKey(s))
					continue;
				
				if(!genesInRow.contains(s.getGene()))
					continue;
				
				Gene g2 = genesInRow.getGene(s.getGene());
				int geneIndex2 = geneIDs.get(g2);
				
				String key = geneIndex2 + "" + geneIndex1;
				if(!indexSnpMapping.containsKey(key)) {
					indexSnpMapping.put(key, new HashSet<SNV>());
				}
				
				Statistics stat = slr.get(s);
				Double beta = stat.beta;
				
				if(Double.compare(stat.p, pThreshold) < 0) {
					if(setting.useLDBlocks()) {
						int bid = ldBlocks.getBlockID(s);
						if(bid != -1) {
							if(usedBlocks.contains(bid)) {
								double v = matrix.getValue(geneIndex2, geneIndex1);
								continue;
							} else {
								String s1 = bid + ":" + geneIndex2;
								String s2 = bid + ":" + geneIndex2;
								System.out.println(s1.hashCode());
								System.out.println(s2.hashCode());
								usedBlocks.add(bid + ":" + geneIndex2);
							}
						}
					}
					
					double newValue = matrix.getValue(geneIndex2, geneIndex1) + 1;
					matrix.setValue(geneIndex2, geneIndex1, newValue);
					indexSnpMapping.get(key).add(s);
					
					if(beta != null) {
						double oldValue = betaMatrix.getValue(geneIndex2, geneIndex1);
						betaMatrix.setValue(geneIndex2,  geneIndex1, oldValue + beta);
					}
				}
			}
		}
		
		Set<Double> distinctBeta = new HashSet<Double>();
		double betaMin = Double.MAX_VALUE;
		double betaMax = Double.MIN_VALUE;
		
		for(int i = 0; i < matrix.nrow(); i++) {
			for(int j = 0; j < matrix.ncol(); j++) {
				Double oldBeta = betaMatrix.getValue(i, j);
				Double count = matrix.getValue(i, j);
				if(Double.compare(count, 0) > 0) {
					Double newBeta = oldBeta / count;
					
					if(!Double.isInfinite(newBeta) && !Double.isNaN(newBeta)) {
						betaMatrix.setValue(i, j, newBeta);
						
						if(Double.compare(betaMin, newBeta) > 0) {
							betaMin = newBeta;
						}
						
						if(Double.compare(betaMax, newBeta) < 0) {
							betaMax = newBeta;
						}
						
						distinctBeta.add(newBeta);
					} else {
						betaMatrix.setValue(i, j, 0);
					}
				}
			}
		}
		
		int numBeta = distinctBeta.size();
		matrixComp.setGradient(betaMin, betaMax, numBeta);
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
			calculateMatrixTwo();
		} else {
			calculateMatrixSingle();
		}
		
		this.matrixComp.getAggregationComp().calculateAggregationData();
		
		repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new AssociationMatrixSetting(this);
		matrixComp.setViewModel();
		
		if(useTlr) {
			calculateMatrixTwo();
		} else {
			calculateMatrixSingle();
		}
		
		calculateInitialSorting();
		
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

	public AssociationMatrixSetting getAssociationMatrixSetting() {
		return this.setting;
	}
}
