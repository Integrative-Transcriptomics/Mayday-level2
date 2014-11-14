package mayday.exportjs.plugins.scatterplot;

import java.awt.Color;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.exportjs.plugins.PlotExportSetting;

public class ScatterPlotExporterSetting extends PlotExportSetting {
	
	// General Settings
	private HierarchicalSetting generalSetting;
	private StringSetting description;
	private IntSetting probesDotSize;
	private DoubleSetting transparency;
	private BooleanSetting switchExperimentsInteraction;
	
	// Size Settings
	private HierarchicalSetting sizeSetting;
	private IntSetting height;
	private IntSetting width;
	
	// Selected Probes Settings
	private BooleanHierarchicalSetting selectedProbesSetting;
	private ColorSetting selectedProbesColor;
	private IntSetting selectedProbesDotSize;
	private BooleanSetting showNames;
	
	// Plot Matrix Settings
	private BooleanHierarchicalSetting matrixSetting;
	private IntSetting matrixPadding;
	
	public ScatterPlotExporterSetting() {
		super("Scatter Plot", null, true);
		
		// General Settings
		this.generalSetting = new HierarchicalSetting("General");
		this.probesDotSize = new IntSetting("Dot Size", null, 25, 1, 100, true,true);
		this.probesDotSize.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.switchExperimentsInteraction = new BooleanSetting("Interaction: Switch Experiments", "Enables you to switch the selected experiments. Not available for the scatter plot matrix.", true);
		this.description = new StringSetting("Description", null, "");
		this.transparency = new DoubleSetting("Transparency", null, 0.7, 0.0, 1.0, true, true);
		this.generalSetting.addSetting(this.probesDotSize).addSetting(this.transparency).addSetting(this.description).addSetting(this.switchExperimentsInteraction);
		
		// Size Settings
		this.sizeSetting = new HierarchicalSetting("Size");
		this.height = new IntSetting("Height", null, 300, 50, 5000, true, true);
		this.width = new IntSetting("Width", null, 300, 50, 5000, true, true);
		this.sizeSetting.addSetting(this.height).addSetting(this.width);
		
		// Selected Probes Settings
		this.selectedProbesSetting = new BooleanHierarchicalSetting("Highlight Selected Probes", null, true);
		this.selectedProbesColor = new ColorSetting("Selected Probes Color", null, Color.red);
		this.selectedProbesDotSize = new IntSetting("Dot Size", null, 35, 1, 200, true, true);
		this.selectedProbesDotSize.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.showNames = new BooleanSetting("Show Names", null, true);
		this.selectedProbesSetting.addSetting(this.selectedProbesDotSize).addSetting(this.selectedProbesColor).addSetting(this.showNames);
		
		// Plot Matrix Settings
		this.matrixSetting = new BooleanHierarchicalSetting("Scatter Plot Matrix", null, false);
		this.matrixPadding = new IntSetting("Matrix Padding", "Matrix Padding", 10, 5, 200, true, true);
		this.matrixPadding.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.matrixSetting.addSetting(this.matrixPadding);
		
		this.addSetting(generalSetting).addSetting(this.sizeSetting).addSetting(this.selectedProbesSetting).addSetting(this.matrixSetting);
	}

	@Override
	public ScatterPlotExporterSetting clone() {
		ScatterPlotExporterSetting clone = new ScatterPlotExporterSetting();
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

	public boolean isShowPlotMatrix() {
		return this.matrixSetting.getBooleanValue();
	}

	public void setShowPlotMatrix(boolean showPlotMatrix) {
		this.matrixSetting.setBooleanValue(showPlotMatrix);
	}
	
	public int getProbesDotSize() {
		return probesDotSize.getIntValue();
	}

	public void setProbesDotSize(int probesDotSize) {
		this.probesDotSize.setIntValue(probesDotSize);
	}

	public boolean isSwitchExperimentsInteraction() {
		return switchExperimentsInteraction.getBooleanValue();
	}

	public void setSwitchExperimentsInteraction(boolean switchExperimentsInteraction) {
		this.switchExperimentsInteraction.setBooleanValue(switchExperimentsInteraction);
	}
	
	public int getPlotMatrixPadding() {
		return matrixPadding.getIntValue();
	}

	public void setPlotMatrixPadding(int plotMatrixPadding) {
		this.matrixPadding.setIntValue(plotMatrixPadding);
	}

	public int getSelectedProbesDotSize() {
		return selectedProbesDotSize.getIntValue();
	}

	public void setSelectedProbesDotSize(int selectedProbesDotSize) {
		this.selectedProbesDotSize.setIntValue(selectedProbesDotSize);
	}

	public double getTransparency() {
		return transparency.getDoubleValue();
	}

	public void setTransparency(double transparency) {
		this.transparency.setDoubleValue(transparency);
	}
	@Override
	public String getDescription() {
		return description.getStringValue();
	}

}
