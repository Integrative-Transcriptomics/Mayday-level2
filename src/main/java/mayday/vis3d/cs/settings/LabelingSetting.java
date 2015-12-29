package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class LabelingSetting extends HierarchicalSetting {
	
	protected BooleanSetting xAxesLabels = new BooleanSetting("Show axes labels", null, true);
	
	/**
	 * 
	 */
	public LabelingSetting() {
		super("Labelling", LayoutStyle.PANEL_VERTICAL,true);
		addSetting(xAxesLabels);
	}
	
	/**
	 * @return true if axes should be labeled, else false
	 */
	public boolean getAxesLabeling() {
		return this.xAxesLabels.getBooleanValue();
	}
	
	public LabelingSetting clone() {
		LabelingSetting ls = new LabelingSetting();
		ls.fromPrefNode(this.toPrefNode());
		return ls;
	}
}
