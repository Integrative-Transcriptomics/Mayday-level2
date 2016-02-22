package mayday.vis3.plots.treeviz3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import mayday.core.ClassSelectionModel;
import mayday.core.DataSet;
import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.meta.types.AnnotationMIO;
import mayday.core.pluginrunner.DataSetPluginRunner;
import mayday.core.structures.trees.layout.Layout;
import mayday.core.structures.trees.tree.Edge;
import mayday.core.structures.trees.tree.Node;
import mayday.vis3.plots.treeviz3.classselection.ClassDialog;
import mayday.vis3.model.ViewModel;
import mayday.vis3.plots.trees.ViewModelExperimentSelectionManager;

/**
 * @author Eugen Netz
 */
public class ClusterCuttingExpSelectionManager extends
		ViewModelExperimentSelectionManager  implements ClusterCuttingSelectionManager<DataSet>{

	private ClassSelectionModel csm;	
	private ArrayList<DataSet> clusters;
	
	public ClusterCuttingExpSelectionManager(ViewModel vm, Layout ly) {
		super(vm, ly);
		csm = null;
		this.clusters  = new ArrayList<DataSet>();
	}
	
	@Override
	public void selectNodes(List<Edge> edgeList, String prefix, String hierarchicalClusteringName, AnnotationMIO annotation) {		
		Collections.sort(edgeList, new EdgeComparator());
		ArrayList<ArrayList<Object>> expListSet = getLeafObjects(edgeList);
		
		if (expListSet.isEmpty())
			return;
		
		ArrayList<Experiment> oldList = new ArrayList<Experiment>();
		oldList.addAll(vm.getDataSet().getMasterTable().getExperiments());
			
		Iterator<ArrayList<Object>> setIt = expListSet.iterator();
		while(setIt.hasNext()) {
			oldList.removeAll(setIt.next());
		}
		
		ArrayList<Object> restExpList = new ArrayList<Object>();
		
		restExpList.addAll(oldList);
		
		if (restExpList.iterator().hasNext()) {
			expListSet.add(restExpList);
		}		
		
		csm = new ClassSelectionModel();
		DataSet ds = vm.getDataSet();
		
		int expCounter = 0;
		for(Experiment exp : ds.getMasterTable().getExperiments()) {
			int cnCounter = 0;
			for(ArrayList<Object> expList : expListSet) {
				String cn = prefix + " " + cnCounter;
				if(!(csm.getClassNames().contains(cn))) {
					csm.addClass(cn);
				}	
				if(expList.contains(exp)) {
					if(!csm.getObjectNames().contains(exp.getName())) {
						csm.addObject(exp.getName(), cn);
						csm.setClass(expCounter, cn);
					}
				}
				cnCounter++;
			}
			expCounter++;
		}
		
		//Open ClassDialog
		ClassDialog cd = new ClassDialog(csm);
		cd.setModal(true);
		cd.setVisible(true);
		
		ArrayList<DataSet> dataSets = new ArrayList<DataSet>();
		
		//Create DataSets
		for (String cn : csm.getClassNames()) {
			DataSet d = new DataSet(cn);
			dataSets.add(d);
			int cf = csm.getClassCount(cn);
			d.getMasterTable().setNumberOfExperiments(cf);
			List<Integer> li = csm.toIndexList(cn);
			
			//Set Experiment names
			for (int i = 0; i != li.size(); ++i) {
				d.getMasterTable().setExperimentName(i,
						ds.getMasterTable().getExperimentName(li.get(i)));
			}
			//Set Probes and Values
			for (Probe pb : ds.getMasterTable().getProbes().values()) {
				Probe pbx = new Probe(d.getMasterTable());
				pbx.setName(pb.getName());
				for (int i = 0; i != li.size(); ++i)
					pbx.setValue(pb.getValue(li.get(i)), i);
				d.getMasterTable().addProbe(pbx);
			}
		}
		this.clusters = dataSets;
			
		//Accept clustering
		if (cd.getApplyClustering()) {
			acceptClustering(null);
		}
		
	}

	private ArrayList<ArrayList<Object>> getLeafObjects(Collection<Edge> edgeList) {
		Iterator<Edge> edgeIt = edgeList.iterator();
		ArrayList<ArrayList<Object>> objectListSet= new ArrayList<ArrayList<Object>>();
		
		while(edgeIt.hasNext()) {
			Edge edge = edgeIt.next();
			Collection<Node> nodes = edge.getNode(1).getLeaves(edge);
			Iterator<Node> nodeIt = nodes.iterator();
			ArrayList<Object> objectList = new ArrayList<Object>();
			
			while(nodeIt.hasNext()) {
				Node node = nodeIt.next();
				Object obj = l.getObject(node);
				
				if (!objectList.contains(obj)) {
					objectList.add(obj);
					this.setSelected(node, true);
				}	
			}
			objectListSet.add(objectList);
		}
		
		return objectListSet;
	}
	
	public ViewModel getViewModel() {
		return vm;
	}

	@Override
	public List<DataSet> getClusters() {
			return this.clusters;
	}
	
	@Override
	public void acceptClustering(String name) {
			DataSetPluginRunner.insertIntoDataSetManager(this.clusters);		
	}

	
	private class EdgeComparator implements Comparator<Edge> {

		@Override
		public int compare(Edge edge1, Edge edge2) {
			return String.CASE_INSENSITIVE_ORDER.compare(edge1.toString(), edge2.toString());
		}

	}
}
