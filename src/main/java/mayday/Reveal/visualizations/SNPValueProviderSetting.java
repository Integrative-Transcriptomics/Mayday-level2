package mayday.Reveal.visualizations;

import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.Gene;
import mayday.Reveal.data.meta.MetaInformation;
import mayday.Reveal.data.meta.MetaInformationManager;
import mayday.Reveal.data.meta.SLResults;
import mayday.Reveal.data.meta.SingleLocusResult;
import mayday.Reveal.data.meta.StatisticalTestResult;
import mayday.Reveal.viewmodel.RevealViewModel;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class SNPValueProviderSetting extends HierarchicalSetting implements ChangeListener {

	public static final int CHROMOSOMAL_LOCATION_VALUE = 1;
	public static final int META_INFORMATION_VALUE = 2;
	public static final int STAT_TEST_RESULT_VALUE = 3;
	public static final int SINGLE_LOCUS_RESULT_VALUE = 4;
	public static final int SNP_INDEX_VALUE = 5;
	
	protected RevealViewModel viewModel;
	protected SNPValueProvider target;
	
	private RestrictedStringSetting statResults;
	private String[] statNames;
	private RestrictedStringSetting singleLocusResults;
	private String[] slrNames;
	private int currentSourceType;
	
	public SNPValueProviderSetting(String name, String description, SNPValueProvider target, RevealViewModel viewModel) {
		super(name);
		this.description = description;
		this.viewModel = viewModel;
		this.target = target;
		
		currentSourceType = target.getSourceType();
		
		DataStorage ds = viewModel.getDataStorage();
		MetaInformationManager miom = ds.getMetaInformationManager();
		
		switch(currentSourceType) {
		case STAT_TEST_RESULT_VALUE:
			List<MetaInformation> statMios = miom.get(StatisticalTestResult.MYTYPE);
			
			if(statMios != null) {
				statNames = new String[statMios.size()];
				
				for(int i = 0; i < statNames.length; i++) {
					statNames[i] = ((StatisticalTestResult)statMios.get(i)).getStatTestName();
				}
			}
			addSetting(statResults = new RestrictedStringSetting("Statistical Test Result", null, 0, statNames));
			break;
		case CHROMOSOMAL_LOCATION_VALUE:
			//TODO maybe later
			break;
		case META_INFORMATION_VALUE:
			//TODO maybe later
			break;
		case SINGLE_LOCUS_RESULT_VALUE:
			List<MetaInformation> slrsMios = miom.get(SLResults.MYTYPE);
			slrNames = new String[slrsMios.size()];
			
			for(int i = 0; i < slrNames.length; i++) {
				slrNames[i] = ((SLResults)slrsMios.get(i)).toString();
			}
			addSetting(singleLocusResults = new RestrictedStringSetting("Single Locus Results", null, 0, slrNames));
			break;
		case SNP_INDEX_VALUE:
			break;
		}
		
		setChildrenAsSubmenus(true);
		addChangeListener(this);
	}
	
	public StatisticalTestResult getSelectedStatResult() {
		int index = statResults.getSelectedIndex();
		if(statNames != null) {
			MetaInformationManager miom = viewModel.getDataStorage().getMetaInformationManager();
			return (StatisticalTestResult)miom.get(StatisticalTestResult.MYTYPE).get(index);
		}
		return null;
	}
	
	public SingleLocusResult getSelectedSingleLocusResult(Gene gene) {
		int index = singleLocusResults.getSelectedIndex();
		if(slrNames != null) {
			MetaInformationManager miom = viewModel.getDataStorage().getMetaInformationManager();
			SLResults slrs = (SLResults) miom.get(SLResults.MYTYPE).get(index);
			SingleLocusResult slr = slrs.get(gene);
			return slr;
		}
		return null;
	}
	
	protected int getMode() {
		return target.getSourceType();
	}
	
	public SNPValueProviderSetting clone() {
		SNPValueProviderSetting cp = new SNPValueProviderSetting(getName(), getDescription(), new SNPValueProvider(viewModel, target.getMenuTitle()), viewModel);
		cp.fromPrefNode(this.toPrefNode());
		return cp;
	}

	@Override
	public void stateChanged(ChangeEvent e) {

	}
}