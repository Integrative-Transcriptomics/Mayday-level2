package mayday.vis3d.plots.pcaplot;

import java.awt.Color;
import java.util.ArrayList;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.SelectableHierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 * @date June 10, 2010
 */
public class PCAPlot3DSetting extends HierarchicalSetting{

	private PCAPlot3DPanel panel;
	
	protected BooleanSetting drawCentroids;
	protected ColorSetting selectionColor;
	protected ColorSetting backgroundColor;
	protected DoubleSetting sphereRadius;
	protected BooleanSetting drawProjections;
	protected BooleanSetting hideSpheres;
	protected BooleanSetting drawEigenvalues;
	protected BooleanSetting calculateConvexHull;
	
	protected RestrictedStringSetting pc1;
	protected RestrictedStringSetting pc2;
	protected RestrictedStringSetting pc3;
	
	private CoordinateSystem3DSetting coordSetting;
	
	/**
	 * @param panel , an instance of PCAPlot3DPanel
	 */
	public PCAPlot3DSetting(PCAPlot3DPanel panel) {
		super("3D Principle Component Plot");
		this.panel = panel;
		
		ArrayList<String> availablePCs = panel.getAvailablePCs();
		
		addSetting(new HierarchicalSetting("Elements").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
			.addSetting(drawProjections = new BooleanSetting("Projection(s)", null, false))
			.addSetting(drawCentroids = new BooleanSetting("Centroid(s)", null, false))
			.addSetting(drawEigenvalues = new BooleanSetting("Eigen-value histogram", null, false))
			.addSetting(hideSpheres = new BooleanSetting("Sphere(s)", null, true))
			.addSetting(sphereRadius = new DoubleSetting("Sphere radius", null, 0.08))
			.addSetting(calculateConvexHull = new BooleanSetting("Convex Hull(s)", null, false)));
		addSetting(backgroundColor = new ColorSetting("Background color", null, Color.WHITE));
		addSetting(selectionColor = new ColorSetting("Selection color", null, Color.RED));
		
		SelectableHierarchicalSetting selectPC1 = new SelectableHierarchicalSetting("PC X-Axis", null, 0, 
				new Object[]{pc1 = new RestrictedStringSetting("Principle Component", null, 0, availablePCs.toArray(new String[0]))
			.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.LIST)});
		pc1.setSelectedIndex(0);
	
		SelectableHierarchicalSetting selectPC2 = new SelectableHierarchicalSetting("PC Y-Axis", null, 0, 
				new Object[]{pc2 = new RestrictedStringSetting("Principle Component", null, 0, availablePCs.toArray(new String[0]))
			.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.LIST)});
		if(panel.getAvailablePCs().size() > 1){
			pc2.setSelectedIndex(1);
		}
	
		SelectableHierarchicalSetting selectPC3 = new SelectableHierarchicalSetting("PC Z-Axis", null, 0, 
				new Object[]{pc3 = new RestrictedStringSetting("Principle Component", null, 0, availablePCs.toArray(new String[0]))
			.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.LIST)});
		
		if(availablePCs.size() > 2){
			pc3.setSelectedIndex(2);
		}
		
		addSetting(selectPC1);
		addSetting(selectPC2);
		addSetting(selectPC3);
	
		addChangeListener(new PCA3DSettingChangeListener());
	}
	/**
	 * @param coordSetting
	 */
	public void addCoordinateSystemSetting(CoordinateSystem3DSetting coordSetting) {
		this.coordSetting = coordSetting;
		addSetting(coordSetting);
	}
	/**
	 * @return the attached coordinate system setting
	 */
	public CoordinateSystem3DSetting getCoordianteSystemSetting() {
		return this.coordSetting;
	}
	/**
	 * @return drawCentroids
	 */
	public boolean getDrawCentroids(){
		return this.drawCentroids.getBooleanValue();
	}
	/**
	 * @return selectionColor
	 */
	public Color getSelectionColor(){
		return this.selectionColor.getColorValue();
	}
	/**
	 * @return sphereRadius
	 */
	public double getSphereRadius(){
		return this.sphereRadius.getDoubleValue();
	}
	/**
	 * @return drawProjections
	 */
	public boolean getDrawProjections(){
		return this.drawProjections.getBooleanValue();
	}
	/**
	 * @return defined background color
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor.getColorValue();
	}
	/**
	 * @return selected index of pc1
	 */
	public int getPC1(){
		return pc1.getSelectedIndex();
	}
	/**
	 * @return selected index of pc2
	 */
	public int getPC2(){
		return pc2.getSelectedIndex();
	}
	/**
	 * @return selected index of pc3
	 */
	public int getPC3(){
		return pc3.getSelectedIndex();
	}
	/**
	 * @return true, if spheres should not be shown, else false
	 */
	public boolean hideSpheres() {
		return !this.hideSpheres.getBooleanValue();
	}
	/**
	 * @return true, if eigenvalue histogram should be shown, else false
	 */
	public boolean drawEigenvalues() {
		return this.drawEigenvalues.getBooleanValue();
	}
	
	/**
	 * @return true, if convex hulls should be drawn, else false
	 */
	public boolean calcConvexHull() {
		return this.calculateConvexHull.getBooleanValue();
	}
	
	public PCAPlot3DSetting clone(){
		PCAPlot3DSetting setting = new PCAPlot3DSetting(panel);
		setting.fromPrefNode(this.toPrefNode());
		return setting;
	}
	
	private class PCA3DSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {

		public PCA3DSettingChangeListener() {
			super("3D PCA Plot Updater");
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			panel.updatePlot();
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			trigger();
		}
	}
}
