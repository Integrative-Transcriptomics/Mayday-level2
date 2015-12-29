package mayday.wapiti.containers.loci.merging.method;

import java.util.List;
import java.util.Map.Entry;

import mayday.core.pluma.PluginInfo;
import mayday.core.pluma.PluginManagerException;
import mayday.core.settings.Setting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;
import mayday.core.structures.maps.NonReplacingMap;
import mayday.genetics.advanced.ChromosomeSetIterator;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.advanced.chromosome.LocusGeneticCoordinateObject;
import mayday.genetics.basic.ChromosomeSetContainer;
import mayday.genetics.basic.coordinate.AbstractGeneticCoordinate;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.containers.loci.merging.LocusMergeMethod;

public class Union extends LocusMergeMethod {

	protected BooleanSetting removeMinimal;
	protected IntSetting minimalSize;
	protected HierarchicalSetting lowerLimit;
	
	public Setting getSetting() {
		if (lowerLimit==null) {
			removeMinimal = new BooleanSetting("Remove small loci","Discard loci below a minimum length?", true);
			minimalSize = new IntSetting("Minimal locus size", null, 10, 0,null,false, false);
			lowerLimit = new HierarchicalSetting("Remove small loci").addSetting(removeMinimal).addSetting(minimalSize);			
		}
		return lowerLimit;
	}
	
	
	public PluginInfo register() throws PluginManagerException {
		return new PluginInfo(
				this.getClass(), 
				MC+".Union", 
				new String[0], 
				MC, null, 
				"Florian Battke", 
				"battke@informatik.uni-tuebingen.de", 
				"Union (use all unique loci, even if they overlap)", 
		"Union (use all unique loci, even if they overlap)");
	}

	public LocusMap run(List<LocusData> input, String name) {
		
		NonReplacingMap<AbstractGeneticCoordinate, String> nrm = new NonReplacingMap<AbstractGeneticCoordinate, String>();
		
		boolean removemin = removeMinimal.getBooleanValue();
		int minsize = minimalSize.getIntValue();
		
		for (LocusData ld : input) {
			
			ChromosomeSetContainer csc = ld.asChromosomeSetContainer();
			ChromosomeSetIterator csi = new ChromosomeSetIterator(csc);
			
			for (AbstractGeneticCoordinate agc : csi) {
				
				if (removemin && agc.length()<minsize)
					continue;
				
				// find a name for the coordinate
				String locusname=null;
				
				if (agc instanceof LocusGeneticCoordinateObject<?>) {
					LocusGeneticCoordinateObject<?> olgc = (LocusGeneticCoordinateObject<?>)agc;
					if (olgc.getObject() instanceof String)
						locusname = (String)olgc.getObject();
				}
				
				if (locusname==null) {
					locusname = agc.toString();  
				}
					
				nrm.put(agc, locusname);
			}
		}
		
		LocusMap result = new LocusMap(name);

		for (Entry<AbstractGeneticCoordinate, String> e : nrm.entrySet()) {
			result.put(e.getValue(),e.getKey());
		}
		
		return result; 
	}
}
