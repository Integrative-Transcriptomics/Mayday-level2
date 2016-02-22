package mayday.vis3.plots.treeviz3;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mayday.clustering.hierarchical.TreeInfo;
import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluma.AbstractPlugin;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;
import mayday.core.structures.trees.painter.TreePainter;
import mayday.core.structures.trees.tree.Edge;
import mayday.vis3.PlotPlugin;
import mayday.vis3.components.PlotWithLegendAndTitle;
import mayday.vis3.gui.PlotWindow;
import mayday.vis3.model.ViewModel;
import mayday.vis3.model.Visualizer;
import mayday.vis3.plots.multiprofile.MultiProfilePlot;
import mayday.vis3.plots.silhouette.SilhouettePlot;

/**
 * @author Eugen Netz
 */
public class ClusterCuttingDelegate extends WindowAdapter {

	private ClusterCuttingSetting setting;
	private TreePainter tPainter;
	private TreeInfo treeInfo;
	private ClusterCuttingSelectionManager<?> selManager;
	private ArrayList<Edge> edgeList;
	private ArrayList<TreeViewModelLinker> vmLinkers;
	private ArrayList<PlotWindow> plots;
	
	public ClusterCuttingDelegate(ClusterCuttingSetting setting, TreePainter tPainter, ViewModel viewModel, TreeInfo treeInfo) {
		this.setting = setting;
		this.tPainter = tPainter;
		this.treeInfo = treeInfo;
		
		if(isProbeTree()) {
			this.selManager = new ClusterCuttingProbeSelectionManager(viewModel, tPainter.getScreenLayout());
		} else {
			this.selManager = new ClusterCuttingExpSelectionManager(viewModel, tPainter.getScreenLayout());
		}
		this.edgeList = null;
		
		this.plots = new ArrayList<PlotWindow>();
		this.vmLinkers = new ArrayList<TreeViewModelLinker>();
	}
	
	/**
	 * Handles the cutting of the tree by the paths, by calling functions,
	 * that search for Edges and generate a partitioning clustering.
	 * @param pathList The list of painted cutting paths
	 */
	public void handleCuttingPath(ArrayList<GeneralPath> pathList) {	
		tPainter.getScreenLayout().clearSelected();
		edgeList = new ArrayList<Edge>();

		for (GeneralPath path : pathList) {
			double[] point = new double[6];
			PathIterator iterator = path.getPathIterator(null);
			iterator.currentSegment(point);

			int oldPointX = (int) point[0];
			int oldPointY = (int) point[1];
			iterator.next();

			while (!iterator.isDone()) {
				iterator.currentSegment(point);
				int pointX = (int) point[0];
				int pointY = (int) point[1];

				searchEdges(oldPointX, oldPointY, pointX, pointY);

				oldPointX = pointX;
				oldPointY = pointY;

				iterator.next();
			}
		}
		
		String hierarchicalClusteringName = selManager.getViewModel().getProbeLists(false).get(0).getName();
		if(setting.getClusteringName().equals("") || setting.getClusteringName() == null) {
			setting.setClusteringName("Clustercutting from " + hierarchicalClusteringName);
		}
		AnnotationMIO annotation = selManager.getViewModel().getProbeLists(false).get(0).getAnnotation();
		String prefix = setting.getClusterPrefix();
		
		selManager.selectNodes(edgeList, prefix, hierarchicalClusteringName, annotation);
		if(isProbeTree()) {
			openPlot(false);
		}
	}
	
	/**
	 * @return TRUE, if the tree is a clustering of ProbeLists and FALSE if it is a clustering of Experiments
	 */
	private boolean isProbeTree() {		
		return (!treeInfo.getSettings().isMatrixTransposed());
	}
	
