package mayday.statistics.rankproduct;

import mayday.core.settings.generic.BooleanHierarchicalSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.DoubleSetting;
import mayday.core.settings.typed.IntSetting;

/**
 *	Settings for one sample rank product.
 *
 */
public class RPOneSampleSetting extends HierarchicalSetting {


	protected IntSetting 		permCount;
	protected BooleanSetting 	logged;
	protected BooleanSetting 	returnPFP;
	protected BooleanSetting 	isPaired;
	protected BooleanSetting 	returnSignificantProbes;
	protected DoubleSetting 	filterByPValue;
	
	/**
	 * 
	 * @param name the frame title
	 */
	public RPOneSampleSetting(String name) {
		super(name);
		addSetting( permCount = new IntSetting("Permutations","Number of permutations. The smallest possible nonzero p-value is 1/permutations.", 100, 1, 1000000, true,true) );
		addSetting( logged = new BooleanSetting("Logarithmic","Activate this if the input data is logarithmic", true));
		addSetting( returnPFP = new BooleanSetting("Return pfp values",
				"If activated, pfp (percent false positive) values are computed. \n" +
				"These are similar to FDR corrected statistical p-values.\n\n" +
				"If deactivated, uncorrected p-values are computed.", true));
		addSetting( returnSignificantProbes = new BooleanHierarchicalSetting("Create probe list of significant probes",null,false).
		addSetting( filterByPValue = new DoubleSetting("p-value threshold",null, 0.05, 0d,1d,true,true)));
	
	}

	/**
	 * 
	 * @return set number of permutations
	 */
	public int getPermutationCount() {
		return permCount.getIntValue();		
	}
	
	/**
	 * 
	 * @return user's selection (TRUE: values are logarithmized. FALSE: values aren't log.)
	 */
	public boolean isLogged() {
		return logged.getBooleanValue();
	}
	
	/**
	 * 
	 * @return user's selection (TRUE: return pfp values and not p-values. FALSE: return p-values.)
	 */
	public boolean returnPFP() {
		return returnPFP.getBooleanValue();
	}
	
	/**
	 * every setting needs to be cloneable
	 */
	public RPOneSampleSetting clone() {
		RPOneSampleSetting cs = new RPOneSampleSetting(getName());
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
	
	/**
	 * 
	 * @return user's choice (TRUE: return list of significant probes. FALSE: don't return list)
	 */
	public BooleanSetting getReturnSignificantProbes() {
		return returnSignificantProbes;
	}
	
	/**
	 * 
	 * @return set p-value/pfp-value below which probes will be reported
	 */
	public DoubleSetting getFilterPValue() {
		return filterByPValue;
	}
	
}
