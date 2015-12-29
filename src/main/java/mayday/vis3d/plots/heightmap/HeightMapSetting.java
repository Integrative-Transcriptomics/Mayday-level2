package mayday.vis3d.plots.heightmap;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.plots.PlotTimepointSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class HeightMapSetting extends HierarchicalSetting{

	private HeightMapPanel panel;
	
	protected PlotTimepointSetting timepoints;
	protected ColorSetting backgroundColor;
	protected ColorSetting selectionColor;
	protected BooleanSetting profiles;
	protected BooleanSetting showLabels;
	protected IntSetting planeTransparency;
	protected IntSetting fontSize;
	protected DoubleSetting profileDistance;
	
	private int oldFontSize = 24;
	private double oldScale = 0.02;
	
	/**
	 * @param panel
	 * 
	 */
	public HeightMapSetting(HeightMapPanel panel) {
		super("Height Map");
		this.panel = panel;
		
		addSetting(backgroundColor = new ColorSetting("Background Color", null, Color.WHITE));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(profiles = new BooleanSetting("Show Profiles", null, false));
		addSetting(profileDistance = new DoubleSetting("Profile Distance", null, 1.0));
		addSetting(showLabels = new BooleanSetting("Show Labels", null, true));
		addSetting(fontSize = new IntSetting("Label Size", null, 24));
		addSetting(timepoints = new PlotTimepointSetting("Time Points", null, true));
		addSetting(planeTransparency = new IntSetting("Set plane transparency", null, 0, 0, 100, true, true).setLayoutStyle(IntSetting.LayoutStyle.SLIDER));
		
		timepoints.setDataSet(panel.viewModel.getDataSet());
		
		addChangeListener(new HeightMapSettingChangeListener());
	}
	
	/**
	 * @return distance between two neighboring profiles
	 */
	public double getProfileDistance() {
		return this.profileDistance.getDoubleValue();
	}
	
	/**
	 * @return size of the labels
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
	 * @return true if profiles showed be drawn, else false
	 */
	public boolean showProfiles() {
		return this.profiles.getBooleanValue();
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
	 * @return plane transparency
	 */
	public double getTransparencyValue() {
		double t = (100 - this.planeTransparency.getIntValue()) / 100.0;
		return t;
	}
	
	/**
	 * @return time point setting
	 */
	public PlotTimepointSetting getTimepoints() {
		return this.timepoints;
	}
	/**
	 * @return true, if labels showed be shown, else false
	 */
	public boolean showLabels() {
		return this.showLabels.getBooleanValue();
	}
	
	public HeightMapSetting clone() {
		HeightMapSetting hms = new HeightMapSetting(this.panel);
		hms.fromPrefNode(this.toPrefNode());
		return hms;
	}
	/*
	 * SettingChangeListener
	 */
	private class HeightMapSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {
		public HeightMapSettingChangeListener() {
			super("Height Map Updater");
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
