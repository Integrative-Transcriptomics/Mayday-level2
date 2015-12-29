package mayday.exportjs.plugins.profileplot;

import java.awt.Color;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.exportjs.plugins.PlotExportSetting;

public class ProfilePlotExporterSetting extends PlotExportSetting {
	
	// General Settings
	private HierarchicalSetting generalSetting;
	private StringSetting description;
	private IntSetting lineWidth;
	private DoubleSetting transparency;
	
	// Size Settings
	private HierarchicalSetting sizeSetting;
	private IntSetting height;
	private IntSetting width;
	
	// Selected Probes Settings
	private BooleanHierarchicalSetting selectedProbesSetting;
	private BooleanSetting showNames;
	private IntSetting selectedProbesLineWidth;
	private ColorSetting selectedProbesColor;
	
	// Special Lines Settings
	private HierarchicalSetting specialLinesSetting;
	private BooleanSetting mean;
	private BooleanSetting median;
	private BooleanSetting firstQuartile;
	private BooleanSetting thirdQuartile;
	
	// Multiple Plots Settings
	private BooleanHierarchicalSetting multiPlotSetting;
	private IntSetting multiPlotPadding;
	
	public ProfilePlotExporterSetting() {
		super("Profile Plot", null, true);
		
		// General Settings
		this.generalSetting = new HierarchicalSetting("General");
		this.lineWidth = new IntSetting("Line Width", null, 1, 1, 10, true,true);
		this.lineWidth.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.description = new StringSetting("Description", null, "");
		this.transparency = new DoubleSetting("Transparency", null, 1, 0.0, 1.0, true, true);
		this.generalSetting.addSetting(this.description).addSetting(this.lineWidth).addSetting(this.transparency);
		
		// Size Settings
		this.sizeSetting = new HierarchicalSetting("Size");
		this.height = new IntSetting("Height", null, 250, 50, 5000, true, true);
		this.width = new IntSetting("Width", null, 450, 50, 5000, true, true);
		this.sizeSetting.addSetting(this.height).addSetting(this.width);
		
		// Selected Probes Settings
		this.selectedProbesSetting = new BooleanHierarchicalSetting("Highlight Selected Probes", null, true);
		this.showNames = new BooleanSetting("Show names", null, true);
		this.selectedProbesLineWidth = new IntSetting("Line Width", null, 3, 1, 10, true, true);
		this.selectedProbesLineWidth.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.selectedProbesColor = new ColorSetting("Selected Probes Color", null, Color.red);
		this.selectedProbesSetting.addSetting(this.selectedProbesLineWidth).addSetting(showNames).addSetting(selectedProbesColor);
		
		// Special Lines Settings
		this.specialLinesSetting = new HierarchicalSetting("Special Lines");
		this.mean = new BooleanSetting("Mean Line", null, true);
		this.median = new BooleanSetting("Median Line", null, true);
		this.firstQuartile = new BooleanSetting("First Quartile Line", null, true);
		this.thirdQuartile = new BooleanSetting("Third Quartile Line", null, true);
		this.specialLinesSetting.addSetting(this.mean).addSetting(this.median).addSetting(this.firstQuartile).addSetting(thirdQuartile);
		
		// Multiple Plots Settings
		this.multiPlotSetting = new BooleanHierarchicalSetting("Show Multiple Plots", null, false);
		this.multiPlotPadding = new IntSetting("Multi Plot Padding", "Multi Plot Padding", 50, 5, 200, true, true);
		this.multiPlotPadding.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.multiPlotSetting.addSetting(this.multiPlotPadding);
		
		this.addSetting(generalSetting).addSetting(this.sizeSetting).addSetting(this.selectedProbesSetting).addSetting(specialLinesSetting).addSetting(this.multiPlotSetting);
	}

	@Override
	public ProfilePlotExporterSetting clone() {
		ProfilePlotExporterSetting clone = new ProfilePlotExporterSetting();
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

	public boolean isShowSelectedProbesLabels() {
		return showNames.getBooleanValue();
	}

	public void setShowSelectedProbesLabels(boolean showSelectedProbesLabels) {
		showNames.setBooleanValue(showSelectedProbesLabels);
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

	public int getMultiplePlotsPadding() {
		return multiPlotPadding.getIntValue();
	}

	public void setMultiplePlotsPadding(int multiplePlotsPadding) {
		multiPlotPadding.setIntValue(multiplePlotsPadding);
	}

	public boolean isShowMultiplePlots() {
		return multiPlotSetting.getBooleanValue();
	}

	public void setShowMultiplePlots(boolean showMultiplePlots) {
		this.multiPlotSetting.setBooleanValue(showMultiplePlots);
	}

	public int getProbesLineWidth() {
		return this.lineWidth.getIntValue();
	}
	
	public void setProbesLineWidth(int probesLineWidth) {
		this.lineWidth.setIntValue(probesLineWidth);
	}
	
	public double getTransparency() {
		return transparency.getDoubleValue();
	}

	public void setTransparency(double transparency) {
		this.transparency.setDoubleValue(transparency);
	}
	
	public boolean isMean() {
		return mean.getBooleanValue();
	}
	public void setMean(boolean mean) {
		this.mean.setBooleanValue(mean);
	}
	public boolean isMedian() {
		return median.getBooleanValue();
	}
	public void setMedian(boolean median) {
		this.median.setBooleanValue(median);
	}
	public boolean isFirstQuartile() {
		return firstQuartile.getBooleanValue();
	}
	public void setFirstQuartile(boolean firstQuartile) {
		this.firstQuartile.setBooleanValue(firstQuartile);
	}
	public boolean isThirdQuartile() {
		return thirdQuartile.getBooleanValue();
	}
	public void setThirdQuartile(boolean thirdQuartile) {
		this.thirdQuartile.setBooleanValue(thirdQuartile);
	}
	@Override
	public String getDescription() {
		return description.getStringValue();
	}

}
