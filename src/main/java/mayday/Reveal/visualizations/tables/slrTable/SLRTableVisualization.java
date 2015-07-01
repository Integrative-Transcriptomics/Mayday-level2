package mayday.Reveal.visualizations.tables.slrTable;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import mayday.Reveal.data.ProjectHandler;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.functions.prerequisite.Prerequisite;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.viewmodel.RevealViewModelEvent;
import mayday.Reveal.visualizations.RevealVisualization;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.vis3.gui.PlotContainer;
import mayday.vis3.model.ViewModelEvent;

public class SLRTableVisualization extends RevealVisualization {

	private SLRTableSetting setting;
	private SLRTable table;
	private SNVList snpList;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8462871728333002317L;
	
	public SLRTableVisualization(ProjectHandler projectHandler) {
		setData(projectHandler.getSelectedProject());
		table = new SLRTable(getData());
		snpList = SNVLists.createUniqueSNVList(projectHandler.getSelectedSNVLists());
		table.getSelectionModel().addListSelectionListener(new SLRTableListSelectionListener());
		
		JScrollPane tableScroller = new JScrollPane(table);
		this.setLayout(new BorderLayout());
		this.add(tableScroller, BorderLayout.CENTER);
	}

	@Override
	public void viewModelChanged(ViewModelEvent vme) {
		switch(vme.getChange()) {
		case RevealViewModelEvent.SNP_SELECTION_CHANGED:
			updateAfterSelectionChange();
			break;
		}
		
	}
	
	public void updateAfterSelectionChange() {
		table.setInternalChange(false);
		table.updateAfterSelectionChange(getViewModel().getSelectedSNPs());
		table.setInternalChange(true);
	}

	@Override
	public void updatePlot() {
		table.update();
	}
	
	public void updateTableData() {
		table.initializeData(table.getModel());
	}

	@Override
	public HierarchicalSetting setupPrerequisites(PlotContainer plotContainer) {
		this.setting = new SLRTableSetting(this);
		this.updateAfterSelectionChange();
		this.table.setSetting(setting);
		return setting;
	}
	
	public class SLRTableListSelectionListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting() && table.isInternalChange()) {
				int[] viewRows = table.getSelectedRows();
				Set<SNV> toggleSNPs = new HashSet<SNV>();
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
		prerequisites.add(Prerequisite.SINGLE_LOCUS_RESULT);
		prerequisites.add(Prerequisite.GENE_EXPRESSION);
		prerequisites.add(Prerequisite.SNP_LIST_SELECTED);
		return prerequisites;
	}
}
