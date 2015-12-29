package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3d.AbstractPlot2DPanel;
import mayday.vis3d.AbstractPlot3DPanel;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class Grid2DSetting extends GridSetting {

	protected BooleanSetting gridVisible = new BooleanSetting("visible", null, true);
	protected IntSetting xIteration; 
	protected IntSetting yIteration;
	
	/**
	 * Default constructor
	 * @param panel 
	 */
	public Grid2DSetting(AbstractPlot3DPanel panel) {
		super(panel);
		double[] iteration = ((AbstractPlot2DPanel)panel).getBestStartIteration();
		
		addSetting(gridVisible);
		addSetting(new HierarchicalSetting("Iteration")
		.addSetting(xIteration = new IntSetting("x iteration", "step size on the x-axis", (int)iteration[0]))
		.addSetting(yIteration = new IntSetting("y iteration", "step size on the y-axis", (int)iteration[1])));
	}
	
	/**
	 * @return true , if grid should be visible, else false
	 */
	public boolean getGridVisible() {
		return this.gridVisible.getBooleanValue();
	}
	
	/**
	 * @return the chosen x iterations
	 */
	public int getXIteration() {
		return this.xIteration.getIntValue() - 1;
	}
	
	/**
	 * @return the chosen y iteration
	 */
	public int getYIteration() {
		return this.yIteration.getIntValue();
	}
	
	/**
	 * @param xIt
	 */
	public void setXIteration(int xIt) {
		this.xIteration.setIntValue(xIt);
	}
	
	/**
	 * @param yIt
	 */
	public void setYIteration(int yIt) {
		this.yIteration.setIntValue(yIt);
	}
	
	public Grid2DSetting clone() {
		Grid2DSetting g2d = new Grid2DSetting(panel);
		g2d.fromPrefNode(this.toPrefNode());
		return g2d;
	}

	@Override
	public int[] getIteration() {
		return new int[]{this.getXIteration(), this.getYIteration()};
	}
}
