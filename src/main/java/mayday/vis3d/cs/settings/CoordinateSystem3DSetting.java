package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3d.AbstractPlot3DPanel;
import mayday.vis3d.cs.PlaneCoordinateSystem3D;
import mayday.vis3d.cs.StandardCoordinateSystem3D;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class CoordinateSystem3DSetting extends CoordinateSystemSetting {

	protected RestrictedStringSetting coordSystems; 
	
	private static final String SCS = StandardCoordinateSystem3D.ID, PCS = PlaneCoordinateSystem3D.ID;
	
	private Grid3DSetting g3dSetting;
	private VisibleAreaSetting visibleAreaSetting;
	private LabelingSetting labelingSetting;
	protected boolean oneCS = false;
	protected IntSetting fontSize;
	protected BooleanSetting xLabelVertical;
	
	private int oldFontSize = 24;
	private double oldScale = 0.02;
	
	/**
	 * @param dimension
	 * @param panel
	 * @param oneCS 
	 */
	public CoordinateSystem3DSetting(AbstractPlot3DPanel panel, boolean oneCS) {
		super(panel, LayoutStyle.PANEL_VERTICAL, true);
		
		this.oneCS = oneCS;
		
		String[] css = {SCS, PCS};
		
		double[] dimension = panel.getInitDimension();
		
		this.coordSystems = new RestrictedStringSetting("Coordinate System", 
				"Select which coordinate system to use.", 0, css)
				.setLayoutStyle(RestrictedStringSetting.LayoutStyle.RADIOBUTTONS);
		
		addSetting(new HierarchicalSetting("Layout", LayoutStyle.PANEL_HORIZONTAL, true)
		.addSetting(g3dSetting = new Grid3DSetting(panel))
		.addSetting(visibleAreaSetting = new VisibleAreaSetting(dimension[0], dimension[1], dimension[2])));
		
		
		HierarchicalSetting csSetting = new HierarchicalSetting("Labelling", LayoutStyle.PANEL_VERTICAL, true)
		.addSetting(labelingSetting = new LabelingSetting())
		.addSetting(fontSize = new IntSetting("Label Size", null, 24))
		.addSetting(xLabelVertical = new BooleanSetting("vertical x-axis labels", null, false));
		
		if(!oneCS) {
			csSetting.addSetting(coordSystems);
		}
		
		addSetting(csSetting);
		setChildrenAsSubmenus(false);
	}
	
	/**
	 * @return the selected coordinate system
	 */
	public String getSelectedCS() {
		return this.coordSystems.getValueString();
	}
	/**
	 * @return the chosen iterations
	 */
	public double[] getIteration() {
		double width = this.visibleAreaSetting.getWidth() * 2;
		double height = this.visibleAreaSetting.getHeight() * 2;
		double depth = this.visibleAreaSetting.getDepth() * 2;
		
		int[] iteration = this.g3dSetting.getIteration();
		
		double xit = width / iteration[0];
		double yit = height / iteration[1];
		double zit = depth / iteration[2];
		
		return new double[]{xit, yit, zit};
	}
	/**
	 * @return true, if x-axis labels should be drawn vertical, else false
	 */
	public boolean getVerticalXAxisLabels() {
		return this.xLabelVertical.getBooleanValue();
	}
	
	/**
	 * @param xIt
	 * @param yIt
	 * @param zIt
	 */
	public void setIteration(Integer xIt, Integer yIt, Integer zIt) {
		this.g3dSetting.setXIteration(xIt);
		this.g3dSetting.setYIteration(yIt);
		this.g3dSetting.setZIteration(zIt);
	}
	
	/**
	 * @return the visible area setting
	 */
	public VisibleAreaSetting getVisibleArea() {
		return this.visibleAreaSetting;
	}
	
	/**
	 * @return the grid setting
	 */
	public Grid3DSetting getGridSetting() {
		return this.g3dSetting;
	}
	
	/**
	 * @return the labeling setting
	 */
	public LabelingSetting getLabelingSetting() {
		return this.labelingSetting;
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
	
	public CoordinateSystem3DSetting clone() {
		CoordinateSystem3DSetting css = new CoordinateSystem3DSetting(panel, oneCS);
		css.fromPrefNode(this.toPrefNode());
		return css;
	}
}
