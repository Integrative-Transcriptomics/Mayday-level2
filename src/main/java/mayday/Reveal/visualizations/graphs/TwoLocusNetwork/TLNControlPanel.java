package mayday.Reveal.visualizations.graphs.TwoLocusNetwork;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.BoundedRangeModel;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.collections15.Predicate;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout2;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ConstantDirectionalEdgeValueTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.EdgeLabelRenderer;
import edu.uci.ics.jung.visualization.util.Animator;
import mayday.core.MaydayDefaults;

/**
 * @author jaeger
 *
 */
@SuppressWarnings({"serial", "rawtypes", "unchecked"})
public class TLNControlPanel extends JFrame {
	protected VisualizationViewer<String, Integer> vv;
	protected TLN plink;
	
	//widgets
	JRadioButton lineButton;
	JRadioButton quadButton;
	JRadioButton cubicButton;
	JRadioButton orthoButton;
	
	JCheckBox parallelLabelBox;
	JSlider edgeLabelPositionSlider;
	JSlider edgeOffsetSlider;
	JSlider edgeWeightSlider;
	JCheckBox edgeStrokeHighlightBox;
	JSlider edgeStrokeTransparencySlider;
	
	JCheckBox showSelfEdgesBox;
	JCheckBox vertexStrokeHighlightBox;
	JCheckBox vertexSizeByExpressionBox;
	JCheckBox includeSingleLocusResultsBox;
	JCheckBox highlightNodeDegreeBox;
	
	JComboBox layoutCombo;

	JLabel labelPositionLabel;
	JLabel edgeDistanceLabel;
	JLabel minEdgeWeightLabel;
	
	JTextField minEdgeWidth;
	
	JButton selectRemainingEdges;
	JButton unselectAllEdges;
	
	Set<String> hiddenNodes = new HashSet<String>();
	
	JButton hideSelectedNodes;
	JButton showAllNodes;
	
	JButton buildNewGraph;
	
	ShowEdgePredicate showEdge;
	ShowNodePredicate showNode;
	
	/**
	 * @param plink
	 */
	public TLNControlPanel(final TLN plink) {
		this.plink = plink;
		this.vv = plink.getVisualizationViewer();
		
		this.initializeWidgets();
		this.buildGUI();
		
		this.addWindowListener(new WindowListener() {
			@Override
			public void windowOpened(WindowEvent e) {}

			@Override
			public void windowClosing(WindowEvent e) {}

			@Override
			public void windowClosed(WindowEvent e) {
				plink.getSetting().getShowEdgeProperties().setBooleanValue(false);
			}

			@Override
			public void windowIconified(WindowEvent e) {}

			@Override
			public void windowDeiconified(WindowEvent e) {}

			@Override
			public void windowActivated(WindowEvent e) {}

			@Override
			public void windowDeactivated(WindowEvent e) {}
		});
	}
	
