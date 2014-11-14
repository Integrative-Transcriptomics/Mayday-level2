package mayday.GWAS.visualizations.matrices.association;

import java.awt.Color;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradientSetting;

public class MatrixSetting extends HierarchicalSetting {

	private ColorSetting selectionColor;
	private IntSetting cellHeight;
	private IntSetting cellWidth;
	private ColorGradientSetting gradient;
	private DoubleSetting circleScaling;
	
	private BooleanSetting plotDiagonal;
	
	private MatrixComponent matrix;
	
	public MatrixSetting(MatrixComponent matrix) {
		super("Matrix Setting");
		this.matrix = matrix;
		
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.RED));
		addSetting(gradient = new ColorGradientSetting("Color Gradient", null, ColorGradient.createDefaultGradient(-1, +1)));
		addSetting(cellHeight = new IntSetting("Cell Height", null, 15));
		addSetting(cellWidth = new IntSetting("Cell Width", null, 15));
		addSetting(plotDiagonal = new BooleanSetting("Highlight Diagonal", "Plot a highlighting diagonal line", false));
		addSetting(circleScaling = new DoubleSetting("Circle Scaling", null, 1., 0., 1., true, true));
		
		addChangeListener(new MSChangeListener());
	}
	
	public ColorGradient getGradient() {
		return this.gradient.getColorGradient();
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
	
	public MatrixSetting clone() {
		MatrixSetting s = new MatrixSetting(matrix);
		s.fromPrefNode(this.toPrefNode());
		return s;
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
	
	private class MSChangeListener implements SettingChangeListener {
		@Override
		public void stateChanged(SettingChangeEvent e) {
			matrix.updatePlot();
		}
	}
}
