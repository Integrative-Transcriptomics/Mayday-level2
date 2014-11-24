package mayday.Reveal.visualizations.manhattanplot;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.data.meta.Genome;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

@SuppressWarnings("serial")
public class ManhattanPlot extends RevealVisualization {

	protected ManhattanPlotSetting setting;
	protected ManhattanPlotComponent plotComponent;
	protected SNPList snps;
	
	public ManhattanPlot(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		this.snps = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
		
		plotComponent = new ManhattanPlotComponent(this);
		setLayout(new BorderLayout());
		add(plotComponent, BorderLayout.CENTER);
	}
	
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			plotComponent.select();
			break;
		}
	}

	@Override
	public void updatePlot() {
		plotComponent.updatePlot();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new ManhattanPlotSetting(this);
		
		Genome g = getData().getGenome();
		
		if(g != null) {
			plotComponent.setXLabeling(g.getLabeling());
		} else {
			plotComponent.setXLabeling(null);
		}
		plotComponent.setGrid(0, 0);
		plotComponent.setGridEmphasize(0, 0.5);
		plotComponent.setScalingUnitX(0.01);
		plotComponent.setAutoGrid(false);
		plotComponent.setYLabeling(null);
		plotComponent.initValueProviders(plotContainer);
		
		return setting;
	}


	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.STAT_TEST_RESULT);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		prerequisites.add(Prerequisite.GENOME);
		return prerequisites;
	}
}
