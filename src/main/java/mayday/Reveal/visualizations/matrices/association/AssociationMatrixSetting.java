package mayday.Reveal.visualizations.matrices.association;

import java.awt.Color;

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

public class AssociationMatrixSetting extends HierarchicalSetting {

	private AssociationMatrix matrix;
	private DoubleSetting pValueThreshold;
	
	public static final int SNPPAIRCOUNT = 0;
	public static final int PVALUE = 1;
	public static final int R2VALUE = 2;
	
	private RestrictedStringSetting dataTypes;
	private String[] dataTypeNames = {"SNP Pair Counts", "Cumulative p-Value", "Cumulative R² Value"};
	
	private BooleanSetting useLDBlocks;
	
	private ColorSetting selectionColor;
	private IntSetting cellHeight;
	private IntSetting cellWidth;
	private ColorGradientSetting matrixGradient;
	private ColorGradientSetting expressionGradient;
	private DoubleSetting circleScaling;
	
	private BooleanSetting plotDiagonal;
	
	public AssociationMatrixSetting(AssociationMatrix matrix) {
		super("Single Locus Association Matrix Setting");
		this.matrix = matrix;
		
		addSetting(pValueThreshold = new DoubleSetting("p-Value Threshold", "p-value threshold for single locus association", 0.05));
		addSetting(dataTypes = new RestrictedStringSetting("Data Types", "Select the data type from single locus results that should be visualized", 0, dataTypeNames));
		addSetting(useLDBlocks = new BooleanSetting("Use LD Blocks", null, false));
		
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(expressionGradient = new ColorGradientSetting("Expression Color Gradient", null, ColorGradient.createDefaultGradient(-1, +1)));
		addSetting(matrixGradient = new ColorGradientSetting("Matrix Color Gradient", null, ColorGradient.createDefaultGradient(-1, +1)));
		addSetting(cellHeight = new IntSetting("Cell Height", null, 15));
		addSetting(cellWidth = new IntSetting("Cell Width", null, 15));
		addSetting(plotDiagonal = new BooleanSetting("Highlight Diagonal", "Plot a highlighting diagonal line", false));
		addSetting(circleScaling = new DoubleSetting("Circle Scaling", null, 1., 0., 1., true, true));
		
		addChangeListener(new SLAMChangeListener());
	}
	
	public boolean useLDBlocks() {
		return this.useLDBlocks.getBooleanValue();
	}
	
	public int getDataType() {
		return this.dataTypes.getSelectedIndex();
	}
	
	public double getPValue() {
		return this.pValueThreshold.getDoubleValue();
	}
	
	public ColorGradient getMatrixColorGradient() {
		return this.matrixGradient.getColorGradient();
	}
	
	public ColorGradient getExpressionColorGradient() {
		return this.expressionGradient.getColorGradient();
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
			matrix.updatePlot();
		}
	}
}
