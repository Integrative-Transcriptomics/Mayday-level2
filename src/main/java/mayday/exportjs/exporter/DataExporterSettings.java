package mayday.exportjs.exporter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import mayday.core.DataSet;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.vis3.ColorProvider;
import mayday.vis3.model.ViewModel;

public class DataExporterSettings {

	DataExporter dataExporter;

	private List<ProbeList> probeLists;
	private DataSet dataSet;

	private Set<Probe> selectedProbes;
	private ColorProvider coloring;
	private int experimentsLength;
	private int ProbeListsLength;
	private double globalMinVal;
	private double globalMaxVal;
	protected ViewModel viewModel;


	private ArrayList<MIGroup> migroup;

	public DataExporterSettings(ViewModel viewModel){
		this.probeLists = viewModel.getProbeLists(false);
		this.dataSet = viewModel.getDataSet();
		this.selectedProbes = viewModel.getSelectedProbes();
		this.coloring = new ColorProvider(viewModel);
		this.experimentsLength = dataSet.getMasterTable().getNumberOfExperiments();
		this.ProbeListsLength = probeLists.size();
		this.globalMinVal = viewModel.getMinimum(null, null);
		this.globalMaxVal = viewModel.getMaximum(null, null);

		this.migroup = new ArrayList<MIGroup>();
		for (MIGroup mg : viewModel.getDataSet().getMIManager().getGroups()) {
			for (Probe pb : viewModel.getProbes()){
				if (mg.contains(pb)) {
					this.migroup.add(mg);
					break;
				}
			}
		}

		this.viewModel = viewModel;
	}

	public int getProbeListsLength() {
		return ProbeListsLength;
	}

	public void setProbeListsLength(int probeListsLength) {
		ProbeListsLength = probeListsLength;
	}

	public double getGlobalMinVal() {
		return globalMinVal;
	}

	public double getGlobalMaxVal() {
		return globalMaxVal;
	}

	public int getExperimentsLength() {
		return experimentsLength;
	}

	public List<ProbeList> getProbeLists() {
		return probeLists;
	}

	public void setProbeLists(List<ProbeList> probeLists) {
		this.probeLists = probeLists;
	}

	public DataSet getDataSet() {
		return dataSet;
	}

	public void setDataSet(DataSet dataSet) {
		this.dataSet = dataSet;
	}

	public ColorProvider getColoring() {
		return coloring;
	}

	public void setColoring(ColorProvider coloring) {
		this.coloring = coloring;
	}

	public Set<Probe> getSelectedProbes() {
		return selectedProbes;
	}

	public void setSelectedProbes(Set<Probe> selectedProbes) {
		this.selectedProbes = selectedProbes;
	}

	public void initDataExporter(){
		this.dataExporter = new DataExporter(this);
	}

	public DataExporter getDataExporter() {
		return dataExporter;
	}

	public ArrayList<MIGroup> getMigroup() {
		return migroup;
	}

	public ViewModel getViewModel() {
		return viewModel;
	}
}
