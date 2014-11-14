package mayday.wapiti.experiments.impl.wiggle;

import mayday.core.math.average.IAverage;
import mayday.core.settings.typed.AveragingSetting;
import mayday.wapiti.experiments.base.ExperimentSetting;

public class WiggleExperimentSetting extends ExperimentSetting {

	protected AveragingSetting averager;
	
	public WiggleExperimentSetting(WiggleExperiment e, String exname) {
		super(e, exname);
		addSetting(averager = new AveragingSetting());
	}

	public IAverage getSummaryFunction() {
		return averager.getSummaryFunction();
	}

	public WiggleExperimentSetting clone() {
		WiggleExperimentSetting wgs = new WiggleExperimentSetting((WiggleExperiment)e, getExperimentName());
		wgs.fromPrefNode(this.toPrefNode());
		return wgs;
	}
	
}
