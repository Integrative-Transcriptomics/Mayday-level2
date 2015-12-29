package mayday.exportjs.plugins;

import mayday.core.settings.generic.BooleanHierarchicalSetting;

public abstract class PlotExportSetting extends BooleanHierarchicalSetting {

	public PlotExportSetting(String Name, String Description, boolean Default) {
		super(Name, Description, Default);
	}

	private boolean probesTableInteraction;
	private boolean metaTableInteraction;
	
	public boolean isProbesTableInteraction() {
		return probesTableInteraction;
	}
	
	public void setProbesTableInteraction(boolean probesTableInteraction) {
		this.probesTableInteraction = probesTableInteraction;
	}

	public boolean isMetaTableInteraction() {
		return metaTableInteraction;
	}

	public void setMetaTableInteraction(boolean metaTableInteraction) {
		this.metaTableInteraction = metaTableInteraction;
	}
	
	public abstract String getDescription();

}
