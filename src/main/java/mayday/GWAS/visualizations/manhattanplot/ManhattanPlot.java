package mayday.GWAS.visualizations.manhattanplot;

import java.awt.BorderLayout;

import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.meta.Genome;
import mayday.GWAS.utilities.SNPLists;
import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.GWAS.visualizations.RevealVisualization;
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
}
