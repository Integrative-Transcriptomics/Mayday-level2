package mayday.Reveal.visualizations.SNPSummaryPlot;

import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.utilities.SNPSorter;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class ChangeSNPOrderSetting extends HierarchicalSetting {
	
	private RestrictedStringSetting orderChooser;
	private RestrictedStringSetting statTestChooser;
	
	private DataStorage ds;
	
	public ChangeSNPOrderSetting(DataStorage ds) {
		super("SNP Order Setting");
		this.ds = ds;
		
		orderChooser = new RestrictedStringSetting("Order SNPs by...", null, 0, SNPSorter.NONE, SNPSorter.GENOMIC_LOCATION, SNPSorter.STATISTICAL_TEST);
		addSetting(orderChooser);
		
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
		return this.orderChooser.getStringValue();
	}

	public void setOrdering(String snpOrder) {
		switch(snpOrder) {
		case SNPSorter.NONE:
			orderChooser.setSelectedIndex(0);
			break;
		case SNPSorter.GENOMIC_LOCATION:
			orderChooser.setSelectedIndex(1);
			break;
		case SNPSorter.STATISTICAL_TEST:
			orderChooser.setSelectedIndex(2);
			break;
		}
	}
	
	public StatisticalTestResult getSelectedStatTestResult() {
		MetaInformation str = ds.getMetaInformationManager().get("STR").get(statTestChooser.getSelectedIndex());
		return (StatisticalTestResult)str;
	}
}
