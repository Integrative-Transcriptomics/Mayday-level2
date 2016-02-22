package mayday.vis3.plots.treeviz3;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;
import java.util.ArrayList;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.core.ProbeList;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.plots.trees.TreeVisualizerComponent;

/**
 * @author Eugen Netz
 */
@SuppressWarnings("serial")
public class PathPainter extends TreeVisualizerComponent 
	implements MouseListener, MouseMotionListener, SettingChangeListener{

	private GeneralPath currentPath;
	private ArrayList<GeneralPath> pathList;
	private ClusterCuttingSetting setting;
	private ClusterCuttingDelegate cuttingDelegate;
	
	public PathPainter() {
		super();
		this.currentPath = new GeneralPath();
		this.pathList = new ArrayList<GeneralPath>();
		
		//Plugin specific Key Bindings
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control C"),
				"toggleUse");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control X"),
				"toggleAddPath");
		this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("control V"),
				"toggleAcceptClustering");
		this.getActionMap().put("toggleUse", new ToggleUseAction());
		this.getActionMap().put("toggleAddPath", new ToggleAddPathAction());
		this.getActionMap().put("toggleAcceptClustering", new ToggleAcceptClusteringAction());
	}
	
	public void paint(Graphics g) {
		super.paint(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setBackground(Color.white);
		g2.setPaint(setting.getLineColor());
		
		for(GeneralPath path : pathList) {
			g2.draw(path);
		}
		g2.draw(currentPath);
		
	}

	@Override
	public void setup(PlotContainer plotContainer) {
		super.setup(plotContainer);
		
		plotContainer.addViewSetting(setting = new ClusterCuttingSetting("Cluster Cutting"), this);
		addMouseListener(this);
		addMouseMotionListener(this);
		setting.addChangeListener(this);
		
		
	}

	@Override
	public void mouseClicked(MouseEvent eve) {
		// Do Nothing		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// Do Nothing		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// Do Nothing		
	}

	@Override
	public void mousePressed(MouseEvent eve) {
		if (!setting.getAddPath()) {
			pathList = new ArrayList<GeneralPath>();
		}
		currentPath = new GeneralPath();
		currentPath.moveTo(eve.getX(), eve.getY());
	}
	

	@Override
	public void mouseReleased(MouseEvent eve) {
		if (setting.getLineOrPath().equals("Line")) {
			currentPath.lineTo(eve.getX(), eve.getY());
		} 
		pathList.add(currentPath);
		if(!setting.getAddPath()) {
			cuttingDelegate.handleCuttingPath(pathList);
			pathList = new ArrayList<GeneralPath>();
			currentPath = new GeneralPath();
		}	
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent eve) {
		if (setting.getLineOrPath().equals("Line")) {
			double[] coords = new double[6];
			currentPath.getPathIterator(null).currentSegment(coords);
			currentPath.reset();
			currentPath.moveTo(coords[0], coords[1]);
		}
		currentPath.lineTo(eve.getX(), eve.getY());

		repaint();
	}
	
	@Override
	public void adoptTree(TreeInfo ti, PlotContainer plotContainer, ProbeList pl) {
		super.adoptTree(ti, plotContainer, pl);
		cuttingDelegate = new ClusterCuttingDelegate(setting, painter, plotContainer.getViewModel(), ti);
	}

	@Override
	public void mouseMoved(MouseEvent eve) {
		//  Do Nothing		
	}

	@Override
	public void stateChanged(SettingChangeEvent eve) {
		if (setting.getApplyClusterCutting()) {
			removeMouseListener(this);
			removeMouseMotionListener(this);
			addMouseListener(this);
			addMouseMotionListener(this);
		} else if (!setting.getApplyClusterCutting()){
			removeMouseListener(this);
			removeMouseMotionListener(this);
		}
		
		if(setting.addPathStateChanged()) {
			if(!setting.getAddPath()) {
				cuttingDelegate.handleCuttingPath(pathList);
				pathList = new ArrayList<GeneralPath>();
				currentPath = new GeneralPath();
			}
			setting.setAddPathState();
		}
		
		if (setting.getAcceptClustering()) {
			setting.resetAcceptClustering();
			cuttingDelegate.acceptClustering();
		}
		
		if (setting.multiProfilePlotChanged() || setting.silhouettePlotChanged()) {
			cuttingDelegate.openPlot(true);			
		}
	}
	
	protected class ToggleUseAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setting.toggleUseClusterCutting();
		}
	}
	
	protected class ToggleAddPathAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setting.toggleAddPath();
		}
	}
	
	protected class ToggleAcceptClusteringAction extends AbstractAction {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			setting.toggleAcceptClustering();
		}
	}
}
