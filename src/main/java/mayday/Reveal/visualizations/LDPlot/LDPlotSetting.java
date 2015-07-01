package mayday.Reveal.visualizations.LDPlot;

import java.awt.Color;

import mayday.Reveal.RevealDefaults;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.StringSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;

public class LDPlotSetting extends HierarchicalSetting {

	private IntSetting cellWidth;
	private ColorSetting selectionColor;
	private ColorSetting snvColor;
	
	private IntSetting snvElongation;
	private DoubleSetting correlationThreshold;
	
	private ColorGradientSetting corColorGradient;
	
	private StringSetting chromPrefix;
	
	private int startIndex;
	private int stopIndex;
	
	public LDPlotSetting() {
		super("LD Plot Setting");
		
		addSetting(cellWidth = new IntSetting("Cell Width", null, 20));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, RevealDefaults.DEFAULT_SELECTION_COLOR));
		addSetting(snvColor = new ColorSetting("SNV Color", null, RevealDefaults.DEFAULT_SNV_COLOR));
		addSetting(chromPrefix = new StringSetting("Chromosome Prefix", null, "Chr"));
		addSetting(snvElongation = new IntSetting("Extend Cor. Matrix", "Define the number of SNVs to show correlation values for outside of the current view port.", 10));
		addSetting(corColorGradient = new ColorGradientSetting("Cor. Color Gradient", null, ColorGradient.createDefaultGradient(0, 1)));
		addSetting(correlationThreshold = new DoubleSetting("Correlation Threshold", null, 0));
	}
	
	public LDPlotSetting clone() {
		LDPlotSetting s = new LDPlotSetting();
		s.fromPrefNode(this.toPrefNode());
		return s;
	}

	public int getCellWidth() {
		return this.cellWidth.getIntValue();
	}
	
	public void setCellWidth(int cellWidth) {
		this.cellWidth.setIntValue(cellWidth);
		fireChanged();
	}

	public int getStartIndex() {
		return this.startIndex;
	}
	
	public int getStopIndex() {
		return this.stopIndex;
	}
	
	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}
	
	public void setStopIndex(int stopIndex) {
		this.stopIndex = stopIndex;
	}
	
	public Color getSelectionColor() {
		return this.selectionColor.getColorValue();
	}

	public Color getSNVColor() {
		return this.snvColor.getColorValue();
	}

	public String getChromosomePrefix() {
		return this.chromPrefix.getStringValue();
	}

	public int getElongation() {
		return this.snvElongation.getIntValue();
	}
	
	public ColorGradient getColorGradient() {
		return this.corColorGradient.getColorGradient();
	}

	public double getCorrelationThreshold() {
		return this.correlationThreshold.getDoubleValue();
	}
}
