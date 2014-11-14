package mayday.GWAS.visualizations.matrices.association;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class AssociationMatrixSetting extends HierarchicalSetting {

	private AssociationMatrix matrix;
	private DoubleSetting pValueThreshold;
	
	public static final int SNPPAIRCOUNT = 0;
	public static final int PVALUE = 1;
	public static final int R2VALUE = 2;
	
	private RestrictedStringSetting dataTypes;
	private String[] dataTypeNames = {"SNP Pair Counts", "Cumulative p-Value", "Cumulative R² Value"};
	
	private BooleanSetting useLDBlocks;
	
	public AssociationMatrixSetting(AssociationMatrix matrix) {
		super("Single Locus Association Matrix Setting");
		this.matrix = matrix;
		
		addSetting(pValueThreshold = new DoubleSetting("p-Value Threshold", "p-value threshold for single locus association", 0.05));
		addSetting(dataTypes = new RestrictedStringSetting("Data Types", "Select the data type from single locus results that should be visualized", 0, dataTypeNames));
		addSetting(useLDBlocks = new BooleanSetting("Use LD Blocks", null, false));
		
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
