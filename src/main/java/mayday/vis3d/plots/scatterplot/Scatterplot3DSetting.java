package mayday.vis3d.plots.scatterplot;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 * @date June 10, 2010
 */
public class Scatterplot3DSetting extends HierarchicalSetting {

	protected BooleanSetting drawProjections;
	protected BooleanSetting drawCentroids;
	protected ColorSetting selectionColor;
	protected ColorSetting backgroundColor;
	protected DoubleSetting sphereRadius;
	protected BooleanSetting hideSpheres;
	protected BooleanSetting calculateConvexHull;
	
	private Scatterplot3DPanel panel;
	private CoordinateSystem3DSetting coordSetting;
	
	/**
	 * @param panel
	 */
	public Scatterplot3DSetting(Scatterplot3DPanel panel){
		super("3D Scatter Plot");
		
		this.panel = panel;
		addSetting(new HierarchicalSetting("Elements").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
			.addSetting(drawProjections = new BooleanSetting("Projection(s)", null, false))
			.addSetting(drawCentroids = new BooleanSetting("Centroid(s)", null, false))
			.addSetting(hideSpheres = new BooleanSetting("Spheres", null, true))
			.addSetting(sphereRadius = new DoubleSetting("Sphere radius", null, 0.08))
			.addSetting(calculateConvexHull = new BooleanSetting("Convex Hull(s)", null, false)));
		addSetting(backgroundColor = new ColorSetting("Background color", null, Color.WHITE));
		addSetting(selectionColor = new ColorSetting("Selection color", null, Color.RED));
		
		addChangeListener(new S3DSettingChangeListener());
	}
	
	/**
	 * Add new Coordinate System Setting
	 * @param coordSetting
	 */
	public void addCoordinateSystemSetting(CoordinateSystem3DSetting coordSetting) {
		this.coordSetting = coordSetting;
		addSetting(coordSetting);
	}
	
	/**
	 * @return the attached Coordinate System Setting
	 */
	public CoordinateSystem3DSetting getCSSetting() {
		return this.coordSetting;
	}
	
	/**
	 * @return drawCentroids
	 */
	public boolean getDrawCentroids(){
		return drawCentroids.getBooleanValue();
	}
	
	/**
	 * @return selectionColor
	 */
	public Color getSelectionColor(){
		return selectionColor.getColorValue();
	}
	
	/**
	 * @return selection color setting
	 */
	public ColorSetting getSelectionColorSetting(){
		return this.selectionColor;
	}
	/**
	 * Change the selection color
	 * @param c
	 */
	public void setSelectionColor(Color c) {
		this.selectionColor.setColorValue(c);
	}
	
	/**
	 * @return drawProjections
	 */
	public boolean getDrawProjections(){
		return drawProjections.getBooleanValue();
	}
	
	/**
	 * @return sphereRadius
	 */
	public double getSphereRadius(){
		return sphereRadius.getDoubleValue();
	}
	
	/**
	 * @return the chosen background color
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor.getColorValue();
	}
	
	/**
	 * @return true, if spheres should not be shown, else false
	 */
	public boolean hideSpheres() {
		return !this.hideSpheres.getBooleanValue();
	}
	
	/**
	 * @return true, if convex hulls should be calculated
	 */
	public boolean calcConvexHull() {
		return this.calculateConvexHull.getBooleanValue();
	}
	
	public Scatterplot3DSetting clone(){
		Scatterplot3DSetting setting = new Scatterplot3DSetting(panel);
		setting.fromPrefNode(this.toPrefNode());
		return setting;
	}
	
	private class S3DSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener{
		
		public S3DSettingChangeListener() {
			super("3D Scatter Plot Updater");
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			trigger();
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			panel.drawTypeUpdate();
		}
	}
}
