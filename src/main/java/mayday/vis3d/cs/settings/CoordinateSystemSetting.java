package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3d.AbstractPlot3DPanel;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class CoordinateSystemSetting extends HierarchicalSetting {

	protected AbstractPlot3DPanel panel;
	
	/**
	 * @param panel
	 */
	public CoordinateSystemSetting(AbstractPlot3DPanel panel) {
		super("Coordinate System");
		this.panel = panel;
	}
	
	/**
	 * @param panel 
	 * @param string
	 * @param panelVertical
	 * @param b
	 */
	public CoordinateSystemSetting(AbstractPlot3DPanel panel, LayoutStyle panelVertical,
			boolean b) {
		super("Coordinate System", panelVertical, b);
		this.panel = panel;
	}

	/**
	 * @return visible area setting
	 */
	public abstract VisibleAreaSetting getVisibleArea();
	/**
	 * @return grid setting
	 */
	public abstract GridSetting getGridSetting();
	
	@Override
	public abstract CoordinateSystemSetting clone();
	
	public abstract double[] getIteration();
}
