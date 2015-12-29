package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Chart2DSetting extends VisibleAreaSetting {
	
	protected BooleanSetting showXLabels = new BooleanSetting("Show X Labels", null, true);
	protected BooleanSetting showYLabels = new BooleanSetting("Show Y Labels", null, true);
	
	/**
	 * Default constructor
	 * @param w 
	 * @param h
	 */
	public Chart2DSetting(Double w, Double h) {
		super(w, h);
		
		xAxisTitle = new StringSetting("X-Axis Title", null, "Experiment");
		yAxisTitle = new StringSetting("Y-Axis Title", null, "Expression Value");
		
		addSetting(new HierarchicalSetting("X-Axis")
		.addSetting(xAxisTitle)
		.addSetting(showXLabels)
		.addSetting(width));
		addSetting(new HierarchicalSetting("Y-Axis")
		.addSetting(yAxisTitle)
		.addSetting(showYLabels)
		.addSetting(height));
	}
	
	/**
	 * @return true , if labels on x axis should be shown, else false
	 */
	public boolean showXLabels() {
		return this.showXLabels.getBooleanValue();
	}
	
	/**
	 * @return true , if labels on y axis should be shown, else false
	 */
	public boolean showYLabels() {
		return this.showYLabels.getBooleanValue();
	}
	
	public Chart2DSetting clone() {
		Chart2DSetting c2d = new Chart2DSetting(width.getDoubleValue(), height.getDoubleValue());
		c2d.fromPrefNode(this.toPrefNode());
		return c2d;
	}
}
