package mayday.vis3.plots.tagcloud;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import mayday.vis3.components.MultiPlotPanel;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.legend.ProbeListLegend;

public class TagCloudLegend extends ProbeListLegend {
	
	private TagCloudViewer viewer;
	
	public TagCloudLegend(TagCloudViewer viewer) {
		super();
		this.viewer = viewer;
		viewer.setLegend(this);
	}

	public void updateLegend() {
		removeAll();
		if(viewer.getModel() instanceof TagCloudModel) {
			Map<Color, Integer> legendMap = ((TagCloudModel)viewer.getModel()).getLegendMap();
			int elementcount = legendMap.size();
			int[] dims;
			switch(style.getSelectedIndex()) {
			case 1: // horizontal
				dims = new int[]{1,elementcount};
				break;
			case 2: // vertical
				dims = new int[]{elementcount,1};
				break;
			default: // matrix
				dims = MultiPlotPanel.findBestRC(elementcount);
			}
			setLayout(new GridLayout(dims[1],dims[0]));	
			
			List<TagCloudLegendItem> items = new LinkedList<TagCloudLegendItem>();
			
			for(Color c : legendMap.keySet()) {
				items.add(new TagCloudLegendItem(c, legendMap.get(c).toString() + " Probes"));
			}
			
			Collections.sort(items);
			
			for(int i = 0; i < items.size(); i++) {
				add(items.get(i));
			}
			
			revalidate();
		}
	}
	
	@Override
	public void setup(PlotContainer plotContainer) {
		viewModel = plotContainer.getViewModel();
		viewModel.addViewModelListener(this);
		plotContainer.addViewSetting(setting, viewer);
		updateLegend();
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if(visible)
			updateLegend();
	}
}
