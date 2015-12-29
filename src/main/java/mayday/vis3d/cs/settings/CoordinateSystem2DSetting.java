package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3d.AbstractPlot3DPanel;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class CoordinateSystem2DSetting extends CoordinateSystemSetting {

	protected Grid2DSetting g2dSetting;
	protected Chart2DSetting chartSetting;
	
	protected IntSetting fontSize;
	protected BooleanSetting verticalXLabels;
	
	private int oldFontSize = 24;
	private double oldScale = 0.4;
	
	/**
	 * Default constructor
	 * @param dimension 
	 * @param panel 
	 */
	public CoordinateSystem2DSetting(AbstractPlot3DPanel panel) {
		super(panel, LayoutStyle.PANEL_VERTICAL, true);
		
		addSetting(new HierarchicalSetting("Layout", LayoutStyle.PANEL_HORIZONTAL, true)
		.addSetting(g2dSetting = new Grid2DSetting(panel))
		.addSetting(chartSetting = new Chart2DSetting(new Double(400), new Double(200))))
		.addSetting(fontSize = new IntSetting("Label Size", null, oldFontSize))
		.addSetting(verticalXLabels = new BooleanSetting("Vertical x-axis labels", null, true));
		
		setChildrenAsSubmenus(false);
	}
	
	/**
	 * @return true, if x-axis labels should be drawn vertically, else false
	 */
	public boolean verticalXLabels() {
		return this.verticalXLabels.getBooleanValue();
	}
	
	/**
	 * @return font size
	 */
	public int getFontSize() {
		return this.fontSize.getIntValue();
	}
	
	/**
	 * @return font scale factor
	 */
	public double getFontScale() {
		if(oldFontSize != this.getFontSize()) {
			double scale = ((double)this.getFontSize() / (double)oldFontSize) * oldScale;
			oldFontSize = this.getFontSize();
			oldScale = scale;
		}
		return oldScale;
	}
	
	/**
	 * @return iteration
	 */
	public double[] getIteration() {
		return new double[]{g2dSetting.getXIteration(), g2dSetting.getYIteration()};
	}
	
	/**
	 * @return chart setting
	 */
	public Chart2DSetting getChartSetting() {
		return this.chartSetting;
	}
	
	public double getXIteration() {
		return getChartSetting().getWidth() / getIteration()[0];
	}
	
	/**
	 * @return grid setting
	 */
	public Grid2DSetting getGridSetting() {
		return this.g2dSetting;
	}
	
	public CoordinateSystem2DSetting clone() {
		CoordinateSystem2DSetting cs2d = new CoordinateSystem2DSetting(panel);
		cs2d.fromPrefNode(this.toPrefNode());
		return cs2d;
	}

	@Override
	public VisibleAreaSetting getVisibleArea() {
		return getChartSetting();
	}
}
