/*
 * TODO:
 * SateliteView?
 * Annotations?
 */

package mayday.Reveal.visualizations.graphs.TwoLocusNetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collection;
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
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.SNVPair;
import mayday.Reveal.data.ld.LDBlocks;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.TLResults;
import mayday.Reveal.data.meta.TwoLocusResult;
import mayday.Reveal.data.meta.TwoLocusResult.Statistics;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.GeneColors;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.graphs.AssociationGraph;
import mayday.Reveal.visualizations.matrices.twolocus.AssociationMatrixSetting;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.structures.linalg.vector.DoubleVector;
import mayday.core.tasks.AbstractTask;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import edu.uci.ics.jung.algorithms.scoring.util.VertexScoreTransformer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.AbstractVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedInfo;
import edu.uci.ics.jung.visualization.picking.PickedState;

/**
 * @author jaeger
 *
 */
@SuppressWarnings({ "serial" })
public class TLN extends AssociationGraph<String, Integer> {
	
	/**
	 * the title of this plot
	 */
	public static final String TITLE = "TLN";
	/**
	 * the description of this plot
	 */
	public static final String DESCRIPTION = "Display gene/SNP associations based on PLINK two locus results";
	
	//map colors to gene names
	protected Map<String, Color> geneColors = new HashMap<String, Color>();
	//map weights to edge IDs
	protected Map<Integer, Double> edgeWeights = new HashMap<Integer, Double>();
	//count how often each weight is represented in the graph
	protected Set<Double> distinctWeights = new TreeSet<Double>();
	//map gene names to edge IDs
	protected Map<Integer, String> edgesToGene = new HashMap<Integer, String>();
	//map edges to genepairs
	protected Map<Integer, GenePair> edgesToGenePairs = new HashMap<Integer, GenePair>();
	
	protected Map<Integer, Set<SNV>> edgesToSNPs = new HashMap<Integer, Set<SNV>>();
	
	protected double maxEdgeWeight = 0;
	protected float maxEdgeWidth = 25.f;
	protected VertexStrokeHighlight<String, Integer> vertexStrokeHighlight;
	protected VertexShapeSizeAspect<String, Integer> vertexShapeSizeAspect;
	
	protected TLNControlPanel edgeControls;
	
	private TLNSetting setting;
	
	private boolean internalChange = true;
	
	private SNVList snps;
	
	/**
	 * @param projectHandler 
	 */
	public TLN(ProjectHandler projectHandler) {
		super("Association Graph");
		setData(projectHandler.getSelectedProject());
		
		GeneList genes = getData().getGenes();

		//initialize color map
		Color[]  colors = GeneColors.colorBrewer(genes.size());
		colors = GeneColors.rainbow(genes.size(), 0.8);
		for(int i = 0; i < colors.length; i++) {
			geneColors.put(genes.getGene(i).getName(), colors[i]);
		}
		
		this.snps = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
	}

	/**
	 * @return list of selected snps
	 */
	public SNVList getSNPsFromSelectedEdges() {
		Collection<Integer> pickedEdges = visualizationViewer.getPickedEdgeState().getPicked();
		
		if(pickedEdges.size() == 0) {
			pickedEdges = graph.getEdges();
		}
		
		HashSet<SNV> uniqueSNPs = new HashSet<SNV>();
		SNVList snpList = new SNVList("Selected SNPs", getData());
		
		Predicate<Context<Graph<String,Integer>,Integer>> p = getVisualizationViewer().getRenderContext().getEdgeIncludePredicate();
		for(Integer e : pickedEdges) {
			boolean showEdge = p.evaluate(Context.getInstance(graph, e));
			if(showEdge) {
				Set<SNV> snps = edgesToSNPs.get(e);
				uniqueSNPs.addAll(snps);
			}
		}
		snpList.addAll(uniqueSNPs);
		return snpList;
	}

