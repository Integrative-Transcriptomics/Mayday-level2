package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3d.AbstractPlot3DPanel;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public abstract class GridSetting extends HierarchicalSetting {

	protected AbstractPlot3DPanel panel;
	
	/**
	 * constructor
	 * @param panel 
	 */
	public GridSetting(AbstractPlot3DPanel panel) {
		super("Grid Setting");
		this.panel = panel;
	}
	
	/**
	 * @return iteration
	 */
	public abstract int[] getIteration();
	
	@Override
	public abstract GridSetting clone();
}