	private void initializeWidgets() {
		final EdgeLabelRenderer edgeLabelRenderer = vv.getRenderContext().getEdgeLabelRenderer();
		
		
        lineButton = new JRadioButton("Line");
        lineButton.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<String,Integer>());
                    vv.repaint();
                }
            }
        });
        lineButton.setSelected(true);
        
        quadButton = new JRadioButton("QuadCurve");
        quadButton.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<String,Integer>());
                    vv.repaint();
                }
            }
        });
        
        cubicButton = new JRadioButton("CubicCurve");
        cubicButton.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.CubicCurve<String,Integer>());
                    vv.repaint();
                }
            }
        });
        
        orthoButton = new JRadioButton("Orthogonal");
        orthoButton.addItemListener(new ItemListener(){
        	public void itemStateChanged(ItemEvent e) {
        		if(e.getStateChange() == ItemEvent.SELECTED) {
        			vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Orthogonal<String, Integer>());
        			vv.repaint();
        		}
        	}
        });
        
        parallelLabelBox = new JCheckBox("<html><center>Parallel</center></html>");
        parallelLabelBox.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                AbstractButton b = (AbstractButton)e.getSource();
                edgeLabelRenderer.setRotateEdgeLabels(b.isSelected());
                vv.repaint();
            }
        });
        parallelLabelBox.setSelected(true);
   
        labelPositionLabel = new JLabel("", JLabel.LEFT);
        edgeDistanceLabel = new JLabel("", JLabel.LEFT);
        minEdgeWeightLabel = new JLabel("", JLabel.LEFT);
        
        MutableDirectionalEdgeValue mv = new MutableDirectionalEdgeValue(.5, labelPositionLabel);
        vv.getRenderContext().setEdgeLabelClosenessTransformer(mv);
        
        edgeLabelPositionSlider = new JSlider(mv.getUndirectedModel()) {
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width /= 2;
                return d;
            }
        };
        
        edgeOffsetSlider = new JSlider(0,50) {
            public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                d.width /= 2;
                return d;
            }
        };
        
        edgeOffsetSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider s = (JSlider)e.getSource();
                AbstractEdgeShapeTransformer<String,Integer> aesf = 
                    (AbstractEdgeShapeTransformer<String,Integer>)vv.getRenderContext().getEdgeShapeTransformer();
                aesf.setControlOffsetIncrement(s.getValue());
                edgeDistanceLabel.setText(s.getValue()+"");
                vv.repaint();
            }
        });

        edgeDistanceLabel.setText(edgeOffsetSlider.getValue()+"");
        
        int weightsSize = plink.distinctWeights.size();
        edgeWeightSlider = new JSlider(0, weightsSize > 0 ? weightsSize-1 : 0);
        edgeWeightSlider.setValue(0);
        edgeWeightSlider.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				minEdgeWeightLabel.setText(plink.distinctWeights.toArray(new Double[0])[((JSlider)e.getSource()).getValue()]+"");
				vv.repaint();
			}
        });
        
        if(plink.distinctWeights.size() > 0)
        	minEdgeWeightLabel.setText(plink.distinctWeights.toArray(new Double[0])[edgeWeightSlider.getValue()]+"");
        
        showSelfEdgesBox = new JCheckBox("<html><center>Show self-edges</center></html>");
        showSelfEdgesBox.setSelected(true);
        showSelfEdgesBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				vv.repaint();
			}
        });
        
        showEdge = new ShowEdgePredicate(plink, edgeWeightSlider, showSelfEdgesBox);
        showNode = new ShowNodePredicate(plink);
        vv.getRenderContext().setEdgeIncludePredicate(showEdge);
        vv.getRenderContext().setVertexIncludePredicate(showNode);
        
		Class[] combos = getCombos();
        layoutCombo = new JComboBox(combos);
        layoutCombo.setRenderer(new DefaultListCellRenderer() {
			public  Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        		String valueString = value.toString();
        		valueString = valueString.substring(valueString.lastIndexOf('.')+1);
        		return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
        	}
        });
        layoutCombo.addActionListener(new LayoutChooser(layoutCombo, vv));
        layoutCombo.setSelectedItem(CircleLayout.class);
        
        
        vertexStrokeHighlightBox = new JCheckBox("Highlight neighboring nodes");
        vertexStrokeHighlightBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton b = (AbstractButton)e.getSource();
				boolean selected = b.isSelected();
				plink.vertexStrokeHighlight.setHighlight(selected);
				vv.repaint();
			}
        });
        
        vertexSizeByExpressionBox = new JCheckBox("Node size by expression");
        vertexSizeByExpressionBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton b = (AbstractButton)e.getSource();
				boolean selected = b.isSelected();
				plink.vertexShapeSizeAspect.setScaling(selected);
				vv.repaint();
			}
        });
        
        includeSingleLocusResultsBox = new JCheckBox("Include Single Locus Results");
        includeSingleLocusResultsBox.addItemListener(new ItemListener(){
			@Override
			public void itemStateChanged(ItemEvent e) {
				plink.updateTask();
			}
        });
        
        minEdgeWidth = new JTextField();
        minEdgeWidth.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
