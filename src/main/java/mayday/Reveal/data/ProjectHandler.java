package mayday.Reveal.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.gui.RevealGUI;
import mayday.Reveal.listeners.DataStorageEvent;
import mayday.Reveal.listeners.DataStorageListener;
import mayday.Reveal.listeners.ProjectEvent;
import mayday.Reveal.listeners.ProjectEventHandler;
import mayday.Reveal.listeners.ProjectEventListener;
import mayday.Reveal.utilities.DateTimeProvider;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.core.DataSet;
import mayday.core.MaydayDefaults;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;
import mayday.tiala.multi.data.viewmodel.NonClosingVisualizer;

/**
 * @author jaeger
 *
 */
public class ProjectHandler implements DataStorageListener {

	private List<DataStorage> dataContainer;
	private HashMap<DataStorage, RevealViewModel> viewModels;
	
	private HashMap<String, Genome> availableGenomes;
	
	//Handling selections
	private Set<SNPList> selectedSNPLists;
	private MetaInformation selectedMetaInformation;
	
	private ProjectEventHandler projectEventHandler;
	
	private DataStorage selectedProject = null;
	
	private RevealGUI gui;
	
	/**
	 * Create a new ProjectHandler
	 */
	public ProjectHandler() {
		dataContainer = new ArrayList<DataStorage>();
		viewModels = new HashMap<DataStorage, RevealViewModel>();
		selectedSNPLists = new HashSet<SNPList>();
		selectedMetaInformation = null;
		availableGenomes = new HashMap<String, Genome>();
	}
	
	public void setGUI(RevealGUI gui) {
		this.gui = gui;
	}
	
	public RevealGUI getGUI() {
		return this.gui;
	}
	
	public void addGenome(Genome genome) {
		this.availableGenomes.put(genome.getName(), genome);
	}
	
	public void removeGenome(Genome genome) {
		this.availableGenomes.remove(genome.getName());
	}
	
	public Genome getGenome(String name) {
		return availableGenomes.get(name);
	}
	
	public Set<String> getAvailableGenomes() {
		return availableGenomes.keySet();
	}
	
	/**
	 * @param data
	 */
	public void fireProjectChanged(DataStorage data) {
		projectEventHandler.fireProjectChanged(data, ProjectEvent.PROJECT_CHANGED);
	}
	
	/**
	 * @param data
	 */
	public void add(DataStorage data) {
		if(containsDataStorage(data.getAttribute().getName())) {
			String currentTime = DateTimeProvider.getCurrentTime();
			data.getAttribute().setName(data.getAttribute().getName() + " (" + currentTime + ")");
			data.getDataSet().setName(data.getDataSet().getName() + " (" + currentTime + ")");
		}
		
		this.dataContainer.add(data);
		data.addDataStorageListener(this);
		projectEventHandler.fireProjectChanged(data, ProjectEvent.PROJECT_ADDED);
	}
	
	/**
	 * @param data
	 */
	public void remove(DataStorage data) {
		if(data == selectedProject) {
			selectedProject = null;
			projectEventHandler.fireProjectChanged(data, ProjectEvent.PROJECT_SELECTION_CHANGED);
		}
		
		this.dataContainer.remove(data);
		this.viewModels.remove(data);
		projectEventHandler.fireProjectChanged(data, ProjectEvent.PROJECT_REMOVED);
	}
	
	/**
	 * @param dataIndex
	 */
	public void remove(int dataIndex) {
		DataStorage data = this.dataContainer.get(dataIndex);
		this.viewModels.remove(data);
		this.dataContainer.remove(dataIndex);
		projectEventHandler.fireProjectChanged(data, ProjectEvent.PROJECT_REMOVED);
	}
	
	/**
	 * @param dataIndex
	 * @return DataStorage object at the specified index
	 */
	public DataStorage get(int dataIndex) {
		return this.dataContainer.get(dataIndex);
	}

	/**
	 * @return the first project in the container
	 */
	public DataStorage getFirst() {
		if(dataContainer.size() > 0) {
			return dataContainer.get(0);
		}
		return null;
	}
	
	/**
	 * @return get the last dataset (most recently added)
	 */
	public DataStorage getLast() {
		if(dataContainer.size() > 0) {
			return dataContainer.get(dataContainer.size() - 1);
		}
		return null;
	}
	
