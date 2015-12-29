package mayday.Reveal.visualizations.graphs.SingleLocusNetwork;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.GeneList;
import mayday.Reveal.data.GenePair;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.SingleLocusResult;
import mayday.Reveal.data.meta.SingleLocusResult.Statistics;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.GeneColors;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.graphs.AssociationGraph;
import mayday.core.Probe;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;

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

@SuppressWarnings("serial")
public class SLN extends AssociationGraph<String, Integer> {
	
	/**
	 * the title of this plot
	 */
	public static final String TITLE = "SLN";
	/**
	 * the description of this plot
	 */
	public static final String DESCRIPTION = "Display gene/SNP associations based on PLINK single locus results";

	private SLNSetting setting;
	
	//map colors to gene names
	protected Map<String, Color> geneColors = new HashMap<String, Color>();
	//map edges to genepairs
	protected Map<Integer, GenePair> edgesToGenePairs = new HashMap<Integer, GenePair>();
	//map weights to edge IDs
	protected Map<Integer, Integer> edgeWeights = new HashMap<Integer, Integer>();
	protected int maxEdgeWeight = 0;
	protected float maxEdgeWidth = 25f;
	private boolean internalChange = true;
	
	protected VertexStrokeHighlight<String, Integer> vertexStrokeHighlight;
	protected VertexShapeSizeAspect<String, Integer> vertexShapeSizeAspect;
	
	public SLN(ProjectHandler projectHandler) {
		super("Single Locus Network");
		setData(projectHandler.getSelectedProject());
		
		GeneList genes = getData().getGenes();

		//initialize color map
		Color[]  colors = GeneColors.colorBrewer(genes.size());
		colors = GeneColors.rainbow(genes.size(), 0.8);
		for(int i = 0; i < colors.length; i++) {
			geneColors.put(genes.getGene(i).getName(), colors[i]);
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
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			break;
		}
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
			g.addEdge(edge, gp.gene1.getName(), gp.gene2.getName(), EdgeType.DIRECTED);
		}
		
