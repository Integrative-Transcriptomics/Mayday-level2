package mayday.GWAS.data.meta.manipulation;

import java.util.Collection;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.meta.MetaInformation;

public class RemoveMetaInformation extends MIManipulationPlugin {
	
	@Override
	public String getName() {
		return "Remove Meta-Information";
	}

	@Override
	public String getType() {
		return "data.meta.manipulation.remove";
	}

	@Override
	public String getDescription() {
		return "Remove selected Meta-Information data from current project";
	}

	@Override
	public String getMenuName() {
		return "Remove";
	}

	@Override
	public void runManipulation(Collection<MetaInformation> mis) {
		DataStorage ds = projectHandler.getSelectedProject();
		ds.getMetaInformationManager().removeAll(mis);
	}
}
