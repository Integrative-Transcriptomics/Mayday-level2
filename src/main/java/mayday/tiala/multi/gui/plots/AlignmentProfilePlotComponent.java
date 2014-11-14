package mayday.tiala.multi.gui.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.tiala.multi.data.AlignmentStore;
import mayday.tiala.multi.data.probes.MultiProbe;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModel;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class AlignmentProfilePlotComponent extends AutoTimepointProfilePlotComponent {
	
	protected DataSeries[] selectionLayers;
	protected Color[] selectionColors;
	protected Color[] colors;
	
	protected AlignmentStore store;
	
	public AlignmentProfilePlotComponent(AlignmentStore store) {
		this.store = store;
		this.selectionLayers = new DataSeries[store.getAlignedDataSets().getNumberOfDataSets()-1];
	}
	
	public void setSelectionColors(Color[] selectionColors) {
		this.selectionColors = selectionColors;
		if (viewModel!=null && viewModel.getSelectedProbes().size()>0)
			updatePlot();
	}
	
	public void setColors(Color[] colors) {
		this.colors = colors;
		if(viewModel != null) {
			updatePlot();
		}
	}
	
	public void setup(PlotContainer c) {
		super.setup(c);
		settings.getInferData().setBooleanValue(false);
		probeColorSetter = new ProbeColorSetter() {
			public void modify(Graphics2D g, Object o) {
				if (o instanceof MultiProbe) {
					MultiProbe pb = (MultiProbe)o;
					int position = pb.position;
					g.setColor(colors[position]);
				}					
			}					
		};
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
	}
	
	@SuppressWarnings("unchecked")
	public void select(Color c) {
		if(selectionColors == null) {
			return;
		}
		
		Set<Probe>[] selections = new HashSet[store.getAlignedDataSets().getNumberOfDataSets()];	
		
		for(int i = 0; i < selections.length; i++) {
			selections[i] = new HashSet<Probe>();
		}
		
		for(ProbeList probe_list : getProbeLists()) {
			for (Probe pb : probe_list.getAllProbes()) {
				int pos = ((MultiProbe)pb).position;
				selections[pos].add(pb);
			}
		}
		
		for(int i = 0; i < selections.length; i++) {
			selections[i].retainAll(viewModel.getSelectedProbes());
		}
		
		currentSelection.clear();
		
		for(int i = 0; i < selections.length; i++) {
			currentSelection.addAll(selections[i]);
		}

		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);
		
		for(int i = 0; i < selectionLayers.length; i++) {
			if(selectionLayers[i] != null) {
				removeDataSeries(selectionLayers[i]);
			}
		}
		
		selectionLayer = view(selections[0]);
		selectionLayer.setColor(selectionColors[0]);
		selectionLayer.setStroke(new BasicStroke(3));
		
		for(int i = 1; i < selections.length; i++) {
			selectionLayers[i-1] = view(selections[i]);
			selectionLayers[i-1].setColor(selectionColors[i]);
			selectionLayers[i-1].setStroke(new BasicStroke(3));
		}
		
		addDataSeries(selectionLayer);
		
		for(int i = 0; i < selectionLayers.length; i++) {
			addDataSeries(selectionLayers[i]);
		}

		clearBuffer();
		repaint();
	}

	public void setInferMissing(boolean b) {
		settings.getInferData().setBooleanValue(b);
	}

	public ViewModel getViewModel() {
		return this.viewModel;
	}
}
