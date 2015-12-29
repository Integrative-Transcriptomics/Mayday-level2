package mayday.vis3d.plots.profileplot;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.settings.typed.RestrictedStringSetting;
import mayday.vis3d.AbstractPlot2DPanel;

/**
 * @author G\u00FCnter J\u00E4ger
 *
 */
public class LabelingSetting extends HierarchicalSetting {

	private AbstractPlot2DPanel panel;
	
	protected BooleanSetting showLabels;
	protected RestrictedStringSetting histAnchors;
	protected RestrictedStringSetting anchorLines;
	protected IntSetting labelsSize;
	
	private int oldFontSize = 24;
	private double oldScale = 0.4;
	
	private static final String AL = "All Profiles", SL = "Selected Profiles", NONE = "No anchor lines";

	/**
	 * @param panel
	 * 
	 */
	public LabelingSetting(AbstractPlot2DPanel panel) {
		super("Profile Plot Labeling");
		this.panel = panel;
		
		String[] anchors = {NONE, AL, SL};
		
		addSetting(showLabels = new BooleanSetting("Show Labels", null, true));
		
		addSetting(histAnchors = new RestrictedStringSetting("Define anchor dimension", null, 0, 
				panel.viewModel.getDataSet().getMasterTable().getExperimentDisplayNames().toArray(new String[0]))
				.setLayoutStyle(mayday.core.settings.generic.ObjectSelectionSetting.LayoutStyle.LIST));
		
		histAnchors.setSelectedIndex(panel.viewModel.getDataSet().getMasterTable().getNumberOfExperiments()-1);
		
		addSetting(anchorLines = new RestrictedStringSetting("Which lines?", null, 0, anchors)
			.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS));
		
		addSetting(labelsSize = new IntSetting("Font Size", null, oldFontSize));
	}
	
	public LabelingSetting clone() {
		LabelingSetting ls = new LabelingSetting(panel);
		ls.fromPrefNode(this.toPrefNode());
		return ls;
	}
	
	/**
	 * @return true, if probe labels should be drawn, else false
	 */
	public boolean drawProbeLabels() {
		return this.showLabels.getBooleanValue();
	}
	/**
	 * @return size of labels in the label histogram
	 */
	public int getLabelsSize() {
		return this.labelsSize.getIntValue();
	}
	
	/**
	 * @return label scale factor
	 */
	public double getLabelsScale() {
		if(oldFontSize != this.getLabelsSize()) {
			double scale = ((double)this.getLabelsSize() / (double)oldFontSize) * oldScale;
			oldFontSize = this.getLabelsSize();
			oldScale = scale;
		}
		return oldScale;
	}
	
	/**
	 * @return anchor dimension
	 */
	public int getLabelAnchorDim() {
		return this.histAnchors.getSelectedIndex();
	}
	
	/**
	 * @param index
	 */
	public void setLabelAnchorDim(int index) {
		int numExps = panel.viewModel.getDataSet().getMasterTable().getNumberOfExperiments();
		if(index >= 0 && index < numExps) {
			this.histAnchors.setSelectedIndex(index);
		}
	}
	
	/**
	 * @return true, if anchor lines should be drawn, else false
	 */
	public boolean drawAnchorLines() {
		return !this.anchorLines.getStringValue().equals(NONE);
	}
	/**
	 * @return true, if all anchor lines should be drawn, else false
	 * 
	 */
	public boolean drawAllAnchorLines() {
		return this.anchorLines.getStringValue().equals(AL);
	}
}
