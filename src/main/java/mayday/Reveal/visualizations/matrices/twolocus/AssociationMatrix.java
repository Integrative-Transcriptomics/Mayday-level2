package mayday.Reveal.visualizations.matrices.twolocus;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.SNPPair;
import mayday.Reveal.data.ld.old.LDStructure;
import mayday.Reveal.data.meta.TLResults;
import mayday.Reveal.data.meta.TwoLocusResult;
import mayday.Reveal.data.meta.TwoLocusResult.Statistics;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class AssociationMatrix extends RevealVisualization {
	
	protected AssociationMatrixSetting setting;
	private AssociationMatrixComponent plotComponent;
	
	protected double maxCellIntensity = 0;
	
	//map intensities to cell IDs
	protected Map<Integer, Double> cellIntensities = new HashMap<Integer, Double>();
	//count how often each intensity is represented in the graph
	protected ArrayList<Double> distinctIntensitiesArray = new ArrayList<Double>();
	//map gene names to cell IDs
	protected Map<Integer, String> cellsToGene = new HashMap<Integer, String>();
	//map cells to gene-pairs
	protected Map<Integer, GenePair> cellsToGenePairs = new HashMap<Integer, GenePair>();
	
	protected Map<String, List<Integer>> genesToCells = new HashMap<String, List<Integer>>();
	
	protected Map<Integer, Set<SNP>> cellsToSNPs = new HashMap<Integer, Set<SNP>>();
	
	protected SNPList selectedSNPS;
	
	protected GeneList genesToUse;
	
	private SNPList snps;
	
	/**
	 * create a new association matrix
	 * @param projectHandler 
	 */
	public AssociationMatrix(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		
		this.plotComponent = new AssociationMatrixComponent(this);
		this.selectedSNPS = new SNPList("Selected SNPs", getData());
		
		this.setLayout(new BorderLayout());
		this.add(plotComponent, BorderLayout.CENTER);
		
		this.snps = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
		
		genesToUse = findUnnecessaryGenes();
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			updatePlot();
			break;
		case ViewModelEvent.DATA_MANIPULATION_CHANGED:
			break;
		}
	}
	
