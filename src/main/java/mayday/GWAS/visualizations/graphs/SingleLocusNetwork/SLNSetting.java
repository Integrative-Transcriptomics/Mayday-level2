package mayday.GWAS.visualizations.graphs.SingleLocusNetwork;

import java.awt.Color;
import java.util.Set;

import mayday.core.settings.events.SettingChangeEvent;
import mayday.core.settings.events.SettingChangeListener;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SLNSetting extends HierarchicalSetting {

	private SLN sln;
	
	private Color defaultSelectionColor = Color.RED;
	
	private BooleanSetting useLDBlocks;
	private DoubleSetting pValue;
	private DoubleSetting r2Value;
	
	private ColorSetting edgeSelectionColor;
	private ColorSetting nodeSelectionColor;
	
	private BooleanSetting displayEdgeLabels;
	private BooleanSetting includeSelfEdges;
	
	private RestrictedStringSetting filterExternalSNPs;
	
	public SLNSetting(SLN sln) {
		super("Single Locus Network Setting");
		this.sln = sln;
		
		//TODO add settings
		addSetting(pValue = new DoubleSetting("p value threshold", null, 0.05));
		addSetting(r2Value = new DoubleSetting("r2 value threshold", null, 0.5)); 
		addSetting(useLDBlocks = new BooleanSetting("Use LD Blocks", null, false));
		
		addSetting(edgeSelectionColor = new ColorSetting("Edge selection color", null, defaultSelectionColor));
		addSetting(nodeSelectionColor = new ColorSetting("Node selection color", null, defaultSelectionColor));
		
		addSetting(displayEdgeLabels = new BooleanSetting("Show edge labels", null, true));
		
		addSetting(includeSelfEdges = new BooleanSetting("Show self edges", null, true));
		
		Set<String> externalSNPListNames = sln.getData().getSNPListNames();
		String[] extNames = new String[externalSNPListNames.size()+1];
		int i = 1;
		extNames[0] = "None";
		for(String name : externalSNPListNames)
			extNames[i++] = name;
		
		addSetting(filterExternalSNPs = new RestrictedStringSetting("Use external SNP List", null, 0, extNames));
		
		addChangeListener(new SLNChangeListener());
	}
	
	/**
	 * @return name of the selected external SNP list
	 */
	public String getExternalSNPListName() {
		if(this.filterExternalSNPs.getSelectedIndex() == 0) {
			return null;
		}
		return this.filterExternalSNPs.getStringValue();
	}
	
	public boolean showEdgeLabels() {
		return this.displayEdgeLabels.getBooleanValue();
	}
	
	public Color getEdgeSelectionColor() {
		return this.edgeSelectionColor.getColorValue();
	}
	
	public Color getNodeSelectionColor() {
		return this.nodeSelectionColor.getColorValue();
	}
	
	public boolean useLDBlocks() {
		return this.useLDBlocks.getBooleanValue();
	}
	
	public double getPValueThreshold() {
		return this.pValue.getDoubleValue();
	}
	
	public double getR2ValueThreshold() {
		return this.r2Value.getDoubleValue();
	}
	
	public SLNSetting clone() {
		SLNSetting slns = new SLNSetting(sln);
		slns.fromPrefNode(toPrefNode());
		return slns;
	}
	
	private class SLNChangeListener implements SettingChangeListener {
		@Override
		public void stateChanged(SettingChangeEvent e) {
			if(e.getSource().equals(edgeSelectionColor)) {
				sln.getVisualizationViewer().repaint();
			} else if(e.getSource().equals(nodeSelectionColor)) {
				sln.getVisualizationViewer().repaint();
			} else if(e.getSource().equals(displayEdgeLabels)) {
				sln.getVisualizationViewer().repaint();
			} else {
				sln.calculateEdgeWeights();
			}
		}
	}
}
