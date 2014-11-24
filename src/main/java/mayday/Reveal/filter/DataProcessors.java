package mayday.Reveal.filter;

import java.util.Set;
import java.util.TreeSet;

import mayday.Reveal.data.SNPList;
import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManager;

public class DataProcessors {
	
	private static Set<Item> processors;
	
	private static void init() {
		Set<PluginInfo> ds = PluginManager.getInstance().getPluginsFor(AbstractDataProcessor.MC);
		processors  = new TreeSet<Item>();
		for (PluginInfo pli : ds) {
			processors.add(new Item(pli));
		}
	}
	
	public static Set<Item> getProcessors() {
		if (processors == null)
			init();
		return processors;
	}
	
	@SuppressWarnings("rawtypes")
	public static Set<Item> getProcessorsAccepting(AbstractDataProcessor ads) {
		Class<?>[] inputClass = ads.getDataClass();
		return getProcessorsAccepting(inputClass);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Set<Item> getProcessorsAccepting(Class<?>[] inputClass) {
		Set<Item> ret = new TreeSet<Item>();
		if (inputClass==null) 
			return ret;
		for (Item dfpi: getProcessors()) {
			AbstractDataProcessor adf = (AbstractDataProcessor)dfpi.pli.getInstance();
			if (adf.isAcceptableInput(inputClass))
				ret.add(dfpi);
		}
		return ret;
	}

	public static Item getProcessorByID(String plumaID) {
		for (Item dfpi: getProcessors()) {
			if (dfpi.pli.getIdentifier().equals(plumaID))
				return dfpi;
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static class Item implements Comparable {
		
		private PluginInfo pli;
		
		public Item(PluginInfo pi) {
			pli=pi;
		}
		
		public String toString() {
			return pli.getName();
		}
		
		public AbstractDataProcessor newInstance(SNPList dynamicSNPList) {
			AbstractDataProcessor adp = ((AbstractDataProcessor)pli.newInstance());
			adp.setSNPList(dynamicSNPList);
			return adp;
		}
		
		public int compareTo(Object o) {
			if (o instanceof Item)
				return pli.compareTo(
						((Item)o).pli);
			return 0;
		}
		
		public PluginInfo getPluginInfo() {
			return pli;
		}
	}
}
