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
import mayday.wapiti.transformations.base.Transformation;

public class SchizophrenicExperiment implements Experiment {

	protected Experiment base;
	protected TransMatrix TM;
	
	public SchizophrenicExperiment(Experiment Base, TransMatrix tm) {
		base=Base;
		TM = tm;
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
		return TM;
	}

	public final long getNumberOfFeatures() {
		return getCurrentState().getNumberOfFeatures();
	}

	public final long getNumberOfLoci() {
		return getCurrentState().getNumberOfLoci();
	}

	/** this is a very cheap operation */
	public final boolean hasLocusInformation() {
		return getCurrentState().hasLocusInformation();
	}
	
	public final ExperimentState getCurrentState() {
  		Transformation t = TM.getLastTransformation(this);
		if (t==null) {
			return base.getInitialState();
		}
		return t.getExperimentState(this);
	}

	public final DataProperties getDataProperties() {
		return getCurrentState().getDataProperties();
	}

	public Iterable<String> featureNames() {
		return getCurrentState().featureNames();
	}

	public Class<? extends ExperimentData> getDataClass() {
		return getCurrentState().getDataClass();
	}

	public LocusData getLocusData() {
		return getCurrentState().getLocusData();
	}

	@SuppressWarnings("unchecked")
	public AbstractExperimentSerializer getSerializer() {
		// will never be serialized!
		throw new RuntimeException("SchizophrenicExperiments are not meant to be serialized");
	}

	@Override
	public List<MIGroup> getAnnotations() {
		return base.getAnnotations();
	}

}
