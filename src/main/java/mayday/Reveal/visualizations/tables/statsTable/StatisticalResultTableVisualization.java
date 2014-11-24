package mayday.Reveal.visualizations.tables.statsTable;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNP;
import mayday.Reveal.data.SNPList;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNPLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

public class StatisticalResultTableVisualization extends RevealVisualization {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 585870186544812067L;

	private StatisticalResultTable table;
	private StatisticalResultTableSetting setting;
	private SNPList snpList;
	
	public StatisticalResultTableVisualization(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		this.snpList = SNPLists.createUniqueSNPList(projectHandler.getSelectedSNPLists());
		this.table = new StatisticalResultTable(getData());
		this.table.getSelectionModel().addListSelectionListener(new MetaInfoListSelectionListener());
		
		TableColumnModel columnModel = this.table.getColumnModel();
		for(int i = 0; i < columnModel.getColumnCount(); i++) {
			columnModel.getColumn(i).setMinWidth(80);
		}

		JScrollPane tableScroller = new JScrollPane(table);
		this.add(tableScroller, BorderLayout.CENTER);
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			upateAfterSelectionChange();
			break;
		}
	}
	
	public void upateAfterSelectionChange() {
		table.setInternalChange(false);
		table.updateAfterSelectionChange(getViewModel().getSelectedSNPs());
		table.setInternalChange(true);
	}

	@Override
	public void updatePlot() {
		table.update();
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		this.setting = new StatisticalResultTableSetting(this);
		this.upateAfterSelectionChange();
		return setting;
	}
	
	public class MetaInfoListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting() && table.isInternalChange()) {
				int[] viewRows = table.getSelectedRows();
				Set<SNP> toggleSNPs = new HashSet<SNP>();
				for(int i = 0; i < viewRows.length; i++) {
					int viewRow = viewRows[i];
					if (viewRow >= 0) {
						int modelRow = table.convertRowIndexToModel(viewRow);
						String snpID = (String)table.getModel().getValueAt(modelRow, 0);
						toggleSNPs.add(snpList.get(snpID));
					}
				}
				getViewModel().setSNPSelection(toggleSNPs);
			}
		}
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
		return prerequisites;
	}
}
