package mayday.Reveal.data.meta;

import java.util.Collection;

import mayday.Reveal.RevealPlugin;
import mayday.Reveal.data.DataStorage;
import mayday.Reveal.data.SNPList;

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
