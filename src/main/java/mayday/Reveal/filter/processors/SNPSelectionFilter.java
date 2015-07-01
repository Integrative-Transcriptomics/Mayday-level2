package mayday.Reveal.filter.processors;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNV;
import mayday.Reveal.filter.AbstractDataProcessor;
import mayday.Reveal.viewmodel.RevealViewModel;

public class SNPSelectionFilter extends AbstractDataProcessor<SNV, Boolean> {

	@Override
	public void dispose() {
		//nothing to do
	}

	@Override
	public Class<?>[] getDataClass() {
		return snpList == null ? null : 
			new Class<?>[]{Boolean.class};
	}

	@Override
	public boolean isAcceptableInput(Class<?>[] inputClass) {
		return SNV.class.isAssignableFrom(inputClass[0]);
	}

	@Override
	public String toString() {
		return "SNP Selection";
	}

	@Override
	protected Boolean convert(SNV value) {
		if(snpList == null)
			return false;
		DataStorage ds = snpList.getDataStorage();
		RevealViewModel model = ds.getProjectHandler().getViewModel(ds);
		return model.isSelected(value);
	}

	@Override
	public String getName() {
		return "SNP Selection Filter";
	}

	@Override
	public String getType() {
		return "data.snplist.filter.snpselection";
	}

	@Override
	public String getDescription() {
		return "Fitler SNPs selected in the current project";
	}
}
