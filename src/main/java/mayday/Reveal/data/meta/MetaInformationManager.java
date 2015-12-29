package mayday.Reveal.data.meta;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import mayday.Reveal.data.DataStorage;
import mayday.Reveal.listeners.DataStorageEvent;

@SuppressWarnings("serial")
public class MetaInformationManager extends HashMap<String, List<MetaInformation>> {
	
	protected DataStorage dataStorage;
	
	public MetaInformationManager(DataStorage dataStorage) {
		super();
		this.dataStorage = dataStorage;
	}
	
	/**
	 * 
	 * @param key
	 * @param info
	 * @return index of info in the list of key
	 */
	public int add(String key, MetaInformation info) {
		if(!containsKey(key)) {
			put(key, new LinkedList<MetaInformation>());
		}
		
		get(key).add(info);
		fireMetaInformationChanged();
		return get(key).size()-1;
	}
	
	/**
	 * 
	 * @param key
	 * @param info
	 * @param index
	 * @return index of info in the list of key
	 */
	public int add(String key, MetaInformation info, int index) {
		if(!containsKey(key)) {
			put(key, new LinkedList<MetaInformation>());
		}
		
		get(key).add(index, info);
		fireMetaInformationChanged();
		return Math.min(index, get(key).size()-1);
	}

	public MetaInformation remove(String key, int index) {
		MetaInformation info = get(key).get(index);
		get(key).remove(index);
		fireMetaInformationChanged();
		return info;
	}
	
	public boolean remove(String key, MetaInformation info) {
		boolean removed =  get(key).remove(info);
		fireMetaInformationChanged();
		return removed;
	}
	
	/**
	 * 
	 * @param key
	 * @param index
	 * @return the meta-information stored in the list of 'key' at index 'index'
	 */
	public MetaInformation get(String key, int index) {
		return get(key).get(index);
	}

	public void serialize(BufferedWriter bw) throws IOException {
		for(String key : keySet()) {
			List<MetaInformation> metaList = get(key);
			for(MetaInformation info : metaList) {
				info.serialize(bw);
			}
		}
	}
	
	public void fireMetaInformationChanged() {
		this.dataStorage.fireDataStorageChanged(DataStorageEvent.META_INFORMATION_CHANGED);
	}

	public String getPathFor(MetaInformation metaInfo) {
		for(String key : keySet()) {
			List<MetaInformation> miList = get(key);
			if(miList.contains(metaInfo)) {
				return key + "/" + miList.indexOf(metaInfo);
			}
		}
		return null;
	}

	public MetaInformation getFromPath(String path) {
		if(path != null) {
			String[] split = path.split("/");
			if(split.length == 2) {
				List<MetaInformation> miList = get(split[0]);
				if(miList != null) {
					int index = Integer.parseInt(split[1]);
					if(index < miList.size() && index >= 0) {
						return miList.get(index);
					}
				}
			}
		}
		return null;
	}

	public boolean removeAll(Collection<MetaInformation> mis) {
		boolean changed = false;
		for(MetaInformation mi : mis) {
			for(String key : keySet()) {
				if(get(key).contains(mi)) {
					get(key).remove(mi);
					changed = true;
					
					if(get(key).size() == 0) {
						remove(key);
					}
					
					break;
				}
			}
		}
		
		if(changed) {
			fireMetaInformationChanged();
		}
		
		return changed;
	}
}
