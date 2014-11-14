package mayday.GWAS.visualizations.snpcharacterization;

import java.awt.BorderLayout;

import mayday.GWAS.data.ProjectHandler;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.data.meta.snpcharacterization.SNPCharacterizations;
import mayday.GWAS.viewmodel.RevealViewModelEvent;
import mayday.GWAS.visualizations.RevealVisualization;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

/**
 * 
 * @author jaeger
 *
 */
@SuppressWarnings("serial")
public class SNPCharInformationTable extends RevealVisualization {

	private SNPCharInformationTableSetting setting;
	private CharacterizationTable table;
	
	public SNPCharInformationTable(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		
		MetaInformation mi = getData().getProjectHandler().getSelectedMetaInformation();
		
		if(mi instanceof SNPCharacterizations) {
			SNPCharacterizations snpChars = (SNPCharacterizations)mi;
			table = new CharacterizationTable(snpChars);
			this.add(table.getContentPane(), BorderLayout.CENTER);
		}
	}
	
	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			break;
		}
	}

	@Override
	public void updatePlot() {
		repaint();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		setting = new SNPCharInformationTableSetting(this);
		return setting;
	}

	@Override
	public HierarchicalSetting getViewSetting() {
		return setting;
	}
}
