package mayday.exportjs.plugins.boxplot;

import java.awt.Color;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.exportjs.plugins.PlotExportSetting;

public class BoxPlotExporterSetting extends PlotExportSetting {
	
	// General Settings
	private HierarchicalSetting generalSetting;
	private StringSetting description;
	private ColorSetting boxColor;
	private IntSetting multiPlotPadding;
	
	// Size Settings
	private HierarchicalSetting sizeSetting;
	private IntSetting height;
	private IntSetting width;
	
	// Selected Probes Settings
	private BooleanHierarchicalSetting selectedProbesSetting;
	private BooleanSetting showNames;
	private IntSetting selectedProbesLineWidth;
	private ColorSetting selectedProbesColor;
	
	public BoxPlotExporterSetting() {
		super("Box Plot", null, true);
		
		// General Settings
		this.generalSetting = new HierarchicalSetting("General");
		this.boxColor = new ColorSetting("Box Color", null, new Color(255, 153, 153));
		this.multiPlotPadding = new IntSetting("Multi Plot Padding", null , 50, 5, 200, true, true);
		this.multiPlotPadding.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.description = new StringSetting("Description", null, "");
		this.generalSetting.addSetting(this.description).addSetting(this.boxColor).addSetting(this.multiPlotPadding);
		
		// Size Settings
		this.sizeSetting = new HierarchicalSetting("Size");
		this.height = new IntSetting("Height", null, 300, 50, 5000, true, true);
		this.width = new IntSetting("Width", null, 500, 50, 5000, true, true);
		this.sizeSetting.addSetting(this.height).addSetting(this.width);
		
		// Selected Probes Settings
		this.selectedProbesSetting = new BooleanHierarchicalSetting("Highlight Selected Probes", null, true);
		this.showNames = new BooleanSetting("Show names", null, true);
		this.selectedProbesLineWidth = new IntSetting("Line Width", null, 3, 1, 10, true, true);
		this.selectedProbesLineWidth.setLayoutStyle(IntSetting.LayoutStyle.SLIDER);
		this.selectedProbesColor = new ColorSetting("Selected Probes Color", null, Color.red);
		this.selectedProbesSetting.addSetting(this.selectedProbesLineWidth).addSetting(showNames).addSetting(selectedProbesColor);
		
		this.addSetting(generalSetting).addSetting(this.sizeSetting).addSetting(this.selectedProbesSetting);
	}

	@Override
	public BoxPlotExporterSetting clone() {
		BoxPlotExporterSetting clone = new BoxPlotExporterSetting();
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

	public Color getBoxColor() {
		return boxColor.getColorValue();
	}

	public void setBoxColor(Color boxColor) {
		this.boxColor.setColorValue(boxColor);
	}

	@Override
	public String getDescription() {
		return description.getStringValue();
	}

}
