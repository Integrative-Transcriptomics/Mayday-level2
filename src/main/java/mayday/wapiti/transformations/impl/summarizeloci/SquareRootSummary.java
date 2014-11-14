package mayday.wapiti.transformations.impl.summarizeloci;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.datamode.Unlogged;
import mayday.wapiti.experiments.properties.processing.Normalized;
import mayday.wapiti.experiments.properties.valuetype.AbsoluteExpression;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class SquareRootSummary extends AbstractTransformationPlugin {
	
	public SquareRootSummary() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (!(LocusReadCountData.class.isAssignableFrom(e.getDataClass())))
				return false;
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires mapped read data";
	}

	public void compute() {		
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new SquareRootSummaryExperimentData(e, (LocusReadCountData)ex, transMatrix.getInputState(this, e).getNumberOfFeatures())
			);
		}	
	}

	public Setting getSetting() {
		return null;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		DataProperties dp = inputState.getDataProperties().clone();
		dp.add(new Unlogged(),true);
		dp.add(new AbsoluteExpression(),true);
		dp.add(new Normalized(), true);
		ExperimentState es = new SummaryExperimentState(inputState, dp);
		return es;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".CombineReads.SquareRootArcsin", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Combine read counts into an expression level for a given locus using the \"square root arcsin\" measure.", 
		"Combine Read Counts (square root arcsin) -- sqrt(n) arcsin (sqrt(x/n))");
	}
	
	public String getIdentifier() {
		return "Square Root Arcsin";
	}

}
