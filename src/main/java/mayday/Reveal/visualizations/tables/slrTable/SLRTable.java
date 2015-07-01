package mayday.Reveal.visualizations.tables.slrTable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.ListSelectionModel;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.SNV;
import mayday.Reveal.data.SNVList;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.SingleLocusResult;
import mayday.Reveal.utilities.SNVLists;
import mayday.Reveal.visualizations.tables.AbstractMetaInformationTable;

public class SLRTable extends AbstractMetaInformationTable {

	private SLRTableSetting setting;
	
	public SLRTable(DataStorage storage) {
		super(storage);
	}
	
	public void setSetting(SLRTableSetting setting) {
		this.setting = setting;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 8993253557097218229L;

	@Override
	public void initializeData(MetaInfoTableModel model) {
		DataStorage ds = getDataStorage();
		Set<SNVList> selectedSNPLists = ds.getProjectHandler().getSelectedSNVLists();
		SNVList snpList = SNVLists.createUniqueSNVList(selectedSNPLists);
		
		List<MetaInformation> slrMis = ds.getMetaInformationManager().get(SLResults.MYTYPE);
		
		Gene gene;
		if(setting != null)
			gene = setting.getSelectedGene();
		else
			gene = (Gene)ds.getGenes().getProbe(0);
			
		//it is not expected that there is more than 1
		SLResults slResults = (SLResults)slrMis.get(0);
		
		//there is nothing to display
		if(slResults == null) {
			return;
		}
		
		SingleLocusResult slr = slResults.get(gene);
		
		Set<SNV> availableSNPs = slr.keySet();
		availableSNPs.retainAll(snpList);
		
		int numSNPs = availableSNPs.size();
		
		//initialize data structures
		String[] columnNames = new String[6];
		Object[][] tableData = new Object[numSNPs][6];
		
		//initialize first column
		columnNames[0] = "SNP ID";
		columnNames[1] = "p";
		columnNames[2] = "t";
		columnNames[3] = "beta";
		columnNames[4] = "r2";
		columnNames[5] = "se";
		
		
		int snpIndex = 0;
		for(SNV s : availableSNPs) {
			tableData[snpIndex++][0] = s.getID();
		}
		
		snpIndex = 0;
		for(int i = 0; i < slrMis.size(); i++) {
			for(SNV s : availableSNPs) {
				SingleLocusResult.Statistics stats = slr.get(s);
				if(stats != null) {
					tableData[snpIndex][1] = stats.p;
					tableData[snpIndex][2] = stats.t;
					tableData[snpIndex][3] = stats.beta;
					tableData[snpIndex][4] = stats.r2;
					tableData[snpIndex][5] = stats.se;
				} else {
					tableData[snpIndex][1] = Double.NaN;
					tableData[snpIndex][2] = Double.NaN;
					tableData[snpIndex][3] = Double.NaN;
					tableData[snpIndex][4] = Double.NaN;
					tableData[snpIndex][5] = Double.NaN;
				}
				snpIndex++;
			}	
		}
		
		model.setData(tableData);
		model.setColumnNames(columnNames);
	}

	@Override
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
	
	private boolean internalChange = true;

	public void setInternalChange(boolean change) {
		this.internalChange = change;
	}
	
	public boolean isInternalChange() {
		return internalChange;
	}
}
