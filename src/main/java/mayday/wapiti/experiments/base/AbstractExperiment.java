package mayday.wapiti.experiments.base;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import mayday.core.meta.MIGroup;
import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.gui.ExperimentPanel;
import mayday.wapiti.transformations.base.Transformation;
import mayday.wapiti.transformations.matrix.TransMatrix;

public abstract class AbstractExperiment implements Experiment {

	protected final String sourceDescription;
	protected ExperimentState initialState;
	protected final TransMatrix transMatrix;
	protected ExperimentPanel gui;
	
	protected LinkedList<MIGroup> additionalData;
	
	public AbstractExperiment(String sourceDescription, ExperimentState initialState, TransMatrix transMatrix) {
		this.sourceDescription = sourceDescription;
		this.transMatrix = transMatrix;
		setInitialState(initialState);
	}
	
	
	public void setInitialState(ExperimentState initialState) {
		this.initialState = initialState;
	}
	
/* State-independent "static" properties **************************/
	
	public final String getSourceDescription() {
		return sourceDescription;
	}

	public final ExperimentPanel getGUIElement() {
		if (gui==null)
			gui = new ExperimentPanel(this);
		return gui;
	}

	public final TransMatrix getTransMatrix() {
		return transMatrix;
	}
	
	public final int compareTo(Experiment o) {
		return new Integer(hashCode()).compareTo(o.hashCode());
	}
	
	public final String toString() {
		return getIdentifier();
	}
	
	public final String getIdentifier(Experiment e) {
		// ignore experiment
		return getIdentifier();
	}
	
	public ExperimentState getInitialState() {
		return initialState;
	}
	
	public final String getName() {
		return getSetting().getExperimentName();
	}
	
	public final void setName(String newName) {
		getSetting().setExperimentName(newName);
	}
	
	public final List<MIGroup> getAnnotations() {
		if (additionalData==null)
			return Collections.<MIGroup>emptyList();
		else
			return additionalData;
	}
	
	public void addAnnotation(MIGroup annotation) {
		if (additionalData==null)
			additionalData = new LinkedList<MIGroup>();
		additionalData.add(annotation);
	}
	 
/* state-dependent properties *************************************/
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
		// two options:
		// a) during set-up this is the state after the last transform in the matrix
		if (!transMatrix.isExecuting()) {
			Transformation t = transMatrix.getLastTransformation(this);
			if (t==null) {
				return getInitialState();
			}
			return t.getExperimentState(this);
		} else {	
			// b) during run-time this is the state after the last executed transform
			return transMatrix.getIntermediateState(this);
		}
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
	

}
