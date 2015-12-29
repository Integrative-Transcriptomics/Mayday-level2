package mayday.Reveal.visualizations.SLProfilePlot;

import java.awt.GridLayout;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import mayday.Reveal.data.Gene;
import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;
import mayday.vis3.vis2base.ChartSetting;

@SuppressWarnings("serial")
public class SLProfilePlot extends RevealVisualization {

	private SLProfilePlotComponent[] plotComponents;
	protected SLProfilePlotSetting setting;
	
	protected SNVList snps;
	
	
	public SLProfilePlot(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		
		snps = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
		
		int numGenes = getData().getGenes().size();
		plotComponents = new SLProfilePlotComponent[numGenes];
		
		for(int i = 0; i < numGenes; i++) {
			Gene g = getData().getGenes().getGene(i);
			plotComponents[i] = new SLProfilePlotComponent(this, g);
		}
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			for(int i = 0; i < getComponentCount(); i++)
				((SLProfilePlotComponent)getComponent(i)).updatePlot();
			break;
		}
	}

	@Override
	public void updatePlot() {
		for(int i = 0; i < getComponentCount(); i++)
			((SLProfilePlotComponent)getComponent(i)).updatePlot();
		repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SLProfilePlotSetting(this);
		
		Genome g = getData().getGenome();
		Map<Double, String> xLabeling = null;
		
		if(g != null) {
			xLabeling = g.getLabeling();
		}
		
		for(int i = 0; i < plotComponents.length; i++) {
			plotComponents[i].setGrid(0, 1);
			plotComponents[i].setGridEmphasize(0, 0);
			plotComponents[i].setScalingUnitX(0.01);
			plotComponents[i].setAutoGrid(false);
			plotComponents[i].setXLabeling(xLabeling);
			plotComponents[i].setYLabeling(null);
			plotComponents[i].initValueProviders(plotContainer);
		}
		
		return setting;
	}
	
	public void addPlotComponent(Gene g) {
		int index = getData().getGenes().indexOf(g);
		add(plotComponents[index]);
		setting.getSynchronizedChartSetting().addChartSetting(plotComponents[index].getSetting());
		revalidate();
		updatePlot();
	}
	
	public void removePlotComponent(Gene g) {
		int index = getData().getGenes().indexOf(g);
		remove(plotComponents[index]);
		setting.getSynchronizedChartSetting().removeChartSetting(plotComponents[index].getSetting());
		revalidate();
		updatePlot();
	}

	public void addPlotComponents(Set<Gene> genes) {
		List<ChartSetting> settings = new LinkedList<ChartSetting>();
		for(Gene g : genes) {
			int index = getData().getGenes().indexOf(g);
			add(plotComponents[index]);
			settings.add(plotComponents[index].getSetting());
		}
		setting.getSynchronizedChartSetting().addChartSettings(settings);
		revalidate();
		updatePlot();
	}
	
	public void removeAll() {
		super.removeAll();
		setting.getSynchronizedChartSetting().removeAllChartSettings();
		revalidate();
		updatePlot();
	}
	
	public void revalidate() {
		this.setLayout(new GridLayout(getComponentCount(),1));
		super.revalidate();
		for(int i = 0; i < getComponentCount(); i++)
			((SLProfilePlotComponent)getComponent(i)).revalidate();
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.SINGLE_LOCUS_RESULT);
		prerequisites.add(Prerequisite.GENE_EXPRESSION);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}
}
