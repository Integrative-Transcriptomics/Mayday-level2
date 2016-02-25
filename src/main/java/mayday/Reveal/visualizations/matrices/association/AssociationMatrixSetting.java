package mayday.Reveal.visualizations.matrices.association;

import java.awt.Color;

import mayday.core.settings.SettingComponent;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.PredefinedGradients;

public class AssociationMatrixSetting extends HierarchicalSetting {

	public static final int NUM_SNV_PAIRS = 0;
	public static final int AVERAGE_P_VALUE = 1;
	
	public static final int MEAN_EXPRESSION = 0;
	public static final int MEDIAN_EXPRESSION = 1;
	public static final int MIN_EXPRESSION = 2;
	public static final int MAX_EXPRESSION = 3;
	
	private AssociationMatrix matrix;
	private DoubleSetting pValueThreshold;
	
	public static final int SNPPAIRCOUNT = 0;
	public static final int PVALUE = 1;
	
	private BooleanSetting normalizeLD;
	
	private ColorSetting selectionColor;
	private IntSetting cellHeight;
	private IntSetting cellWidth;
	private ColorGradientSetting betaGradient;
	private ColorGradientSetting expressionGradient;
	private DoubleSetting circleScaling;
	
	private BooleanSetting plotDiagonal;
	
	private RestrictedStringSetting snvAggregationSetting;
	private RestrictedStringSetting geneAggregationSetting;
	
	private BooleanSetting showGradients;
	
	public AssociationMatrixSetting(AssociationMatrix matrix) {
		super("Association Matrix Setting");
		this.matrix = matrix;
		
		addSetting(cellWidth = new IntSetting("Cell Width", null, 20));
		addSetting(cellHeight = new IntSetting("Cell Height", null, 20));
		addSetting(circleScaling = new DoubleSetting("Scaling", null, 0.005, 0., 1., true, true));

		addSetting(geneAggregationSetting = new RestrictedStringSetting("Gene Aggregation Method", null, 0, "Mean", "Median", "Minimum", "Maximum"));
		addSetting(snvAggregationSetting = new RestrictedStringSetting("SNV Aggregation Method", null, 0, "Number of SNV-Pairs", "Average p-Value"));
		
		addSetting(pValueThreshold = new DoubleSetting("p-Value Threshold", "p-value threshold for association", 0.05));
		addSetting(normalizeLD = new BooleanSetting("Normalize LD", null, false));
		
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED.brighter()));
		addSetting(expressionGradient = new ColorGradientSetting("Expression Color Gradient", null, ColorGradient.createDefaultGradient(-1, +1, PredefinedGradients.BREWER_BLUE_WHITE_RED)));
		addSetting(betaGradient = new ColorGradientSetting("SNV Effect Color Gradient", null, ColorGradient.createDefaultGradient(-1, +1, PredefinedGradients.BREWER_BLUE_WHITE_RED)));

		addSetting(plotDiagonal = new BooleanSetting("Highlight Diagonal", "Plot a highlighting diagonal line", false));
		
		addSetting(showGradients = new BooleanSetting("Show Gradients", null, false));
		
		addChangeListener(new SLAMChangeListener());
	}
	
	public boolean getShowGradients() {
		return this.showGradients.getBooleanValue();
	}
	
	public SettingComponent getBetaColorGradientGUI() {
		return this.betaGradient.getGUIElement();
	}
	
	public SettingComponent getExpressionColorGradientGUI() {
		return this.expressionGradient.getGUIElement();
	}
	
	public int getGeneAggregationMethod() {
		return this.geneAggregationSetting.getSelectedIndex();
	}
	
	public int getSNVAggregationMethod() {
		return this.snvAggregationSetting.getSelectedIndex();
	}
	
	public boolean normalizeLD() {
		return this.normalizeLD.getBooleanValue();
	}
	
	public double getPValueThreshold() {
		return this.pValueThreshold.getDoubleValue();
	}
	
	public ColorGradient getBetaColorGradient() {
		return this.betaGradient.getColorGradient();
	}
	
	public void setBetaGradient(double min, double max, int resolution) {
		ColorGradient cg = this.betaGradient.getColorGradient();
		cg.setMin(min);
		cg.setMax(max);
		cg.setResolution(resolution);
		this.betaGradient.setColorGradient(cg);
	}
	
	public ColorGradient getExpressionColorGradient() {
		return this.expressionGradient.getColorGradient();
	}
	
	public void setExpressionColorGradient(double min, double max, int resolution) {
		boolean changed = false;
		ColorGradient cg = this.expressionGradient.getColorGradient();
		if(cg.getMin() != min){
			cg.setMin(min);
			changed = true;
		}
		if(cg.getMax() != max){
			cg.setMax(max);
			changed = true;
		}
		if(cg.getResolution() != resolution){
			cg.setResolution(resolution);
			changed = true;
		}
		if(changed){
			this.expressionGradient.setColorGradient(cg);
		}
	}
	
	public boolean plotDiagonal() {
		return this.plotDiagonal.getBooleanValue();
	}

	public Color getSelectionColor() {
		return this.selectionColor.getColorValue();
	}
	
	public int getCellHeight() {
		return cellHeight.getIntValue();
	}
	
	public int getCellWidth() {
		return cellWidth.getIntValue();
	}
	
	public double getCircleScaling() {
		return circleScaling.getDoubleValue();
	}
	
	public void modifyCellWidth(int rot) {
		int oldValue = cellWidth.getIntValue();
		if(oldValue + rot > 1) {
			this.cellWidth.setIntValue(oldValue + rot);
			fireChanged();
		}
	}
	
	public void modifyCellHeight(int rot) {
		int oldValue = cellHeight.getIntValue();
		if(oldValue + rot > 1) {
			this.cellHeight.setIntValue(oldValue + rot);
			fireChanged();
		}
	}
	
	public void modifyCellSize(int rot) {
		int oldWidth = cellWidth.getIntValue();
		int oldHeight = cellHeight.getIntValue();
		boolean change = false;
		if(oldWidth + rot > 1) {
			this.cellWidth.setIntValue(oldWidth + rot);
			change = true;
		}
		if(oldHeight + rot > 1) {
			this.cellHeight.setIntValue(oldHeight + rot);
			change = true;
		}
		if(change)
			fireChanged();
	}
	
	public AssociationMatrixSetting clone() {
		AssociationMatrixSetting s = new AssociationMatrixSetting(matrix);
		s.fromPrefNode(this.toPrefNode());
		return s;
	}
	
	private class SLAMChangeListener implements SettingChangeListener {

		@Override
		public void stateChanged(SettingChangeEvent e) {
			Object source = e.getSource();
			
			boolean recalculate = false;
			
			if(source == geneAggregationSetting 
					|| source == snvAggregationSetting
					|| source == pValueThreshold
					|| source == normalizeLD)
				recalculate = true;
			
			matrix.updatePlot(recalculate);
		}
	}
}
