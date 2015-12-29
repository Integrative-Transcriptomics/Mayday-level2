package mayday.vis3d.cs.settings;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.StringSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class VisibleAreaSetting extends HierarchicalSetting {
	
	protected DoubleSetting width = new DoubleSetting("Width", null, 5);
	protected DoubleSetting height = new DoubleSetting("Height", null, 5);
	protected DoubleSetting depth = new DoubleSetting("Depth", null, 5);
	
	protected StringSetting xAxisTitle = new StringSetting("Titel", null, "X");
	protected StringSetting yAxisTitle = new StringSetting("Titel", null, "Y");
	protected StringSetting zAxisTitle = new StringSetting("Titel", null, "Z");

	/**
	 * @param w
	 * @param h
	 * @param d
	 * 
	 */
	public VisibleAreaSetting(Double w, Double h, Double d) {
		super("Visible Area");
		
		if(w != null) {
			this.width.setDoubleValue(w);
		}
		
		if(h != null) {
			this.height.setDoubleValue(h);
		}
		
		if(d != null) {
			this.depth.setDoubleValue(d);
		}
		
		this.initSetting();
	}
	
	/**
	 * @param w
	 * @param h
	 */
	public VisibleAreaSetting(Double w, Double h) {
		super("Visible Area");
		
		if(w != null) {
			this.width.setDoubleValue(w);
		}
		
		if(h != null) {
			this.height.setDoubleValue(h);
		}
	}
	
	private void initSetting() {
		addSetting(new HierarchicalSetting("X-Axis")
		.addSetting(xAxisTitle)
		.addSetting(width));
		addSetting(new HierarchicalSetting("Y-Axis")
		.addSetting(yAxisTitle)
		.addSetting(height));
		addSetting(new HierarchicalSetting("Z-Axis")
		.addSetting(zAxisTitle)
		.addSetting(depth));
	}
	
	/**
	 * @return width
	 */
	public double getWidth() {
		return this.width.getDoubleValue();
	}
	
	/**
	 * @return height
	 */
	public double getHeight() {
		return this.height.getDoubleValue();
	}
	
	/**
	 * @return depth
	 */
	public double getDepth() {
		return this.depth.getDoubleValue();
	}
	
	/**
	 * @return dimension
	 */
	public double[] getDimension() {
		return new double[]{getWidth(), getHeight(), getDepth()};
	}
	
	/**
	 * @return x axis title
	 */
	public String getXAxisTitle() {
		return this.xAxisTitle.getStringValue();
	}
	
	/**
	 * @return y axis title
	 */
	public String getYAxisTitle() {
		return this.yAxisTitle.getStringValue();
	}
	
	/**
	 * @return z axis title
	 */
	public String getZAxisTitle() {
		return this.zAxisTitle.getStringValue();
	}
	
	public VisibleAreaSetting clone() {
		VisibleAreaSetting vas = new VisibleAreaSetting(width.getDoubleValue(), height.getDoubleValue(), depth.getDoubleValue());
		vas.fromPrefNode(this.toPrefNode());
		return vas;
	}
	
	/**
	 * @param width
	 */
	public void setWidth(double width) {
		this.width.setDoubleValue(width);
	}
	
	/**
	 * @param height
	 */
	public void setHeight(double height) {
		this.height.setDoubleValue(height);
	}
	
	/**
	 * @param depth
	 */
	public void setDepth(double depth) {
		this.depth.setDoubleValue(depth);
	}
}
