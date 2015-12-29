package mayday.vis3.plots.chromogram;

import mayday.core.DataSet;
import mayday.core.meta.MIGroup;
import mayday.core.meta.types.StringListMIO;
import mayday.core.settings.Setting;
import mayday.core.settings.typed.MIGroupSetting;
import mayday.core.settings.typed.MappingSourceSetting;

public class NameSourceSetting extends MappingSourceSetting
{
	public final static int LIST_MIO = 3;
	protected MIGroupSetting miListGroup;

	public NameSourceSetting(DataSet ds) 
	{
		super(ds);
		miListGroup.setAcceptableClass(StringListMIO.class);
	}
	
	public int getMappingSource() {
		Object o = getObjectValue();
		if (o==PROBENAME)
			return PROBE_NAMES;
		if (o==PROBEDISPLAYNAME)
			return PROBE_DISPLAY_NAMES;
		if (o==migroup)
			return MIO;
		else
			return LIST_MIO;
	}

	public NameSourceSetting clone() 
	{
		NameSourceSetting gs = new NameSourceSetting(ds);
		for (Setting childSetting : children) 
		{
			gs.addSetting(childSetting.clone());
		}
		return gs;
	}

	public MIGroup getListGroup()
	{
		return miListGroup.getMIGroup();
	}

	protected Object[] createPredefinedArray(DataSet ds) {
		return new Object[]{
				PROBENAME,
				PROBEDISPLAYNAME,
				migroup = createMIGroupSetting(ds),
				miListGroup=new MIGroupSetting("String List MIO Group", null, null, ds.getMIManager(), true)
		};
	}

}