		return g;
	}

	@Override
	protected void applyTransformers(VisualizationViewer<String, Integer> vv) {
		//define colors for the genes
				Transformer<String, Paint> vertexPaint = new Transformer<String, Paint>() {
					@Override
					public Paint transform(String geneName) {	
						Color c = geneColors.get(geneName);
						Gene g = getData().getGenes().getGene(geneName);
						SLResults slrs = (SLResults) getData().getMetaInformationManager().get(SLResults.MYTYPE).get(0);
						if(slrs.get(g) == null) {
							c = Color.GRAY;
						}
						return c;
					}
				};
				//define colors for the edges
				Transformer<Integer, Paint> edgePaint = new Transformer<Integer, Paint>() {
					@Override
					public Paint transform(Integer edgeID) {
						boolean picked = visualizationViewer.getPickedEdgeState().isPicked(edgeID);
						if(picked) {
							return setting.getEdgeSelectionColor();
						} else {
							return Color.BLACK;
						}
					}
				};
				//set the edge stroke proportional to the edge weight
				Transformer<Integer, Stroke> edgeStroke = new Transformer<Integer, Stroke>() {
					@Override
					public Stroke transform(Integer edgeID) {
						//scale: maximal edge width = node.width * 0.8
						if(edgeWeights.get(edgeID) == null) {
							return new BasicStroke(0);
						}
						return new BasicStroke((edgeWeights.get(edgeID).floatValue()/maxEdgeWeight) * maxEdgeWidth);
					}
				};
				//set edge weights as edge labels
				Transformer<Integer, String> edgeLabeller = new Transformer<Integer, String>() {
					@Override
					public String transform(Integer edgeID) {
						if(setting.showEdgeLabels()) {
							if(edgeWeights.get(edgeID) == null)
								return "";
							return edgeWeights.get(edgeID).toString();
						} else
							return "";
					}
				};
				
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
		        
		        vertexShapeSizeAspect = new VertexShapeSizeAspect<String, Integer>(graph);
		        
		        Predicate<Context<Graph<String,Integer>,Integer>> showEdge = new Predicate<Context<Graph<String,Integer>,Integer>>() {
					@Override
					public boolean evaluate(
							Context<Graph<String, Integer>, Integer> context) {
						Integer edge = context.element;
						if(edgeWeights.get(edge) == 0)
							return false;
						return true;
					}
		        };
		        
		        RenderContext<String, Integer> renderContext = vv.getRenderContext();
		        renderContext.setVertexFillPaintTransformer(vertexPaint);
				renderContext.setEdgeDrawPaintTransformer(edgePaint);
				renderContext.setEdgeStrokeTransformer(edgeStroke);
				renderContext.setVertexLabelTransformer(new ToStringLabeller<String>());
				renderContext.setEdgeLabelTransformer(edgeLabeller);
				renderContext.setVertexShapeTransformer(vertexShapeSizeAspect);
				renderContext.setVertexStrokeTransformer(vertexStrokeHighlight);
				renderContext.setVertexDrawPaintTransformer(vertexDrawPaint);
				renderContext.setEdgeIncludePredicate(showEdge);
	}
	
	protected void calculateEdgeWeights() {
//		LDStructure ldStructure = null;
//		if(setting.useLDBlocks()) {
//			ldStructure = getData().getLDStructure(0);
//		}
		SLResults slrs = (SLResults) getData().getMetaInformationManager().get(SLResults.MYTYPE).get(0);
		GeneList genes = getData().getGenes();
		
		int edge = 0;
		maxEdgeWeight = 0;
		edgesToGenePairs.clear();
		edgeWeights.clear();
		
		String externalSNPListName = setting.getExternalSNPListName();
		HashSet<SNV> externalSNPs = new HashSet<SNV>();
		if(externalSNPListName != null) {
			externalSNPs.addAll(getData().getSNVList(externalSNPListName));
		}
		
		HashMap<GenePair, Integer> edgeCounts = new HashMap<GenePair, Integer>();
		
		for(int i = 0; i < genes.size(); i++) {
			Gene gene = genes.getGene(i);
			SingleLocusResult slr = slrs.get(gene);
			
			for(SNV s: slr.keySet()) {
				//use only snps from the chosen SNPList
				if(externalSNPs.size() != 0) {
					if(!externalSNPs.contains(s)) {
						continue;
					}
				}
				
				double pT = setting.getPValueThreshold();
				double r2T = setting.getR2ValueThreshold();
				
				Statistics stat = slr.get(s);
				
				Gene gene2 = genes.getGene(s.getGene());
				GenePair gp = new GenePair(gene2, gene);
				
				//if p-value is too high
				if(Double.compare(stat.p, pT) >= 0) {
					if(!edgeCounts.containsKey(gp)) {
						edgeCounts.put(gp, 0);
					}
					continue;
				}
				
				Integer count = edgeCounts.get(gp);
				if(count == null) {
					count = 1;
				} else {
					count++;
				}
				edgeCounts.put(gp, count);
				
				if(count > maxEdgeWeight) {
					maxEdgeWeight = count;
				}
			}
		}
		
		for(GenePair gp : edgeCounts.keySet()) {
			edgesToGenePairs.put(edge, gp);
			edgeWeights.put(edge, edgeCounts.get(gp));
			edge++;
		}
		
		//FIXME huge problem: 
		//if the graph does not contain the same edges as
		//calculated after a setting changed it results in a nullpointer!!!
		
//		if(setting.useLDBlocks()) {
//			ldStructure.resetEdges();
//		}
		
		if(getVisualizationViewer() != null) {
			graph = buildGraph();
			getVisualizationViewer().repaint();
		}	
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SLNSetting(this);
		
		this.calculateEdgeWeights();
		this.start();
		getVisualizationViewer().repaint();
		//TODO
//		getVisualizationViewer().getRenderContext().getPickedEdgeState().addItemListener(new ItemListener() {
//			@Override
//			public void itemStateChanged(ItemEvent e) {
//				SNPList snps = getSNPsFromSelectedEdges();
//				viewModel.setSNPSelection(snps);
//			}
//		});
		
		return setting;
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
        protected boolean funny_shapes = false;
        protected Graph<V,E> graph;
//        protected AffineTransform scaleTransform = new AffineTransform();
        
        public VertexShapeSizeAspect(Graph<V,E> graphIn) {
        	this.graph = graphIn;
            
            setSizeTransformer(new Transformer<V,Integer>() {
	        	public Integer transform(V v) {
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