	@Override
	protected Graph<String, Integer> buildGraph() {
		Graph<String, Integer> g = new SparseMultigraph<String, Integer>();
		
		GeneList genes = getData().getGenes();
		//add vertices (genes)
		for(int i = 0; i < genes.size(); i++) {
			g.addVertex(genes.getGene(i).getName());
		}
		
		//add edges
		Set<Integer> edges = edgesToGenePairs.keySet();
		for(Integer edge : edges) {
			GenePair gp = edgesToGenePairs.get(edge);
			g.addEdge(edge, gp.gene1.getName(), gp.gene2.getName(), EdgeType.UNDIRECTED);
		}
		
		return g;
	}
	
	protected void rebuildGraph() {
		Collection<Integer> edges = new HashSet<Integer>(this.graph.getEdges());
		for(Integer edge : edges) {
			this.graph.removeEdge(edge);
		}
		
		//add edges
		Set<Integer> newEdges = edgesToGenePairs.keySet();
		for(Integer edge : newEdges) {
			GenePair gp = edgesToGenePairs.get(edge);
			this.graph.addEdge(edge, gp.gene1.getName(), gp.gene2.getName(), EdgeType.UNDIRECTED);
		}
		
		//set the edge stroke proportional to the edge weight
		Transformer<Integer, Stroke> edgeStroke = new Transformer<Integer, Stroke>() {
			@Override
			public Stroke transform(Integer edgeID) {
				//scale: maximal edge width = node.width * 0.8
				return new BasicStroke((edgeWeights.get(edgeID).floatValue()/(float)maxEdgeWeight) * 25.f);
			}
		};
		//set edge weights as edge labels
		Transformer<Integer, String> edgeLabeller = new Transformer<Integer, String>() {
			@Override
			public String transform(Integer edgeID) {
				if(setting.showEdgeLabels())
					return edgeWeights.get(edgeID).toString();
				else
					return "";
			}
		};
		
		RenderContext<String, Integer> renderContext = this.visualizationViewer.getRenderContext();
		renderContext.setEdgeStrokeTransformer(edgeStroke);
		renderContext.setEdgeLabelTransformer(edgeLabeller);
		
		this.visualizationViewer.revalidate();
		this.visualizationViewer.repaint();
	}

	@Override
	protected void applyTransformers(VisualizationViewer<String, Integer> vv) {
		//define colors for the genes
		Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {
			@Override
			public Paint transform(String geneName) {	
				Color c = geneColors.get(geneName);
				Gene g = getData().getGenes().getGene(geneName);
				TLResults tlrs = (TLResults) getData().getMetaInformationManager().get(TLResults.MYTYPE).get(0);
				if(tlrs.get(g) == null) {
					c = Color.GRAY;
				}
				return c;
			}
		};
		//define colors for the edges
		Transformer<Integer, Paint> edgePaint = new Transformer<Integer, Paint>() {
			@Override
			public Paint transform(Integer edgeID) {
				Color c = geneColors.get(edgesToGene.get(edgeID));
		
				if(edgeControls.edgeStrokeHighlightBox.isSelected()) {
					String vertex = edgesToGene.get(edgeID);
					boolean picked = visualizationViewer.getPickedVertexState().isPicked(vertex);

					if(picked) {
						return c;
					} else {
						int alpha = 255 - edgeControls.edgeStrokeTransparencySlider.getValue();
						Color c2 = new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha);
						return c2;
					}
				}
				
				return c; 
			}
		};
		//set the edge stroke proportional to the edge weight
		Transformer<Integer, Stroke> edgeStroke = new Transformer<Integer, Stroke>() {
			@Override
			public Stroke transform(Integer edgeID) {
				//scale: maximal edge width = node.width * 0.8
				return new BasicStroke((edgeWeights.get(edgeID).floatValue()/(float)maxEdgeWeight) * 25.f);
			}
		};
		//set edge weights as edge labels
		Transformer<Integer, String> edgeLabeller = new Transformer<Integer, String>() {
			@Override
			public String transform(Integer edgeID) {
				if(setting.showEdgeLabels())
					return edgeWeights.get(edgeID).toString();
				else
					return "";
			}
		};
		
		ExpressionScorer expressionScorer = new ExpressionScorer();
		Transformer<String, Double> expressions = new VertexScoreTransformer<String, Double>(expressionScorer);
		vertexShapeSizeAspect = new VertexShapeSizeAspect<String, Integer>(graph, expressions);
		
