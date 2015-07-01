package mayday.Reveal.visualizations.tables.statsTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.visualizations.tables.AbstractMetaInformationTable;

public class StatisticalResultTable extends AbstractMetaInformationTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public StatisticalResultTable(DataStorage storage) {
		super(storage);
	}

	public void update() {
		repaint();
	}
	
	public void updateAfterSelectionChange(Set<SNV> selectedSNPs) {
		MetaInfoTableModel model = getModel();
		ListSelectionModel selectionModel = getSelectionModel();
		Set<SNV> copySelected = new HashSet<SNV>(selectedSNPs);
		
		selectionModel.clearSelection();
		
		for(SNV s : copySelected) {
			int snpIndex = model.getFirstColumnPosition(s.getID());
			if(snpIndex != -1)
				selectionModel.addSelectionInterval(snpIndex, snpIndex);
		}
	}

	public void initializeData(MetaInfoTableModel model) {
		DataStorage ds = getDataStorage();
		Set<SNVList> selectedSNPLists = ds.getProjectHandler().getSelectedSNVLists();
		SNVList snps = SNVLists.createUniqueSNVList(selectedSNPLists);
		
		List<MetaInformation> mis = ds.getMetaInformationManager().get(StatisticalTestResult.MYTYPE);
		
		String[] columnNames = new String[mis.size() + 1];
		Object[][] tableData = new Object[snps.size()][mis.size() + 1];
		
		//set SNP names
		columnNames[0] = "SNP ID";
		for(int i = 0; i < snps.size(); i++) {
			tableData[i][0] = snps.get(i).getID();
		}
		
		for(int i = 0; i < mis.size(); i++) {
			MetaInformation mi = mis.get(i);
			StatisticalTestResult str = (StatisticalTestResult)mi;
			columnNames[i+1] = str.getStatTestName();
			
			for(int j = 0; j < snps.size(); j++) {
				SNV s = snps.get(j);
				tableData[j][i+1] = str.getPValue(s);
			}
		}
		
		model.setData(tableData);
		model.setColumnNames(columnNames);	
	}
	
	private boolean internalChange = true;

	public void setInternalChange(boolean change) {
		this.internalChange = change;
	}
	
	public boolean isInternalChange() {
		return internalChange;
	}
}
