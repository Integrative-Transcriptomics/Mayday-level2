package mayday.wapiti.transformations.impl.locustransform;

import mayday.genetics.advanced.LocusData;
import mayday.wapiti.containers.loci.transform.LocusTransformSetting;
import mayday.wapiti.experiments.base.AbstractExperimentState;
import mayday.wapiti.experiments.base.ExperimentState;

public class LocusTransformedExperimentState extends AbstractExperimentState {

	protected LocusTransformSetting setting;
	
	public LocusTransformedExperimentState(ExperimentState previousState, LocusTransformSetting lts) {
		super(previousState);
		setting = lts;
	}

	public LocusData getLocusData() {
		return new LocusTransformedExperimentData( inputState.getLocusData(), setting.getTransformer() );
	}
}
