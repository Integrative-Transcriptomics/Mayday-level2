package mayday.statistics.rankproduct;

import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.IntSetting;

public class RPTwoSampleSetting extends HierarchicalSetting {

	protected IntSetting permCount;
	protected BooleanSetting logged;
	protected BooleanSetting returnPFP;
	protected BooleanSetting isPaired;
	
	public RPTwoSampleSetting(String Name) {
		super(Name);
		addSetting( permCount = new IntSetting("Permutations","Number of permutations. The smallest possible nonzero p-value is 1/permutations.", 100, 1, 1000000, true,true) );
		addSetting( logged = new BooleanSetting("Logarithmic","Activate this if the input data is logarithmic", true));
		addSetting( returnPFP = new BooleanSetting("Return pfp values",
				"If activated, pfp (percent false positive) values are computed. \n" +
				"These are similar to FDR corrected statistical p-values.\n\n" +
				"If deactivated, uncorrected p-values are computed.", true));
		addSetting( isPaired = new BooleanSetting("Paired", "Members of each class are paired in the order of their appearance", false));
	}

	public int getPermutationCount() {
		return permCount.getIntValue();		
	}
	
	public boolean isLogged() {
		return logged.getBooleanValue();
	}
	
	public boolean returnPFP() {
		return returnPFP.getBooleanValue();
	}
	
	public boolean isPaired(){
		return isPaired.getBooleanValue();
	}
	
	public RPTwoSampleSetting clone() {
		RPTwoSampleSetting cs = new RPTwoSampleSetting(getName());
		cs.fromPrefNode(this.toPrefNode());
		return cs;
	}
}
