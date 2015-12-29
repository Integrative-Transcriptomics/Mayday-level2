package mayday.vis3.plots.chromogram.probelistsort;

import java.util.ArrayList;
import java.util.List;

import mayday.core.DataSet;
import mayday.core.meta.ComparableMIO;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.BooleanSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class ProbeSortSetting extends HierarchicalSetting
{
	private BooleanSetting reverseSetting;
	private MIGroupSetting miGroup;
	private ObjectSelectionSetting<Integer>  experimentSetting;
	private RestrictedStringSetting modeSetting;
	private DataSet ds;
	
	
	
	public ProbeSortSetting(DataSet ds) 
	{
		super("Sort Probe Lists by...");
		this.ds=ds;
		miGroup=new MIGroupSetting("Configure MI Group","The MI Group that should be used to order probes by",null,ds.getMIManager(),true);
		miGroup.setAcceptableClass(ComparableMIO.class);
		
		List<Integer> expNumbers=new ArrayList<Integer>();
		for(int i=0; i!= ds.getMasterTable().getNumberOfExperiments(); ++i)
			expNumbers.add(i);
		
		experimentSetting=new ObjectSelectionSetting<Integer>("Configure Experiment","The experiment which values should be used for sorting",0,expNumbers.toArray(new Integer[0]));
		
		modeSetting=new RestrictedStringSetting("Sort by","How should the probes be sorted",0,ProbeComparisonMode.NAMES);
		reverseSetting=new BooleanSetting("Descending","checked: sort descendingly\n unchecked: ascendingly",false);
		
		addSetting(reverseSetting).addSetting(modeSetting).addSetting(experimentSetting).addSetting(miGroup);
	}
	
	/**
	 * @return the miGroup
	 */
	public MIGroupSetting getMiGroup() {
		return miGroup;
	}

	/**
	 * @param miGroup the miGroup to set
	 */
	public void setMiGroup(MIGroupSetting miGroup) {
		this.miGroup = miGroup;
	}



	/**
	 * @return the modeSetting
	 */
	public RestrictedStringSetting getModeSetting() {
		return modeSetting;
	}

	/**
	 * @param modeSetting the modeSetting to set
	 */
	public void setModeSetting(RestrictedStringSetting modeSetting) {
		this.modeSetting = modeSetting;
	}

	/**
	 * @return the reverseSetting
	 */
	public BooleanSetting getReverseSetting() {
		return reverseSetting;
	}

	/**
	 * @param reverseSetting the reverseSetting to set
	 */
	public void setReverseSetting(BooleanSetting reverseSetting) {
		this.reverseSetting = reverseSetting;
	}

	/**
	 * @return the experimentSetting
	 */
	public ObjectSelectionSetting<Integer> getExperimentSetting() {
		return experimentSetting;
	}

	/**
	 * @param experimentSetting the experimentSetting to set
	 */
	public void setExperimentSetting(
			ObjectSelectionSetting<Integer> experimentSetting) {
		this.experimentSetting = experimentSetting;
	}
	
	@Override
	public ProbeSortSetting clone() 
	{
		ProbeSortSetting clone=new ProbeSortSetting(ds);
		clone.fromPrefNode(toPrefNode());
		return clone;
	}
	
	
}
