package mayday.wapiti.containers.loci.merging;

import java.util.List;

import mayday.core.pluma.AbstractPlugin;
import mayday.genetics.advanced.LocusData;
import mayday.genetics.locusmap.LocusMap;
import mayday.wapiti.Constants;

/**
 * Locus Merging methods construct a new set of loci from a given input set.
 * These methods only consider primitive genetic coordinates.
 * Thus, if sets of complex coordinates (eg exon models) are merged, the resulting set will NOT 
 * contain exon models but rather primitive coordinates! 
 */

public abstract class LocusMergeMethod extends AbstractPlugin {

	public final static String MC = Constants.MCBASE+"LocusMerging";
	
	public void init() {
	}
	
	public abstract LocusMap run( List<LocusData> input, String name );
		
}
