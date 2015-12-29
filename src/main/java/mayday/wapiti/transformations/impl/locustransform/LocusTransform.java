package mayday.wapiti.transformations.impl.locustransform;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.wapiti.containers.loci.transform.LocusTransformSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class LocusTransform extends AbstractTransformationPlugin {

	protected LocusTransformSetting mySetting = new LocusTransformSetting("Locus Transformation");

	public LocusTransform() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (!e.hasLocusInformation())
				return false;
		return true;
	}

	public String getApplicabilityRequirements() {
		return "Requires experiments with locus information";
	}
	
	public void compute() {		
		// this transform does not change the experimental data
	}

	public LocusTransformSetting getSetting() {
		return mySetting;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return new LocusTransformedExperimentState(inputState, mySetting);
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".TransformLocus", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Change Locus information", 
		"Transform Locus");
	}

}
