package mayday.exportjs.exporter;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;

import mayday.core.Experiment;
import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.core.meta.MIGroup;
import mayday.vis3.ColorProvider;

public class DataExporter {

	DataExporterSettings dataExporterSettings;

	public DataExporter(DataExporterSettings dataExporterSettings){
		this.dataExporterSettings = dataExporterSettings;
	}

	
	public String getString() {
		String data = "var globalMinVal = " + this.dataExporterSettings.getGlobalMinVal() + ";\n"
		+ "var globalMaxVal = " + this.dataExporterSettings.getGlobalMaxVal() + ";\n\n"
		+ "var experimentsLength = " + this.dataExporterSettings.getExperimentsLength() + ";\n"
		+ "var probeListsLength = " + this.dataExporterSettings.getProbeListsLength() + ";\n\n"
		+ createExperimentsString()
		+ createProbeListsString()
		+ createMetaHeader();
		return data;
	}
	
	private String createMetaHeader() {
		String result = "var metaHeader = [";
		for (MIGroup mg : this.dataExporterSettings.getMigroup()){
			result += "\"" + mg.getName() + "\", ";
		}
		result = cutLastTwoChars(result);
		return result + "];\n\n";
	}
	

	/* Struktur der Experiment-Daten in JavaScript
	 * 
	 * var experiments = ["experiment1", "experiment2", ...]; 
	 */
	private String createExperimentsString(){
		String result = "var experiments = [";
		Iterator<Experiment> it = this.dataExporterSettings.getDataSet().getMasterTable().getExperiments().iterator();
		while(it.hasNext()){
			Experiment e = it.next();
			result += "\"" + e.getName() + "\"";
			result += ", ";
		}
		result = cutLastTwoChars(result);
		result += "];\n\n";
		return result;
	}


	/* Struktur der Probes-Daten in JavaScript
	 * 
	 * var probeLists = [
	 * 			{name: "ProbeList1", color: "rgb(0,0,0)", probes: [{name: "probe1", values: [1,2,4]}, {name: "probe2", values: [3,4,5]}]},
	 *          {name: "ProbeList2", color: "rgb(0,0,0)", probes: [{name: "probe3", values: [1,2,4]}, {name: "probe4", values: [3,4,5]}]},
	 *          {name: "ProbeList3", color: "rgb(0,0,0)", probes: [{name: "probe4", values: [1,2,4]}, {name: "probe5", values: [3,4,5]}]}
	 *          ];
	 */

	private String createProbeListsString(){
		String result = "var probeLists = [";
		ListIterator<ProbeList> it = this.dataExporterSettings.getProbeLists().listIterator(this.dataExporterSettings.getProbeLists().size());
		
		while(it.hasPrevious()){
			result += getProbeListString(it.previous(), this.dataExporterSettings.getColoring());
			result += ", \n";
		}
		
		result = cutLastThreeChars(result);
		result += "];\n\n";
		return result;
	}

	private String getProbeListString(ProbeList probeList, ColorProvider coloring){
		String result = "{\nname: " + '"' + probeList.getName() + '"'
		+ ", \nminVals:  " + getProbeListMinValsString(probeList, this.dataExporterSettings.getExperimentsLength())
		+ ", \nmaxVals: " + getProbeListMaxValsString(probeList, this.dataExporterSettings.getExperimentsLength())
		+ ", \nfirstQuartiles: " + getQuartilesString(probeList, 1)
		+ ", \nthirdQuartiles: " + getQuartilesString(probeList, 3)
		+ ", \nmedians: " + getMediansString(probeList)
		+ ", \nmeans: " + getMeansString(probeList)
		+ ", \nprobes: ";
		Set<Probe> probes = probeList.getAllProbes();
		result += getProbesString(probes, coloring);
		result += "}";
		return result;
	}

	private String getProbesString(Set<Probe> probes, ColorProvider coloring){
		String result = "[";
		Iterator <Probe> it = probes.iterator();
		while(it.hasNext()){
			Probe probe = it.next();
			result += getProbeString(probe, coloring);
			result += ", \n";
		}
		result = cutLastThreeChars(result);
		result += "]";
		return result;
	}

	private String getProbeString(Probe probe, ColorProvider coloring){
		String result = "{name: " + "\"" + probe.getDisplayName() + "\"";
		int red = coloring.getColor(probe).getRed();
		int green = coloring.getColor(probe).getGreen();
		int blue = coloring.getColor(probe).getBlue();
		result += ", color: \"rgb(" + red + ',' + green + ',' + blue + ")\"";
		result += ", selected: " + isSelectedProbe(probe);
		result += ", values: ";
		double[] values = dataExporterSettings.getViewModel().getProbeValues(probe);
		result += getProbeValuesString(values);
		result += ", meta: ";
		result += getMetaString(probe);
		result += "}";
		return result;
	}

	private String getMetaString(Probe probe) {
		String result = "[";
		for(MIGroup mg : this.dataExporterSettings.getMigroup()){
			Object vs = mg.getMIO(probe);
			result += "\"" + (vs!=null?vs:"") + "\", ";
		}
		result = cutLastTwoChars(result);
		return result + "]";
	}

	private String getProbeValuesString(double[] values){
		String result = "[";
		int len = values.length;
		for(int i=0; i<len; i++){
			result += values[i];
			result += ", ";
		}
		result = cutLastTwoChars(result);
		result += "]";
		return result;
	}

	private String cutLastThreeChars(String s){
		if (s.endsWith(", \n")){
			s = s.substring(0, s.length()-3);
		}
		return s;
	}

	private String cutLastTwoChars(String s){
		if (s.endsWith(", ")){
			s = s.substring(0, s.length()-2);
		}
		return s;
	}

	private boolean isSelectedProbe(Probe probe){
		boolean result = false;
		Iterator<Probe> it = this.dataExporterSettings.getSelectedProbes().iterator();
		while(it.hasNext() && result == false){
			Probe p = it.next();
			if (probe.equals(p)){
				result = true;
			}
		}
		return result;
	}
	
	private String getProbeListMinValsString(ProbeList probeList, int experimentsLength){
		String result = "[";
		for(int i = 0; i < experimentsLength; i++){
			result += probeList.getMinValue(i) + ", ";
		}
		result = cutLastTwoChars(result);
		result += "]";
		return result;
	}
	
	private String getProbeListMaxValsString(ProbeList probeList, int experimentsLength){
		String result = "[";
		for(int i = 0; i < experimentsLength; i++){
			result += probeList.getMaxValue(i) + ", ";
		}
		result = cutLastTwoChars(result);
		result += "]";
		return result;
	}
	
	private String getQuartilesString(ProbeList probeList, int n){
		String result = "[";
		double[] quartiles = probeList.getQuartile(n).getValues();
		for(int i = 0; i < quartiles.length; i++){
			result += quartiles[i] + ", ";
		}
		result = cutLastTwoChars(result);
		result += "]";
		return result;
	}
	
	private String getMediansString(ProbeList probeList){
		String result = "[";
		double[] medians = probeList.getMedian().getValues();
		for(int i = 0; i < medians.length; i++){
			result += medians[i] + ", ";
		}
		result = cutLastTwoChars(result);
		result += "]";
		return result;
	}
	
	private String getMeansString(ProbeList probeList){
		String result = "[";
		double[] means = probeList.getMean().getValues();
		for(int i = 0; i < means.length; i++){
			result += means[i] + ", ";
		}
		result = cutLastTwoChars(result);
		result += "]";
		return result;
	}
}
