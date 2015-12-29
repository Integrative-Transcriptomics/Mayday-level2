package mayday.vis3d.plots.radialprofileplot;

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
public class RadialProfileplotSetting extends HierarchicalSetting {

	private RadialProfileplotPanel panel;
	
	protected ColorSetting selectionColor;
	protected ColorSetting backgroundColor;
	protected PlotTimepointSetting timepoints;
	protected BooleanSetting drawWireFrame;
	protected BooleanSetting drawSurface;
	protected BooleanSetting drawLabels;
	protected IntSetting fontSize;
	protected DoubleSetting radius;
	protected AnimatorSetting animator;
	
	private int oldFontSize = 24;
	private double oldScale = 0.02;
	
	/**
	 * @param panel
	 */
	public RadialProfileplotSetting(RadialProfileplotPanel panel) {
		super("Radial Profile Plot");
		this.panel = panel;
		
		addSetting(new HierarchicalSetting("Elements").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL)
			.addSetting(drawWireFrame = new BooleanSetting("Draw Wire Frame", null, true))
			.addSetting(drawSurface = new BooleanSetting("Draw Surface", null, true))
			.addSetting(drawLabels = new BooleanSetting("Draw Labels", null, true))
			.addSetting(fontSize = new IntSetting("Label Size", null, 24)));
		
		
		addSetting(timepoints = new PlotTimepointSetting("Time points", null, true));
		addSetting(backgroundColor = new ColorSetting("Background Color", null, Color.WHITE));
		addSetting(selectionColor = new ColorSetting("Selection color", null, Color.RED));
		addSetting(radius = new DoubleSetting("Radius", null, 5.0));
		addSetting(animator = new AnimatorSetting());
		
		timepoints.setDataSet(panel.viewModel.getDataSet());
		
		this.addChangeListener(new RadialProfileplotSettingChangeListener());
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
	 * @return time-points setting
	 */
	public PlotTimepointSetting getTimepoints() {
		return this.timepoints;
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
	 * @return true, if wire frame should be drawn, else false
	 */
	public boolean getDrawWireframe() {
		return this.drawWireFrame.getBooleanValue();
	}
	/**
	 * @return true, if surface should be drawn, else false
	 */
	public boolean getDrawSurface() {
		return this.drawSurface.getBooleanValue();
	}
	/**
	 * @return true, if labels should be drawn, else false
	 */
	public boolean getDrawLabels() {
		return this.drawLabels.getBooleanValue();
	}
	/**
	 * @return true, if scene should be animated, else false
	 */
	public boolean animateScene() {
		return this.animator.animateScene();
	}
	/**
	 * @return rotation angle increment around pivot axis
	 */
	public double getAnimationSpeed() {
		return this.animator.getRotationSpeed();
	}
	/**
	 * @return radius
	 */
	public double getRadius() {
		return this.radius.getDoubleValue();
	}
	
	/**
	 * @param r
	 */
	public void setRadius(double r) {
		this.radius.setDoubleValue(r);
	}
	
	public RadialProfileplotSetting clone() {
		RadialProfileplotSetting rps = new RadialProfileplotSetting(this.panel);
		rps.fromPrefNode(this.toPrefNode());
		return rps;
	}
	
	private class RadialProfileplotSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {

		public RadialProfileplotSettingChangeListener() {
			super("RadialProfileplot Updater");
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
