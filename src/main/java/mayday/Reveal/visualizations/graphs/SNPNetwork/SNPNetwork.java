package mayday.Reveal.visualizations.graphs.SNPNetwork;

import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.SNPPair;
import mayday.Reveal.data.meta.TLResults;
import mayday.Reveal.data.meta.TwoLocusResult;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.visualizations.graphs.AssociationGraph;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AggregateLayout;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SNPNetwork extends AssociationGraph<String, Integer> {
	
	public static final String TITLE = "SNP Network";
	public static final String DESCRIPTION = "Create a new SNP Network based on selected SNPs";

	protected HashMap<Integer, SNPPair> edgeToSNPPair = new HashMap<Integer, SNPPair>();
	protected HashMap<SNPPair, Integer> edgeWeights = new HashMap<SNPPair, Integer>();
	
	@SuppressWarnings("rawtypes")
	protected Class subLayoutType = CircleLayout.class;
	protected AggregateLayout<String, Integer> clusteringLayout;
	
	
	private VertexShapeFunction vertexShapeFunction;
	private VertexLabelTransformer<String> vertexLabelTransformer;
	
	protected SNPList snps;
	
	protected SNPNetworkSetting setting;
	
	/**
	 * @param gwasfr
	 */
	public SNPNetwork(DataStorage ds) {
		super("SNP Graph");
		setData(ds);
		this.snps = SNPLists.createUniqueSNPList(ds.getProjectHandler().getSelectedSNPLists());
		
		this.start();
		
		visualizationViewer.setGraphLayout(new CircleLayout<String, Integer>(graph));
		this.clusteringLayout  = new AggregateLayout<String, Integer>(new KKLayout<String, Integer>(graph));
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
//			internalChange = false;
			Set<Probe> selected = viewModel.getSelectedProbes();
			ArrayList<String> geneNames = new ArrayList<String>();
			
			for(Probe p : selected) {
				geneNames.add(p.getName());
			}
			
			PickedState<String> pickedState = visualizationViewer.getPickedVertexState();
			for(int i = 0; i < snps.size(); i++) {
				pickedState.pick(snps.get(i).getID(), false);
			}		
			
			for(SNP s: snps) {
				if(geneNames.contains(s.getGene())) {
					pickedState.pick(s.getID(), true);
				}
			}
//			internalChange = true;
			break;
		}
	}

	@Override
	protected Graph<String, Integer> buildGraph() {
		Graph<String, Integer> g = new SparseMultigraph<String, Integer>();
		GeneList allGenes = getData().getGenes();
		Set<Gene> genes = new HashSet<Gene>();
		
		for(SNP s: snps) {
			g.addVertex(s.getID());
			genes.add(allGenes.getGene(s.getGene()));
		}
		
		TLResults tlrs = (TLResults) getData().getMetaInformationManager().get(TLResults.MYTYPE).get(0);
		
		int edgeIdentifier = 0;
		for(Gene gene: tlrs.keySet()) {
			TwoLocusResult tlr = tlrs.get(gene);
			if(tlr != null) {
				System.out.println("Processing gene " + gene.getDisplayName());
				for(GenePair gp : tlr.keySet()) {
					List<SNPPair> snpPairs = tlr.get(gp);
					for(SNPPair sp : snpPairs) {
						if(snps.contains(sp.snp1) && snps.contains(sp.snp2)) {
							if(edgeWeights.containsKey(sp)) {
								edgeWeights.put(sp, edgeWeights.get(sp)+1);
							} else {
								int id = edgeIdentifier++;
								System.out.println(sp.snp1.getID() + " - " + sp.snp2.getID());
								g.addEdge(id, sp.snp1.getID(), sp.snp2.getID());
								edgeToSNPPair.put(id, sp);
								edgeWeights.put(sp, 1);
							}
						}
					}
				}
			}
		}
		
		System.out.println("---- SNPs with edge weight > 1 ----");
		
		for(SNPPair sp : edgeWeights.keySet()) {
			if(edgeWeights.get(sp) > 1) {
				System.out.println(sp.snp1.getID() + " - " + sp.snp2.getID() + " : " + edgeWeights.get(sp));
			}
		}
		
		System.out.println("Number of SNPs: " + snps.size());
		System.out.println("Number of edges: " + (edgeIdentifier-1));
		
		return g;
	}

	@Override
	protected void applyTransformers(VisualizationViewer<String, Integer> vv) {
		
		this.vertexShapeFunction = new VertexShapeFunction();
		this.vertexLabelTransformer = new VertexLabelTransformer<String>();
		
		
		//inform connected plots about internal selection changes
		PickedState<String> ps = visualizationViewer.getPickedVertexState();
		ps.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
//				if(internalChange) {
//					String snpID = (String)e.getItem();
//					SNP snp = gwasfr.getSNPs().get(snpID);
//					String geneName = snp.getGene();
//					Probe pb = gwasfr.getGenes().getGene(geneName);
//					viewModel.toggleProbeSelected(pb);
//				}
			}
		});
		
		visualizationViewer.setVertexToolTipTransformer(new ToStringLabeller<String>());
		visualizationViewer.getRenderContext().setVertexShapeTransformer(vertexShapeFunction);
		visualizationViewer.getRenderContext().setVertexLabelTransformer(vertexLabelTransformer);
	}

	//FIXME add this as Settings
	public void buildMenu() {
		//TODO this does belong into the selection setting
//		JMenuItem clusterPicked = new JMenuItem("Cluster Selected SNPs");
//		JMenuItem unclusterAll = new JMenuItem("UnCluster all SNPs");
//		
//		menu.addSeparator();
//		
//		clusterPicked.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				clusterPicked();
//			}
//		});
//		
//		unclusterAll.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				unclusterAll();
//			}
//		});
//		
//		addJMenuItem(clusterPicked);
//		addJMenuItem(unclusterAll);
//		
//		Component parent = this;
//		while(!(parent instanceof Window)) {
//			parent = parent.getParent();
//		}
//		
//		JMenu settingsItem = (JMenu)this.setting.getMenuItem((Window)parent);
//		for(int i = 0; i < settingsItem.getItemCount(); i++) {
//			addJMenuItem(settingsItem.getItem(i));
//		}
	}
	
	private void clusterPicked() {
		cluster(true);
	}
	
	private void unclusterAll() {
		cluster(false);
	}
	
	@SuppressWarnings("unchecked")
	private void cluster(boolean state) {
		PickedState<String> ps = visualizationViewer.getPickedVertexState();
		Dimension subLayoutSize = new Dimension(100, 100);
		 
    	if(state == true) {
    		// put the picked vertices into a new sublayout 
    		Collection<String> picked = ps.getPicked();
    		if(picked.size() > 1) {
    			Point2D center = new Point2D.Double();
    			double x = 0;
    			double y = 0;
    			for(String vertex : picked) {
    				Point2D p = clusteringLayout.transform(vertex);
    				x += p.getX();
    				y += p.getY();
    			}
    			x /= picked.size();
    			y /= picked.size();
				center.setLocation(x,y);

//    			String firstVertex = picked.iterator().next();
//    			Point2D center = clusteringLayout.transform(firstVertex);
    			Graph<String, Integer> subGraph;
    			try {
    				subGraph = graph.getClass().newInstance();
    				for(String vertex : picked) {
    					subGraph.addVertex(vertex);
    					Collection<Integer> incidentEdges = graph.getIncidentEdges(vertex);
    					for(Integer edge : incidentEdges) {
    						Pair<String> endpoints = graph.getEndpoints(edge);
    						if(picked.containsAll(endpoints)) {
    							// put this edge into the subgraph
    							subGraph.addEdge(edge, endpoints.getFirst(), endpoints.getSecond());
    						}
    					}
    				}

					Layout<String, Integer> subLayout = getLayoutFor(subLayoutType, subGraph);
    				subLayout.setInitializer(visualizationViewer.getGraphLayout());
    				subLayout.setSize(subLayoutSize);
    				clusteringLayout.put(subLayout,center);
    				visualizationViewer.setGraphLayout(clusteringLayout);

    			} catch (Exception e) {
    				e.printStackTrace();
    			}
    		}
    	} else {
    		// remove all sublayouts
    		clusteringLayout.removeAll();
    		visualizationViewer.setGraphLayout(clusteringLayout);
    	}
    }
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Layout<String, Integer> getLayoutFor(Class<CircleLayout<?, ?>> layoutClass, Graph<String, Integer> graph) throws Exception {
    	Object[] args = new Object[]{graph};
    	Constructor<CircleLayout<?, ?>> constructor = layoutClass.getConstructor(new Class[] {Graph.class});
    	return  (Layout)constructor.newInstance(args);
    }

	/**
	 * @param selectedSNPs
	 */
	public void setSNPs(SNPList selectedSNPs) {
		this.snps = selectedSNPs;
		this.graph = buildGraph();
	}
	
	private class VertexShapeFunction extends EllipseVertexShapeTransformer<String> {

		VertexShapeFunction() {
			setSizeTransformer(new VertexSizeFunction(40));
		}

		@Override
		public Shape transform(String v) {
			if(setting != null) {
				if(setting.highlightNodes()) {
					int size = graph.degree(v);
					if (size < 8) {
						int sides = Math.max(size, 3);
						return factory.getRegularPolygon(v, sides);
					} else {
						return factory.getRegularStar(v, size);
					}
				}
			}
			return super.transform(v);
		}
	}
	
	private class VertexSizeFunction implements Transformer<String, Integer> {
		int size;

		VertexSizeFunction(Integer size) {
			this.size = size;
		}

		public Integer transform(String v) {
			return size;
		}
	}
	
	private class VertexLabelTransformer<V> implements Transformer<V,String> {
		@Override
		public String transform(V id) {
			if(setting != null) {
				if(setting.showNodeLabels()) {
					return id.toString();
				}
			}
			return "";
		}
	}
	
	public void updatePlot() {
		visualizationViewer.repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		this.setting = new SNPNetworkSetting(this);
		return setting;
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
