package mayday.vis3.plots.treeviz3;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.gui.GUIUtilities;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluginrunner.ProbeListPluginRunner;
import mayday.core.probelistmanager.ProbeListManager;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.trees.ViewModelProbeSelectionManager;

/**
 * @author Eugen Netz
 */
public class ClusterCuttingProbeSelectionManager extends
		ViewModelProbeSelectionManager implements ClusterCuttingSelectionManager<ProbeList>{
	
	private ArrayList<ProbeList> clusters;

	public ClusterCuttingProbeSelectionManager(ViewModel vm, Layout ly) {
		super(vm, ly);
		this.clusters  = new ArrayList<ProbeList>();
	}
	
	public void selectNodes(List<Edge> edgeList, String prefix, String hierarchicalClusteringName, AnnotationMIO annotation) {
		
		Iterator<Edge> edgeIt = edgeList.iterator();
		ArrayList<ProbeList> probeListSet= new ArrayList<ProbeList>();		
		ArrayList<Probe> oldList = new ArrayList<Probe>();
		oldList.addAll(vm.getProbeLists(false).get(0).toCollection());
		AnnotationMIO newAnnotation = new AnnotationMIO("Clustercutting from " + hierarchicalClusteringName + ". " + annotation.getInfo(), "Clustercutting. " + annotation.getQuickInfo());
		
		DataSet ds = vm.getDataSet();
		
		int nameCounter = 0;
		while(edgeIt.hasNext()) {
			Edge edge = edgeIt.next();
			Collection<Node> nodes = edge.getNode(1).getLeaves(edge);
			Iterator<Node> nodeIt = nodes.iterator();
				
			ProbeList probeList = new ProbeList(ds, false);
			probeList.setName(prefix + " " + nameCounter++);
			
			while(nodeIt.hasNext()) {
				Node node = nodeIt.next();
				Probe probe = (Probe) l.getObject(node);
				
				if (!probeList.contains(probe)) {
					probeList.addProbe(probe);
					this.setSelected(node, true);
				}	
			}
			probeList.setAnnotation(newAnnotation);
			probeListSet.add(probeList);
		}
		
		if (probeListSet.isEmpty())
			return;
			
		Iterator<ProbeList> setIt = probeListSet.iterator();
		while(setIt.hasNext()) {
			ProbeList pl = setIt.next();
			oldList.removeAll(pl.toCollection());
		}
		
		ProbeList oldProbeList = new ProbeList(ds, false);
		oldProbeList.setName(prefix + " " + nameCounter++);
		
		oldProbeList.setProbes(oldList);
		oldProbeList.setAnnotation(newAnnotation);
		
		if (oldProbeList.iterator().hasNext()) {
			probeListSet.add(oldProbeList);
		}
		
		Iterator<ProbeList> probeListIt = probeListSet.iterator();		
		Color[] colors = GUIUtilities.rainbow(nameCounter, 0.75 );
		int counter = 0;
		
		while(probeListIt.hasNext()) {		
			ProbeList pl = probeListIt.next();
			pl.setColor(colors[counter++]);
		}
			
		this.clusters = probeListSet;	
	}
	
	public ViewModel getViewModel() {
		return vm;
	}

	@Override
	public List<ProbeList> getClusters() {
		return this.clusters;
	}
	
	@Override
	public void acceptClustering(String name) {
		ProbeListManager plm = vm.getDataSet().getProbeListManager();
		ProbeListPluginRunner.insertProbeListsIntoProbeListManager(new ArrayList<ProbeList>(), this.clusters, plm, name);
	}
}
