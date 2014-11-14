package mayday.wapiti.transformations.impl.readexpression;

import java.util.Collection;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.IntSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentData;
import mayday.wapiti.experiments.base.ExperimentState;
import mayday.wapiti.experiments.generic.locusreadcount.LocusReadCountData;
import mayday.wapiti.transformations.base.AbstractTransformationPlugin;

public class PseudoCount extends AbstractTransformationPlugin {
	
	protected IntSetting pseudo = new IntSetting("Pseudocounts to add",null,1,1,null,true,false);
	
	public PseudoCount() {}

	public boolean applicableTo(Collection<Experiment> exps) {
		for (Experiment e:exps)
			if (!(LocusReadCountData.class.isAssignableFrom(e.getDataClass())))
				return false;
		return true;
	}
	
	public String getApplicabilityRequirements() {
		return "Requires mapped read data.";
	}

	public void compute() {		
		for (Experiment e : transMatrix.getExperiments(this)) {
			ExperimentData ex = transMatrix.getIntermediateData(e);
			transMatrix.setIntermediateData(e, 
					new PseudoCountAddedExperimentData((LocusReadCountData)ex, pseudo.getIntValue())
			);
		}	
	}

	public Setting getSetting() {
		return pseudo;
	}

	protected ExperimentState makeState(ExperimentState inputState) {
		return inputState;
	}
	
	@Override
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".AddPseudoCount", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Add a pseudo count of one read to each feature. This allows, e.g. to take the logarithm afterwards.", 
		"Add pseudo read count");
	}
	
	public String getIdentifier() {
		return "+pseudo";
	}

}
