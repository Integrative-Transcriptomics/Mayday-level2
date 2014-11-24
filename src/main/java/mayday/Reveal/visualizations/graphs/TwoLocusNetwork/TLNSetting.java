package mayday.Reveal.visualizations.graphs.TwoLocusNetwork;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

/**
 * @author jaeger
 *
 */
public class TLNSetting extends HierarchicalSetting {

	private BooleanSetting showEdgeProperties;
	private TLN graph;
	
	private DoubleSetting singlelocuspValue;
	private DoubleSetting twolocuspValue;
	private DoubleSetting r2Value;
	
	private BooleanSetting showEdgeLabels;
	private BooleanSetting correctLDBlocks;
	
	private RestrictedStringSetting dataValues;
	
	public static final int NUMBER_OF_SNP_PAIRS = 0;
	public static final int P_VALUE = 1;
	
	/**
	 * @param graph
	 */
	public TLNSetting(TLN graph) {
		super("Gene Association Graph Setting");
		this.graph = graph;
		
		addSetting(showEdgeProperties = new BooleanSetting("Show graph modification dialog", null, false));
		
		addSetting(dataValues = new RestrictedStringSetting("Data Values", "Choose which type of data should be visualized.", 0, "# SNP Pairs", "p-Value"));
		
		addSetting(twolocuspValue = new DoubleSetting("Two Locus p-value threshold", null, 0.05));
		addSetting(singlelocuspValue = new DoubleSetting("Single Locus p-value threshold", null, 0.05));
		addSetting(r2Value = new DoubleSetting("Single Locus R2-value threshold", null, 0.1));
		
		HierarchicalSetting edgeSetting = new HierarchicalSetting("Edge Properties");
		HierarchicalSetting nodeSetting = new HierarchicalSetting("Node Properties");
		
		edgeSetting.addSetting(showEdgeLabels = new BooleanSetting("Show edge labels", null, true));
		
		addSetting(nodeSetting);
		addSetting(edgeSetting);
		
		HierarchicalSetting ldBlocksSetting = new HierarchicalSetting("LD Block Setting");
		ldBlocksSetting.addSetting(correctLDBlocks = new BooleanSetting("Use LD Blocks", "Correct edge weights based on LD block structures", false));
		addSetting(ldBlocksSetting);
		
		showEdgeProperties.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {
				TLNSetting.this.graph.edgeControls.setVisible(showEdgeProperties.getBooleanValue());
			}
		});
		
		this.addChangeListener(new SettingChangeListener() {
			@Override
			public void stateChanged(SettingChangeEvent e) {
				TLN g = TLNSetting.this.graph;
				Object source = e.getSource();
				if(source.equals(showEdgeProperties)) {
					return;
				}else if(source.equals(correctLDBlocks)) {
					g.updateTask();
				} else if(source.equals(dataValues)) {
					g.updateTask();
				} else {
					g.getVisualizationViewer().repaint();
				}
			}
		});
	}
	
	public int getDataValues() {
		return this.dataValues.getSelectedIndex();
	}
	
	/**
	 * @return showedgeproperties setting
	 */
	public BooleanSetting getShowEdgeProperties() {
		return this.showEdgeProperties;
	}
	
	/**
	 * @return single locus p value threshold
	 */
	public double getSingleLocusPValueThreshold() {
		return this.singlelocuspValue.getDoubleValue();
	}
	
	/**
	 * @return two locus p-value threshold
	 */
	public double getTwoLocusPValueThreshold() {
		return this.twolocuspValue.getDoubleValue();
	}
	
	/**
	 * @return R2 value threshold
	 */
	public double getR2Threshold() {
		return this.r2Value.getDoubleValue();
	}
	
	/**
	 * @return true if LD blocks should be considered
	 */
	public boolean useLDBlocks() {
		return this.correctLDBlocks.getBooleanValue();
	}
	
	public TLNSetting clone() {
		TLNSetting pgs = new TLNSetting(graph);
		pgs.fromPrefNode(this.toPrefNode());
		return pgs;
	}
	
	public boolean showEdgeLabels() {
		return this.showEdgeLabels.getBooleanValue();
	}
}
