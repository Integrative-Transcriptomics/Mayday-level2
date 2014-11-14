package mayday.tiala.multi.settings;

import java.awt.Color;

import mayday.core.gui.GUIUtilities;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.ColorSetting;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.AlignmentStoreEvent;

/**
 * @author jaeger
 *
 */
public class AlignmentStoreSettings extends HierarchicalSetting {
	
	protected AlignmentStore store;
	
	protected BooleanSetting showOnlyMatchingTimePoints;
	protected BooleanSetting isScoringForAll;
	protected ColorSetting[] selectionColors;
	
	/**
	 * default constructor
	 * @param store
	 */
	public AlignmentStoreSettings(AlignmentStore store) {
		super("Alignment Store Settings");
		this.store = store;
		
		int numDatasets = store.getTimepointDatasets().size();
		selectionColors = new ColorSetting[numDatasets];
		Color[] colors = this.generateSelectionColors();
		
		this.addSetting(showOnlyMatchingTimePoints = new BooleanSetting("Show only matching time points?", "Show only matching time points?", true));
		this.addSetting(isScoringForAll = new BooleanSetting("Calculate scores for all probes?", "Calculate scores for all probes?", true));
		
		for(int i = 0; i < numDatasets; i++) {
			String name = store.getTimepointDatasets().get(i).getDataSet().getName();
			this.addSetting(selectionColors[i] = new ColorSetting("Selection Color of data set " + name, "Selection Color of data set " + name, colors[i]));
		}
	}
	
	/**
	 * @return true if only matching time points should be displayed
	 */
	public boolean showOnlyMatching() {
		return this.showOnlyMatchingTimePoints.getBooleanValue();
	}

	/**
	 * @param onlyMatching
	 */
	public void setShowOnlyMatching(boolean onlyMatching) {
		this.showOnlyMatchingTimePoints.setBooleanValue(onlyMatching);
		store.eventFirer.fireEvent(new AlignmentStoreEvent(store, AlignmentStoreEvent.MATCHINGDISPLAY_CHANGED));
	}

	/**
	 * @return scoring for all ? true : false
	 */
	public boolean isScoringForAll() {
		return this.isScoringForAll.getBooleanValue();
	}

	/**
	 * @param scoringForAll
	 */
	public void setScoringForAll(boolean scoringForAll) {
		this.isScoringForAll.setBooleanValue(scoringForAll);
		store.fireScoringChanged();
	}

	/**
	 * @return array of selection colors
	 */
	public Color[] getSelectionColors() {
		Color[] colors = new Color[this.selectionColors.length];
		for(int i = 0; i < colors.length; i++) {
			colors[i] = this.selectionColors[i].getColorValue();
		}
		return colors;
	}

	/**
	 * @param datasetID
	 * @param c
	 */
	public void setSelectionColor(int datasetID, Color c) {
		this.selectionColors[datasetID].setColorValue(c);
		store.fireSelectionColorChanged();
	}
	
	private Color[] generateSelectionColors() {
		int numDataSets = store.getTimepointDatasets().size();
		return GUIUtilities.rainbow(numDataSets, 0.75);
	}
	
	public AlignmentStoreSettings clone() {
		AlignmentStoreSettings ass = new AlignmentStoreSettings(store);
		ass.fromPrefNode(this.toPrefNode());
		return ass;
	}

	/**
	 * @param ID
	 * @return selection color for the dataset with identifier = ID
	 */
	public Color getSelectionColor(int ID) {
		return this.selectionColors[ID].getColorValue();
	}
}
