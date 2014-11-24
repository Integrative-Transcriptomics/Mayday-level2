package mayday.Reveal.visualizations.matrices.twolocus;

import java.awt.Color;

import mayday.core.settings.AbstractSetting;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.GenericSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3.gradient.ColorGradient;
import mayday.vis3.gradient.ColorGradient.MIDPOINT_MODE;
import mayday.vis3.gradient.ColorGradientSetting;
import mayday.vis3.gradient.agents.Agent_Tricolore;

/**
 * @author jaeger
 *
 */
public class AssociationMatrixSetting extends HierarchicalSetting {

	public static final int RESIZE_PLOT = 1;
	public static final int REARRANGE_PLOT_COMPONENTS = 2;
	public static final int LD_BLOCKS = 3;
	public static final int DATA_VALUES = 4;
	
	public static final int NUMMBER_OF_SNPS = 0;
	public static final int P_VALUE = 1;
	
	private AssociationMatrix matrix;
	
	private ColorGradientSetting colorGradient;
	
	private ColorSetting selectionColor;
	
	private RestrictedStringSetting selectionMode;
	
	private IntSetting minCellWidth;
	private IntSetting minCellHeight;
	private IntSetting headerSize;
	private IntSetting numCols;
	
	private RestrictedStringSetting dataValues;
	
	private BooleanSetting correctLDBlocks;

	/**
	 * @param matrix
	 */
	public AssociationMatrixSetting(AssociationMatrix matrix) {
		super("Association Matrix Setting");	
		this.matrix = matrix;
		
		
		addSetting(dataValues = new RestrictedStringSetting("Data Values", null, 0, "# SNP Pairs", "p-Value"));
		addSetting(colorGradient = new ColorGradientSetting("Color Gradient", null, ColorGradient.createDefaultGradient(0, 100))
			.setLayoutStyle(ColorGradientSetting.LayoutStyle.FULL));
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.CYAN));		
		addSetting(selectionMode = new RestrictedStringSetting("Selection Mode", null, 0, "Probes", "SNPs"));
		
		HierarchicalSetting sizeSetting = new HierarchicalSetting("Layout components");
		
		sizeSetting.addSetting(numCols = new IntSetting("Number of Probe columns", null, 3));
		sizeSetting.addSetting(minCellWidth = new IntSetting("Minimal SNP cell width", null, 10));
		sizeSetting.addSetting(minCellHeight = new IntSetting("Minimal SNP cell height", null, 10));
		sizeSetting.addSetting(headerSize = new IntSetting("Header(s) size", null, 50));
		
		addSetting(sizeSetting);
		
		HierarchicalSetting ldBlocksSetting = new HierarchicalSetting("LD Block Setting");
		ldBlocksSetting.addSetting(correctLDBlocks = new BooleanSetting("Use LD Blocks", "Correct edge weights based on LD block structures", false));
		
		addSetting(ldBlocksSetting);
		
		this.addChangeListener(new AMChangeListener());
	}
	
	public int getDataValues() {
		return this.dataValues.getSelectedIndex();
	}
	
	/**
	 * @return true if LD blocks should be considered
	 */
	public boolean useLDBlocks() {
		return this.correctLDBlocks.getBooleanValue();
	}
	
	/**
	 * @return minimal SNP cell width
	 */
	public int getMinCellWidth() {
		return this.minCellWidth.getIntValue();
	}
	
	/**
	 * @return minimal SNP cell height
	 */
	public int getMinCellHeight() {
		return this.minCellHeight.getIntValue();
	}
	
	/**
	 * @return header size
	 */
	public int getHeaderSize() {
		return this.headerSize.getIntValue();
	}
	
	/**
	 * @return number of probe columns
	 */
	public int getNumberOfProbeColumns() {
		return this.numCols.getIntValue();
	}
	
	/**
	 * @return true if gene selection mode is enabled, else false
	 */
	public boolean isGeneSelectionMode() {
		return selectionMode.getSelectedIndex() == 0;
	}
	
	/**
	 * @return true if snp selection mode is enabled, else false
	 */
	public boolean isSNPSelectionMode() {
		return selectionMode.getSelectedIndex() == 1;
	}
	
	/**
	 * @return the color for selections
	 */
	public Color getSelectionColor() {
		return selectionColor.getColorValue();
	}
	
	/**
	 * set an initial color gradient
	 */
	public void setColorGradient() {
		double Min = 0;
		double Max = matrix.distinctIntensitiesArray.size();
		
		Color c1 = new Color(69, 117, 180);
		c1 = Color.WHITE;
		Color c12 = new Color(255, 255, 191);
		Color c2 = new Color(215, 48, 39);
		
		ColorGradient cg = new ColorGradient(Min, (Max-Min)/2d+Min, Max, 
				false, (int)Max, MIDPOINT_MODE.Center, 
				new Agent_Tricolore(false, c1, c12, c2, 0.));
		cg.setMidpoint(40d);
		colorGradient.setColorGradient(cg);
	}
	
	/**
	 * @return the chosen color gradient
	 */
	public ColorGradient getColorGradient() {
		return this.colorGradient.getColorGradient();
	}

	public AssociationMatrixSetting clone() {
		AssociationMatrixSetting ams = new AssociationMatrixSetting(matrix);
		ams.fromPrefNode(this.toPrefNode());
		return ams;
	}
	
	private class AMChangeListener implements SettingChangeListener {
		private AbstractSetting source;

		@Override
		public void stateChanged(SettingChangeEvent e) {
			source = (GenericSetting)e.getSource();
			
			if(source.equals(minCellWidth) || source.equals(minCellHeight) || source.equals(headerSize)){
				matrix.updatePlot(RESIZE_PLOT);
			} else if(source.equals(numCols)){
				matrix.updatePlot(REARRANGE_PLOT_COMPONENTS);
			} else if(source.equals(correctLDBlocks)) {
				matrix.updatePlot(LD_BLOCKS);
			} else if(source.equals(dataValues)) {
				matrix.updatePlot(DATA_VALUES);
			} else {
				matrix.updatePlot();
			}
		}
	}

	/**
	 * switch to probe selection mdoe
	 */
	public void useProbeSelectionMode() {
		this.selectionMode.setSelectedIndex(0);
	}

	/**
	 * switch to snp selection mode
	 */
	public void useSNPSelectionMode() {
		this.selectionMode.setSelectedIndex(1);
	}
}
