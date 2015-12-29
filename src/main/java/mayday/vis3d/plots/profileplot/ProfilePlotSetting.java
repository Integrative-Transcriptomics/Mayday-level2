package mayday.vis3d.plots.profileplot;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.vis3.plots.PlotTimepointSetting;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class ProfilePlotSetting extends HierarchicalSetting {

	private ProfilePlotPanel panel;
	protected ColorSetting selectionColor;
	protected ColorSetting backgroundColor;
	protected PlotTimepointSetting timepoints;
	protected LabelingSetting labelingSetting;
	
	
	/**
	 * Default constructor
	 * @param panel 
	 */
	public ProfilePlotSetting(ProfilePlotPanel panel) {
		super("Profile Plot with Labeling");
		this.panel = panel;
		
		addSetting(backgroundColor = new ColorSetting("Background Color", null, Color.WHITE));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(timepoints = new PlotTimepointSetting("Time Points", null, true));
		addSetting(labelingSetting = new LabelingSetting(panel));
		
		timepoints.setDataSet(panel.viewModel.getDataSet());
		
		this.addChangeListener(new ProfilePlot2DSettingChangeListener());
	}
	
	@Override
	public ProfilePlotSetting clone() {
		ProfilePlotSetting pps = new ProfilePlotSetting(this.panel);
		pps.fromPrefNode(this.toPrefNode());
		return pps;
	}
	
	/**
	 * @return time point setting
	 */
	public PlotTimepointSetting getTimepoints() {
		return this.timepoints;
	}
	/**
	 * @return selection color setting
	 */
	public ColorSetting getSelectionColor() {
		return this.selectionColor;
	}
	/**
	 * @return background color
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor.getColorValue();
	}
	
	/**
	 * @return labeling setting
	 */
	public LabelingSetting getLabelingSetting() {
		return this.labelingSetting;
	}
	
	private class ProfilePlot2DSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {

		public ProfilePlot2DSettingChangeListener() {
			super("Profile Plot with Label Histogram Updater");
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
