package mayday.Reveal.visualizations.graphs;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;

import mayday.Reveal.visualizations.RevealVisualization;
import mayday.vis3.model.ViewModelListener;
import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.renderers.BasicVertexLabelRenderer.InsidePositioner;
import edu.uci.ics.jung.visualization.renderers.Renderer;


/**
 * @author jaeger
 *
 * @param <V>
 * @param <E>
 */
@SuppressWarnings("serial")
public abstract class AssociationGraph<V, E> extends RevealVisualization implements KeyListener, ViewModelListener {

	public Graph<V,E> graph;
	protected VisualizationViewer<V,E> visualizationViewer;
	protected DefaultModalGraphMouse<V, E> graphMouse;
	protected GraphZoomScrollPane scrollPane;
	
	/**
	 * @param name 
	 * @param menu 
	 * 
	 */
	public AssociationGraph(String name) {
		this.addKeyListener(this);
		this.setLayout(new BorderLayout());
	}
	
	
	/**
	 * start the calculations
	 */
	public void start() {
		graph = this.buildGraph();
		
		Layout<V, E> layout = new CircleLayout<V, E>(graph);
		layout.setSize(new Dimension(800, 600));
		
		//define a new visualization view
		visualizationViewer = new VisualizationViewer<V, E>(layout);
		visualizationViewer.setPreferredSize(new Dimension(800, 600));
		visualizationViewer.setBackground(Color.WHITE);
		visualizationViewer.setForeground(Color.BLACK);
		
		//add graph mouse
		graphMouse = new DefaultModalGraphMouse<V, E>();
		graphMouse.setMode(DefaultModalGraphMouse.Mode.TRANSFORMING);
		visualizationViewer.setGraphMouse(graphMouse);
		visualizationViewer.addKeyListener(graphMouse.getModeKeyListener());
		visualizationViewer.addKeyListener(this);
//		visualizationViewer.setToolTipText("<html><center>Type 'p' for Pick mode<p>Type 't' for Transform mode</center></html>");
		
		//place vertex labels inside by default
		visualizationViewer.getRenderer().getVertexLabelRenderer().setPositioner(new InsidePositioner());
		visualizationViewer.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
		
		this.applyTransformers(visualizationViewer);
		
		//scroll pane for zooming
		scrollPane = new GraphZoomScrollPane(visualizationViewer);
		add(scrollPane);
	}
	
	protected abstract Graph<V, E> buildGraph();
	
	protected abstract void applyTransformers(VisualizationViewer<V,E> vv);
	
	/**
	 * @return the visualization viewer
	 */
	public VisualizationViewer<V,E> getVisualizationViewer() {
		return this.visualizationViewer;
	}
	
//	/**
//	 * @return possible JMenus
//	 */
//	public JMenu[] getJMenus() {
//		JMenu modeMenu = graphMouse.getModeMenu();
//		modeMenu.setText("Mouse Mode");
//		modeMenu.setIcon(null);
//		modeMenu.setPreferredSize(new Dimension(120, 20));
//		return new JMenu[]{modeMenu};
//	}
//	
//	/**
//	 * @return a JMenuBar that contains all possible JMenus for manipulating the graph
//	 */
//	public JMenuBar getJMenuBar() {
//		JMenuBar bar = new JMenuBar();
//		for(JMenu m : getJMenus()) {
//			bar.add(m);
//		}
//		return bar;
//	}

	@Override
	public void keyTyped(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_F1) {
			new ShortcutDialog();
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
	
	protected class ShortcutDialog {
		
		public ShortcutDialog() {
			
			JFrame f = new JFrame("Shortcuts");
			f.setLayout(new BoxLayout(f.getContentPane(), BoxLayout.PAGE_AXIS));
			
			//general
			f.add(new JLabel("<html>F1\tShow help dialog"));
			
			//mouse actions
			f.add(new JLabel("<html>T\tSwitch to mouse transforming mode"));
			f.add(new JLabel("<html>P\tSwitch to mouse picking mode"));
			
			f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			f.pack();
			
			f.setVisible(true);
		}
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		paintPlot((Graphics2D)g);
	}
	
	public void updatePlot() {}
	
	@Override
	public void removeNotify() {
		if(viewModel != null)
			viewModel.removeViewModelListener(this);
		super.removeNotify();
	}

	public void paintPlot(Graphics2D g) {
		scrollPane.repaint();
		visualizationViewer.repaint();
	}
	
	/**
	 * @return graph mouse
	 */
	public DefaultModalGraphMouse<V, E> getGraphMouse() {
		return this.graphMouse;
	}
	
	/**
	 * @return scroll pane
	 */
	public GraphZoomScrollPane getScrollPane() {
		return this.scrollPane;
	}
}