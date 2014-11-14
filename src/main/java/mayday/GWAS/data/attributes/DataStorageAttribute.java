package mayday.GWAS.data.attributes;

import mayday.GWAS.data.DataStorage;
import mayday.GWAS.listeners.DataStorageEvent;

public class DataStorageAttribute extends Attribute {

	private DataStorage dataStorage;
	
	public DataStorageAttribute(DataStorage dataStorage) {
		super();
		this.dataStorage = dataStorage;
	}
	
	public DataStorageAttribute(DataStorage dataStorage, String name, String information) {
		super(name, information);
		this.dataStorage = dataStorage;
	}
	
	public void setName(String name) {
		super.setName(name);
		dataStorage.fireDataStorageChanged(DataStorageEvent.DATA_CHANGED);
	}
}