		PickedState<String> pickedState = vv.getPickedVertexState();
		
		pickedState.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(internalChange) {
					String node = (String)e.getItem();
					Probe pb = getData().getGenes().getGene(node);
					viewModel.toggleProbeSelected(pb);
				}
			}
		});
		
        vertexStrokeHighlight = new VertexStrokeHighlight<String, Integer>(graph, pickedState, vv);
        
        Transformer<String, Paint> vertexDrawPaint = new Transformer<String, Paint>() {
			@Override
			public Paint transform(String geneName) {
				if(visualizationViewer.getPickedVertexState().isPicked(geneName)) {
					return Color.RED;
				}
				return Color.BLACK;
			}
        };
        
		//get the render context and apply the transformations
		RenderContext<String, Integer> renderContext = vv.getRenderContext();
		renderContext.setVertexFillPaintTransformer(vertexPaint);
		renderContext.setEdgeDrawPaintTransformer(edgePaint);
		renderContext.setEdgeStrokeTransformer(edgeStroke);
		renderContext.setVertexLabelTransformer(new ToStringLabeller<String>());
		renderContext.setEdgeLabelTransformer(edgeLabeller);
		renderContext.setVertexShapeTransformer(vertexShapeSizeAspect);
		renderContext.setVertexStrokeTransformer(vertexStrokeHighlight);
		renderContext.setVertexDrawPaintTransformer(vertexDrawPaint);
	}
	
	protected void calculateEdgeWeights() {
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
		
		boolean includeSingleLocusResults = false;
		if(edgeControls != null)
			includeSingleLocusResults = edgeControls.includeSingleLocusResultsBox.isSelected();
		
		TLResults tlrs = (TLResults) getData().getMetaInformationManager().get(TLResults.MYTYPE).get(0);
//		SLResults slrs = (SLResults) getData().getMetaInformationManager().get(SLResults.MYTYPE).get(0);
		GeneList genes = getData().getGenes();
		
		int edge = 0;
		distinctWeights.clear();
		edgesToGene.clear();
		edgesToGenePairs.clear();
		edgesToSNPs.clear();
		maxEdgeWeight = 0;
//		int sumWeights = 0;
//		int sumSNPPairs = 0;
		Set<SNV> distinctSNPs = new HashSet<SNV>();
		Set<SNV> allDistinctSNPs = new HashSet<SNV>();
		
		Set<Integer> usedBlocks = new HashSet<Integer>();
		
		for(int i = 0; i < genes.size(); i++) {
			Gene gene = genes.getGene(i);
			TwoLocusResult tlr = tlrs.get(gene);
			if(tlr != null) {
				Set<GenePair> genePairs = tlr.keySet();
				for(GenePair gp : genePairs) {
					double currentIntensity = 0;
					int snpCount = 0;
					
					List<SNVPair> snpPairs = tlr.get(gp);
					List<Statistics> stats = tlr.statMapping.get(gp);
					
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
						
						//single locus filter
//						if(includeSingleLocusResults) {
//							SingleLocusResult slr1 = slrs.get(gp.gene1);
//							SingleLocusResult slr2 = slrs.get(gp.gene2);
//							if(!((slr1.get(sp.snp1).p < setting.getSingleLocusPValueThreshold() 
//									&& slr1.get(sp.snp1).r2 > setting.getR2Threshold())
//								||(slr2.get(sp.snp2).p < setting.getSingleLocusPValueThreshold() 
//									&& slr2.get(sp.snp2).r2 > setting.getR2Threshold()))) {
//								continue;
//							}
//						}
							
						Statistics sts = stats.get(j);
							
						switch(setting.getDataValues()) {
						case TLNSetting.NUMBER_OF_SNP_PAIRS:
							currentIntensity += 1;
							break;
						case TLNSetting.P_VALUE:
							currentIntensity += sts.p > 0 ? -Math.log10(sts.p) : 0;
							break;
						}
							
						snpCount++;
						
						distinctSNPs.add(sp.snp1);
						distinctSNPs.add(sp.snp2);
							
						allDistinctSNPs.add(sp.snp1);
						allDistinctSNPs.add(sp.snp2);
					}
					
					if(currentIntensity > 0) {
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
						
						cellIntensity = Math.round(cellIntensity*100.)/100.;
						
						//determine the maximum edge weight to scale the edge widths
						if(cellIntensity > maxEdgeWeight)
							maxEdgeWeight = cellIntensity;

						distinctWeights.add(cellIntensity);
						edgeWeights.put(edge, cellIntensity);
						//store edges in the corresponding mapping structures
						edgesToGenePairs.put(edge, gp);
						edgesToGene.put(edge, gene.getName());
						edgesToSNPs.put(edge, distinctSNPs);
						distinctSNPs = new HashSet<SNV>();
						//increase edge identifier
						edge++;
					}
				}
			}
		}
		
		if(edgeControls != null) {
			edgeControls.edgeWeightSlider.setMaximum(distinctWeights.size()-1);
			edgeControls.edgeWeightSlider.revalidate();
			//applyTransformers(visualizationViewer);
		}
		
		if(setting.useLDBlocks()) {
			usedBlocks.clear();
		}
		
		if(getVisualizationViewer() != null) {
			getVisualizationViewer().repaint();
		}
	}
	
	protected final static class VertexStrokeHighlight<V,E> implements Transformer<V,Stroke> {
        protected boolean highlight = false;
        protected Stroke heavy = new BasicStroke(5);
        protected Stroke medium = new BasicStroke(2);
        protected Stroke light = new BasicStroke(1);
        protected PickedInfo<V> pi;
        protected Graph<V,E> graph;
        protected VisualizationViewer<V, E> vv;
        
        public VertexStrokeHighlight(Graph<V,E> graph, PickedInfo<V> pi, VisualizationViewer<V, E> vv) {
        	this.graph = graph;
            this.pi = pi;
            this.vv = vv;
        }
        
        public void setHighlight(boolean highlight) {
            this.highlight = highlight;
        }
        
        public Stroke transform(V v) {
        	if(pi.isPicked(v)) {
        		return heavy;
        	}
        	
            if (highlight) {
                if (pi.isPicked(v))
                    return heavy;
                else {
                	for(E e : graph.getInEdges(v)) {
                		Predicate<Context<Graph<V,E>,E>> p = vv.getRenderContext().getEdgeIncludePredicate();
                		boolean showEdge = p.evaluate(Context.getInstance(graph, e));

                		if(showEdge) {
                			Pair<V> endpoints = graph.getEndpoints(e);
                			
                			if(pi.isPicked(endpoints.getFirst()))
                				return medium;
                		}
                	}
                	for(E e : graph.getOutEdges(v)) {
                		Predicate<Context<Graph<V,E>,E>> p = vv.getRenderContext().getEdgeIncludePredicate();
                		boolean showEdge = p.evaluate(Context.getInstance(graph, e));
                		if(showEdge) {
                			Pair<V> endpoints = graph.getEndpoints(e);
                			
                			if(pi.isPicked(endpoints.getSecond()))
                				return medium;
                		}
                	}
                    return light;
                }
            }
            else
                return light; 
        }
    }
	
	protected final static class VertexShapeSizeAspect<V,E>
    extends AbstractVertexShapeTransformer <V>
    implements Transformer<V,Shape>  {
    	
        protected boolean stretch = false;
        protected boolean scale = false;
        protected boolean funny_shapes = false;
        protected Transformer<V,Double> expressions;
        protected Graph<V,E> graph;
//        protected AffineTransform scaleTransform = new AffineTransform();
        
        public VertexShapeSizeAspect(Graph<V,E> graphIn, Transformer<V,Double> expressionsIn) {
        	this.graph = graphIn;
            this.expressions = expressionsIn;
            
            setSizeTransformer(new Transformer<V,Integer>() {
	        	public Integer transform(V v) {
		            if (scale)
		                return (int)(expressions.transform(v) * 200) + 60;
		            else
		                return 60;
				}
            });
            
            setAspectRatioTransformer(new Transformer<V,Float>() {
				public Float transform(V v) {
		            if (stretch) {
		                return (float)(graph.inDegree(v) + 1) / 
		                	(graph.outDegree(v) + 1);
		            } else {
		                return 0.5f;
		            }
				}
			});
        }
        
		public void setStretching(boolean stretch) {
            this.stretch = stretch;
        }
        
        public void setScaling(boolean scale) {
            this.scale = scale;
        }
        
        public void useFunnyShapes(boolean use) {
            this.funny_shapes = use;
        }
        
        public Shape transform(V v) {
            if (funny_shapes) {
                if (graph.degree(v) < 5) {	
                    int sides = Math.max(graph.degree(v), 3);
                    return factory.getRegularPolygon(v, sides);
                }
                else
                    return factory.getRegularStar(v, graph.degree(v));
            }
            else
            	return factory.getRectangle(v);
        }
    }
	
	/**
	 * @author jaeger
	 *
	 */
	public class ExpressionScorer implements VertexScorer<String, Double> {
		@Override
		public Double getVertexScore(String v) {
			Gene g = data.getGenes().getGene(v);
			
			Integer[] affected = data.getSubjects().getAffectedSubjectIndices();
			Integer[] unaffected = data.getSubjects().getUnaffectedSubjectIndices();
			
			DoubleVector affectedValues = new DoubleVector(affected.length);
			DoubleVector unaffectedValues = new DoubleVector(unaffected.length);
			
			int index = 0;
			for(int personIndex : affected) {
				affectedValues.set(index++, g.getValue(personIndex));
			}
			index = 0;
			for(int personIndex : unaffected) {
				unaffectedValues.set(index, g.getValue(personIndex));
			}
			
			double meanAffected = affectedValues.mean();
			double meanUnaffected = unaffectedValues.mean();
//			System.out.println("Gene: " + g.getName() + " A: " + meanAffected + " U: " + meanUnaffected);
			return meanUnaffected - meanAffected;
		}
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case ViewModelEvent.PROBE_SELECTION_CHANGED:
			internalChange = false;
			Set<Probe> selected = viewModel.getSelectedProbes();
			PickedState<String> pickedState = visualizationViewer.getPickedVertexState();
			//remove previous pick states
			for(String v : graph.getVertices()) {
				pickedState.pick(v, false);
			}
			//add new pick states
			for(Probe p: selected) {
				pickedState.pick(p.getName(), true);
			}
			internalChange = true;
			break;
		case ViewModelEvent.TOTAL_PROBES_CHANGED: 
			//TODO react on this event!
			break;
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
//			System.out.println("PLINKGraph: SNP Selection Changed!");
		}
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		this.setting = new TLNSetting(this);
		
		this.calculateEdgeWeights();
		this.start();
		
		edgeControls = new TLNControlPanel(this);
		
		getVisualizationViewer().getRenderContext().getPickedEdgeState().addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				SNVList snps = getSNPsFromSelectedEdges();
				viewModel.setSNPSelection(snps);
			}
		});
		
		getVisualizationViewer().repaint();
		
		return setting;
	}
	
	/**
	 * @return this setting
	 */
	public TLNSetting getSetting() {
		return this.setting;
	}
	
	public void updateTask() {
		AbstractTask updateTask = new AbstractTask("Update Gene Association Network Edge Weights") {
			@Override
			protected void initialize() {}

			@Override
			protected void doWork() throws Exception {
				calculateEdgeWeights();
				rebuildGraph();
			}
		};
		
		updateTask.start();
	}
	
	public void selectRemainingEdges() {
		Collection<Integer> edges = this.graph.getEdges();
		PickedState<Integer> pickedState = getVisualizationViewer().getPickedEdgeState();
		Predicate<Context<Graph<String,Integer>,Integer>> p = getVisualizationViewer().getRenderContext().getEdgeIncludePredicate();
		for(Integer e : edges) {
			boolean showEdge = p.evaluate(Context.getInstance(graph, e));
			if(showEdge) {
				pickedState.pick(e, true);
			} else {
				pickedState.pick(e, false);
			}
		}
	}
	
	public void deselectAllEdges() {
		Collection<Integer> edges = this.graph.getEdges();
		PickedState<Integer> pickedState = getVisualizationViewer().getPickedEdgeState();
		for(Integer e : edges) {
			pickedState.pick(e, false);
		}
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
