package mayday.wapiti.containers.loci.peakfind;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;

public class PeakFinderSetting extends HierarchicalSetting {	

	IntSetting maxDistance;
	DoubleSetting minCoverage;
	IntSetting minReads;
	IntSetting minSize;
	BooleanHierarchicalSetting useMaxDistance, useMinCoverage, useMinReads, useMinSize;

	public PeakFinderSetting(String Name) {
		super(Name);
		addSetting(
				useMaxDistance = new BooleanHierarchicalSetting( "Maximum distance between reads", null, true)
				.addSetting(maxDistance = new IntSetting("Maximum distance",null,100))
		);
		addSetting(
				useMinReads = new BooleanHierarchicalSetting( "Minimum number of reads", null, true)
				.addSetting(minReads = new IntSetting("Minimum number of reads",null,2))
		);
		addSetting(
				useMinCoverage = new BooleanHierarchicalSetting( "Minimum coverage in the locus", null, false)
				.addSetting(minCoverage = new DoubleSetting("Minimum coverage in the locus",
						"Activate this setting to find optimally covered loci, deactivate to find maximally large ones."
				,1.0))
		);
		addSetting(
				useMinSize = new BooleanHierarchicalSetting( "Minimum size of a locus", null, true)
				.addSetting(minSize = new IntSetting("Maximum locus size",null,100))
		);
		
	}

	public PeakFinderSetting clone() {
		PeakFinderSetting gs = new PeakFinderSetting(getName());
		gs.fromPrefNode(this.toPrefNode());
		return gs;
	}
	
	public int getMaxDistance() {
		return useMaxDistance.getBooleanValue()?maxDistance.getIntValue():Integer.MAX_VALUE;
	}
	
	public int getMinReads() {
		return useMinReads.getBooleanValue()?minReads.getIntValue():0;
	}
	
	public double getMinCoverage() {
		return useMinCoverage.getBooleanValue()?minCoverage.getDoubleValue():0d;
	}
	
	public int getMinSize() {
		return useMinSize.getBooleanValue()?minSize.getIntValue():0;
	}


}
