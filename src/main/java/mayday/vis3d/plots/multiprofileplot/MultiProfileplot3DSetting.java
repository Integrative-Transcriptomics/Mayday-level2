package mayday.vis3d.plots.multiprofileplot;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.PlotTimepointSetting;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class MultiProfileplot3DSetting extends HierarchicalSetting{
	
	private MultiProfileplot3DPanel panel;
	
	protected ColorSetting selectionColor;
	protected ColorSetting backgroundColor;
	protected PlotTimepointSetting timepoints;
	protected BooleanSetting centroids;
	protected BooleanSetting centroidPlanes;
	protected BooleanSetting profiles;
	protected IntSetting planeTransparency;
	
	private CoordinateSystem3DSetting coordSetting;
	
	/**
	 * @param panel
	 */
	public MultiProfileplot3DSetting(MultiProfileplot3DPanel panel){
		super("3D Multi Profile Plot");
		this.panel = panel;
		
		addSetting(new HierarchicalSetting("Elements").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
			.addSetting(profiles = new BooleanSetting("Draw Profiles", null, true))
			.addSetting(centroids = new BooleanSetting("Draw Centroids", null, false))
			.addSetting(centroidPlanes = new BooleanSetting("Planes between centroids", null, false))
			.addSetting(planeTransparency = new IntSetting("Set centroid plane transparency", null, 25, 0, 100, true, true).setLayoutStyle(IntSetting.LayoutStyle.SLIDER)));
		
		addSetting(timepoints = new PlotTimepointSetting("Time points", null, true));
		addSetting(backgroundColor = new ColorSetting("Background Color", null, Color.WHITE));
		addSetting(selectionColor = new ColorSetting("Selection color", null, Color.RED));
		
		timepoints.setDataSet(panel.viewModel.getDataSet());
		
		addChangeListener(new MP3DSettingChangeListener());
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
	public CoordinateSystem3DSetting getCSSetting() {
		return this.coordSetting;
	}
	
	/**
	 * @return selectionColor
	 */
	public Color getSelectionColor(){
		return selectionColor.getColorValue();
	}
	/**
	 * @return defined background color
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor.getColorValue();
	}
	
	/**
	 * @return time-points setting
	 */
	public PlotTimepointSetting getTimepoints() {
		return this.timepoints;
	}
	/**
	 * @return true, if profiles should be drawn
	 */
	public boolean getDrawProfiles() {
		return this.profiles.getBooleanValue();
	}
	/**
	 * @return true, if centroids should be drawn, else false
	 */
	public boolean getDrawCentroids() {
		return this.centroids.getBooleanValue();
	}
	/**
	 * @return true, if centroids should be drawn and there should be planes drawn 
	 * between them to show the differences better, else false
	 */
	public boolean drawCentroidPlanes() {
		return this.centroids.getBooleanValue() && this.centroidPlanes.getBooleanValue();
	}
	/**
	 * @return plane transparency
	 */
	public double getTransparencyValue() {
		double t = (100 - this.planeTransparency.getIntValue()) / 100.0;
		return t;
	}
	
	public MultiProfileplot3DSetting clone(){
		MultiProfileplot3DSetting setting = new MultiProfileplot3DSetting(panel);
		setting.fromPrefNode(this.toPrefNode());
		return setting;
	}
	
	private class MP3DSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {
		
		public MP3DSettingChangeListener() {
			super("3D Multi Profile Plot Updater");
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			panel.drawTypeUpdate();
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			trigger();
		}
	}
}
