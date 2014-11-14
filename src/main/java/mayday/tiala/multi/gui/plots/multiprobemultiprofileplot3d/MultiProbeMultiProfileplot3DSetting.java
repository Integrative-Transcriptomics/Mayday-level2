package mayday.tiala.multi.gui.plots.multiprobemultiprofileplot3d;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.MasterTable;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.tiala.multi.data.mastertables.MultiProbesMasterTable;
import mayday.vis3.plots.PlotTimepointSetting;
import mayday.vis3d.cs.settings.CoordinateSystem3DSetting;

/**
 * @author jaeger
 */
public class MultiProbeMultiProfileplot3DSetting extends HierarchicalSetting {
	
	private MultiProbeMultiProfileplot3DPanel panel;
	private int numMultiProbes;
	
	protected BooleanSetting inferData;
	protected ColorSetting[] selectionColor;
	Color[] oldSelectionColors;
	protected ColorSetting backgroundColor;
	protected ColorSetting labelColor;
	protected PlotTimepointSetting timepoints;
	
	protected BooleanSetting[] profilePlane;
	protected IntSetting planeTransparency;
	protected IntSetting profileTransparency;
	
	private CoordinateSystem3DSetting coordSetting;

	/**
	 * @param panel
	 * @param numMultiProbes
	 */
	public MultiProbeMultiProfileplot3DSetting(MultiProbeMultiProfileplot3DPanel panel, int numMultiProbes) {
		super("MultiProbeMultiProfileplot3D Settings");
		this.panel = panel;
		this.numMultiProbes = numMultiProbes;
		selectionColor = new ColorSetting[numMultiProbes];
		oldSelectionColors = new Color[numMultiProbes];
		profilePlane = new BooleanSetting[numMultiProbes];
		
		
		//elements setting
		HierarchicalSetting elements = new HierarchicalSetting("Elements").setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL);
		elements.addSetting(profileTransparency = new IntSetting("Profile Transparency", null, 0, 0, 100, true, true)
		.setLayoutStyle(IntSetting.LayoutStyle.SLIDER))
		.addSetting(planeTransparency = new IntSetting("Surface Transparency", null, 25, 0, 100, true, true)
			.setLayoutStyle(IntSetting.LayoutStyle.SLIDER));			
		
		//color-settings
		addSetting(backgroundColor = new ColorSetting("Background Color", null, Color.WHITE));
		addSetting(labelColor = new ColorSetting("Labels Color", null, Color.BLACK));
		
		MasterTable[] masterTables = ((MultiProbesMasterTable)panel.viewModel.getDataSet().getMasterTable()).getStore().getMasterTables();
		
		//selection colors setting
		HierarchicalSetting selectionColors = new HierarchicalSetting("Selection Colors")
			.setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL);
		for(int i = 0; i < numMultiProbes; i++) {
			selectionColors.addSetting(selectionColor[i] = 
				new ColorSetting("Selection Color for " + masterTables[i].getDataSet().getName(), null, Color.RED));
			oldSelectionColors[i] = Color.RED;
		}
		
		HierarchicalSetting profilePlanes = new HierarchicalSetting("Profile Surfaces")
			.setLayoutStyle(HierarchicalSetting.LayoutStyle.PANEL_VERTICAL);
		for(int i = 0; i < numMultiProbes; i++) {
			profilePlanes.addSetting(profilePlane[i] = 
				new BooleanSetting("Profile Surface for " + masterTables[i].getDataSet().getName(), null, false));
		}
		
		addSetting(selectionColors);
		elements.addSetting(profilePlanes);
		addSetting(elements);
		
		//time-points setting
		addSetting(timepoints = new PlotTimepointSetting("Time points", null, true));
		timepoints.setDataSet(panel.viewModel.getDataSet());
		
		addChangeListener(new MPMP3DSettingChangeListener());
	}
	
	/**
	 * @return surface transparency
	 */
	public double getTransparencyValue() {
		return (100 - this.planeTransparency.getIntValue()) / 100.0;
	}
	
	/**
	 * @return profile transparency
	 */
	public double getProfileTransparency() {
		return (100 - this.profileTransparency.getIntValue()) / 100.0;
	}
	
	/**
	 * @return time-points setting
	 */
	public PlotTimepointSetting getTimepointSetting() {
		return this.timepoints;
	}
	/**
	 * @return true if inferData else false
	 */
	public BooleanSetting getInferDataSetting() {
		return this.inferData;
	}
	
	/**
	 * @param index
	 * @return true if the surface for the multiprobe at index i should be drawn, else false
	 */
	public boolean drawProfileSurface(int index) {
		return this.profilePlane[index].getBooleanValue();
	}	
	/**
	 * @return selection color setting
	 */
	public ColorSetting[] getSelectionColorSettings() {
		return this.selectionColor;
	}
	/**
	 * @return true, if planes should be drawn between profiles, else false
	 */
	public boolean getDrawProfilePlanes() {
		for(int i = 0; i < this.profilePlane.length; i++) {
			if(this.profilePlane[i].getBooleanValue()) {
				return true;
			}
		}
		return false;
	}
	/**
	 * @return defined background color
	 */
	public Color getBackgroundColor() {
		return this.backgroundColor.getColorValue();
	}
	/**
	 * @return color value of the labels
	 */
	public Color getLabelsColor() {
		return this.labelColor.getColorValue();
	}
	
	/**
	 * @param selectionColors
	 */
	public void setSelectionColors(Color[] selectionColors) {
		for(int i = 0; i < selectionColor.length; i++) {
			selectionColor[i].setColorValue(selectionColors[i]);
		}
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
	
	@Override
	public MultiProbeMultiProfileplot3DSetting clone() {
		MultiProbeMultiProfileplot3DSetting mpmp3ds = new MultiProbeMultiProfileplot3DSetting(panel, numMultiProbes);
		mpmp3ds.fromPrefNode(this.toPrefNode());
		return mpmp3ds;
	}
	
	private class MPMP3DSettingChangeListener extends DelayedUpdateTask implements SettingChangeListener {
		
		public MPMP3DSettingChangeListener() {
			super("3D Multi Profile Plot Updater");
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			boolean changed = false;
			for(int i = 0; i < selectionColor.length; i++) {
				if(!oldSelectionColors[i].equals(selectionColor[i].getColorValue())) {
					oldSelectionColors[i] = selectionColor[i].getColorValue();
					changed = true;
					if(changed) {
						panel.Store.getSettings().setSelectionColor(i, selectionColor[i].getColorValue());
						changed = false;
					}
				}
			}
			panel.drawTypeUpdate();
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			trigger();
		}
	}
}
