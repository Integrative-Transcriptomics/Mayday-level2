package mayday.wapiti.experiments.impl.clone;

import java.util.LinkedList;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.experiments.base.AbstractExperiment;
import mayday.wapiti.experiments.base.AbstractExperimentSerializer;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.transformations.matrix.TransMatrix;

public class ClonedExperiment extends AbstractExperiment {

	protected Experiment base;
	protected ExperimentSetting setting;
	protected int baseIndex=-1;
	protected String cachedName;
	
	public ClonedExperiment(Experiment base) {
		super(base.getSourceDescription(), base.getInitialState(), base.getTransMatrix());
		this.base = base;
	}
	
	public ClonedExperiment(TransMatrix tm, String sdesc, int baseIndex) {
		super(sdesc, new ExperimentState() {
			public boolean hasLocusInformation() {return false; }
			public long getNumberOfLoci() { return 0; }
			public long getNumberOfFeatures() {return 0; }
			public LocusData getLocusData() { return null; }
			public DataProperties getDataProperties() { 
				return new DataProperties();
			}
			public Class<? extends ExperimentData> getDataClass() {return ExperimentData.class; }
			public Iterable<String> featureNames() {
				return new LinkedList<String>();
			}
		}, tm);
		this.baseIndex = baseIndex;
	}
	
	@Override
	public String getIdentifier() {
		initBase();
		if (base==null)
			return "Clone missing its base experiment";
		return base.getIdentifier();
	}

	@Override
	public ExperimentData getInitialData() {
		initBase();
		if (base==null)
			return new ExperimentData(){};
		return base.getInitialData();
	}

	@SuppressWarnings("unchecked")
	@Override
	public AbstractExperimentSerializer getSerializer() {
		return new ClonedExperimentSerializer();
	}

	@Override
	public ExperimentState getInitialState() {
		initBase();
		if (base!=null)
			return base.getInitialState();
		return initialState;
	}
	
	@Override
	public ExperimentSetting getSetting() {
		initBase();
		return (setting!=null)?
				setting
				:
				(setting=new ExperimentSetting(this, (base==null)?"Cloned experiment":base.getName()));
	}
	
	public int getBaseIndex() {
		if (baseIndex==-1) 
			return base.getGUIElement().index();  
		return baseIndex;
	}
	
	protected boolean noreentry = false;
	
	protected void initBase() {
		if (base==null && !noreentry) {
			noreentry = true;
			LinkedList<Experiment> le = new LinkedList<Experiment>(getTransMatrix().getExperiments());
			if (le.size()>baseIndex && baseIndex>-1) {
				base = le.get(baseIndex);
				if (base==this) 
					base = null;
				else
					baseIndex = -1;
			}
			noreentry = false;
		}
		if (base!=null) {
			if (!getTransMatrix().containsExperiment(base)) {
				System.out.println("Cloned experiment is missing its source: "+base.getIdentifier());
				base = null;
			}

		}
	}

}