//	private boolean isLDStructure(SNPPair sp, LDResults ldResults) {
//		//check for ld structure
//		if(setting.useLDBlocks() && ldResults != null) {
//			if(!sp.snp1.getGene().equals(sp.snp2.getGene())) {
//				Double value = ldResults.get(sp);
//				
//				if(value != null) {
//					System.out.println(sp.snp1 + " " +sp.snp2 + " = " + value);
//				}
//				
//				if(value != null) {
//					if(value > setting.getLDR2Threshold()) {
//						return true;
//					}
//				}
//			}
//		}
//		
//		return false;
//	}
	
	protected void calculateCellIntensities() {
		LDStructure ldStructure = null;
		if(setting.useLDBlocks()) {
			ldStructure = getData().getLDStructure(0);
			if(ldStructure == null) {
				System.out.println("No LD Structure Data found!");
			}
		}
		
		TLResults tlrs = (TLResults) data.getMetaInformationManager().get(TLResults.MYTYPE).get(0);
		GeneList genes = data.getGenes();
		int cell = 0;
		Set<Double> distinctIntensities = new TreeSet<Double>();
		cellsToGene.clear();
		cellsToGenePairs.clear();
		cellsToSNPs.clear();
		maxCellIntensity = 0;
		genesToCells.clear();
		Set<SNP> distinctSNPs = new HashSet<SNP>();
		
		for(int i = 0; i < genes.size(); i++) {
			Gene gene = genes.getGene(i);
			TwoLocusResult tlr = tlrs.get(gene);
			List<Integer> cellIDs = new LinkedList<Integer>();
			
			if(tlr != null) {
				Set<GenePair> genePairs = tlr.keySet();
				for(GenePair gp : genePairs) {
					
					//get snp pairs
					List<SNPPair> snpPairs = tlr.get(gp);
					List<Statistics> stats = tlr.statMapping.get(gp);
					
					double currentIntensity = 0;
					int snpCount = 0;
					
					for(int j = 0; j < snpPairs.size(); j++) {
						SNPPair sp = snpPairs.get(j);
						
						if(!snps.contains(sp.snp1) && !snps.contains(sp.snp2)) {
							//skip if not at least one snp from the snp pair
							//is contained in available snps list
							continue; 
						}
						
						Statistics sts = stats.get(j);
						snpCount++;
						
						//correct for ld structure
						if(setting.useLDBlocks()) {
							if(ldStructure.hasLDEdge(sp))
								continue;
						}
						
						switch(setting.getDataValues()) {
						case AssociationMatrixSetting.NUMMBER_OF_SNPS:
							currentIntensity += 1;
							break;
						case AssociationMatrixSetting.P_VALUE:
							currentIntensity += sts.p > 0 ? -Math.log10(sts.p) : 0;
							break;
						}
						
						distinctSNPs.add(sp.snp1);
						distinctSNPs.add(sp.snp2);
					}
					
					double cellIntensity = currentIntensity;
					
					switch(setting.getDataValues()) {
					case AssociationMatrixSetting.NUMMBER_OF_SNPS:
						//nothing to do
						break;
					case AssociationMatrixSetting.P_VALUE:
						//take mean p-value
						if(cellIntensity > 0)
							cellIntensity /= snpCount;
						break;
					}
					
					//determine the maximum edge weight to scale the edge widths
					if(cellIntensity > maxCellIntensity)
						maxCellIntensity = cellIntensity;
					
					if(cellIntensity > 0) {
						distinctIntensities.add(cellIntensity);
						cellIntensities.put(cell, cellIntensity);
						//store edges in the corresponding mapping structures
						cellsToGenePairs.put(cell, gp);
						cellsToGene.put(cell, gene.getName());
						cellsToSNPs.put(cell, distinctSNPs);
						distinctSNPs = new HashSet<SNP>();
						cellIDs.add(cell);
						//increase edge identifier
						cell++;
					}
				}
			}
			genesToCells.put(gene.getName(), cellIDs);
		}
		
		distinctIntensitiesArray.clear();
		for(Double intensity : distinctIntensities) {
			distinctIntensitiesArray.add(intensity);
		}
		
		if(setting.useLDBlocks()) {
			ldStructure.resetEdges();
		}
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new AssociationMatrixSetting(this);
		this.calculateCellIntensities();
		setting.setColorGradient();
		
		plotComponent.initialize();
		
		return setting;
	}
	
	/**
	 * @param source
	 */
	public void updatePlot(int source) {
		switch(source) {
		case AssociationMatrixSetting.RESIZE_PLOT:
			plotComponent.resize();
			updatePlot();
			break;
		case AssociationMatrixSetting.REARRANGE_PLOT_COMPONENTS:
			plotComponent.arrangeComponents();
			updatePlot();
			break;
		case AssociationMatrixSetting.LD_BLOCKS:
			updateTask(true);
			break;
		case AssociationMatrixSetting.DATA_VALUES:
			updateTask(true);
			break;
		default:
			updatePlot();
		}
	}
	
	private void updateTask(final boolean updateColorGradient) {
		AbstractTask updateTask = new AbstractTask("Update Gene Association Matrix Cell Intensities") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				calculateCellIntensities();
				if(updateColorGradient)
					setting.setColorGradient();
				updatePlot();
			}
		};
		
		updateTask.start();
	}
	
	private GeneList findUnnecessaryGenes() {
		GeneList genes = getData().getGenes();
		HashSet<Probe> unnecessary = new HashSet<Probe>();
		unnecessary.addAll(genes.toCollection());
		
		TLResults tlrs = (TLResults) getData().getMetaInformationManager().get(TLResults.MYTYPE).get(0);
		
		for(Probe p : genes) {
			Gene g = (Gene)p;
			TwoLocusResult tlr = tlrs.get(g);
			if(tlr == null) {
				continue;
			}
			Set<GenePair> gps = tlr.keySet();
			//if there is a gp in tlr then there is an edge between these two genes
			//neither of them is unnecessary
			for(GenePair gp : gps) {
				unnecessary.remove(gp.gene1);
				unnecessary.remove(gp.gene2);
			}
		}
		
		GeneList usableGenes = new GeneList(getData().getDataSet());
		for(Probe p : genes) {
			Gene g = (Gene)p;
			if(!unnecessary.contains(g)) {
				usableGenes.addGene(g);
			}
		}
		
		return usableGenes;
	}

	@Override
	public void updatePlot() {
		repaint();
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.TWO_LOCUS_RESULT);
		prerequisites.add(Prerequisite.GENE_EXPRESSION);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}
}