	/**
	 * @return the number of data sets handled by this project handler
	 * 
	 */
	public int numberOfProjects() {
		return this.dataContainer.size();
	}

	/**
	 * @param projectEventHandler
	 */
	public void setProjectEventHandler(ProjectEventHandler projectEventHandler) {
		this.projectEventHandler = projectEventHandler;
	}

	/**
	 * @return the project event handler
	 */
	public ProjectEventHandler getProjectEventHandler() {
		return this.projectEventHandler;
	}
	
	/**
	 * @param lastSelectedProject
	 */
	public void setSelectedProject(DataStorage lastSelectedProject) {
		if(this.selectedProject != lastSelectedProject) {
			this.selectedProject = lastSelectedProject;
			projectEventHandler.fireProjectChanged(this, ProjectEvent.PROJECT_SELECTION_CHANGED);
		}
	}
	
	/**
	 * @return the selected data storage object
	 */
	public DataStorage getSelectedProject() {
		return this.selectedProject;
	}
	
	/**
	 * @param data
	 * @return the viewmodel associated with this project
	 */
	public RevealViewModel getViewModel(DataStorage data) {
		if(this.viewModels.get(data) == null) {
			RevealViewModel model = createNewViewModel(data);
			this.viewModels.put(data, model);
		}
		return this.viewModels.get(data);
	}
	
	private RevealViewModel createNewViewModel(DataStorage project) {
		NonClosingVisualizer ncv = new NonClosingVisualizer();
		LinkedList<ProbeList> pls = new LinkedList<ProbeList>();
		
		String globalName = MaydayDefaults.GLOBAL_PROBE_LIST_NAME;
		DataSet ds = project.getDataSet();
		ProbeList global = ds.getProbeListManager().getProbeList(globalName);
		pls.add(global);
		
		RevealViewModel vm = new RevealViewModel(ncv, ds, pls, project, null, null);
		vm.setTopPrioritySNPList(project.getGlobalSNPList());
		project.getGlobalSNPList().setTopPriority(true);
		ncv.setViewModel(vm);
		
		return vm;
	}

	@Override
	public void dataChanged(DataStorageEvent dse) {
		switch(dse.getChange()) {
		case DataStorageEvent.DATA_CHANGED:
			fireProjectChanged((DataStorage)dse.getSource());
			break;
		case DataStorageEvent.STATTEST_SELECTION_CHANGED:
			break;
		case DataStorageEvent.SNPLIST_SELECTION_CHANGED:
			break;
		case DataStorageEvent.META_INFORMATION_CHANGED:
			fireProjectChanged((DataStorage) dse.getSource());
		}
	}

	public void clear() {
		for(ProjectEventListener l : this.projectEventHandler.getProjectEventListeners()) {
			this.projectEventHandler.removeProjectEventListener(l);
		}
		
		for(DataStorage ds : dataContainer) {
			for(DataStorageListener l : ds.getDataStorageListeners()) {
				ds.removeDataStorageListener(l);
			}
			DataSetManager.singleInstance.removeObject(ds.getDataSet());
		}
		
		this.selectedSNPLists.clear();
		this.dataContainer.clear();
		this.viewModels.clear();
	}

	public List<DataStorage> getProjects() {
		return this.dataContainer;
	}
	
	public void setSelectedSNPLists(Collection<SNPList> snpLists) {
		this.selectedSNPLists.clear();
		this.selectedSNPLists.addAll(snpLists);
		projectEventHandler.fireProjectChanged(this, ProjectEvent.SNP_SELECTION_CHANGED);
	}

	public Set<SNPList> getSelectedSNPLists() {
		return this.selectedSNPLists;
	}

	public void clearSNPListSelection() {
		this.selectedSNPLists.clear();
		projectEventHandler.fireProjectChanged(this, ProjectEvent.SNP_SELECTION_CLEARED);
	}

	public MetaInformation getSelectedMetaInformation() {
		return selectedMetaInformation;
	}
	
	public void setSelectedMetaInformation(MetaInformation selected) {
		this.selectedMetaInformation = selected;
		projectEventHandler.fireProjectChanged(this, ProjectEvent.METAINFO_SELECTION_CHANGED);
	}
	
	private boolean containsDataStorage(String name) {
		for(DataStorage ds : dataContainer)
			if(ds.getAttribute().getName().equals(name))
				return true;
		return false;
	}

	public void setupViewModel(DataStorage data) {
		this.getViewModel(data);
	}
}