package mayday.tiala.pairwise.gui.plots;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.HashSet;
import java.util.Set;

import mayday.core.Probe;
import mayday.core.ProbeList;
import mayday.tiala.pairwise.data.probes.PairedProbe;
import mayday.vis3.ColorProviderSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.vis2base.DataSeries;

@SuppressWarnings("serial")
public class AlignmentProfilePlotComponent extends AutoTimepointProfilePlotComponent {
	
	protected DataSeries selectionLayer2;
	protected Color selectionColor2;
	
	protected Color color1, color2; 
	
	public void setSelectionColors(Color sel1, Color sel2) {
		settings.getSelectionColor().setColorValue(sel1);
		selectionColor2 = sel2;
		if (viewModel!=null && viewModel.getSelectedProbes().size()>0)
			updatePlot();
	}
	
	public void setColors(Color col1, Color col2) {
		color1=col1;
		color2=col2;
		if (viewModel!=null)
			updatePlot();
	}
	
	public void setup(PlotContainer c) {
		super.setup(c);
		settings.getInferData().setBooleanValue(false);
		probeColorSetter = new ProbeColorSetter() {
			public void modify(Graphics2D g, Object o) {
				if (o instanceof PairedProbe) {
					PairedProbe pb = (PairedProbe)o;
					if (pb.firstProbe)
						g.setColor(color1);
					else 
						g.setColor(color2);
				}					
			}					
		};
		coloring.setMode(ColorProviderSetting.COLOR_BY_EXPERIMENT_VALUE);
	}
	
	public void select(Color c) {
		Set<Probe> s = new HashSet<Probe>();		
		Set<Probe> s2 = new HashSet<Probe>();		
		
		for(ProbeList probe_list : getProbeLists()) {
			for (Probe pb : probe_list.getAllProbes()) {
				if (((PairedProbe)pb).firstProbe)
					s.add(pb);
				else 
					s2.add(pb);
			}
		}
			
		s.retainAll(viewModel.getSelectedProbes());
		s2.retainAll(viewModel.getSelectedProbes());
		
		currentSelection.clear();
		currentSelection.addAll(s);
		currentSelection.addAll(s2);

		if (selectionLayer!=null)
			removeDataSeries(selectionLayer);
		if (selectionLayer2!=null)
			removeDataSeries(selectionLayer2);
		
		selectionLayer = view(s);
		selectionLayer.setColor(settings.getSelectionColor().getColorValue());
		selectionLayer.setStroke(new BasicStroke(3));
		
		selectionLayer2 = view(s2);
		selectionLayer2.setColor(selectionColor2);
		selectionLayer2.setStroke(new BasicStroke(3));
		
		addDataSeries(selectionLayer);
		addDataSeries(selectionLayer2);

		clearBuffer();
		repaint();
	}

	public void setInferMissing(boolean b) {
		settings.getInferData().setBooleanValue(b);
	}	

}
