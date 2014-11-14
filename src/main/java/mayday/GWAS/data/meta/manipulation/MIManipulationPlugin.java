package mayday.GWAS.data.meta.manipulation;

import java.util.Collection;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.SNPList;
import mayday.GWAS.data.meta.MetaInformation;
import mayday.GWAS.utilities.RevealMenuConstants;
import mayday.core.pluma.Constants;

public abstract class MIManipulationPlugin extends RevealPlugin {

	public static final String CATEGORY = "Meta Information/Manipulation";
	public static final String MC = Constants.MC_REVEAL + "/" + CATEGORY;
	
	@Override
	public void run(Collection<SNPList> snpLists) {
		return; //nothing to do
	}
	
	public abstract void runManipulation(Collection<MetaInformation> mios);

	@Override
	public String getMenu() {
		return RevealMenuConstants.META_INFORMATION;
	}

	@Override
	public String getCategory() {
		return CATEGORY;
	}
}
