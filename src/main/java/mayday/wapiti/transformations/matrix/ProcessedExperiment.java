package mayday.wapiti.transformations.matrix;

import java.util.List;

import mayday.core.meta.MIGroup;
import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.gui.ExperimentPanel;

/** ProcessedExperiment contains the result of processing the transmatrix for a given experiment.
 * It contains the original data as well as the result of applying the transformations.
 * @author battke
 *
 */
public class ProcessedExperiment implements Experiment {

	protected Experiment base;
	protected ExperimentData data;
	
	public ProcessedExperiment(Experiment Base, ExperimentData Data) {
		base=Base;
		data=Data;
	}
	
	public ExperimentData getOutputData() {
		return data;
	}
	
	public ExperimentData getInitialData() {
		return base.getInitialData();	
	}

	public ExperimentSetting getSetting() {
		return base.getSetting();
	}

	public String getIdentifier() {
		return base.getIdentifier();
	}

	public String getIdentifier(Experiment e) {
		return base.getIdentifier(e);
	}	
	
	public String getName() {
		return base.getName();
	}
	
	public void setName(String newName) {
		base.setName(newName);
	}
	
	public ExperimentState getCurrentState() {
		return base.getCurrentState();
	}

	public ExperimentPanel getGUIElement() {
		return base.getGUIElement();
	}

	public ExperimentState getInitialState() {
		return base.getInitialState();
	}

	public String getSourceDescription() {
		return base.getSourceDescription();
	}

	public int compareTo(Experiment o) {
		return base.compareTo(o);
	}

	public TransMatrix getTransMatrix() {
		return base.getTransMatrix();
	}

	public Iterable<String> featureNames() {
		return base.featureNames();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return base.getDataClass();
	}

	public DataProperties getDataProperties() {
		return base.getDataProperties();
	}

	public LocusData getLocusData() {
		return base.getLocusData();
	}

	public long getNumberOfFeatures() {
		return base.getNumberOfFeatures();
	}

	public long getNumberOfLoci() {
		return base.getNumberOfLoci();
	}

	public boolean hasLocusInformation() {
		return base.hasLocusInformation();
	}

	@SuppressWarnings("unchecked")
	public AbstractExperimentSerializer getSerializer() {
		// will never be serialized!
		throw new RuntimeException("ProcessedExperiments are not meant to be serialized");
	}

	@Override
	public List<MIGroup> getAnnotations() {
		return base.getAnnotations();
	}

}
