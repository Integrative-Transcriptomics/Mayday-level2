package mayday.Reveal.visualizations.SNVSummaryPlot;

import java.awt.Color;

import mayday.Reveal.RevealDefaults;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;

public class SNVSummaryPlotSetting extends HierarchicalSetting {

	private IntSetting cellWidth;
	private ColorSetting selectionColor;
	
	private IntSetting statTestIndex;
	private BooleanSetting showStatTestValueString;
	private IntSetting pValueDigits;
	
	//TODO add statistics selection
	
	private BooleanSetting isAggregationStacked;
	
	private ColorSetting aggHomColor;
	private ColorSetting aggHetColor;
	private ColorSetting aggRefColor;
	
	private IntSetting subjectIndex;
	
	
	private int startIndex;
	private int stopIndex;
	
	public SNVSummaryPlotSetting() {
		super("SNV Summary Plot Setting");
		
		addSetting(cellWidth = new IntSetting("Cell Width", null, 20));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, RevealDefaults.DEFAULT_SELECTION_COLOR));
		
		addSetting(statTestIndex = new IntSetting("Statistical Test Index", null, 0));
		addSetting(showStatTestValueString = new BooleanSetting("Show p-values", null, true));
		addSetting(pValueDigits = new IntSetting("# p-Value Digits", null, 4));
		
		addSetting(isAggregationStacked = new BooleanSetting("Stacked Aggregation View", null, false));
		
		addSetting(aggHomColor = new ColorSetting("Homozygous SNV Color", null, Color.DARK_GRAY.darker()));
		addSetting(aggHetColor = new ColorSetting("Heterozygous SNV Color", null, Color.GRAY));
		addSetting(aggRefColor = new ColorSetting("Reference SNV Color", null, Color.WHITE));
		
		addSetting(subjectIndex = new IntSetting("Subject Index", null, 0));
	}
	
	public int getCellWidth() {
		return this.cellWidth.getIntValue();
	}
	
	public Color getSelectionColor() {
		return this.selectionColor.getColorValue();
	}
	
	public SNVSummaryPlotSetting clone() {
		SNVSummaryPlotSetting s = new SNVSummaryPlotSetting();
		s.fromPrefNode(this.toPrefNode());
		return s;
	}

	public int getStartIndex() {
		return this.startIndex;
	}
	
	public int getStopIndex() {
		return this.stopIndex;
	}
	
	public void setStartIndex(int start) {
		this.startIndex = start;
	}
	
	public void setStopIndex(int stop) {
		this.stopIndex = stop;
	}
	
	public boolean isAggregationStacked() {
		return this.isAggregationStacked.getBooleanValue();
	}
	
	public Color getAggHomColor() {
		return this.aggHomColor.getColorValue();
	}
	
	public Color getAggHetColor() {
		return this.aggHetColor.getColorValue();
	}
	
	public Color getAggRefColor() {
		return this.aggRefColor.getColorValue();
	}
	
	public int getSubjectIndex() {
		return this.subjectIndex.getIntValue();
	}

	public int getStatTestIndex() {
		return this.statTestIndex.getIntValue();
	}
	
	public boolean showStatTestValues() {
		return this.showStatTestValueString.getBooleanValue();
	}
	
	public int getPValueDigits() {
		return this.pValueDigits.getIntValue();
	}

	public IntSetting getCellWidthSetting() {
		return this.cellWidth;
	}

	public void setCellWidth(int cellWidth) {
		this.cellWidth.setIntValue(cellWidth);
		this.fireChanged();
	}
}
