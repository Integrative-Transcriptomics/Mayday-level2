package mayday.vis3.plots.tagcloud;

import mayday.core.DataSet;
import mayday.core.meta.NominalMIO;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.generic.HierarchicalSetting;
import mayday.core.settings.generic.ObjectSelectionSetting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.RestrictedStringSetting;

public class TagCloudSettings extends HierarchicalSetting
{
	private RestrictedStringSetting layouterSetting;
	private MIGroupSetting miGroupSetting; 
//	private MIGroupSetting miListGroupSetting; 

	private DataSet ds;
	
	public TagCloudSettings(DataSet ds) 
	{
		super("Tag Cloud Settings");
		this.ds=ds;
		
		layouterSetting=new RestrictedStringSetting("Tag cloud layout","The way tags are layouted",0,TagLayout.layoutNames)
		.setLayoutStyle(ObjectSelectionSetting.LayoutStyle.RADIOBUTTONS);
		miGroupSetting=new MIGroupSetting("Tag source",null,null,ds.getMIManager(),false).setAcceptableClass(NominalMIO.class, StringListMIO.class);
//		miListGroupSetting=new MIGroupSetting("Tag source (List) ",null,null,ds.getMIManager(),false).setAcceptableClass();
		addSetting(layouterSetting).addSetting(miGroupSetting); //.addSetting(miListGroupSetting);
	}

	/**
	 * @return the layouterSetting
	 */
	public RestrictedStringSetting getLayouterSetting() {
		return layouterSetting;
	}

	/**
	 * @param layouterSetting the layouterSetting to set
	 */
	public void setLayouterSetting(RestrictedStringSetting layouterSetting) {
		this.layouterSetting = layouterSetting;
	}

	/**
	 * @return the miGroupSetting
	 */
	public MIGroupSetting getMiGroupSetting() {
		return miGroupSetting;
	}

	/**
	 * @param miGroupSetting the miGroupSetting to set
	 */
	public void setMiGroupSetting(MIGroupSetting miGroupSetting) {
		this.miGroupSetting = miGroupSetting;
	}

//	/**
//	 * @return the miListGroupSetting
//	 */
//	public MIGroupSetting getMiListGroupSetting() {
//		return miListGroupSetting;
//	}

	/**
	 * @param miListGroupSetting the miListGroupSetting to set
	 */
//	public void setMiListGroupSetting(MIGroupSetting miListGroupSetting) {
//		this.miListGroupSetting = miListGroupSetting;
//	}
	
	@Override
	public TagCloudSettings clone() 
	{
		TagCloudSettings clone=new TagCloudSettings(ds);
		clone.fromPrefNode(this.toPrefNode());
//		clone.miGroupSetting.setMIGroup(miGroupSetting.getMIGroup());
//		clone.miListGroupSetting.setMIGroup(miListGroupSetting.getMIGroup());
		return clone;
	}
	

	
}
