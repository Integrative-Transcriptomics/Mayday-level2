package mayday.wapiti.transformations.impl.fakereads;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.locusmap.LocusMap;
import mayday.genetics.locusmap.LocusMapSetting;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.featureexpression.FeatureExpressionData;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.datamode.Unlogged;
import mayday.wapiti.experiments.properties.processing.Raw;
import mayday.wapiti.experiments.properties.valuetype.AbsoluteExpression;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class Array2ReadCount extends AbstractTransformationPlugin {
	
	protected LocusMapSetting locusmap;
	protected IntSetting totalNoReads;
	protected DoubleSetting minExpression;
	protected HierarchicalSetting setting;
	
	public Array2ReadCount() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (!(FeatureExpressionData.class.isAssignableFrom(e.getDataClass())) || !e.hasLocusInformation())
				return false;
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires feature expression data with locus information";
	}


	public void compute() {		
		LocusMap lm = locusmap.getLocusMap();
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new Array2ReadCountData(e, (FeatureExpressionData)ex, lm, totalNoReads.getIntValue(), minExpression.getDoubleValue())
			);
		}	
	}

	public Setting getSetting() {
		if (setting==null) {
			locusmap = new LocusMapSetting();
			totalNoReads = new IntSetting("Total number of reads", 
					"Specify how many reads shall be simulated.\n" +
					"The more reads you allow, the more accurate the simulation will be.", 
					10000000);
			minExpression = new DoubleSetting("Minimal Expression to consider", 
					"The value specified here will be substracted from the gene's expression before\n" +
					"computing the number of reads. This is equivalent to a subtraction background correction.",4d,0d,null,true,false);
			setting = new HierarchicalSetting("Fake Reads").addSetting(minExpression).addSetting(totalNoReads).addSetting(locusmap);
		}
		return setting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		final DataProperties dp = inputState.getDataProperties().clone();
		dp.add(new Unlogged(),true);
		dp.add(new AbsoluteExpression(),true);
		dp.add(new Raw(), true);
		final LocusMap lm = locusmap.getLocusMap();
		ExperimentState es = new AbstractExperimentState(inputState) {

			public Class<? extends ExperimentData> getDataClass() {
				return LocusReadCountData.class;
			}
			
			public long getNumberOfFeatures() {
				return totalNoReads.getIntValue();
			}

			public long getNumberOfLoci() {
				return lm.size();
			}

			public boolean hasLocusInformation() {
				return true;
			}

			public Iterable<String> featureNames() {
				return null;
			}

			public LocusData getLocusData() {
				throw new RuntimeException("TODO: LocusData");
			}
			public DataProperties getDataProperties() {
				return dp;
			}
		};
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".FakeReads", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Simulate read counts from expression values ", 
				"Create simulated reads counts from expression data");
	}
	
	public String getIdentifier() {
		return "Sim. Reads";
	}

}
