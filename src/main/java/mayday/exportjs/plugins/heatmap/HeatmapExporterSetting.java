package mayday.exportjs.plugins.heatmap;

import java.awt.Color;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.exportjs.plugins.PlotExportSetting;

public class HeatmapExporterSetting extends PlotExportSetting {
	
	// General Settings
	private HierarchicalSetting generalSetting;
	private StringSetting description;
	
	// Size Settings
	private HierarchicalSetting sizeSetting;
	private IntSetting height;
	private IntSetting width;
	
	// Color Settings
	private HierarchicalSetting gradientSetting;
	private ColorSetting minColor;
	private ColorSetting midColor;
	private ColorSetting maxColor;
	
	// Selected Probes Settings
	private BooleanHierarchicalSetting selectedProbesSetting;
	private IntSetting selectedProbesLineWidth;
	private ColorSetting selectedProbesColor;
	
	public HeatmapExporterSetting() {
		super("Heatmap", null, true);
		
		// General Settings
		this.generalSetting = new HierarchicalSetting("General");
		this.description = new StringSetting("Description", null, "");
		this.generalSetting.addSetting(this.description);
		
		// Size Settings
		this.sizeSetting = new HierarchicalSetting("Size");
		this.height = new IntSetting("Box Height", null, 20, 10, 500, true, true);
		this.width = new IntSetting("Box Width", null, 20, 10, 500, true, true);
		this.sizeSetting.addSetting(this.height).addSetting(this.width);
		
		// Color Settings
		this.gradientSetting = new HierarchicalSetting("Color Gradient");
		this.minColor = new ColorSetting("Min Color", null, Color.white);
		this.midColor = new ColorSetting("Mid Color", null, new Color(70, 130, 180));
		this.maxColor = new ColorSetting("Max Color", null, Color.black);
		this.gradientSetting.addSetting(this.minColor).addSetting(this.midColor).addSetting(this.maxColor);
		
		// Selected Probes Settings
		this.selectedProbesSetting = new BooleanHierarchicalSetting("Highlight Selected Probes", null, true);
		this.selectedProbesLineWidth = new IntSetting("Line Width", null, 2, 1, 10, true, true);
		this.selectedProbesLineWidth.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.selectedProbesColor = new ColorSetting("Selected Probes Color", null, Color.red);
		this.selectedProbesSetting.addSetting(this.selectedProbesLineWidth).addSetting(selectedProbesColor);
		
		this.addSetting(this.generalSetting).addSetting(this.sizeSetting).addSetting(this.gradientSetting).addSetting(this.selectedProbesSetting);
	}

	@Override
	public HeatmapExporterSetting clone() {
		HeatmapExporterSetting clone = new HeatmapExporterSetting();
		clone.fromPrefNode(this.toPrefNode());
		return clone;
	}

	public int getHeight() {
		return height.getIntValue();
	}
	
	public int getWidth() {
		return width.getIntValue();
	}
	
	public boolean isShowSelectedProbes() {
		return selectedProbesSetting.getBooleanValue();
	}

	public void setShowSelectedProbes(boolean showSelectedProbes) {
		selectedProbesSetting.setBooleanValue(showSelectedProbes);
	}

	public Color getSelectedProbesColor() {
		return selectedProbesColor.getColorValue();
	}

	public void setSelectedProbesColor(Color selectedProbesColor) {
		this.selectedProbesColor.setColorValue(selectedProbesColor);
	}

	public int getSelectedProbesLineWidth() {
		return selectedProbesLineWidth.getIntValue();
	}

	public void setSelectedProbesLineWidth(int selectedProbesLineWidth) {
		this.selectedProbesLineWidth.setIntValue(selectedProbesLineWidth);
	}
	
	public Color getMinColor() {
		return minColor.getColorValue();
	}
	
	public void setMinColor(Color minColor) {
		this.minColor.setColorValue(minColor);
	}
	
	public Color getMidColor() {
		return midColor.getColorValue();
	}

	
	public void setMidColor(Color midColor) {
		this.midColor.setColorValue(midColor);
	}

	public Color getMaxColor() {
		return maxColor.getColorValue();
	}
	
	public void setMaxColor(Color maxColor) {
		this.maxColor.setColorValue(maxColor);
	}
	@Override
	public String getDescription() {
		return description.getStringValue();
	}

}
