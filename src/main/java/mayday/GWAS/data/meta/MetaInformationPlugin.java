package mayday.GWAS.data.meta;

import java.util.Collection;

import mayday.GWAS.RevealPlugin;
import mayday.GWAS.data.DataStorage;
import mayday.GWAS.data.SNPList;

public abstract class MetaInformationPlugin extends RevealPlugin implements MetaInformation {
	
	protected DataStorage dataStorage;
	
	public String getCategory() {
		return "Meta Information";
	}
	
	@Override
	public void run(Collection<SNPList> snpLists) {
		return; //nothing to do here
	}
	
	@Override
	public String getMenuName() {
		return null; //no menu item
	}

	@Override
	public String getMenu() {
		return null; //no menu item
	}

	public void setDataStorage(DataStorage dataStorage) {
		this.dataStorage = dataStorage;
	}
}
