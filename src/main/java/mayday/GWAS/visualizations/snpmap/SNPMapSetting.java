package mayday.GWAS.visualizations.snpmap;

import java.awt.Color;

import mayday.core.DelayedUpdateTask;
import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.IntSetting;

public class SNPMapSetting extends HierarchicalSetting {

	private IntSetting cellWidth;
	private IntSetting cellHeight;
	private IntSetting aggregationCellHeight;
	
	private ColorSetting noSNPColor;
	private ColorSetting heteroSNPColor;
	private ColorSetting homoSNPColor;
	
	private ColorSetting selectionColor;
	
	private Color noSNPc = new Color(26, 152, 80);
	private Color heteroSNPc = new Color(254, 224, 139);
	private Color homoSNPc = new Color(215, 48, 39);
	
	private SNPMap snpMap;
	
	/**
	 * @param snpMap
	 */
	public SNPMapSetting(SNPMap snpMap) {
		super("SNPMap Setting");
		this.snpMap = snpMap;
		
		HierarchicalSetting cellDimensions = new HierarchicalSetting("Cell Dimensions");
		cellDimensions.addSetting(cellWidth = new IntSetting("Cell Width", null, 10));
		cellDimensions.addSetting(cellHeight = new IntSetting("Cell Height", null, 10));
		cellDimensions.addSetting(aggregationCellHeight = new IntSetting("Aggregation Cell Height", null, 10));
		
		addSetting(cellDimensions);
		
		HierarchicalSetting snpSetting = new HierarchicalSetting("SNP Colors");
		snpSetting.addSetting(noSNPColor = new ColorSetting("Set 'no SNP color'", null, noSNPc));
		snpSetting.addSetting(heteroSNPColor = new ColorSetting("Set 'hetero SNP color'", null, heteroSNPc));
		snpSetting.addSetting(homoSNPColor = new ColorSetting("Set 'homo SNP color'", null, homoSNPc));
		
		addSetting(snpSetting);
		addSetting(selectionColor = new ColorSetting("Selection Color", null, Color.BLACK));
		
		this.addChangeListener(new SMChangeListener());
	}
	
	public int getCellWidth() {
		return this.cellWidth.getIntValue();
	}
	
	public int getCellHeight() {
		return this.cellHeight.getIntValue();
	}
	
	public Color getNoSNPColor() {
		return this.noSNPColor.getColorValue();
	}
	
	public Color getHeteroSNPColor() {
		return this.heteroSNPColor.getColorValue();
	}
	
	public Color getHomoSNPColor() {
		return this.homoSNPColor.getColorValue();
	}
	
	public Color getSelectionColor() {
		return this.selectionColor.getColorValue();
	}
	
	public SNPMapSetting clone() {
		SNPMapSetting sms = new SNPMapSetting(snpMap);
		sms.fromPrefNode(this.toPrefNode());
		return sms;
	}
	
	private class SMChangeListener extends DelayedUpdateTask implements SettingChangeListener {

		public SMChangeListener() {
			super("SMPMap Updater");
		}

		@Override
		public void stateChanged(SettingChangeEvent e) {
			trigger();
		}

		@Override
		protected boolean needsUpdating() {
			return true;
		}

		@Override
		protected void performUpdate() {
			snpMap.resize();
			snpMap.updatePlot();
		}
	}

	public int getAggregationCellHeight() {
		return aggregationCellHeight.getIntValue();
	}
}