//				plink.maxEdgeWidth = ()e.getSource()
			}
        });
        
        highlightNodeDegreeBox = new JCheckBox("Highlight Node Degree");
        highlightNodeDegreeBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				AbstractButton b = (AbstractButton)e.getSource();
				boolean selected = b.isSelected();
				plink.vertexShapeSizeAspect.useFunnyShapes(selected);
				vv.repaint();
			}
        });
        
        edgeStrokeHighlightBox = new JCheckBox("Filter edges with the same color as the selected node(s)");
        edgeStrokeHighlightBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				vv.repaint();
			}
        });
        
        edgeStrokeTransparencySlider = new JSlider(0, 255);
        edgeStrokeTransparencySlider.setValue(200);
        edgeStrokeTransparencySlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				vv.repaint();
			}
        });
        
        
        selectRemainingEdges = new JButton("Select remaining edges");
        selectRemainingEdges.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				plink.selectRemainingEdges();
				vv.repaint();
			}
        });
        
        unselectAllEdges = new JButton("Deselect all edges");
        unselectAllEdges.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				plink.deselectAllEdges();
				vv.repaint();
			}
        });
        
        hideSelectedNodes = new JButton("Hide selected nodes");
        hideSelectedNodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for(String node : plink.graph.getVertices()) {
					boolean isPicked = vv.getPickedVertexState().isPicked(node);
					if(isPicked) {
						hiddenNodes.add(node);
					}
				}
				vv.repaint();
			}
        });
        
        showAllNodes = new JButton("Show all nodes");
        showAllNodes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				hiddenNodes.clear();
				vv.repaint();
			}
        });
        
        buildNewGraph = new JButton("Rebuild graph from visible elements");
        buildNewGraph.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				plink.rebuildGraphFromVisibleElements();
				vv.repaint();
			}
		});
	}
	
	private void buildGUI() {
		
		ButtonGroup radio = new ButtonGroup();
        radio.add(lineButton);
        radio.add(quadButton);
        radio.add(cubicButton);
        radio.add(orthoButton);
        
        Box controls = Box.createVerticalBox();
        
        JPanel layoutSettings = new JPanel(new GridLayout(0,1));
        layoutSettings.setBorder(BorderFactory.createTitledBorder("Layout"));
        layoutSettings.add(layoutCombo);
        layoutSettings.add(lineButton);
        layoutSettings.add(quadButton);
        layoutSettings.add(cubicButton);
        layoutSettings.add(orthoButton);
        
        JPanel edgeSettings = new JPanel(new GridLayout(0,1));
        edgeSettings.setBorder(BorderFactory.createTitledBorder("Edges"));
        
        JPanel edgeCheckBoxes = new JPanel(new GridLayout(3,1));
        edgeCheckBoxes.add(parallelLabelBox);
        edgeCheckBoxes.add(showSelfEdgesBox);
        edgeCheckBoxes.add(includeSingleLocusResultsBox);
        
        edgeSettings.add(edgeCheckBoxes);
        
        JPanel sliderPanel = new JPanel(new GridLayout(3,1));
        JPanel sliderLabelPanel = new JPanel(new GridLayout(3,1));
        JPanel sliderValuePanel = new JPanel(new GridLayout(3,1));
        
        sliderValuePanel.add(labelPositionLabel);
        sliderValuePanel.add(edgeDistanceLabel);
        sliderValuePanel.add(minEdgeWeightLabel);
        
        sliderPanel.add(edgeLabelPositionSlider);
        sliderPanel.add(edgeOffsetSlider);
        sliderPanel.add(edgeWeightSlider);
        
        sliderLabelPanel.add(new JLabel("Label Position", JLabel.RIGHT));
        sliderLabelPanel.add(new JLabel("Edge Distance", JLabel.RIGHT));
        sliderLabelPanel.add(new JLabel("Filter edges by weight", JLabel.RIGHT));
        
        JPanel offsetPanel = new JPanel(new BorderLayout());
        offsetPanel.add(sliderLabelPanel, BorderLayout.WEST);
        offsetPanel.add(sliderPanel, BorderLayout.CENTER);
        offsetPanel.add(sliderValuePanel, BorderLayout.EAST);
        
        edgeSettings.add(offsetPanel);
        
        JPanel edgeStrokePanel = new JPanel(new GridLayout(3,1));
        edgeStrokePanel.add(edgeStrokeHighlightBox);
        
        edgeStrokePanel.add(new JLabel("Edge Transparency", JLabel.LEFT));
        edgeStrokePanel.add(edgeStrokeTransparencySlider);
        edgeSettings.add(edgeStrokePanel);
        
        JPanel nodeSettings = new JPanel(new GridLayout(0,1));
        nodeSettings.setBorder(BorderFactory.createTitledBorder("Node Settings"));
        nodeSettings.add(vertexStrokeHighlightBox);
        nodeSettings.add(vertexSizeByExpressionBox);
        nodeSettings.add(highlightNodeDegreeBox);
        nodeSettings.add(hideSelectedNodes);
        nodeSettings.add(showAllNodes);
        nodeSettings.add(buildNewGraph);
        
        controls.add(nodeSettings);
        controls.add(edgeSettings);
        controls.add(layoutSettings);
        
        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.add(selectRemainingEdges);
        selectionPanel.add(unselectAllEdges);
        selectionPanel.setBorder(BorderFactory.createTitledBorder("Edge Selection"));
        
        getContentPane().add(controls, BorderLayout.CENTER);
        getContentPane().add(selectionPanel, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(400,200);
        pack();
	}
	
    /**
     * @return
     */
    private static Class<? extends Layout>[] getCombos()
    {
        List<Class<? extends Layout>> layouts = new ArrayList<Class<? extends Layout>>();
        layouts.add(KKLayout.class);
        layouts.add(FRLayout.class);
        layouts.add(CircleLayout.class);
        layouts.add(SpringLayout.class);
        layouts.add(SpringLayout2.class);
        layouts.add(ISOMLayout.class);
        return layouts.toArray(new Class[0]);
    }
	
	@SuppressWarnings("deprecation")
	public void setVisible(boolean visible) {
		if(visible)
			MaydayDefaults.centerWindowOnScreen(this);
		super.setVisible(visible);
	}
	
	/**
     * subclassed to hold two BoundedRangeModel instances that
     * are used by JSliders to move the edge label positions
     * @author Tom Nelson
     *
     *
     */
    class MutableDirectionalEdgeValue extends ConstantDirectionalEdgeValueTransformer<String,Integer> {
        BoundedRangeModel undirectedModel = new DefaultBoundedRangeModel(5,0,0,10);
//        BoundedRangeModel directedModel = new DefaultBoundedRangeModel(7,0,0,10);
        
        public MutableDirectionalEdgeValue(double undirected, final JLabel label) {
            super(undirected, 0);
            undirectedModel.setValue((int)(undirected*10));
            label.setText(undirectedModel.getValue()+"");
//            directedModel.setValue((int)(directed*10));
            
            undirectedModel.addChangeListener(new ChangeListener(){
                public void stateChanged(ChangeEvent e) {
                    setUndirectedValue(new Double(undirectedModel.getValue()/10f));
                    DefaultBoundedRangeModel m = (DefaultBoundedRangeModel)e.getSource();
                    label.setText(m.getValue()+"");
                    vv.repaint();
                }
            });
//            directedModel.addChangeListener(new ChangeListener(){
//                public void stateChanged(ChangeEvent e) {
//                    setDirectedValue(new Double(directedModel.getValue()/10f));
//                    vv.repaint();
//                }
//            });
        }
//        /**
//         * @return Returns the directedModel.
//         */
//        public BoundedRangeModel getDirectedModel() {
//            return directedModel;
//        }
        /**
         * @return Returns the undirectedModel.
         */
        public BoundedRangeModel getUndirectedModel() {
            return undirectedModel;
        }
    }
    
    public final static class ShowNodePredicate implements Predicate<Context<Graph<String, Integer>, String>> {

    	protected TLN plink;
    	
    	public ShowNodePredicate(TLN plink) {
    		this.plink = plink;
    	}
    	
		@Override
		public boolean evaluate(Context<Graph<String, Integer>, String> context) {
			String node = context.element;
			
			if(plink.edgeControls.hiddenNodes.contains(node)) {
				return false;
			}
			
			return true;
		}
    }
    
    /**
     * @author jaeger
     *
     */
    public final static class ShowEdgePredicate implements Predicate<Context<Graph<String,Integer>,Integer>> {
    	
    	protected TLN plink;
    	protected JCheckBox b;
    	protected JSlider s;
    	
    	/**
    	 * @param plink
    	 * @param s 
    	 * @param b
    	 */
    	public ShowEdgePredicate(TLN plink, JSlider s, JCheckBox b) {
    		this.plink = plink;
    		this.b = b;
    		this.s = s;
    	}
    	
		@Override
		public boolean evaluate(Context<Graph<String, Integer>, Integer> context) {
			Graph<String,Integer> graph = context.graph;
			Integer edge = context.element;
			
			if(plink.edgeWeights.get(edge) >= plink.distinctWeights.toArray(new Double[0])[s.getValue()]) {
				//exclude edges with weight = 0
				if(plink.edgeWeights.get(edge) == 0) 
					return false;
				
				if(plink.edgeControls.edgeStrokeHighlightBox.isSelected()) {
					String vertex = plink.edgesToGene.get(edge);
					boolean picked = plink.edgeControls.vv.getPickedVertexState().isPicked(vertex);

					if(!picked) {
						return false;
					}
				}
				
				boolean showSelfEdges = b.isSelected();
				if(!showSelfEdges) {
					Pair<String> endpoints = graph.getEndpoints(edge);
					boolean isSelfEdge = endpoints.getFirst().equals(endpoints.getSecond());
					return !isSelfEdge;
				}
				
				return true;
			}
			return false;
		}
    }
    
    /**
     * @author jaeger
     *
     */
    public class LayoutChooser implements ActionListener {
		private JComboBox box;
    	private VisualizationViewer<String, Integer> vv;
    	
    	/**
    	 * @param box
    	 * @param vv
    	 */
    	public LayoutChooser(JComboBox box, VisualizationViewer<String, Integer> vv) {
    		super();
    		this.box = box;
    		this.vv = vv;
    	}
    	
		public void actionPerformed(ActionEvent e) {
    		Class<? extends Layout<String, Integer>> layoutClass = (Class<? extends Layout<String, Integer>>) box.getSelectedItem();
    		
    		try {
    			Constructor<? extends Layout<String, Integer>> constructor = layoutClass.getConstructor(new Class[]{Graph.class});
    			Object o = constructor.newInstance(vv.getGraphLayout().getGraph());
    			Layout<String, Integer> l = (Layout<String, Integer>) o;
    			l.setInitializer(vv.getGraphLayout());
    			l.setSize(vv.getSize());
    			
    			LayoutTransition<String, Integer> lt = new LayoutTransition<String, Integer>(vv, vv.getGraphLayout(), l);
    			Animator animator = new Animator(lt);
    			animator.start();
    			
    			vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
    			vv.repaint();
    		} catch (Exception ex) {
    			ex.printStackTrace();
    		}
    	}
    }
}
