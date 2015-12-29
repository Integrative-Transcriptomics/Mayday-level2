package mayday.wapiti.experiments.impl.legacy;

import mayday.core.settings.Setting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.wapiti.experiments.base.Experiment;
import mayday.wapiti.experiments.base.ExperimentSetting;
import mayday.wapiti.experiments.properties.DataProperties;
import mayday.wapiti.experiments.properties.PropertyParticle;
import mayday.wapiti.experiments.properties.channels.ChannelCount;



public class DataSetExperimentSetting extends ExperimentSetting {
		
	protected DataModeSetting mode;
	
	public DataSetExperimentSetting(Experiment e, String exname) {
		super(e, exname);
		addSetting(mode = new DataModeSetting());
	}
	
	@SuppressWarnings("unchecked")
	public DataProperties getDataProperties() {
		DataProperties f = new DataProperties();
		for (Setting s : mode.getChildren())
			f.add(((ObjectSelectionSetting<PropertyParticle>)s).getObjectValue());
		f.add(new ChannelCount(1, null));
		return f;
	}	
	
	public DataSetExperimentSetting clone() {
		DataSetExperimentSetting cs = new DataSetExperimentSetting(e, e.getName());
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}

	
}
