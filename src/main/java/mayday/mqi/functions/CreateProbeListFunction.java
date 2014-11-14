package mayday.mqi.functions;

import mayday.core.DataSet;
import mayday.core.ProbeList;
import mayday.core.datasetmanager.DataSetManager;

public class CreateProbeListFunction 
{
	public static int execute(String probeList, String probe, String dataset)
	{
		// find dataset:
		DataSet ds=null;
		for(DataSet d:DataSetManager.singleInstance.getDataSets())
		{
			if(d.getName().equals(dataset))
			{
				ds=d;
				break;
			}
		}
		if(ds==null) return 0;
		// does probe list exist?
		ProbeList pl= null;
		
		for(ProbeList p:ds.getProbeListManager().getProbeLists())
		{
			if(p.getName().equals(probeList))
			{
				pl=p;
				break;
			}
		}
		if(pl==null)
		{
			pl=new ProbeList(ds,false);
			pl.setName(probeList);
			ds.getProbeListManager().addObjectAtTop(pl);
		}
		if(!pl.contains(probe))
			pl.addProbe(ds.getMasterTable().getProbe(probe));		
		return 1;
	}
	
	public static void createProbeList(String probeList, String probe, String dataset)
	{
		// find dataset:
		DataSet ds=null;
		for(DataSet d:DataSetManager.singleInstance.getDataSets())
		{
			if(d.getName().equals(dataset))
			{
				ds=d;
				break;
			}
		}
		if(ds==null) return;
		// does probe list exist?
		ProbeList pl= null;
		
		for(ProbeList p:ds.getProbeListManager().getProbeLists())
		{
			if(p.getName().equals(probeList))
			{
				pl=p;
				break;
			}
		}
		if(pl==null)
		{
			pl=new ProbeList(ds,false);
			pl.setName(probeList);
			ds.getProbeListManager().addObjectAtTop(pl);
		}
		if(!pl.contains(probe))
			pl.addProbe(ds.getMasterTable().getProbe(probe));		
		return;
	}
}
