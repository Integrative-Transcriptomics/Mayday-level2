package mayday.Reveal.visualizations.snpcharacterization;

import java.awt.BorderLayout;
import java.util.LinkedList;
import java.util.List;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.snpcharacterization.SNPCharacterizations;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
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
	
	@Override
	public List<Integer> getPrerequisites() {
		List<Integer> prerequisites = new LinkedList<Integer>();
		prerequisites.add(Prerequisite.SNP_CHARACTERIZATION);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}
}