	/**
	 * Searches for edges, that are cut by one cutting path
	 * @param startX The X-Coordinate of the starting point of the path
	 * @param startY The Y-Coordinate of the starting point of the path
	 * @param endX The X-Coordinate of the ending point of the path
	 * @param endY The Y-Coordinate of the ending point of the path
	 */
	private void searchEdges(int startX, int startY, int endX, int endY) {

		int centerX = computeCenter(startX, endX);
		int centerY = computeCenter(startY, endY);

		Collection<Edge> edges = tPainter.getScreenLayout().getRoot().postorderEdgeList();
		Edge edge = null;
		double oldD = 3;

		for (Edge e : edges) {
			double newD = tPainter.getScreenLayout().getLayout(e).getPainter()
					.distance(e, tPainter.getScreenLayout(), centerX, centerY);

			if (newD <= oldD) {
				oldD = newD;
				edge = e;
			}
		}

		if ((edge != null) && (!edgeList.contains(edge))) {
//			TODO Selection doesn't work right with Experiment Tree
			tPainter.getScreenLayout().setSelected(edge, true);
			System.out.println("ClusterCuttingDelegate: Edge Added, Selected Tree Parts: " + tPainter.getScreenLayout().getSelected().size());
			edgeList.add(edge);
		}

		if (Math.abs(centerX - startX) >= 3 || Math.abs(centerY - startY) >= 3) {
			searchEdges(startX, startY, centerX, centerY);
			searchEdges(centerX, centerY, endX, endY);
		}
	}
			
	/**
	 * Computes the position of the middle between two one-dimensional Values
	 * @param start
	 * @param end
	 * @return The middle value
	 */
	private int computeCenter(int start, int end) {
		int center;
		if (start <= end) {
			center = start + (end - start) / 2;
		} else {
			center = end + (start - end) / 2;
		}
		return center;
	}

	/**
	 * Closes all temporary Plots, previously opened by this function and opens new plots 
	 * used by ProbeList clusterings (MultiProfilePlot and/or SilhouettePlot) according to the settings
	 * @param calledBySetting Indicates, whether the function was called because of a change in Settings
	 */
	public void openPlot(boolean calledBySetting) {		
		List<ProbeList> probeListSet = (List<ProbeList>) selManager.getClusters();
		if (!probeListSet.isEmpty()) {
			if (!calledBySetting) {
				for (PlotWindow plotWindow : plots) {
					plotWindow.closePlot();
				}
				plots.clear();
			}

			if (setting.multiProfilePlotChanged() || !calledBySetting) {
				if (setting.getMultiProfilePlot()) {
					openSpecificPlot(MultiProfilePlot.class, probeListSet);
				}
			}
			if (setting.silhouettePlotChanged() || !calledBySetting) {
				if (setting.getSilhouettePlot()) {
					openSpecificPlot(SilhouettePlot.class, probeListSet);
				}
			}
			setting.setChosenPlots();
		}
	}
	
	/**
	 * Opens a plot.
	 * @param plotClass The Type of the plot
	 * @param probeListSet The ProbeLists used by the plot
	 */
	public void openSpecificPlot(Class<? extends AbstractPlugin> plotClass, List<ProbeList> probeListSet) {

		PluginInfo pli = PluginManager.getInstance().getPluginFromClass(plotClass);
		ViewModel treeVM = selManager.getViewModel();
		PlotWithLegendAndTitle c = (PlotWithLegendAndTitle) ((PlotPlugin) pli.newInstance()).getComponent();
		if (c == null)
			return;

		DataSet ds = probeListSet.get(0).getDataSet();
		Visualizer newVis = Visualizer.createWithPlot(ds, probeListSet, c);
		ViewModel newVM = newVis.getViewModel();
		PlotWindow plotWindow = (PlotWindow) newVis.getMembers().iterator().next();
		
		plotWindow.addWindowListener(this);

		vmLinkers.add(new TreeViewModelLinker(treeVM, newVM));
		plots.add(plotWindow);				
	}
	
	/**
	 * Accepts the temporary clustering and makes it permanent
	 */
	public void acceptClustering() {
			selManager.acceptClustering(setting.getClusteringName());
	}

	/**
	 * Breaks the synchronizing links between Visualizers, if there are no more plots open to synchronize
	 */
	@Override
	public void windowClosed(WindowEvent eve) {
		TreeViewModelLinker[] linkers = new TreeViewModelLinker[0];
		linkers = vmLinkers.toArray(linkers);
		
		for (int i = 0; i < linkers.length; i++) {
				if (linkers[i].hasNoPlots()) {
					linkers[i].breakLink();
					vmLinkers.remove(linkers[i]);
				}
		}
	}

	@Override
	public void windowClosing(WindowEvent eve) {
		plots.remove((PlotWindow) eve.getWindow());
	}		
}
