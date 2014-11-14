package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3d.AbstractPlot3DPanel;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Grid3DSetting extends GridSetting {
	
	protected BooleanSetting gridVisible = new BooleanSetting("visible", null, true);
	protected IntSetting xIteration = new IntSetting("x iteration", "number of steps on the x-axis", 10);
	protected IntSetting yIteration = new IntSetting("y iteration", "number of steps on the y-axis", 10);
	protected IntSetting zIteration = new IntSetting("z iteration", "number of steps on the z-axis", 10);
	
	/**
	 * @param panel 
	 * @param dimension
	 */
	public Grid3DSetting(AbstractPlot3DPanel panel) {
		super(panel);
		addSetting(gridVisible);
		addSetting(new HierarchicalSetting("Iteration")
		.addSetting(xIteration)
		.addSetting(yIteration)
		.addSetting(zIteration));
	}
	
	/**
	 * @return true , if grid should be visible, else false
	 */
	public boolean getGridVisible() {
		return this.gridVisible.getBooleanValue();
	}
	
	public Grid3DSetting clone() {
		Grid3DSetting g3ds = new Grid3DSetting(panel);
		g3ds.fromPrefNode(this.toPrefNode());
		return g3ds;
	}

	/**
	 * @return the chosen iterations
	 */
	public int[] getIteration() {
		return new int[]{this.xIteration.getIntValue(), this.yIteration.getIntValue(), this.zIteration.getIntValue()};
	}

	/**
	 * @param xIt
	 */
	public void setXIteration(Integer xIt) {
		if(xIt != null) {
			this.xIteration.setIntValue(xIt.intValue());
		}
	}

	/**
	 * @param yIt
	 */
	public void setYIteration(Integer yIt) {
		if(yIt != null) {
			this.yIteration.setIntValue(yIt.intValue());
		}
	}

	/**
	 * @param zIt
	 */
	public void setZIteration(Integer zIt) {
		if(zIt != null) {
			this.zIteration.setIntValue(zIt.intValue());
		}
	}
}
