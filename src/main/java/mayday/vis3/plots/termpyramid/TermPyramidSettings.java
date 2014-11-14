package mayday.vis3.plots.termpyramid;

import java.util.HashMap;
import java.util.Map;

import mayday.core.ProbeList;
import mayday.core.meta.GenericMIO;
import mayday.core.settings.generic.ExtendableObjectSelectionSetting;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.vis3.model.ViewModel;

public class TermPyramidSettings  extends HierarchicalSetting
{
	private MIGroupSetting miGroup;
	private ExtendableObjectSelectionSetting<String> leftProbeListSetting;
	private ExtendableObjectSelectionSetting<String> rightProbeListSetting;
	private ViewModel model;
	private Map<String,ProbeList> probeLists=new HashMap<String, ProbeList>();

	public TermPyramidSettings(ViewModel model) 
	{
		super("Settings");
		this.model=model;
		miGroup=new MIGroupSetting("Term Source", "The list of terms to be compared", null, model.getDataSet().getMIManager(), false);
		miGroup.setAcceptableClass(GenericMIO.class);
		leftProbeListSetting=new ExtendableObjectSelectionSetting<String>("Left Probe List",null,0, new String[]{""});
		rightProbeListSetting=new ExtendableObjectSelectionSetting<String>("Right Probe List", null, 0, new String[]{""});
		setProbeLists();		
		
		addSetting(miGroup).addSetting(leftProbeListSetting).addSetting(rightProbeListSetting);
		
		
	}

	public void setProbeLists()
	{
		String[] plNames=new String[model.getProbeLists(false).size()];
		int i=0;
		for(ProbeList pl:model.getProbeLists(false))
		{
			probeLists.put(pl.getName(), pl);
			plNames[i]=pl.getName();
			++i;
		}
		leftProbeListSetting.updatePredefined(plNames);
		if(leftProbeListSetting.getSelectedIndex() <0 )
			leftProbeListSetting.setSelectedIndex(0);
		rightProbeListSetting.updatePredefined(plNames);
		if(rightProbeListSetting.getSelectedIndex() <0 )
			rightProbeListSetting.setSelectedIndex(0);
		
		
	}

	public MIGroupSetting getMiGroup() {
		return miGroup;
	}

	public void setMiGroup(MIGroupSetting miGroup) {
		this.miGroup = miGroup;
	}

	public ExtendableObjectSelectionSetting<String> getLeftProbeListSetting() {
		return leftProbeListSetting;
	}

	public ExtendableObjectSelectionSetting<String> getRightProbeListSetting() {
		return rightProbeListSetting;
	}



	public ProbeList getLeftProbeList()
	{
		return probeLists.get(leftProbeListSetting.getStringValue());
	}

	public ProbeList getRightProbeList()
	{
		return probeLists.get(rightProbeListSetting.getStringValue());
	}

	@Override
	public TermPyramidSettings clone() 
	{
		TermPyramidSettings settings=new TermPyramidSettings(model);
		settings.miGroup=miGroup.clone();		
		settings.leftProbeListSetting=leftProbeListSetting.clone();
		settings.rightProbeListSetting=rightProbeListSetting.clone();

		return settings;
	}
}
