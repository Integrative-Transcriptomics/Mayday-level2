package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.utilities.SNPSorter;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class ChangeSortingSetting extends HierarchicalSetting {
	
	private RestrictedStringSetting orderingChooser;
	private RestrictedStringSetting statTestChooser;
	
	private DataStorage ds;
	
	public ChangeSortingSetting(DataStorage ds) {
		super("SNP Sorting Setting");
		this.ds = ds;
		
		orderingChooser = new RestrictedStringSetting("SNP Ordering", null, 0, SNPSorter.NONE, SNPSorter.GENOMIC_LOCATION, SNPSorter.STATISTICAL_TEST);
		addSetting(orderingChooser);
		
		List<MetaInformation> strs = this.ds.getMetaInformationManager().get("STR");
		if(strs != null && strs.size() > 0) {
			String[] strNames = new String[strs.size()];
			for(int i = 0; i < strs.size(); i++) {
				strNames[i] = ((StatisticalTestResult)strs.get(i)).getStatTestName();
			}
			statTestChooser = new RestrictedStringSetting("Statistical-Test-Result", null, 0, strNames);
			addSetting(statTestChooser);
		}
	}

	public String getOrdering() {
		return this.orderingChooser.getStringValue();
	}

	public void setOrdering(String snpOrder) {
		if(snpOrder.equals(SNPSorter.NONE)) {
			orderingChooser.setSelectedIndex(0);
		} else if(snpOrder.equals(SNPSorter.GENOMIC_LOCATION)) {
			orderingChooser.setSelectedIndex(1);
		} else if(snpOrder.equals(SNPSorter.STATISTICAL_TEST)) {
			orderingChooser.setSelectedIndex(2);
		}
	}
	
	public StatisticalTestResult getSelectedStatTestResult() {
		MetaInformation str = ds.getMetaInformationManager().get("STR").get(statTestChooser.getSelectedIndex());
		return (StatisticalTestResult)str;
	}
}
