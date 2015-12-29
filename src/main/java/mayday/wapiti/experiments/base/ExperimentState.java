package mayday.wapiti.experiments.base;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.properties.DataProperties;

public interface ExperimentState {
	
	/* data properties describe the content of the experiment */
	public DataProperties getDataProperties();

	/* feature information */
	/* features are Probes, Probe Sets or Read counts */
	public long getNumberOfFeatures();	
	public abstract Iterable<String> featureNames();		
	
	/* locus information*/
	/* this is a very cheap operation */
	public boolean hasLocusInformation();
	/* these are potentially expensive */
	public long getNumberOfLoci();
	public LocusData getLocusData();
	
	/* the class of the experiment data object in this state */
	public Class<? extends ExperimentData> getDataClass();
		
}
